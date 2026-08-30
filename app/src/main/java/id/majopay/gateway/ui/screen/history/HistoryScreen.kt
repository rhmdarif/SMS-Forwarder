package id.majopay.gateway.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import id.majopay.gateway.data.local.dao.AppInfo
import id.majopay.gateway.domain.model.ForwardingHistory
import id.majopay.gateway.domain.model.ForwardingStatus
import id.majopay.gateway.ui.theme.ErrorRed
import id.majopay.gateway.ui.theme.SuccessGreen
import id.majopay.gateway.ui.theme.WarningAmber
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

private fun hasActiveFilters(uiState: HistoryUiState): Boolean =
    uiState.searchQuery.isNotEmpty() ||
        uiState.selectedApp.isNotEmpty() ||
        uiState.patternFilter.isNotEmpty()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedHistoryEntry by remember { mutableStateOf<ForwardingHistory?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MajopayTopBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = listState
        ) {
            item { HistoryHeader() }
            item {
                OverviewCard(
                    total = uiState.totalCount,
                    matched = uiState.matchedCount,
                    success = uiState.successCount,
                    failed = uiState.failedCount
                )
            }
            item {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    onClear = { viewModel.updateSearchQuery("") }
                )
            }
            item {
                ActionRow(
                    hasFilters = hasActiveFilters(uiState),
                    canClear = uiState.history.isNotEmpty(),
                    onFilter = { showFilterDialog = true },
                    onRefresh = { viewModel.refreshHistory() },
                    onClearAll = { showClearAllDialog = true }
                )
            }

            if (hasActiveFilters(uiState)) {
                item {
                    ActiveFiltersRow(
                        uiState = uiState,
                        onClearFilters = viewModel::clearFilters,
                        onRemoveAppFilter = { viewModel.updateAppFilter("") },
                        onRemovePatternFilter = { viewModel.updatePatternFilter("") }
                    )
                }
            }

            item { SectionTitle("Riwayat Pesan") }

            if (uiState.isLoading && uiState.history.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }

            if (!uiState.isLoading && uiState.history.isEmpty()) {
                item { EmptyStateCard() }
            }

            items(items = uiState.history, key = { it.id }) { entry ->
                SwipeToDeleteHistoryItem(
                    entry = entry,
                    onDelete = { viewModel.deleteHistoryEntry(entry.id) },
                    onClick = { selectedHistoryEntry = entry }
                )
            }

            if (uiState.hasMorePages && uiState.history.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoadingMore) {
                            CircularProgressIndicator()
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.loadNextPage() },
                                shape = MaterialTheme.shapes.small
                            ) { Text("Muat lebih banyak") }
                        }
                    }
                }
            }
        }
    }

    selectedHistoryEntry?.let { selected ->
        val liveEntry = uiState.history.firstOrNull { it.id == selected.id } ?: selected
        HistoryDetailDialog(
            historyEntry = liveEntry,
            isResending = uiState.resendingIds.contains(liveEntry.id),
            onResend = { viewModel.resendHistoryEntry(liveEntry) },
            onDismiss = { selectedHistoryEntry = null }
        )
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Hapus semua riwayat") },
            text = { Text("Semua entri riwayat akan dihapus. Aksi ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearAllDialog = false
                    }
                ) { Text("Hapus semua") }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showFilterDialog) {
        FilterDialog(
            uiState = uiState,
            onDismiss = { showFilterDialog = false },
            onApplyAppFilter = viewModel::updateAppFilter,
            onApplyPatternFilter = viewModel::updatePatternFilter,
            onClearFilters = viewModel::clearFilters
        )
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) { viewModel.clearError() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MajopayTopBar(
    title: String = "Majopay Gateway",
    onNavigate: (() -> Unit)? = null,
    navigationIcon: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CloudDone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        navigationIcon = navigationIcon,
        actions = {
            if (onNavigate == null) {
                Icon(
                    imageVector = Icons.Outlined.CloudDone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun HistoryHeader() {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(
            text = "Riwayat Pesan",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Pantau seluruh pesan dan notifikasi yang diteruskan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OverviewCard(total: Int, matched: Int, success: Int, failed: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "RINGKASAN HARI INI",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$total pesan diproses",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatPill("Cocok", matched, Modifier.weight(1f))
                StatPill("Sukses", success, Modifier.weight(1f), accent = SuccessGreen)
                StatPill("Gagal", failed, Modifier.weight(1f), accent = ErrorRed)
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    accent: Color = Color.Unspecified
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (accent != Color.Unspecified) accent else MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Cari pesan, pengirim, aplikasi…") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Bersihkan pencarian")
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun ActionRow(
    hasFilters: Boolean,
    canClear: Boolean,
    onFilter: () -> Unit,
    onRefresh: () -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChipAction(
            icon = Icons.Outlined.FilterList,
            label = if (hasFilters) "Filter aktif" else "Filter",
            highlighted = hasFilters,
            onClick = onFilter,
            modifier = Modifier.weight(1f)
        )
        ChipAction(
            icon = Icons.Outlined.Refresh,
            label = "Muat ulang",
            onClick = onRefresh,
            modifier = Modifier.weight(1f)
        )
        if (canClear) {
            ChipAction(
                icon = Icons.Outlined.Delete,
                label = "Hapus",
                onClick = onClearAll,
                modifier = Modifier.weight(1f),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ChipAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    val background = if (highlighted)
        MaterialTheme.colorScheme.secondaryContainer
    else
        MaterialTheme.colorScheme.surfaceContainerLowest
    Surface(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = background,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = tint)
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = tint)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada riwayat",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pesan SMS dan notifikasi yang diproses akan tampil di sini.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SwipeToDeleteHistoryItem(
    entry: ForwardingHistory,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var itemWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val deleteThreshold = with(density) { 120.dp.toPx() }

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Geser untuk menghapus",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { itemWidth = it.width }
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < -deleteThreshold) onDelete()
                            offsetX = 0f
                        }
                    ) { _, dragAmount ->
                        val newOffset = offsetX + dragAmount
                        offsetX = newOffset.coerceAtMost(0f).coerceAtLeast(-itemWidth * 0.4f)
                    }
                }
        ) {
            HistoryCard(entry = entry, onClick = onClick)
        }
    }
}

@Composable
private fun HistoryCard(entry: ForwardingHistory, onClick: () -> Unit) {
    val statusInfo = entry.statusInfo()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = entry.iconForSource(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.getSourceDisplayName(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatRelativeTime(entry.timestamp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = entry.getContentPreview(80),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusChip(statusInfo)
            }
        }
    }
}

private data class StatusInfo(val label: String, val accent: Color)

private fun ForwardingHistory.statusInfo(): StatusInfo = when (status) {
    ForwardingStatus.SUCCESS -> StatusInfo("BERHASIL DITERUSKAN", SuccessGreen)
    ForwardingStatus.FAILED -> StatusInfo("GAGAL", ErrorRed)
    ForwardingStatus.RETRY -> StatusInfo("DICOBA ULANG", WarningAmber)
    ForwardingStatus.RECEIVED -> StatusInfo("DITERIMA", WarningAmber)
    ForwardingStatus.NO_RULE_MATCHED -> StatusInfo("TANPA ATURAN", WarningAmber)
}

private fun ForwardingHistory.iconForSource(): ImageVector =
    if (isSms()) Icons.Outlined.Sms else Icons.AutoMirrored.Outlined.Chat

@Composable
private fun StatusChip(info: StatusInfo) {
    Surface(
        shape = RoundedCornerShape(50),
        color = info.accent.copy(alpha = 0.12f)
    ) {
        Text(
            text = info.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = info.accent,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActiveFiltersRow(
    uiState: HistoryUiState,
    onClearFilters: () -> Unit,
    onRemoveAppFilter: () -> Unit,
    onRemovePatternFilter: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (uiState.selectedApp.isNotEmpty()) {
            FilterChip(
                selected = true,
                onClick = onRemoveAppFilter,
                label = { Text("App: ${uiState.selectedApp}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
        if (uiState.patternFilter.isNotEmpty()) {
            FilterChip(
                selected = true,
                onClick = onRemovePatternFilter,
                label = { Text("Pola: ${uiState.patternFilter}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onClearFilters) { Text("Bersihkan") }
    }
}

@Composable
private fun HistoryDetailDialog(
    historyEntry: ForwardingHistory,
    isResending: Boolean = false,
    onResend: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var showResendConfirm by remember { mutableStateOf(false) }
    val canResend = historyEntry.matchedRule &&
        !historyEntry.endpoint.isNullOrBlank() &&
        !historyEntry.method.isNullOrBlank() &&
        !historyEntry.requestBody.isNullOrBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Detail Pesan",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Analisis pengiriman #${historyEntry.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                LifecycleCard(historyEntry)
                Spacer(modifier = Modifier.height(12.dp))
                OriginatorCard(historyEntry)
                Spacer(modifier = Modifier.height(12.dp))
                NotificationContentCard(historyEntry)

                if (historyEntry.matchedRule) {
                    Spacer(modifier = Modifier.height(12.dp))
                    DestinationCard(historyEntry)

                    if (!historyEntry.requestBody.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CodeSection(title = "Payload Terkirim", value = historyEntry.requestBody)
                    }

                    if (historyEntry.responseCode != null || !historyEntry.responseBody.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ResponseCard(historyEntry)
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(
                        title = "Tidak ada aturan cocok",
                        message = historyEntry.errorMessage ?: "Konten tidak cocok dengan aturan manapun.",
                        accent = WarningAmber
                    )
                }

                if (!historyEntry.errorMessage.isNullOrBlank() && historyEntry.matchedRule) {
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(
                        title = "Detail kesalahan",
                        message = historyEntry.errorMessage,
                        accent = ErrorRed
                    )
                }

                if (canResend) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { showResendConfirm = true },
                        enabled = !isResending,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isResending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mengirim ulang…")
                        } else {
                            Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Resend ke Webhook", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(50)
                    ) { Text("Tutup") }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(50)
                    ) { Text("Tutup") }
                }
            }
        }
    }

    if (showResendConfirm) {
        AlertDialog(
            onDismissRequest = { showResendConfirm = false },
            title = { Text("Resend ke Webhook?") },
            text = {
                Text(
                    "Payload yang sama akan dikirim ulang ke ${historyEntry.endpoint}. " +
                        "Status, response, dan waktu pengiriman pada entry ini akan diperbarui."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showResendConfirm = false
                    onResend()
                }) { Text("Resend") }
            },
            dismissButton = {
                TextButton(onClick = { showResendConfirm = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun LifecycleCard(entry: ForwardingHistory) {
    val received = LifecycleStep(
        title = "Diterima",
        subtitle = formatTimestamp(entry.timestamp),
        icon = Icons.Outlined.NotificationsActive,
        completed = true
    )
    val matched = LifecycleStep(
        title = if (entry.matchedRule) "Aturan cocok" else "Tidak ada aturan cocok",
        subtitle = if (entry.matchedRule) "Rule ID #${entry.ruleId ?: "-"}" else "—",
        icon = Icons.Outlined.CheckCircle,
        completed = entry.matchedRule
    )
    val sent = LifecycleStep(
        title = when (entry.status) {
            ForwardingStatus.SUCCESS -> "Berhasil dikirim"
            ForwardingStatus.FAILED -> "Pengiriman gagal"
            ForwardingStatus.RETRY -> "Sedang mencoba"
            else -> "Belum dikirim"
        },
        subtitle = entry.endpoint ?: "—",
        icon = when (entry.status) {
            ForwardingStatus.SUCCESS -> Icons.AutoMirrored.Outlined.Send
            ForwardingStatus.FAILED -> Icons.Outlined.ErrorOutline
            else -> Icons.AutoMirrored.Outlined.Send
        },
        completed = entry.status == ForwardingStatus.SUCCESS,
        accent = when (entry.status) {
            ForwardingStatus.SUCCESS -> SuccessGreen
            ForwardingStatus.FAILED -> ErrorRed
            else -> WarningAmber
        }
    )

    SectionCard(title = "Siklus Pengiriman") {
        LifecycleRow(received)
        LifecycleConnector()
        LifecycleRow(matched)
        LifecycleConnector()
        LifecycleRow(sent)
    }
}

private data class LifecycleStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val completed: Boolean,
    val accent: Color = Color.Unspecified
)

@Composable
private fun LifecycleRow(step: LifecycleStep) {
    val accent = if (step.accent != Color.Unspecified) step.accent
    else if (step.completed) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(step.icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(step.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = step.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LifecycleConnector() {
    Box(
        modifier = Modifier
            .padding(start = 17.dp)
            .width(2.dp)
            .height(16.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
private fun OriginatorCard(entry: ForwardingHistory) {
    SectionCard(title = "Sumber") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(entry.iconForSource(), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.getSourceDisplayName(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (entry.isSms()) "Pesan SMS" else (entry.sourcePackage ?: "Notifikasi"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NotificationContentCard(entry: ForwardingHistory) {
    SectionCard(
        title = "Konten Pesan",
        trailing = {
            StatusChip(entry.statusInfo())
        }
    ) {
        if (!entry.isSms() && !entry.notificationTitle.isNullOrBlank()) {
            Text(
                text = entry.notificationTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = entry.notificationText ?: entry.messageBody,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DestinationCard(entry: ForwardingHistory) {
    SectionCard(title = "Tujuan") {
        DetailRow("Endpoint", entry.endpoint ?: "—")
        DetailRow("Metode", entry.method ?: "—")
        entry.ruleId?.let { DetailRow("Rule ID", it.toString()) }
    }
}

@Composable
private fun ResponseCard(entry: ForwardingHistory) {
    SectionCard(title = "Response") {
        entry.responseCode?.let {
            DetailRow("Kode", it.toString())
            DetailRow("Status", entry.getHttpStatusDescription() ?: "—")
        }
        entry.responseBody?.let {
            Spacer(modifier = Modifier.height(8.dp))
            CodeBlock(it)
        }
    }
}

@Composable
private fun CodeSection(title: String, value: String) {
    SectionCard(title = title) {
        CodeBlock(value)
    }
}

@Composable
private fun CodeBlock(value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Text(
            text = value,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoCard(title: String, message: String, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = accent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
internal fun SectionCard(
    title: String,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                trailing?.invoke()
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun formatRelativeTime(timestamp: kotlinx.datetime.Instant): String {
    val now = kotlinx.datetime.Clock.System.now()
    val diff = (now - timestamp).inWholeSeconds
    return when {
        diff < 60 -> "Baru saja"
        diff < 3600 -> "${diff / 60} mnt"
        diff < 86400 -> "${diff / 3600} jam"
        else -> formatTimestamp(timestamp)
    }
}

private fun formatTimestamp(timestamp: kotlinx.datetime.Instant): String {
    val ldt = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val pad: (Int) -> String = { it.toString().padStart(2, '0') }
    return "${ldt.date} ${pad(ldt.hour)}:${pad(ldt.minute)}"
}

@Composable
private fun FilterDialog(
    uiState: HistoryUiState,
    onDismiss: () -> Unit,
    onApplyAppFilter: (String) -> Unit,
    onApplyPatternFilter: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    var selectedApp by remember { mutableStateOf(uiState.selectedApp) }
    var patternText by remember { mutableStateOf(uiState.patternFilter) }
    var expandedApp by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Riwayat",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Tutup") }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Aplikasi", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))

                Box {
                    OutlinedTextField(
                        value = selectedApp.ifEmpty { "Semua aplikasi" },
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedApp = !expandedApp }) {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    )
                    DropdownMenu(expanded = expandedApp, onDismissRequest = { expandedApp = false }) {
                        DropdownMenuItem(
                            text = { Text("Semua aplikasi") },
                            onClick = {
                                selectedApp = ""
                                expandedApp = false
                            }
                        )
                        uiState.availableApps.forEach { app ->
                            DropdownMenuItem(
                                text = { Text(app.app_name) },
                                onClick = {
                                    selectedApp = app.app_name
                                    expandedApp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Pola konten", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = patternText,
                    onValueChange = { patternText = it },
                    placeholder = { Text("Cari pola di isi pesan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onClearFilters()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) { Text("Bersihkan") }
                    Button(
                        onClick = {
                            onApplyAppFilter(selectedApp)
                            onApplyPatternFilter(patternText)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) { Text("Terapkan") }
                }
            }
        }
    }
}

data class HistoryUiState(
    val history: List<ForwardingHistory> = emptyList(),
    val totalCount: Int = 0,
    val matchedCount: Int = 0,
    val successCount: Int = 0,
    val failedCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedApp: String = "",
    val patternFilter: String = "",
    val availableApps: List<AppInfo> = emptyList(),
    val showFilterDialog: Boolean = false,
    val resendingIds: Set<Long> = emptySet()
)
