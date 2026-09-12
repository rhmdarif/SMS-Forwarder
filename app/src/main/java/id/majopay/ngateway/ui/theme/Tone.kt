package id.majopay.ngateway.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** true jika tema gelap aktif; disediakan oleh [SMSForwarderTheme]. */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

/** Pasangan warna flat: [foreground] untuk ikon/teks, [background] untuk blok lembut di belakangnya. */
data class ToneColors(val foreground: Color, val background: Color)

/**
 * Nada warna semantik/aksen yang dipakai komponen bersama (chip, ikon, banner).
 * Selalu ambil warnanya lewat [colors] agar otomatis mengikuti mode terang/gelap.
 */
enum class Tone {
    Primary, Success, Warning, Error, Info, Neutral, Teal, Purple, Orange, Pink
}

@Composable
fun Tone.colors(): ToneColors {
    val dark = LocalIsDarkTheme.current
    return when (this) {
        Tone.Primary -> ToneColors(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
        Tone.Success -> if (dark) ToneColors(SuccessGreenDark, SuccessGreenDarkBg) else ToneColors(SuccessGreen, SuccessGreenLight)
        Tone.Warning -> if (dark) ToneColors(WarningAmberDark, WarningAmberDarkBg) else ToneColors(WarningAmber, WarningAmberLight)
        Tone.Error -> if (dark) ToneColors(ErrorRedDark, ErrorRedDarkBg) else ToneColors(ErrorRed, ErrorRedLight)
        Tone.Info -> if (dark) ToneColors(InfoBlueDark, InfoBlueDarkBg) else ToneColors(InfoBlue, InfoBlueLight)
        Tone.Neutral -> if (dark) ToneColors(NeutralGrayDark, NeutralGrayDarkBg) else ToneColors(NeutralGray, NeutralGrayLight)
        Tone.Teal -> if (dark) ToneColors(AccentTealDark, AccentTealDarkBg) else ToneColors(AccentTeal, AccentTealLight)
        Tone.Purple -> if (dark) ToneColors(AccentPurpleDark, AccentPurpleDarkBg) else ToneColors(AccentPurple, AccentPurpleLight)
        Tone.Orange -> if (dark) ToneColors(AccentOrangeDark, AccentOrangeDarkBg) else ToneColors(AccentOrange, AccentOrangeLight)
        Tone.Pink -> if (dark) ToneColors(AccentPinkDark, AccentPinkDarkBg) else ToneColors(AccentPink, AccentPinkLight)
    }
}
