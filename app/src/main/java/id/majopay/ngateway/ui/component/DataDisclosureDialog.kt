package id.majopay.ngateway.ui.component

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.majopay.ngateway.BuildConfig
import id.majopay.ngateway.ui.theme.Tone
import id.majopay.ngateway.ui.theme.colors

/**
 * Jenis akses data sensitif yang butuh "prominent disclosure" ala Google Play
 * sebelum akses diaktifkan.
 */
enum class SensitiveAccess(
    val title: String,
    val icon: ImageVector,
    val tone: Tone,
    val intro: String,
    val points: List<String>
) {
    Notification(
        title = "Sebelum kamu mengaktifkan akses notifikasi",
        icon = Icons.Outlined.NotificationsActive,
        tone = Tone.Purple,
        intro = "Majopay Gateway membaca notifikasi dari aplikasi yang kamu pilih untuk dipantau, " +
            "lalu meneruskan yang cocok aturan secara otomatis. Berikut yang perlu kamu tahu:",
        points = listOf(
            "Yang dibaca: nama aplikasi, judul, dan isi teks notifikasi yang muncul di perangkat ini.",
            "Tujuannya: mencocokkan notifikasi dengan aturan yang kamu buat, misalnya notifikasi dari aplikasi tertentu.",
            "Yang dikirim keluar: hanya notifikasi yang cocok dengan aturan, lewat HTTPS ke relay api-proxy.majopay.id yang meneruskannya ke aplikasi/endpoint milikmu.",
            "Semua notifikasi yang diproses, termasuk yang tidak cocok, dicatat di Riwayat dan hanya tersimpan di perangkat ini.",
            "Akses ini bisa kamu cabut kapan saja lewat pengaturan Android."
        )
    )
}

/**
 * Dialog "prominent disclosure" sesuai kebijakan User Data Google Play: menjelaskan data apa
 * yang diakses, untuk apa, dan ke mana dikirim, lalu meminta persetujuan eksplisit
 * SEBELUM dialog izin sistem dimunculkan.
 *
 * @param onAccept Dipanggil saat user menyetujui; pemanggil lalu meminta izin sistem.
 */
@Composable
fun DataDisclosureDialog(
    access: SensitiveAccess,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = access.tone.colors()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp,
        icon = {
            Icon(
                imageVector = access.icon,
                contentDescription = null,
                tint = colors.foreground,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(text = access.title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = access.intro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SoftBlock {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        access.points.forEach { point ->
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = colors.foreground,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                TextButton(
                    onClick = { openPrivacyPolicy(context) },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Baca kebijakan privasi",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            PrimaryButton(text = "Setuju & lanjut", onClick = onAccept, tone = access.tone)
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Nanti dulu", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

/** Buka URL kebijakan privasi (dari BuildConfig) di browser; beri tahu user kalau gagal. */
fun openPrivacyPolicy(context: android.content.Context) {
    val url = BuildConfig.PRIVACY_POLICY_URL.trim()
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val opened = url.startsWith("http", ignoreCase = true) &&
        runCatching { context.startActivity(intent) }.isSuccess
    if (!opened) {
        Toast.makeText(context, "Nggak bisa membuka kebijakan privasi: $url", Toast.LENGTH_LONG).show()
    }
}
