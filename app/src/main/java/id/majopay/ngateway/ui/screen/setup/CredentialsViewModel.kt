package id.majopay.ngateway.ui.screen.setup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.majopay.ngateway.data.repository.CredentialRepository
import id.majopay.ngateway.domain.model.ApiCredentials
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel bersama untuk semua UI yang menyentuh kredensial API:
 * layar setup, banner peringatan di Rules/History, dan section di Settings.
 *
 * Tidak menyimpan state form; form dikelola lokal oleh composable supaya tiap
 * pemanggil (setup penuh vs dialog "Ubah" di Settings) bebas mengatur siklus hidupnya.
 */
@HiltViewModel
class CredentialsViewModel @Inject constructor(
    private val credentialRepository: CredentialRepository
) : ViewModel() {

    /** `null` = belum diatur. */
    val credentials: StateFlow<ApiCredentials?> = credentialRepository.credentials

    /**
     * Validasi lalu simpan. Mengembalikan pesan error (untuk ditampilkan di form)
     * atau `null` jika berhasil.
     */
    fun save(apiKey: String, apiSecret: String): String? {
        val key = apiKey.trim()
        val secret = apiSecret.trim()
        if (key.isEmpty()) return "API Key wajib diisi"
        if (secret.isEmpty()) return "API Secret wajib diisi"
        if (key.any { it.isWhitespace() }) return "API Key tidak boleh mengandung spasi"
        return try {
            credentialRepository.save(key, secret)
            null
        } catch (e: Exception) {
            e.message ?: "Gagal menyimpan kredensial"
        }
    }

    fun clear() {
        credentialRepository.clear()
    }
}
