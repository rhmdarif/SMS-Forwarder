package id.majopay.ngateway.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import id.majopay.ngateway.data.worker.SmsMonitoringWorker

/**
 * BroadcastReceiver for device boot completion and self-update.
 * Ensures SMS monitoring service starts automatically when the device boots
 * or after this app is updated.
 */
class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
        private const val WORK_NAME = "sms_monitoring_work"
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Boot completed, action: ${intent?.action}")
        
        if (context == null || intent == null) {
            Log.w(TAG, "Context or intent is null")
            return
        }
        
        // Hanya boot selesai atau aplikasi ini sendiri baru di-update (bukan paket lain).
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            Log.d(TAG, "Ignoring unexpected intent: ${intent.action}")
            return
        }
        
        try {
            // Schedule SMS monitoring work to start after boot
            val workRequest = OneTimeWorkRequestBuilder<SmsMonitoringWorker>()
                .addTag("boot_startup")
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            
            Log.d(TAG, "Scheduled SMS monitoring work after boot completion")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling work after boot: ${e.message}", e)
        }
    }
} 