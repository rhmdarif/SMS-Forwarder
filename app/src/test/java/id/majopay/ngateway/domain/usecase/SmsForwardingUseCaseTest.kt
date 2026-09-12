package id.majopay.ngateway.domain.usecase

import com.google.gson.Gson
import id.majopay.ngateway.data.config.WebhookConfig
import id.majopay.ngateway.data.config.WebhookUrlBuilder
import id.majopay.ngateway.data.remote.api.ForwardingApiService
import id.majopay.ngateway.data.remote.client.HttpClient
import id.majopay.ngateway.data.repository.CredentialRepository
import id.majopay.ngateway.data.repository.HistoryRepository
import id.majopay.ngateway.data.repository.RuleRepository
import id.majopay.ngateway.domain.model.ApiCredentials
import id.majopay.ngateway.domain.model.ForwardingHistory
import id.majopay.ngateway.domain.model.ForwardingStatus
import id.majopay.ngateway.domain.model.Rule
import id.majopay.ngateway.domain.model.SmsMessage
import id.majopay.ngateway.domain.model.SourceType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SmsForwardingUseCaseTest {

    private companion object {
        const val BASE_URL = "https://api-proxy.dev-ngalehkuy.workers.dev/"
        const val API_KEY = "mp_live_key_9f8e"
        const val API_SECRET = "super-secret-value-1234"
    }

    private val ruleRepository: RuleRepository = mock()
    private val historyRepository: HistoryRepository = mock()
    private val apiService: ForwardingApiService = mock()
    private val webhookConfig: WebhookConfig = mock()
    private val credentialRepository: CredentialRepository = mock()

    private lateinit var useCase: SmsForwardingUseCase

    private val now = Clock.System.now()
    private val rule = Rule(
        id = 7,
        name = "Semua SMS",
        pattern = "transfer",
        source = SourceType.SMS,
        createdAt = now,
        updatedAt = now
    )
    private val sms = SmsMessage(body = "Anda menerima transfer Rp 50.000", sender = "+628123", timestamp = now)

    @Before
    fun setUp() = runTest {
        whenever(webhookConfig.url).thenReturn(BASE_URL)
        whenever(webhookConfig.method).thenReturn("POST")
        whenever(webhookConfig.headers).thenReturn(emptyMap())
        whenever(webhookConfig.isConfigured()).thenReturn(true)
        whenever(webhookConfig.resolveEndpoint(any(), any())).doAnswer { invocation ->
            WebhookUrlBuilder.build(BASE_URL, invocation.getArgument(0), invocation.getArgument(1))
        }

        whenever(ruleRepository.getActiveRules()).thenReturn(flowOf(listOf(rule)))
        whenever(historyRepository.createHistory(any())).thenReturn(42L)
        whenever(apiService.postToEndpoint(any(), any(), any())).thenReturn(Response.success("ok"))

        useCase = SmsForwardingUseCase(
            ruleRepository = ruleRepository,
            historyRepository = historyRepository,
            httpClient = HttpClient(apiService, Gson()),
            webhookConfig = webhookConfig,
            credentialRepository = credentialRepository
        )
    }

    @Test
    fun `tanpa kredensial webhook tidak dikirim dan history FAILED`() = runTest {
        whenever(credentialRepository.current()).thenReturn(null)

        val results = useCase.processSms(sms)

        verify(apiService, never()).postToEndpoint(any(), any(), any())

        assertEquals(1, results.size)
        val history = results.first()
        assertEquals(ForwardingStatus.FAILED, history.status)
        assertEquals(SmsForwardingUseCase.CREDENTIALS_MISSING_MESSAGE, history.errorMessage)
        assertEquals(BASE_URL, history.endpoint)
        assertFalse(history.requestHeaders.containsKey(ApiCredentials.SECRET_HEADER))

        val updated = argumentCaptor<ForwardingHistory>()
        verify(historyRepository).updateHistory(updated.capture())
        assertEquals(ForwardingStatus.FAILED, updated.firstValue.status)
    }

    @Test
    fun `dengan kredensial endpoint berisi api_key dan header x-app-key berisi secret`() = runTest {
        whenever(credentialRepository.current()).thenReturn(ApiCredentials(API_KEY, API_SECRET))

        useCase.processSms(sms)

        val url = argumentCaptor<String>()
        val headers = argumentCaptor<Map<String, String>>()
        verify(apiService).postToEndpoint(url.capture(), headers.capture(), any())

        assertEquals("https://api-proxy.dev-ngalehkuy.workers.dev/$API_KEY", url.firstValue)
        assertEquals(API_SECRET, headers.firstValue[ApiCredentials.SECRET_HEADER])
        assertEquals("application/json", headers.firstValue["Content-Type"])
        assertEquals("SMS", headers.firstValue["X-Source-Type"])
    }

    @Test
    fun `snapshot history di-mask dan tidak menyimpan kredensial utuh`() = runTest {
        whenever(credentialRepository.current()).thenReturn(ApiCredentials(API_KEY, API_SECRET))

        val results = useCase.processSms(sms)
        val history = results.first()

        assertEquals(ForwardingStatus.SUCCESS, history.status)
        assertEquals("https://api-proxy.dev-ngalehkuy.workers.dev/****9f8e", history.endpoint)
        assertEquals("****1234", history.requestHeaders[ApiCredentials.SECRET_HEADER])

        val serialized = history.endpoint + history.requestHeaders.toString()
        assertFalse("api_key bocor ke history", serialized.contains(API_KEY))
        assertFalse("api_secret bocor ke history", serialized.contains(API_SECRET))

        val created = argumentCaptor<ForwardingHistory>()
        verify(historyRepository).createHistory(created.capture())
        assertEquals("****1234", created.firstValue.requestHeaders[ApiCredentials.SECRET_HEADER])
        assertTrue(created.firstValue.endpoint!!.endsWith("/****9f8e"))
    }

    @Test
    fun `resend tanpa kredensial dilempar sebagai IllegalArgumentException`() = runTest {
        whenever(credentialRepository.current()).thenReturn(null)
        val history = ForwardingHistory(
            id = 1,
            ruleId = rule.id,
            matchedRule = true,
            messageBody = sms.body,
            sourceType = "SMS",
            requestBody = "{}",
            status = ForwardingStatus.FAILED,
            timestamp = now
        )

        try {
            useCase.resendHistory(history)
            fail("Seharusnya melempar IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals(SmsForwardingUseCase.CREDENTIALS_MISSING_MESSAGE, e.message)
        }
        verify(apiService, never()).postToEndpoint(any(), any(), any())
    }

    @Test
    fun `resend membangun ulang endpoint dan header dari kredensial aktif`() = runTest {
        whenever(credentialRepository.current()).thenReturn(ApiCredentials(API_KEY, API_SECRET))
        val history = ForwardingHistory(
            id = 1,
            ruleId = rule.id,
            matchedRule = true,
            messageBody = sms.body,
            sourceType = "SMS",
            endpoint = "https://api-proxy.dev-ngalehkuy.workers.dev/****old1",
            requestHeaders = mapOf(ApiCredentials.SECRET_HEADER to "****old1"),
            requestBody = "{\"body\":\"x\"}",
            status = ForwardingStatus.FAILED,
            timestamp = now
        )

        val result = useCase.resendHistory(history)

        val url = argumentCaptor<String>()
        val headers = argumentCaptor<Map<String, String>>()
        verify(apiService).postToEndpoint(url.capture(), headers.capture(), eq("{\"body\":\"x\"}"))
        assertEquals("https://api-proxy.dev-ngalehkuy.workers.dev/$API_KEY", url.firstValue)
        assertEquals(API_SECRET, headers.firstValue[ApiCredentials.SECRET_HEADER])

        assertEquals(ForwardingStatus.SUCCESS, result.status)
        assertEquals("https://api-proxy.dev-ngalehkuy.workers.dev/****9f8e", result.endpoint)
        assertEquals("****1234", result.requestHeaders[ApiCredentials.SECRET_HEADER])
    }
}
