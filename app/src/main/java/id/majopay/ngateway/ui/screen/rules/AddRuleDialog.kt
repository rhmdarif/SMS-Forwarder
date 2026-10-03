package id.majopay.ngateway.ui.screen.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.majopay.ngateway.domain.model.Rule
import id.majopay.ngateway.domain.model.SourceType
import id.majopay.ngateway.ui.component.AppTextField
import id.majopay.ngateway.ui.component.IconBubble
import id.majopay.ngateway.ui.component.InfoBanner
import id.majopay.ngateway.ui.component.PrimaryButton
import id.majopay.ngateway.ui.component.SoftBlock
import id.majopay.ngateway.ui.component.ToggleRow
import id.majopay.ngateway.ui.component.TonalButton
import id.majopay.ngateway.ui.theme.Tone
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleDialog(
    onDismiss: () -> Unit,
    onSave: (Rule) -> Unit,
    ruleToEdit: Rule? = null
) {
    val isEditMode = ruleToEdit != null

    var name by remember { mutableStateOf(ruleToEdit?.name ?: "") }
    var pattern by remember { mutableStateOf(ruleToEdit?.pattern ?: "") }
    var isRegex by remember { mutableStateOf(ruleToEdit?.isRegex ?: false) }
    var packageFilter by remember { mutableStateOf(ruleToEdit?.packageFilter ?: "") }
    var usePackageFilter by remember { mutableStateOf(ruleToEdit?.packageFilter != null) }
    var selectedAppName by remember { mutableStateOf(ruleToEdit?.packageFilter ?: "") }
    var showAppPicker by remember { mutableStateOf(false) }

    val isValid = name.isNotBlank() && pattern.isNotBlank() &&
        (!usePackageFilter || packageFilter.isNotBlank())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEditMode) "Ubah aturan" else "Aturan baru",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tentukan pola notifikasi yang mau kamu tangkap.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Nama aturan
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nama aturan",
                    placeholder = "Contoh: Pesanan masuk",
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pola
                AppTextField(
                    value = pattern,
                    onValueChange = { pattern = it },
                    label = "Pola yang dicari",
                    placeholder = "Contoh: 'pesan baru' atau 'WhatsApp.*' untuk regex",
                    singleLine = false,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                SoftBlock {
                    ToggleRow(
                        title = "Pakai regex",
                        subtitle = "Cocokkan dengan ekspresi reguler, tidak peduli huruf besar/kecil.",
                        icon = Icons.Outlined.Code,
                        tone = Tone.Teal
                    ) {
                        FlatSwitch(checked = isRegex, onChange = { isRegex = it })
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                SoftBlock {
                    ToggleRow(
                        title = "Filter per aplikasi",
                        subtitle = "Hanya tangkap notifikasi dari satu aplikasi tertentu.",
                        icon = Icons.Outlined.FilterAlt,
                        tone = Tone.Purple
                    ) {
                        FlatSwitch(checked = usePackageFilter, onChange = { usePackageFilter = it })
                    }

                    if (usePackageFilter) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AppPickerButton(
                            appName = selectedAppName.ifEmpty { "Pilih aplikasi" },
                            packageName = packageFilter,
                            onClick = { showAppPicker = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tujuan webhook — URL/method/headers dikunci dari build config
                InfoBanner(
                    icon = Icons.Outlined.Lock,
                    tone = Tone.Info,
                    title = "Tujuan webhook sudah diatur",
                    message = "URL, metode HTTP, dan header sudah dikunci dari konfigurasi aplikasi, jadi kamu nggak perlu isi apa-apa."
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol aksi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TonalButton(
                        text = "Batal",
                        onClick = onDismiss,
                        tone = Tone.Neutral,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = if (isEditMode) "Simpan perubahan" else "Simpan",
                        onClick = {
                            val now = Clock.System.now()
                            val rule = Rule(
                                id = ruleToEdit?.id ?: 0L,
                                name = name.trim(),
                                pattern = pattern.trim(),
                                isRegex = isRegex,
                                isActive = ruleToEdit?.isActive ?: true,
                                source = SourceType.NOTIFICATION,
                                packageFilter = if (usePackageFilter) {
                                    packageFilter.trim().takeIf { it.isNotBlank() }
                                } else null,
                                createdAt = ruleToEdit?.createdAt ?: now,
                                updatedAt = now
                            )
                            onSave(rule)
                        },
                        enabled = isValid,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showAppPicker) {
        AppPickerDialog(
            onDismiss = { showAppPicker = false },
            onAppSelected = { selectedApp ->
                packageFilter = selectedApp.packageName
                selectedAppName = selectedApp.appName
                showAppPicker = false
            }
        )
    }
}

@Composable
private fun FlatSwitch(checked: Boolean, onChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun AppPickerButton(
    appName: String,
    packageName: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(
                icon = Icons.Outlined.Apps,
                tone = Tone.Purple,
                size = 36.dp,
                iconSize = 18.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (packageName.isNotEmpty()) {
                    Text(
                        text = packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
