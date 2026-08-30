package id.majopay.gateway.data.remote.client

import com.google.gson.Gson
import id.majopay.gateway.data.remote.api.ForwardingApiService
import id.majopay.gateway.domain.model.SmsMessage
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HTTP client for forwarding SMS messages and notifications to external APIs.
 * Handles retry logic and different HTTP methods.
 *
 * Exposes payload + header builders so the caller can persist the exact bytes that will
 * be sent. This guarantees that a later "resend" replays the original request byte-for-byte.
 */
@Singleton
class HttpClient @Inject constructor(
    private val apiService: ForwardingApiService,
    private val gson: Gson
) {

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY = 1000L
        private const val RETRY_MULTIPLIER = 2L
    }

    /**
     * Build the canonical JSON payload for an SMS forward.
     */
    fun buildSmsPayloadJson(smsMessage: SmsMessage): String =
        gson.toJson(smsMessage.toApiPayload())

    /**
     * Build the canonical JSON payload for a notification forward.
     */
    fun buildNotificationPayloadJson(
        packageName: String,
        appLabel: String,
        title: String,
        text: String,
        postTime: Long,
        extras: Map<String, Any?>
    ): String {
        val cleanExtras: Map<String, Any> = extras
            .filterValues { it != null }
            .mapValues { entry ->
                when (val value = entry.value!!) {
                    is String, is Number, is Boolean -> value
                    is Collection<*> -> value.mapNotNull { it?.toString() }
                    is Array<*> -> value.mapNotNull { it?.toString() }
                    else -> value.toString()
                }
            }
        val payload: Map<String, Any> = mapOf(
            "sourceType" to "NOTIFICATION",
            "packageName" to packageName,
            "appLabel" to appLabel,
            "title" to title,
            "text" to text,
            "postTime" to postTime,
            "extras" to cleanExtras,
            "timestamp" to System.currentTimeMillis()
        )
        return gson.toJson(payload)
    }

    /**
     * Build the effective header map that will be sent on the wire (rule headers
     * plus standard defaults). Returned as a plain Map so callers can persist it.
     */
    fun buildEffectiveHeaders(
        ruleHeaders: Map<String, String>,
        sourceType: String
    ): Map<String, String> =
        ruleHeaders.toMutableMap().apply {
            putIfAbsent("Content-Type", "application/json")
            putIfAbsent("User-Agent", "SMS-Forwarder/1.0")
            putIfAbsent("X-Source-Type", sourceType)
        }

    /**
     * Send a request with a pre-built endpoint, method, headers, and body.
     * This is the single send path: used both for the initial forward and for "resend
     * from history" so the exact same bytes go out in both cases.
     */
    suspend fun forwardRaw(
        endpoint: String,
        method: String,
        headers: Map<String, String>,
        body: String
    ): ForwardingResult {
        val effectiveHeaders = headers.toMutableMap().apply {
            putIfAbsent("Content-Type", "application/json")
        }

        return executeWithRetry(
            maxAttempts = MAX_RETRY_ATTEMPTS,
            initialDelay = INITIAL_RETRY_DELAY
        ) {
            when (method.uppercase()) {
                "GET" -> {
                    val queryParams = decodeBodyToQueryParams(body)
                    apiService.getFromEndpoint(endpoint, effectiveHeaders, queryParams)
                }
                "POST" -> apiService.postToEndpoint(endpoint, effectiveHeaders, body)
                "PUT" -> apiService.putToEndpoint(endpoint, effectiveHeaders, body)
                "PATCH" -> apiService.patchToEndpoint(endpoint, effectiveHeaders, body)
                else -> apiService.postToEndpoint(endpoint, effectiveHeaders, body)
            }
        }
    }

    /**
     * Decode a JSON body back into a flat query param map. Used for GET requests so the
     * payload shape stored in history (as JSON) can still be sent over the wire when the
     * rule uses GET.
     */
    private fun decodeBodyToQueryParams(body: String): Map<String, String> {
        return try {
            @Suppress("UNCHECKED_CAST")
            val map = gson.fromJson(body, Map::class.java) as? Map<String, Any?> ?: emptyMap()
            map.filterValues { it != null }.mapValues { it.value.toString() }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private suspend fun executeWithRetry(
        maxAttempts: Int,
        initialDelay: Long,
        request: suspend () -> Response<String>
    ): ForwardingResult {
        var lastException: Exception? = null
        var currentDelay = initialDelay

        repeat(maxAttempts) { attempt ->
            try {
                val response = request()

                return if (response.isSuccessful) {
                    ForwardingResult.Success(
                        responseCode = response.code(),
                        responseBody = response.body()
                    )
                } else {
                    ForwardingResult.HttpError(
                        responseCode = response.code(),
                        responseBody = response.errorBody()?.string(),
                        message = "HTTP ${response.code()}: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                lastException = e

                if (attempt < maxAttempts - 1) {
                    delay(currentDelay)
                    currentDelay *= RETRY_MULTIPLIER
                }
            }
        }

        return ForwardingResult.NetworkError(
            exception = lastException ?: Exception("Unknown network error"),
            message = "Failed after $maxAttempts attempts: ${lastException?.message}"
        )
    }
}

/**
 * Result of a forwarding attempt (SMS or notification).
 */
sealed class ForwardingResult {
    data class Success(
        val responseCode: Int,
        val responseBody: String?
    ) : ForwardingResult()

    data class HttpError(
        val responseCode: Int,
        val responseBody: String?,
        val message: String
    ) : ForwardingResult()

    data class NetworkError(
        val exception: Exception,
        val message: String
    ) : ForwardingResult()
}
