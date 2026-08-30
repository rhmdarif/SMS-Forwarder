package id.majopay.gateway.data.config

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.majopay.gateway.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Webhook configuration sourced from `BuildConfig`. Values are bundled at compile time
 * from `local.properties` (or environment variables) and cannot be modified by the user
 * at runtime. See `local.properties.example` in the project root for the expected keys.
 *
 * Single global webhook: every rule that matches forwards to the same URL/method/headers.
 */
@Singleton
class WebhookConfig @Inject constructor(
    private val gson: Gson
) {
    val url: String = BuildConfig.WEBHOOK_URL

    val method: String = BuildConfig.WEBHOOK_METHOD.ifBlank { "POST" }

    val headers: Map<String, String> by lazy { parseHeaders(BuildConfig.WEBHOOK_HEADERS_JSON) }

    fun isConfigured(): Boolean =
        url.startsWith("http://", ignoreCase = true) ||
            url.startsWith("https://", ignoreCase = true)

    private fun parseHeaders(raw: String): Map<String, String> {
        if (raw.isBlank()) return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson<Map<String, String>>(raw, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
