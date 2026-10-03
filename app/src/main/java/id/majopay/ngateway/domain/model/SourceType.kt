package id.majopay.ngateway.domain.model

/**
 * Enum representing the source type for forwarding rules.
 * Saat ini hanya notifikasi aplikasi; sumber SMS sudah dihapus (lihat MIGRATION_4_5).
 */
enum class SourceType {
    /**
     * Rule applies to notifications from other apps.
     */
    NOTIFICATION
} 