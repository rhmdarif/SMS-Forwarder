package id.majopay.ngateway.ui.screen.rules

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import id.majopay.ngateway.data.repository.AppInfo
import id.majopay.ngateway.data.repository.AppRepository
import id.majopay.ngateway.ui.component.AppTextField
import id.majopay.ngateway.ui.component.IconBubble
import id.majopay.ngateway.ui.component.SectionLabel
import id.majopay.ngateway.ui.component.StatusChip
import id.majopay.ngateway.ui.theme.Tone
import kotlinx.coroutines.delay

data class SelectedApp(
    val packageName: String,
    val appName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerDialog(
    onDismiss: () -> Unit,
    onAppSelected: (SelectedApp) -> Unit,
    appRepository: AppRepository = hiltViewModel<AppPickerViewModel>().appRepository
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchSuggestions by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            searchSuggestions = emptyList()
            return@LaunchedEffect
        }
        isLoading = true
        delay(300)
        try {
            searchSuggestions = appRepository.getSearchSuggestions(searchQuery, 15)
        } catch (e: Exception) {
            searchSuggestions = emptyList()
        } finally {
            isLoading = false
        }
    }

    val mruApps by appRepository.mruApps.collectAsState()

    LaunchedEffect(Unit) { appRepository.initialize() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pilih aplikasi",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Aturan ini cuma berlaku untuk notifikasi dari aplikasi yang kamu pilih.",
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

                Spacer(modifier = Modifier.height(14.dp))

                AppTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Cari aplikasi…",
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Bersihkan")
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (searchQuery.isEmpty()) {
                    if (mruApps.isNotEmpty()) {
                        SectionLabel("Baru-baru ini dipakai", modifier = Modifier.padding(bottom = 8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(mruApps, key = { it.packageName }) { app ->
                                AppItem(appInfo = app) {
                                    appRepository.addToMru(app)
                                    onAppSelected(SelectedApp(app.packageName, app.appName))
                                }
                            }
                        }
                    } else {
                        EmptyMessage(
                            icon = Icons.Outlined.Apps,
                            title = "Cari aplikasinya dulu, yuk",
                            subtitle = "Ketik nama aplikasi yang terpasang di perangkat ini."
                        )
                    }
                } else {
                    when {
                        isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        searchSuggestions.isEmpty() -> EmptyMessage(
                            icon = Icons.Outlined.SearchOff,
                            title = "Nggak ketemu",
                            subtitle = "Coba kata kunci lain, ya."
                        )
                        else -> {
                            SectionLabel(
                                "Hasil pencarian (${searchSuggestions.size})",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(searchSuggestions, key = { it.packageName }) { app ->
                                    AppItem(appInfo = app) {
                                        appRepository.addToMru(app)
                                        onAppSelected(SelectedApp(app.packageName, app.appName))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMessage(icon: ImageVector, title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconBubble(icon = icon, tone = Tone.Purple, size = 64.dp, iconSize = 30.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AppItem(appInfo: AppInfo, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconBitmap = remember(appInfo.packageName) {
                try {
                    appInfo.icon.toBitmap(48, 48).asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }
            if (iconBitmap != null) {
                Image(
                    painter = BitmapPainter(iconBitmap),
                    contentDescription = "${appInfo.appName} icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = appInfo.appName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (appInfo.isSystemApp) {
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(text = "Sistem", tone = Tone.Neutral)
            }
        }
    }
}

@dagger.hilt.android.lifecycle.HiltViewModel
class AppPickerViewModel @javax.inject.Inject constructor(
    val appRepository: AppRepository
) : androidx.lifecycle.ViewModel()
