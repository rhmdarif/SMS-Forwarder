package id.majopay.gateway.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import id.majopay.gateway.domain.model.SmsMessage
import id.majopay.gateway.data.service.SmsForwardingService
import kotlinx.datetime.Clock

/**
 * BroadcastReceiver for intercepting incoming SMS messages.
 * This receiver handles SMS_RECEIVED broadcasts and forwards valid messages
 * to the SmsForwardingService for processing.
 */
class SmsReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "SmsReceiver"
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "=== SMS BROADCAST RECEIVED ===")
        Log.i(TAG, "Action: ${intent?.action}")
        Log.i(TAG, "Context: ${context?.javaClass?.simpleName}")
        
        if (context == null || intent == null) {
            Log.e(TAG, "❌ Context or intent is null - SMS processing aborted")
            return
        }
        
        // Only process SMS_RECEIVED intents
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Log.w(TAG, "❌ Ignoring non-SMS intent: ${intent.action}")
            return
        }
        
        Log.i(TAG, "✅ SMS_RECEIVED intent confirmed - processing...")
        
        try {
            // Extract SMS messages from the intent
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            if (smsMessages.isNullOrEmpty()) {
                Log.e(TAG, "❌ No SMS messages found in intent")
                return
            }
            
            Log.i(TAG, "📱 Found ${smsMessages.size} SMS message(s) in intent")
            
            // Process each SMS message
            for ((i, smsMessage) in smsMessages.withIndex()) {
                val sender = smsMessage.originatingAddress ?: "Unknown"
                val body = smsMessage.messageBody ?: ""
                val timestamp = Clock.System.now() // Use current time as fallback
                
                Log.i(TAG, "📨 Processing SMS #${i + 1}:")
                Log.i(TAG, "  From: $sender")
                Log.i(TAG, "  Body length: ${body.length}")
                Log.i(TAG, "  Body preview: \"${body.take(50)}${if (body.length > 50) "..." else ""}\"")
                
                if (body.isNotBlank()) {
                    val domainSms = SmsMessage(
                        body = body,
                        sender = sender,
                        timestamp = timestamp
                    )
                    
                    // Start the foreground service to handle SMS forwarding
                    val serviceIntent = Intent(context, SmsForwardingService::class.java).apply {
                        action = SmsForwardingService.ACTION_PROCESS_SMS
                        putExtra(SmsForwardingService.EXTRA_SMS_BODY, body)
                        putExtra(SmsForwardingService.EXTRA_SMS_SENDER, sender)
                        putExtra(SmsForwardingService.EXTRA_SMS_TIMESTAMP, timestamp.epochSeconds)
                    }
                    
                    Log.i(TAG, "🚀 Starting SmsForwardingService...")
                    context.startForegroundService(serviceIntent)
                    Log.i(TAG, "✅ SmsForwardingService started successfully")
                } else {
                    Log.w(TAG, "⚠️ SMS body is blank, skipping")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error processing SMS: ${e.message}", e)
        }
    }
} 