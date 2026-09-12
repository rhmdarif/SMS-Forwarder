package id.majopay.ngateway.data.config

import java.net.URLEncoder

/**
 * Menggabungkan base URL webhook dengan api_key sebagai path segment.
 * Dipisah dari [WebhookConfig] supaya bisa diuji tanpa `BuildConfig`.
 */
object WebhookUrlBuilder {

    /**
     * Hasil: `<baseUrl tanpa trailing slash>/<apiKey ter-encode>`.
     *
     * - Trailing slash pada [baseUrl] dinormalisasi agar tidak menghasilkan `//`.
     * - [apiKey] di-URL-encode (path segment) sebagai pengaman jika mengandung
     *   karakter di luar alfanumerik/tanda hubung.
     * - Jika [apiKey] kosong, base URL dikembalikan apa adanya (tanpa trailing slash).
     * - [encode] = false hanya untuk nilai tampilan (mis. api_key yang sudah di-mask
     *   `****abcd`) supaya karakter mask tidak berubah jadi `%2A`.
     */
    fun build(baseUrl: String, apiKey: String, encode: Boolean = true): String {
        val trimmedBase = baseUrl.trim().trimEnd('/')
        val key = apiKey.trim()
        if (key.isEmpty()) return trimmedBase
        val segment = if (encode) encodePathSegment(key) else key
        return "$trimmedBase/$segment"
    }

    private fun encodePathSegment(segment: String): String =
        URLEncoder.encode(segment, "UTF-8")
            // URLEncoder mengikuti form-encoding: spasi jadi '+' dan '~' ikut di-escape.
            // Koreksi supaya sesuai path segment RFC 3986.
            .replace("+", "%20")
            .replace("%7E", "~")
}
