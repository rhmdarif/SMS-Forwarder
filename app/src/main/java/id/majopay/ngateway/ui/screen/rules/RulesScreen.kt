package id.majopay.ngateway.ui.screen.rules

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.majopay.ngateway.domain.model.Rule
import id.majopay.ngateway.domain.model.SourceType
import id.majopay.ngateway.ui.component.CredentialsBanner
import id.majopay.ngateway.ui.component.FlatCard
import id.majopay.ngateway.ui.component.FriendlyEmptyState
import id.majopay.ngateway.ui.component.IconBubble
import id.majopay.ngateway.ui.component.MajopayTopBar
import id.majopay.ngateway.ui.component.ScreenHeader
import id.majopay.ngateway.ui.component.ScreenHorizontalPadding
import id.majopay.ngateway.ui.component.SectionLabel
import id.majopay.ngateway.ui.component.SoftBlock
import id.majopay.ngateway.ui.component.SolidIconBubble
import id.majopay.ngateway.ui.component.StatTile
import id.majopay.ngateway.ui.component.StatusChip
import id.majopay.ngateway.ui.theme.Tone
import id.majopay.ngateway.ui.theme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    onSetupClick: () -> Unit = {},
    viewModel: RulesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var ruleToEdit by remember { mutableStateOf<Rule?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MajopayTopBar() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Aturan baru", style = MaterialTheme.typography.labelLarge) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = ScreenHorizontalPadding,
                end = ScreenHorizontalPadding,
                top = 8.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { RulesHeader(uiState.rules.count { it.isActive }, uiState.rules.size) }
            item { CredentialsBanner(onSetupClick = onSetupClick) }

            if (uiState.rules.isEmpty()) {
                item {
                    FriendlyEmptyState(
                        icon = Icons.Outlined.Inbox,
                        title = "Belum ada aturan nih",
                        message = "Yuk buat aturan pertama supaya pesan penting langsung diteruskan.",
                        actionLabel = "Buat aturan",
                        onAction = { showAddDialog = true }
                    )
                }
            } else {
                item { SectionLabel("Aturan kamu", modifier = Modifier.padding(top = 4.dp)) }
                items(uiState.rules, key = { it.id }) { rule ->
                    RuleCard(
                        rule = rule,
                        onToggleActive = { viewModel.toggleRuleStatus(rule.id, !rule.isActive) },
                        onEdit = { ruleToEdit = rule },
                        onDelete = { viewModel.deleteRule(rule.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
            item { HowItWorksCard() }
        }
    }

    if (showAddDialog) {
        AddRuleDialog(
            onDismiss = { showAddDialog = false },
            onSave = { rule ->
                viewModel.createRule(rule)
                showAddDialog = false
            }
        )
    }

    ruleToEdit?.let { rule ->
        AddRuleDialog(
            onDismiss = { ruleToEdit = null },
            onSave = { updatedRule ->
                viewModel.updateRule(updatedRule)
                ruleToEdit = null
            },
            ruleToEdit = rule
        )
    }
}

@Composable
private fun RulesHeader(activeCount: Int, totalCount: Int) {
    Column {
        ScreenHeader(
            title = "Aturan otomatis",
            subtitle = "Tentukan pesan dan notifikasi mana yang otomatis kamu teruskan."
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = "Aktif",
                value = activeCount.toString(),
                tone = Tone.Success,
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Total aturan",
                value = totalCount.toString(),
                tone = Tone.Primary,
                icon = Icons.Outlined.Star,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RuleCard(
    rule: Rule,
    onToggleActive: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isSms = rule.source == SourceType.SMS
    val sourceIcon: ImageVector = if (isSms) Icons.Outlined.Sms else Icons.Outlined.NotificationsActive
    val sourceTone = if (isSms) Tone.Primary else Tone.Purple

    FlatCard(onClick = onEdit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(
                icon = sourceIcon,
                tone = sourceTone,
                size = 44.dp,
                iconSize = 22.dp,
                shape = MaterialTheme.shapes.small
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isSms) "Pesan SMS" else "Notifikasi aplikasi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = rule.isActive,
                onCheckedChange = { onToggleActive() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusChip(
                text = if (rule.isRegex) "Regex" else "Substring",
                tone = if (rule.isRegex) Tone.Teal else Tone.Neutral
            )
            if (!isSms && !rule.packageFilter.isNullOrBlank()) {
                StatusChip(
                    text = rule.packageFilter,
                    tone = Tone.Purple,
                    icon = Icons.Outlined.Apps,
                    maxWidth = 180.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SoftBlock {
            Text(
                text = "Pola",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = rule.pattern,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onEdit,
                shape = CircleShape,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ubah", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(
                onClick = onDelete,
                shape = CircleShape,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hapus", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun HowItWorksCard() {
    val tone = Tone.Info
    FlatCard(color = tone.colors().background, contentPadding = PaddingValues(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SolidIconBubble(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                tone = tone,
                size = 40.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Gimana cara kerjanya?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        HowItWorksStep(
            number = 1,
            icon = Icons.Outlined.Sms,
            text = "Setiap SMS atau notifikasi yang masuk dicek satu per satu."
        )
        Spacer(modifier = Modifier.height(10.dp))
        HowItWorksStep(
            number = 2,
            icon = Icons.Outlined.FilterAlt,
            text = "Kalau isinya cocok dengan pola aturan kamu, pesan itu lolos."
        )
        Spacer(modifier = Modifier.height(10.dp))
        HowItWorksStep(
            number = 3,
            icon = Icons.Outlined.Send,
            text = "Pesan langsung diteruskan ke Majopay, dicoba ulang otomatis kalau gagal."
        )
    }
}

@Composable
private fun HowItWorksStep(number: Int, icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SolidIconBubble(
            icon = icon,
            tone = Tone.Info,
            size = 32.dp,
            iconSize = 16.dp,
            shape = MaterialTheme.shapes.extraSmall
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$number. $text",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

data class RulesUiState(
    val rules: List<Rule> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
