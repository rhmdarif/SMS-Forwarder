package id.majopay.ngateway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.majopay.ngateway.ui.navigation.SmsForwarderNavigation
import id.majopay.ngateway.ui.theme.SMSForwarderTheme
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