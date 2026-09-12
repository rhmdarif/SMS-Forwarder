package id.majopay.ngateway.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import id.majopay.ngateway.domain.model.ApiCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Penyimpanan kredensial API (api_key + api_secret) menggunakan
 * [EncryptedSharedPreferences] yang kuncinya dilindungi Android Keystore.
 *
 * Nilai saat ini diekspos sebagai [StateFlow] sehingga UI (banner, navigasi, settings)
 * dan use case pengiriman webhook selalu membaca kondisi terbaru tanpa I/O tambahan.
 *
 * File prefs `api_credentials.xml` dikecualikan dari Auto Backup (lihat
 * `res/xml/backup_rules.xml`) karena Keystore tidak bisa dipindahkan antar perangkat.
 */
@Singleton
class CredentialRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "CredentialRepository"
        private const val PREFS_FILE = "api_credentials"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_API_SECRET = "api_secret"
    }

    private val prefs: SharedPreferences by lazy { createEncryptedPrefs() }

    private val _credentials = MutableStateFlow(readFromPrefs())

    /** `null` berarti kredensial belum diatur (atau sudah dihapus). */
    val credentials: StateFlow<ApiCredentials?> = _credentials.asStateFlow()

    /** Snapshot sinkron; nyaman untuk use case yang berjalan di service/worker. */
    fun current(): ApiCredentials? = _credentials.value

    fun isConfigured(): Boolean = _credentials.value?.isValid() == true

    /**
     * Simpan kredensial. Input di-trim; jika salah satu kosong akan dilempar
     * [IllegalArgumentException] supaya caller (ViewModel) menampilkan error validasi.
     */
    fun save(apiKey: String, apiSecret: String) {
        val value = ApiCredentials(apiKey.trim(), apiSecret.trim())
        require(value.isValid()) { "api_key dan api_secret tidak boleh kosong" }
        prefs.edit(commit = true) {
            putString(KEY_API_KEY, value.apiKey)
            putString(KEY_API_SECRET, value.apiSecret)
        }
        _credentials.value = value
        Log.i(TAG, "Kredensial API disimpan (secret: ${value.maskedSecret})")
    }

    fun clear() {
        prefs.edit(commit = true) {
            remove(KEY_API_KEY)
            remove(KEY_API_SECRET)
        }
        _credentials.value = null
        Log.i(TAG, "Kredensial API dihapus")
    }

    private fun readFromPrefs(): ApiCredentials? {
        return try {
            val key = prefs.getString(KEY_API_KEY, null) ?: return null
            val secret = prefs.getString(KEY_API_SECRET, null) ?: return null
            ApiCredentials(key, secret).takeIf { it.isValid() }
        } catch (e: Exception) {
            // Keyset rusak (mis. hasil restore dari perangkat lain). Anggap belum diatur;
            // user akan diminta input ulang lewat layar setup.
            Log.e(TAG, "Gagal membaca kredensial terenkripsi, dianggap kosong", e)
            null
        }
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
