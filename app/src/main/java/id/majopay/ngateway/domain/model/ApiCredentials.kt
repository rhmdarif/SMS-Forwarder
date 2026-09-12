package id.majopay.ngateway.domain.model

/**
 * Kredensial API yang diinput user dan disimpan terenkripsi di perangkat.
 *
 * - [apiKey] dipakai sebagai path segment pada URL webhook: `WEBHOOK_URL/{apiKey}`.
 * - [apiSecret] dikirim sebagai header `x-app-key`.
 *
 * Keduanya tidak pernah disimpan ke tabel history secara utuh; gunakan [maskSecret]
 * untuk membuat snapshot yang aman.
 */
data class ApiCredentials(
    val apiKey: String,
    val apiSecret: String
) {
    fun isValid(): Boolean = apiKey.isNotBlank() && apiSecret.isNotBlank()

    /** api_secret dalam bentuk `****` + 4 karakter terakhir, aman untuk ditampilkan/dicatat. */
    val maskedSecret: String get() = maskSecret(apiSecret)

    companion object {
        /** Nama header tempat api_secret dikirim. */
        const val SECRET_HEADER = "x-app-key"

        private const val MASK = "****"
        private const val VISIBLE_SUFFIX = 4

        /**
         * Sembunyikan nilai rahasia, sisakan [VISIBLE_SUFFIX] karakter terakhir.
         * Nilai pendek (<= 4 karakter) di-mask seluruhnya agar tidak bocor utuh.
         */
        fun maskSecret(value: String): String {
            if (value.isBlank()) return ""
            if (value.length <= VISIBLE_SUFFIX) return MASK
            return MASK + value.takeLast(VISIBLE_SUFFIX)
        }
    }
}
