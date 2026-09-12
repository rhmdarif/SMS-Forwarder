package id.majopay.ngateway.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import id.majopay.ngateway.ui.screen.setup.CredentialsViewModel
import id.majopay.ngateway.ui.theme.Tone

/**
 * Banner peringatan yang tampil di Rules/History selama kredensial API belum diatur.
 * Tidak merender apa pun jika kredensial sudah ada.
 *
 * @param onSetupClick Navigasi ke layar setup.
 */
@Composable
fun CredentialsBanner(
    onSetupClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CredentialsViewModel = hiltViewModel()
) {
    val credentials by viewModel.credentials.collectAsState()
    if (credentials != null) return

    InfoBanner(
        modifier = modifier,
        icon = Icons.Outlined.Key,
        tone = Tone.Warning,
        title = "Kredensial API belum diisi",
        message = "Pesan yang cocok belum bisa diteruskan sampai kamu mengisi API Key dan Secret.",
        actionLabel = "Isi sekarang",
        onAction = onSetupClick
    )
}
