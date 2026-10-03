package id.majopay.ngateway.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Palet "Flat & Friendly" Majopay Gateway.
 *
 * Prinsip flat design yang dipakai:
 * - Tidak ada bayangan/elevasi; hierarki dibangun dari blok warna solid.
 * - Kartu putih polos di atas latar biru-abu sangat muda, tanpa bingkai.
 * - Warna status cerah dan jelas, selalu berpasangan (teks pekat + latar lembut).
 */

// ---------- Brand (biru Majopay) ----------
val BrandBlue = Color(0xFF004AC6)
val BrandBlueBright = Color(0xFF2563EB)   // untuk FAB / aksen yang butuh lebih "hidup"
val BrandBlueSoft = Color(0xFFE3EBFF)     // latar lembut untuk ikon & chip biru
val BrandBlueDeep = Color(0xFF00174B)

// ---------- Light palette ----------
val LightPrimary = BrandBlue
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = BrandBlueSoft
val LightOnPrimaryContainer = BrandBlueDeep
val LightSecondary = Color(0xFF4F5D73)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFDCE6F5)
val LightOnSecondaryContainer = Color(0xFF2B3A4E)
val LightTertiary = Color(0xFF0D9488)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFCCFBF1)
val LightOnTertiaryContainer = Color(0xFF134E4A)
val LightError = Color(0xFFDC2626)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFEE2E2)
val LightOnErrorContainer = Color(0xFF991B1B)
val LightBackground = Color(0xFFF3F5FA)
val LightOnBackground = Color(0xFF14213D)
val LightSurface = Color(0xFFF3F5FA)
val LightOnSurface = Color(0xFF14213D)
val LightSurfaceVariant = Color(0xFFE6EBF5)
val LightOnSurfaceVariant = Color(0xFF5B6478)
val LightOutline = Color(0xFF8A93A6)
val LightOutlineVariant = Color(0xFFD9DFEA)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF8F9FD)
val LightSurfaceContainer = Color(0xFFEDF1F8)
val LightSurfaceContainerHigh = Color(0xFFE4E9F3)
val LightSurfaceContainerHighest = Color(0xFFDCE2EE)
val LightInverseSurface = Color(0xFF243047)
val LightInverseOnSurface = Color(0xFFF1F4FB)
val LightInversePrimary = Color(0xFFB4C5FF)

// ---------- Dark palette ----------
val DarkPrimary = Color(0xFFB4C5FF)
val DarkOnPrimary = BrandBlueDeep
val DarkPrimaryContainer = Color(0xFF1E40AF)
val DarkOnPrimaryContainer = Color(0xFFDBE4FF)
val DarkSecondary = Color(0xFFB9C6DB)
val DarkOnSecondary = Color(0xFF233142)
val DarkSecondaryContainer = Color(0xFF34435A)
val DarkOnSecondaryContainer = Color(0xFFD9E3F5)
val DarkTertiary = Color(0xFF5EEAD4)
val DarkOnTertiary = Color(0xFF003733)
val DarkTertiaryContainer = Color(0xFF115E59)
val DarkOnTertiaryContainer = Color(0xFFCCFBF1)
val DarkError = Color(0xFFFCA5A5)
val DarkOnError = Color(0xFF450A0A)
val DarkErrorContainer = Color(0xFF7F1D1D)
val DarkOnErrorContainer = Color(0xFFFEE2E2)
val DarkBackground = Color(0xFF0E131C)
val DarkOnBackground = Color(0xFFE4E8F1)
val DarkSurface = Color(0xFF0E131C)
val DarkOnSurface = Color(0xFFE4E8F1)
val DarkSurfaceVariant = Color(0xFF2A3446)
val DarkOnSurfaceVariant = Color(0xFFB6BFD0)
val DarkOutline = Color(0xFF7D8699)
val DarkOutlineVariant = Color(0xFF3A4457)
val DarkSurfaceContainerLowest = Color(0xFF171D28)
val DarkSurfaceContainerLow = Color(0xFF141A24)
val DarkSurfaceContainer = Color(0xFF1B2230)
val DarkSurfaceContainerHigh = Color(0xFF232C3C)
val DarkSurfaceContainerHighest = Color(0xFF2C3648)
val DarkInverseSurface = Color(0xFFE4E8F1)
val DarkInverseOnSurface = Color(0xFF1B2230)
val DarkInversePrimary = BrandBlue

// ---------- Warna semantik (status) ----------
// Pasangan "pekat / lembut" untuk mode terang dan gelap. Gunakan lewat [Tone] agar
// otomatis menyesuaikan tema; konstanta di bawah tetap ada untuk kompatibilitas.
val SuccessGreen = Color(0xFF16A34A)
val SuccessGreenLight = Color(0xFFDCFCE7)
val SuccessGreenDark = Color(0xFF4ADE80)
val SuccessGreenDarkBg = Color(0xFF14532D)

val WarningAmber = Color(0xFFD97706)
val WarningAmberLight = Color(0xFFFEF3C7)
val WarningAmberDark = Color(0xFFFBBF24)
val WarningAmberDarkBg = Color(0xFF78350F)

val ErrorRed = Color(0xFFDC2626)
val ErrorRedLight = Color(0xFFFEE2E2)
val ErrorRedDark = Color(0xFFF87171)
val ErrorRedDarkBg = Color(0xFF7F1D1D)

val InfoBlue = Color(0xFF2563EB)
val InfoBlueLight = Color(0xFFDBEAFE)
val InfoBlueDark = Color(0xFF93C5FD)
val InfoBlueDarkBg = Color(0xFF1E3A8A)

val NeutralGray = Color(0xFF64748B)
val NeutralGrayLight = Color(0xFFE2E8F0)
val NeutralGrayDark = Color(0xFFCBD5E1)
val NeutralGrayDarkBg = Color(0xFF334155)

// ---------- Aksen ramah untuk ikon/ilustrasi ----------
// Dipakai untuk membedakan kategori (status, izin, dsb.) agar UI terasa hangat.
val AccentTeal = Color(0xFF0D9488)
val AccentTealLight = Color(0xFFCCFBF1)
val AccentTealDark = Color(0xFF5EEAD4)
val AccentTealDarkBg = Color(0xFF134E4A)

val AccentPurple = Color(0xFF7C3AED)
val AccentPurpleLight = Color(0xFFEDE9FE)
val AccentPurpleDark = Color(0xFFC4B5FD)
val AccentPurpleDarkBg = Color(0xFF4C1D95)

val AccentOrange = Color(0xFFF97316)
val AccentOrangeLight = Color(0xFFFFEDD5)
val AccentOrangeDark = Color(0xFFFDBA74)
val AccentOrangeDarkBg = Color(0xFF7C2D12)

val AccentPink = Color(0xFFDB2777)
val AccentPinkLight = Color(0xFFFCE7F3)
val AccentPinkDark = Color(0xFFF9A8D4)
val AccentPinkDarkBg = Color(0xFF831843)
