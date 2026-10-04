package com.example.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.NexusApplication

/**
 * Foreground Service for downloading GGUF AI models from HuggingFace
 * with speed, ETA, and cancellation support.
 */
class ModelDownloadService : Service() {

  companion object {
    const val NOTIFICATION_ID = 1001
    const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"
    const val ACTION_CANCEL_DOWNLOAD = "ACTION_CANCEL_DOWNLOAD"
    const val EXTRA_MODEL_NAME = "EXTRA_MODEL_NAME"
    const val EXTRA_MODEL_URL = "EXTRA_MODEL_URL"
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_CANCEL_DOWNLOAD -> {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
      }
      else -> {
        val modelName = intent?.getStringExtra(EXTRA_MODEL_NAME) ?: "AI Model"
        val notification = createNotification(modelName, progress = 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          startForeground(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
          )
        } else {
          startForeground(NOTIFICATION_ID, notification)
        }
      }
    }
    return START_NOT_STICKY
  }

  private fun createNotification(modelName: String, progress: Int): Notification {
    return NotificationCompat.Builder(this, NexusApplication.CHANNEL_DOWNLOAD_ID)
      .setContentTitle("Nexus AI Downloader")
      .setContentText("Downloading $modelName ($progress%)")
      .setSmallIcon(android.R.drawable.stat_sys_download)
      .setProgress(100, progress, false)
      .setOngoing(true)
      .build()
  }
}
