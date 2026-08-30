package id.majopay.gateway.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SyncPrimary,
    onPrimary = SyncOnPrimary,
    primaryContainer = SyncPrimaryContainer,
    onPrimaryContainer = SyncOnPrimaryContainer,
    secondary = SyncSecondary,
    onSecondary = SyncOnSecondary,
    secondaryContainer = SyncSecondaryContainer,
    onSecondaryContainer = SyncOnSecondaryContainer,
    tertiary = SyncTertiary,
    onTertiary = SyncOnTertiary,
    tertiaryContainer = SyncTertiaryContainer,
    onTertiaryContainer = SyncOnTertiaryContainer,
    error = SyncError,
    onError = SyncOnError,
    errorContainer = SyncErrorContainer,
    onErrorContainer = SyncOnErrorContainer,
    background = SyncBackground,
    onBackground = SyncOnBackground,
    surface = SyncSurface,
    onSurface = SyncOnSurface,
    surfaceVariant = SyncSurfaceVariant,
    onSurfaceVariant = SyncOnSurfaceVariant,
    outline = SyncOutline,
    outlineVariant = SyncOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = SyncDarkPrimary,
    onPrimary = SyncDarkOnPrimary,
    primaryContainer = SyncDarkPrimaryContainer,
    onPrimaryContainer = SyncDarkOnPrimaryContainer,
    secondary = SyncDarkSecondary,
    onSecondary = SyncDarkOnSecondary,
    secondaryContainer = SyncDarkSecondaryContainer,
    onSecondaryContainer = SyncDarkOnSecondaryContainer,
    tertiary = SyncTertiary,
    onTertiary = SyncOnTertiary,
    tertiaryContainer = SyncTertiaryContainer,
    onTertiaryContainer = SyncOnTertiaryContainer,
    error = SyncError,
    onError = SyncOnError,
    errorContainer = SyncErrorContainer,
    onErrorContainer = SyncOnErrorContainer,
    background = SyncDarkBackground,
    onBackground = SyncDarkOnBackground,
    surface = SyncDarkSurface,
    onSurface = SyncDarkOnSurface,
    surfaceVariant = SyncDarkSurfaceVariant,
    onSurfaceVariant = SyncDarkOnSurfaceVariant,
    outline = SyncDarkOutline,
    outlineVariant = SyncDarkOutlineVariant,
    inversePrimary = SyncPrimary
)

@Composable
fun SMSForwarderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            runCatching {
                val window = (view.context as? Activity)?.window ?: return@runCatching
                if (Build.VERSION.SDK_INT < 35) {
                    @Suppress("DEPRECATION")
                    window.statusBarColor = colorScheme.background.toArgb()
                    @Suppress("DEPRECATION")
                    window.navigationBarColor = colorScheme.background.toArgb()
                }
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
