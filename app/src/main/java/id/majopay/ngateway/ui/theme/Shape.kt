package id.majopay.ngateway.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Skala bentuk "friendly": sudut lebih bulat dari default Material 3.
// extraSmall (chip kecil/kode): 8dp, small (input/tombol kecil): 12dp,
// medium (blok info/tile): 16dp, large (kartu): 20dp, extraLarge (dialog/hero): 28dp.
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
