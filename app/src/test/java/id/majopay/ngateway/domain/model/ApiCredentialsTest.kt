package id.majopay.ngateway.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiCredentialsTest {

    @Test
    fun `maskSecret keeps only last four characters`() {
        assertEquals("****wxyz", ApiCredentials.maskSecret("secret-abcd-wxyz"))
    }

    @Test
    fun `maskSecret hides short values entirely`() {
        assertEquals("****", ApiCredentials.maskSecret("abcd"))
        assertEquals("****", ApiCredentials.maskSecret("ab"))
    }

    @Test
    fun `maskSecret returns empty for blank input`() {
        assertEquals("", ApiCredentials.maskSecret(""))
        assertEquals("", ApiCredentials.maskSecret("   "))
    }

    @Test
    fun `maskedSecret property masks the api secret`() {
        val credentials = ApiCredentials(apiKey = "key", apiSecret = "very-secret-1234")
        assertEquals("****1234", credentials.maskedSecret)
    }

    @Test
    fun `isValid requires both fields non-blank`() {
        assertTrue(ApiCredentials("key", "secret").isValid())
        assertFalse(ApiCredentials("", "secret").isValid())
        assertFalse(ApiCredentials("key", "   ").isValid())
    }

    @Test
    fun `secret header name is x-app-key`() {
        assertEquals("x-app-key", ApiCredentials.SECRET_HEADER)
    }
}
