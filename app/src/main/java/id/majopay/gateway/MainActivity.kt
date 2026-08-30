package id.majopay.gateway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.majopay.gateway.ui.navigation.SmsForwarderNavigation
import id.majopay.gateway.ui.theme.SMSForwarderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMSForwarderTheme {
                SmsForwarderNavigation()
            }
        }
    }
}