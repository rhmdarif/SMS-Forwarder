package id.majopay.gateway.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Shape scale matches the Notification Sync System template:
// small (buttons, inputs): 8dp, medium (chips/badges): 12dp,
// large (cards/modals): 16dp, extraLarge (hero/large containers): 24dp.
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
