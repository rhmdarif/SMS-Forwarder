package id.majopay.ngateway.ui.screen.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import id.majopay.ngateway.ui.component.AppTextField
import id.majopay.ngateway.ui.component.InfoBanner
import id.majopay.ngateway.ui.theme.Tone

/**
 * State form kredensial yang di-hoist ke pemanggil supaya bisa dipakai baik di
 * layar setup penuh maupun dialog "Ubah" di Settings.
 */
class CredentialsFormState(
    initialApiKey: String = "",
    initialApiSecret: String = ""
) {
    var apiKey by mutableStateOf(initialApiKey)
    var apiSecret by mutableStateOf(initialApiSecret)
    var error by mutableStateOf<String?>(null)

    companion object {
        val Saver: Saver<CredentialsFormState, Any> = listSaver(
            save = { listOf(it.apiKey, it.apiSecret, it.error ?: "") },
            restore = { saved ->
                CredentialsFormState(saved[0], saved[1]).apply {
                    error = saved[2].ifEmpty { null }
                }
            }
        )
    }
}

@Composable
fun rememberCredentialsFormState(
    initialApiKey: String = "",
    initialApiSecret: String = ""
): CredentialsFormState = rememberSaveable(saver = CredentialsFormState.Saver) {
    CredentialsFormState(initialApiKey, initialApiSecret)
}

/**
 * Dua field input flat: API Key (teks biasa) dan API Secret (password dengan toggle lihat).
 * Label berada di atas input; pesan error ditampilkan sebagai banner di bawah form.
 */
@Composable
fun CredentialsFormFields(
    state: CredentialsFormState,
    enabled: Boolean = true,
    onSubmit: () -> Unit = {}
) {
    var showSecret by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        AppTextField(
            value = state.apiKey,
            onValueChange = {
                state.apiKey = it
                state.error = null
            },
            label = "API Key",
            placeholder = "mis. mp_live_xxxxxxxx",
            helper = "Ada di dashboard Majopay, menu API.",
            enabled = enabled,
            leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null) },
            isError = state.error != null && state.apiKey.isBlank(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        AppTextField(
            value = state.apiSecret,
            onValueChange = {
                state.apiSecret = it
                state.error = null
            },
            label = "API Secret",
            placeholder = "Rahasia, jangan dibagikan ya",
            enabled = enabled,
            leadingIcon = { Icon(Icons.Outlined.VpnKey, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showSecret = !showSecret }) {
                    Icon(
                        imageVector = if (showSecret) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (showSecret) "Sembunyikan secret" else "Tampilkan secret"
                    )
                }
            },
            isError = state.error != null && state.apiSecret.isBlank(),
            visualTransformation = if (showSecret) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSubmit() })
        )
        state.error?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            InfoBanner(
                message = message,
                icon = Icons.Outlined.ErrorOutline,
                tone = Tone.Error
            )
        }
    }
}
