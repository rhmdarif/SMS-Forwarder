package id.majopay.ngateway.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import id.majopay.ngateway.data.local.dao.AppInfo
import id.majopay.ngateway.domain.model.ForwardingHistory
import id.majopay.ngateway.domain.model.ForwardingStatus
import id.majopay.ngateway.ui.component.AppTextField
import id.majopay.ngateway.ui.component.CredentialsBanner
import id.majopay.ngateway.ui.component.FlatCard
import id.majopay.ngateway.ui.component.FriendlyEmptyState
import id.majopay.ngateway.ui.component.IconBubble
import id.majopay.ngateway.ui.component.InfoBanner
import id.majopay.ngateway.ui.component.MajopayTopBar
import id.majopay.ngateway.ui.component.PrimaryButton
import id.majopay.ngateway.ui.component.ScreenHeader
import id.majopay.ngateway.ui.component.ScreenHorizontalPadding
import id.majopay.ngateway.ui.component.SectionLabel
import id.majopay.ngateway.ui.component.SelectablePill
import id.majopay.ngateway.ui.component.SoftBlock
import id.majopay.ngateway.ui.component.StatTile
import id.majopay.ngateway.ui.component.StatusChip
import id.majopay.ngateway.ui.component.TonalButton
import id.majopay.ngateway.ui.theme.Tone
import id.majopay.ngateway.ui.theme.colors
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
    onSetupClick: () -> Unit = {},
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
            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = listState
        ) {
            item {
                ScreenHeader(
                    title = "Riwayat pesan",
                    subtitle = "Semua pesan yang sudah diproses ada di sini. Ketuk untuk lihat detailnya."
                )
            }
            item { CredentialsBanner(onSetupClick = onSetupClick) }
            item {
                OverviewStats(
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

            item { SectionLabel("Pesan terbaru", modifier = Modifier.padding(top = 8.dp)) }

            if (uiState.isLoading && uiState.history.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                }
            }

            if (!uiState.isLoading && uiState.history.isEmpty()) {
                item {
                    if (hasActiveFilters(uiState)) {
                        FriendlyEmptyState(
                            icon = Icons.Outlined.SearchOff,
                            title = "Nggak ketemu",
                            message = "Coba ubah kata kunci atau filter kamu.",
                            tone = Tone.Neutral,
                            actionLabel = "Reset filter",
                            onAction = viewModel::clearFilters
                        )
                    } else {
                        FriendlyEmptyState(
                            icon = Icons.Outlined.Inbox,
                            title = "Belum ada riwayat",
                            message = "Pesan yang cocok aturan akan muncul di sini setelah diproses.",
                            tone = Tone.Teal
                        )
                    }
                }
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
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        } else {
                            TonalButton(
                                text = "Muat lebih banyak",
                                onClick = { viewModel.loadNextPage() },
                                compact = true
                            )
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
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 0.dp,
            icon = { IconBubble(icon = Icons.Outlined.Delete, tone = Tone.Error) },
            title = { Text("Hapus semua riwayat?") },
            text = { Text("Semua riwayat pesan bakal dihapus dan nggak bisa dikembalikan lagi.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearAllDialog = false
                    }
                ) { Text("Hapus semua", color = MaterialTheme.colorScheme.error) }
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

// ---------------------------------------------------------------------------
// Bagian atas: statistik, pencarian, aksi
// ---------------------------------------------------------------------------

@Composable
private fun OverviewStats(total: Int, matched: Int, success: Int, failed: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatTile("Diproses", total.toString(), Modifier.weight(1f), tone = Tone.Primary, icon = Icons.Outlined.Inbox)
            StatTile("Cocok aturan", matched.toString(), Modifier.weight(1f), tone = Tone.Info, icon = Icons.Outlined.CheckCircle)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatTile("Sukses", success.toString(), Modifier.weight(1f), tone = Tone.Success, icon = Icons.AutoMirrored.Outlined.Send)
            StatTile("Gagal", failed.toString(), Modifier.weight(1f), tone = Tone.Error, icon = Icons.Outlined.ErrorOutline)
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    AppTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = "Cari pesan, pengirim, atau aplikasi…",
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Bersihkan pencarian")
                }
            }
        } else null
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
        SelectablePill(
            text = if (hasFilters) "Filter aktif" else "Filter",
            selected = hasFilters,
            onClick = onFilter,
            icon = Icons.Outlined.FilterList,
            modifier = Modifier.weight(1f)
        )
        SelectablePill(
            text = "Muat ulang",
            selected = false,
            onClick = onRefresh,
            icon = Icons.Outlined.Refresh,
            modifier = Modifier.weight(1f)
        )
        if (canClear) {
            TonalButton(
                text = "Hapus",
                onClick = onClearAll,
                icon = Icons.Outlined.Delete,
                tone = Tone.Error,
                compact = true,
                modifier = Modifier.weight(1f)
            )
        }
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
            RemovableChip(text = "App: ${uiState.selectedApp}", onRemove = onRemoveAppFilter)
        }
        if (uiState.patternFilter.isNotEmpty()) {
            RemovableChip(text = "Pola: ${uiState.patternFilter}", onRemove = onRemovePatternFilter)
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onClearFilters) { Text("Bersihkan") }
    }
}

/** Chip filter aktif: pill biru lembut dengan ikon Close, ketuk untuk menghapus. */
@Composable
private fun RemovableChip(text: String, onRemove: () -> Unit) {
    Box(modifier = Modifier.clip(CircleShape).clickable(onClick = onRemove)) {
        StatusChip(text = text, tone = Tone.Primary, icon = Icons.Outlined.Cancel, maxWidth = 180.dp)
    }
}

// ---------------------------------------------------------------------------
// Daftar riwayat
// ---------------------------------------------------------------------------

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
    val errorColors = Tone.Error.colors()

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = MaterialTheme.shapes.large,
            color = errorColors.background,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
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
                    tint = errorColors.foreground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Geser untuk hapus",
                    style = MaterialTheme.typography.labelMedium,
                    color = errorColors.foreground
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
    val status = entry.statusInfo()
    FlatCard(onClick = onClick, contentPadding = PaddingValues(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            IconBubble(icon = entry.iconForSource(), tone = entry.sourceTone())
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
                    text = entry.getContentPreview(120),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusChip(text = status.label, tone = status.tone, icon = status.icon)
            }
        }
    }
}

/** Pemetaan status pengiriman ke nada warna, label ramah, dan ikon. */
private data class StatusInfo(val label: String, val tone: Tone, val icon: ImageVector)

private fun ForwardingHistory.statusInfo(): StatusInfo = when (status) {
    ForwardingStatus.SUCCESS -> StatusInfo("Berhasil diteruskan", Tone.Success, Icons.Outlined.CheckCircle)
    ForwardingStatus.FAILED -> StatusInfo("Gagal", Tone.Error, Icons.Outlined.ErrorOutline)
    ForwardingStatus.RETRY -> StatusInfo("Dicoba ulang", Tone.Warning, Icons.Outlined.Refresh)
    ForwardingStatus.RECEIVED -> StatusInfo("Diterima", Tone.Warning, Icons.Outlined.Schedule)
    ForwardingStatus.NO_RULE_MATCHED -> StatusInfo("Tanpa aturan", Tone.Neutral, Icons.Outlined.Cancel)
}

private fun ForwardingHistory.iconForSource(): ImageVector =
    if (isSms()) Icons.Outlined.Sms else Icons.AutoMirrored.Outlined.Chat

private fun ForwardingHistory.sourceTone(): Tone =
    if (isSms()) Tone.Primary else Tone.Purple

// ---------------------------------------------------------------------------
// Dialog detail
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryDetailDialog(
    historyEntry: ForwardingHistory,
    isResending: Boolean = false,
    onResend: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var showResendConfirm by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val status = historyEntry.statusInfo()
    val canResend = historyEntry.matchedRule &&
        !historyEntry.endpoint.isNullOrBlank() &&
        !historyEntry.method.isNullOrBlank() &&
        !historyEntry.requestBody.isNullOrBlank()

    // Bottom sheet satu permukaan: tanpa kartu bertumpuk, bagian dipisah label + garis tipis.
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenHorizontalPadding)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            // Header: sumber + status ringkas (pengganti timeline)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBubble(
                    icon = historyEntry.iconForSource(),
                    tone = historyEntry.sourceTone(),
                    size = 48.dp,
                    iconSize = 24.dp,
                    shape = MaterialTheme.shapes.small
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = historyEntry.getSourceDisplayName(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (historyEntry.isSms()) "Pesan SMS" else (historyEntry.sourcePackage ?: "Notifikasi"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(text = status.label, tone = status.tone, icon = status.icon)
                StatusChip(
                    text = formatTimestamp(historyEntry.timestamp),
                    tone = Tone.Neutral,
                    icon = Icons.Outlined.Schedule
                )
                if (historyEntry.matchedRule && historyEntry.ruleId != null) {
                    StatusChip(text = "Aturan #${historyEntry.ruleId}", tone = Tone.Info)
                }
            }
            Spacer(modifier = Modifier.height(18.dp))

            DetailSection(title = "Isi pesan") {
                ExpandableText(
                    title = if (!historyEntry.isSms()) historyEntry.notificationTitle?.takeIf { it.isNotBlank() } else null,
                    text = historyEntry.notificationText ?: historyEntry.messageBody
                )
            }

            if (historyEntry.matchedRule) {
                DetailSection(title = "Tujuan") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailLine("Endpoint", historyEntry.endpoint ?: "-")
                        DetailLine("Metode", historyEntry.method ?: "-")
                    }
                }
                if (!historyEntry.requestBody.isNullOrBlank()) {
                    DetailSection(title = "Payload terkirim") {
                        CodeBlock(historyEntry.requestBody)
                    }
                }
                if (historyEntry.responseCode != null || !historyEntry.responseBody.isNullOrBlank()) {
                    DetailSection(title = "Respons server") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            historyEntry.responseCode?.let { code ->
                                val desc = historyEntry.getHttpStatusDescription()
                                DetailLine("Kode", if (desc != null) "$code · $desc" else code.toString())
                            }
                            historyEntry.responseBody?.takeIf { it.isNotBlank() }?.let { CodeBlock(it) }
                        }
                    }
                }
                if (!historyEntry.errorMessage.isNullOrBlank()) {
                    InfoBanner(
                        icon = Icons.Outlined.ErrorOutline,
                        tone = Tone.Error,
                        title = "Ada kesalahan",
                        message = historyEntry.errorMessage
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                InfoBanner(
                    icon = Icons.Outlined.Cancel,
                    tone = Tone.Warning,
                    title = "Nggak ada aturan yang cocok",
                    message = historyEntry.errorMessage ?: "Isi pesan ini nggak cocok dengan aturan mana pun, jadi nggak diteruskan."
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (canResend) {
                Spacer(modifier = Modifier.height(4.dp))
                if (isResending) {
                    TonalButton(
                        text = "Mengirim ulang…",
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    PrimaryButton(
                        text = "Kirim ulang ke webhook",
                        onClick = { showResendConfirm = true },
                        icon = Icons.AutoMirrored.Outlined.Send,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showResendConfirm) {
        AlertDialog(
            onDismissRequest = { showResendConfirm = false },
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 0.dp,
            icon = { IconBubble(icon = Icons.AutoMirrored.Outlined.Send, tone = Tone.Primary) },
            title = { Text("Kirim ulang ke webhook?") },
            text = {
                Text(
                    "Payload yang sama bakal dikirim lagi ke ${historyEntry.endpoint}. " +
                        "Status, response, dan waktu pengiriman di entri ini akan diperbarui."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showResendConfirm = false
                    onResend()
                }) { Text("Kirim ulang") }
            },
            dismissButton = {
                TextButton(onClick = { showResendConfirm = false }) { Text("Batal") }
            }
        )
    }
}

/** Bagian di dalam sheet detail: garis tipis + label kecil + konten. Tanpa kartu. */
@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
    Spacer(modifier = Modifier.height(14.dp))
    SectionLabel(title)
    Spacer(modifier = Modifier.height(8.dp))
    Column(content = content)
    Spacer(modifier = Modifier.height(16.dp))
}

/** Baris label-nilai ringkas: label di kiri dengan lebar tetap, nilai di kanan boleh membungkus. */
@Composable
private fun DetailLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(88.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Teks panjang dalam blok lembut, dipotong ke [collapsedLines] baris dengan tombol
 * "Lihat semua" / "Sembunyikan" hanya jika memang terpotong.
 */
@Composable
private fun ExpandableText(
    text: String,
    title: String? = null,
    collapsedLines: Int = 5,
    monospace: Boolean = false
) {
    var expanded by remember(text) { mutableStateOf(false) }
    var overflows by remember(text) { mutableStateOf(false) }
    SoftBlock(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.medium) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = text,
            style = if (monospace) MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
            else MaterialTheme.typography.bodyMedium,
            color = if (monospace) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) overflows = it.hasVisualOverflow }
        )
        if (overflows || expanded) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (expanded) "Sembunyikan" else "Lihat semua",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun CodeBlock(value: String) {
    ExpandableText(text = value, collapsedLines = 8, monospace = true)
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

// ---------------------------------------------------------------------------
// Dialog filter
// ---------------------------------------------------------------------------

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
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBubble(icon = Icons.Outlined.FilterList, tone = Tone.Primary, size = 40.dp, iconSize = 20.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Filter riwayat",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Persempit daftar sesuai kebutuhan kamu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Tutup") }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box {
                    AppTextField(
                        value = selectedApp.ifEmpty { "Semua aplikasi" },
                        onValueChange = { },
                        readOnly = true,
                        label = "Aplikasi",
                        leadingIcon = { Icon(Icons.Outlined.Apps, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { expandedApp = !expandedApp }) {
                                Icon(Icons.Default.Search, contentDescription = "Pilih aplikasi")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedApp,
                        onDismissRequest = { expandedApp = false },
                        shape = MaterialTheme.shapes.medium,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        tonalElevation = 0.dp
                    ) {
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

                Spacer(modifier = Modifier.height(14.dp))
                AppTextField(
                    value = patternText,
                    onValueChange = { patternText = it },
                    label = "Pola isi pesan",
                    placeholder = "Misal: transfer masuk",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TonalButton(
                        text = "Bersihkan",
                        onClick = {
                            onClearFilters()
                            onDismiss()
                        },
                        tone = Tone.Neutral,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "Terapkan",
                        onClick = {
                            onApplyAppFilter(selectedApp)
                            onApplyPatternFilter(patternText)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
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
