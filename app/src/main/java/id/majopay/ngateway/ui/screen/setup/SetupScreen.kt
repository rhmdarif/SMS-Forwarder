package id.majopay.ngateway.ui.screen.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.majopay.ngateway.ui.component.FlatCard
import id.majopay.ngateway.ui.component.InfoBanner
import id.majopay.ngateway.ui.component.PrimaryButton
import id.majopay.ngateway.ui.component.ScreenHeader
import id.majopay.ngateway.ui.component.ScreenHorizontalPadding
import id.majopay.ngateway.ui.component.SolidIconBubble
import id.majopay.ngateway.ui.theme.Tone

/**
 * Layar setup kredensial API. Ditampilkan saat kredensial belum diatur
 * (install pertama atau setelah dihapus) atau saat user menekan "Isi sekarang" di banner.
 *
 * Tidak memblok: tombol "Lewati dulu" membawa user masuk ke aplikasi dengan banner peringatan.
 *
 * @param onDone Dipanggil setelah kredensial tersimpan atau user memilih melewati.
 */
@Composable
fun SetupScreen(
    onDone: () -> Unit,
    viewModel: CredentialsViewModel = hiltViewModel()
) {
    val existing by viewModel.credentials.collectAsState()
    val form = rememberCredentialsFormState(
        initialApiKey = existing?.apiKey ?: "",
        initialApiSecret = existing?.apiSecret ?: ""
    )

    val submit = {
        val error = viewModel.save(form.apiKey, form.apiSecret)
        if (error == null) onDone() else form.error = error
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenHorizontalPadding, vertical = 28.dp)
        ) {
            SetupHeader()

            Spacer(modifier = Modifier.height(24.dp))

            FlatCard(modifier = Modifier.fillMaxWidth()) {
                CredentialsFormFields(state = form, onSubmit = submit)
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryButton(
                    text = "Simpan & lanjut",
                    onClick = submit,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lewati dulu",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoBanner(
                icon = Icons.Outlined.Lock,
                tone = Tone.Teal,
                title = "Aman di perangkatmu",
                message = "Kredensial disimpan terenkripsi di perangkat ini dan nggak ikut backup. " +
                    "Tanpa kredensial, aplikasi tetap bisa dibuka, tapi pesan belum bisa diteruskan."
            )
        }
    }
}

@Composable
private fun SetupHeader() {
    Column {
        SolidIconBubble(
            icon = Icons.Outlined.CloudDone,
            tone = Tone.Primary,
            size = 72.dp,
            iconSize = 36.dp,
            shape = MaterialTheme.shapes.large
        )
        Spacer(modifier = Modifier.height(20.dp))
        ScreenHeader(
            title = "Hubungkan ke Majopay",
            subtitle = "Masukkan API Key dan Secret dari dashboard Majopay biar pesan yang " +
                "cocok bisa langsung diteruskan."
        )
    }
}
