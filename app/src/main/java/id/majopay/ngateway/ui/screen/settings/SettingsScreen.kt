package id.majopay.ngateway.ui.screen.settings

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import id.majopay.ngateway.data.repository.AppRepository
import id.majopay.ngateway.data.service.NotifRouterService
import id.majopay.ngateway.domain.model.ApiCredentials
import id.majopay.ngateway.ui.component.IconListRow
import id.majopay.ngateway.ui.component.InfoBanner
import id.majopay.ngateway.ui.component.MajopayTopBar
import id.majopay.ngateway.ui.component.PrimaryButton
import id.majopay.ngateway.ui.component.PrimaryButtonSmall
import id.majopay.ngateway.ui.component.ScreenHeader
import id.majopay.ngateway.ui.component.ScreenHorizontalPadding
import id.majopay.ngateway.ui.component.SectionCard
import id.majopay.ngateway.ui.component.SoftBlock
import id.majopay.ngateway.ui.component.StatusChip
import id.majopay.ngateway.ui.component.TonalButton
import id.majopay.ngateway.ui.screen.setup.CredentialsFormFields
import id.majopay.ngateway.ui.screen.setup.CredentialsViewModel
import id.majopay.ngateway.ui.screen.setup.rememberCredentialsFormState
import id.majopay.ngateway.ui.theme.Tone
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    onCredentialsCleared: () -> Unit = {},
    appRepository: AppRepository = hiltViewModel<SettingsViewModel>().appRepository,
    credentialsViewModel: CredentialsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentials by credentialsViewModel.credentials.collectAsState()

    val permissionsToRequest = mutableListOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = permissionsToRequest)
    val isNotificationListenerEnabled = remember(context) {
        NotifRouterService.isServiceEnabled(context)
    }

    val openNotificationListenerSettings = {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        context.startActivity(intent)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MajopayTopBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = ScreenHorizontalPadding,
                end = ScreenHorizontalPadding,
                top = 8.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ScreenHeader(
                    title = "Pengaturan",
                    subtitle = "Atur izin, kredensial, dan lihat info aplikasi di sini."
                )
            }

            item {
                SectionCard(title = "Kredensial API", icon = Icons.Outlined.Key, tone = Tone.Primary) {
                    CredentialsSection(
                        credentials = credentials,
                        onSave = { key, secret -> credentialsViewModel.save(key, secret) },
                        onClear = {
                            credentialsViewModel.clear()
                            onCredentialsCleared()
                        }
                    )
                }
            }

            item {
                SectionCard(title = "Izin akses", icon = Icons.Outlined.Lock, tone = Tone.Orange) {
                    PermissionItem(
                        title = "Izin SMS",
                        description = "Biar aplikasi bisa menerima dan membaca SMS.",
                        icon = Icons.Outlined.Sms,
                        tone = Tone.Primary,
                        isGranted = permissionsState.permissions.filter {
                            it.permission == Manifest.permission.RECEIVE_SMS ||
                                it.permission == Manifest.permission.READ_SMS
                        }.all { it.status.isGranted },
                        onRequest = { permissionsState.launchMultiplePermissionRequest() }
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PermissionItem(
                            title = "Izin notifikasi",
                            description = "Dibutuhkan untuk layanan latar belakang (Android 13+).",
                            icon = Icons.Outlined.Notifications,
                            tone = Tone.Purple,
                            isGranted = permissionsState.permissions.find {
                                it.permission == Manifest.permission.POST_NOTIFICATIONS
                            }?.status?.isGranted ?: false,
                            onRequest = { permissionsState.launchMultiplePermissionRequest() }
                        )
                    }
                    PermissionItem(
                        title = "Akses notifikasi",
                        description = "Biar aplikasi bisa membaca notifikasi dari aplikasi lain.",
                        icon = Icons.Outlined.NotificationsActive,
                        tone = Tone.Purple,
                        isGranted = isNotificationListenerEnabled,
                        onRequest = openNotificationListenerSettings
                    )
                    PermissionItem(
                        title = "Akses internet",
                        description = "Dipakai untuk meneruskan pesan ke endpoint HTTP.",
                        icon = Icons.Outlined.Language,
                        tone = Tone.Teal,
                        isGranted = true,
                        onRequest = { }
                    )
                }
            }

            item {
                SectionCard(title = "Tentang aplikasi", icon = Icons.Outlined.Info, tone = Tone.Info) {
                    InfoRow("Versi", "1.1.0")
                    InfoRow("Package", "id.majopay.ngateway")
                    InfoRow("Target SDK", "34 (Android 14)")
                    InfoRow("Min SDK", "29 (Android 10)")
                }
            }

            item {
                SectionCard(title = "Tips singkat", icon = Icons.Outlined.TipsAndUpdates, tone = Tone.Teal) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBanner(
                            icon = Icons.Outlined.Sms,
                            tone = Tone.Info,
                            title = "Coba SMS",
                            message = "Izinkan akses SMS, buat aturan, lalu kirim SMS ke nomormu untuk mengujinya."
                        )
                        InfoBanner(
                            icon = Icons.Outlined.NotificationsActive,
                            tone = Tone.Info,
                            title = "Coba notifikasi",
                            message = "Aktifkan akses notifikasi, buat aturan, lalu picu notifikasi dari aplikasi mana saja."
                        )
                        InfoBanner(
                            icon = Icons.Outlined.Search,
                            tone = Tone.Info,
                            title = "Lagi debug?",
                            message = "Semua pesan muncul di tab Riwayat, termasuk yang nggak cocok aturan. Berguna buat cek kenapa pesan nggak diteruskan."
                        )
                    }
                }
            }

            item {
                var debugInfo by remember { mutableStateOf("") }
                var showDebugDialog by remember { mutableStateOf(false) }

                SectionCard(title = "Alat debug", icon = Icons.Outlined.Build, tone = Tone.Neutral) {
                    Text(
                        text = "Cek aplikasi mana saja yang bisa dilihat oleh Majopay Gateway.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TonalButton(
                        text = "Uji visibilitas aplikasi",
                        icon = Icons.Outlined.Search,
                        onClick = {
                            scope.launch {
                                debugInfo = try {
                                    appRepository.debugAppVisibility()
                                } catch (e: Exception) {
                                    "Debug failed: ${e.message}"
                                }
                                showDebugDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (showDebugDialog) {
                    AlertDialog(
                        onDismissRequest = { showDebugDialog = false },
                        shape = MaterialTheme.shapes.extraLarge,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        tonalElevation = 0.dp,
                        title = { Text("Hasil debug", style = MaterialTheme.typography.titleLarge) },
                        text = {
                            SoftBlock(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = debugInfo,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        confirmButton = {
                            TonalButton(
                                text = "Tutup",
                                onClick = { showDebugDialog = false },
                                compact = true
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionItem(
    title: String,
    description: String,
    icon: ImageVector,
    tone: Tone,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    IconListRow(
        icon = icon,
        title = title,
        subtitle = description,
        tone = tone,
        onClick = if (isGranted) null else onRequest,
        trailing = {
            if (isGranted) {
                StatusChip(text = "Aktif", tone = Tone.Success)
            } else {
                PrimaryButtonSmall(text = "Izinkan", onClick = onRequest)
            }
        }
    )
}

/**
 * Section kredensial API di Settings: tampilkan api_key utuh, secret di-mask dengan
 * toggle lihat, tombol Ubah (dialog form) dan Hapus (dialog konfirmasi).
 *
 * @param onSave Mengembalikan pesan error atau `null` jika berhasil.
 */
@Composable
private fun CredentialsSection(
    credentials: ApiCredentials?,
    onSave: (apiKey: String, apiSecret: String) -> String?,
    onClear: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var revealSecret by remember { mutableStateOf(false) }

    if (credentials == null) {
        InfoBanner(
            icon = Icons.Outlined.Key,
            tone = Tone.Warning,
            title = "Belum diisi",
            message = "Pesan yang cocok belum bisa diteruskan sampai kamu mengisi API Key dan Secret."
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = "Isi kredensial",
            icon = Icons.Outlined.Key,
            onClick = { showEditDialog = true },
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusChip(text = "Tersimpan", tone = Tone.Success)
        }
        Spacer(modifier = Modifier.height(10.dp))
        CredentialRow(label = "API Key", value = credentials.apiKey)
        Spacer(modifier = Modifier.height(8.dp))
        CredentialRow(
            label = "API Secret",
            value = if (revealSecret) credentials.apiSecret else credentials.maskedSecret,
            trailing = {
                IconButton(onClick = { revealSecret = !revealSecret }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (revealSecret) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (revealSecret) "Sembunyikan secret" else "Tampilkan secret",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TonalButton(
                text = "Ubah",
                icon = Icons.Outlined.Edit,
                onClick = { showEditDialog = true },
                modifier = Modifier.weight(1f),
                compact = true
            )
            TonalButton(
                text = "Hapus",
                icon = Icons.Outlined.Delete,
                onClick = { showDeleteDialog = true },
                modifier = Modifier.weight(1f),
                tone = Tone.Error,
                compact = true
            )
        }
    }

    if (showEditDialog) {
        CredentialsEditDialog(
            initial = credentials,
            onDismiss = { showEditDialog = false },
            onSave = { key, secret ->
                val error = onSave(key, secret)
                if (error == null) showEditDialog = false
                error
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 0.dp,
            title = { Text("Hapus kredensial?", style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    text = "API Key dan Secret akan dihapus dari perangkat ini. Pengiriman webhook " +
                        "berhenti sampai kamu mengisinya lagi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                PrimaryButton(
                    text = "Hapus",
                    tone = Tone.Error,
                    onClick = {
                        showDeleteDialog = false
                        onClear()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun CredentialRow(
    label: String,
    value: String,
    trailing: (@Composable () -> Unit)? = null
) {
    SoftBlock(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailing?.let {
                Spacer(modifier = Modifier.width(8.dp))
                it()
            }
        }
    }
}

/**
 * Dialog ubah/atur kredensial. [onSave] mengembalikan pesan error untuk ditampilkan
 * di form, atau `null` jika tersimpan (pemanggil yang menutup dialog).
 */
@Composable
private fun CredentialsEditDialog(
    initial: ApiCredentials?,
    onDismiss: () -> Unit,
    onSave: (apiKey: String, apiSecret: String) -> String?
) {
    val form = rememberCredentialsFormState(
        initialApiKey = initial?.apiKey ?: "",
        initialApiSecret = initial?.apiSecret ?: ""
    )
    val submit = { form.error = onSave(form.apiKey, form.apiSecret) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp,
        title = {
            Text(
                text = if (initial == null) "Isi kredensial API" else "Ubah kredensial API",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = { CredentialsFormFields(state = form, onSubmit = submit) },
        confirmButton = {
            PrimaryButton(text = "Simpan", onClick = submit)
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
