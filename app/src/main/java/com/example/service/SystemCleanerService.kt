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
 * Foreground Service for scanning and cleaning junk files & memory cache
 * in background with status notifications.
 */
class SystemCleanerService : Service() {

  companion object {
    const val NOTIFICATION_ID = 1002
    const val ACTION_START_CLEAN = "ACTION_START_CLEAN"
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val notification = createNotification("Optimizing RAM and cleaning junk files...")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      startForeground(
        NOTIFICATION_ID,
        notification,
        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
      )
    } else {
      startForeground(NOTIFICATION_ID, notification)
    }
    return START_NOT_STICKY
  }

  private fun createNotification(statusText: String): Notification {
    return NotificationCompat.Builder(this, NexusApplication.CHANNEL_CLEANER_ID)
      .setContentTitle("Nexus System Cleaner")
      .setContentText(statusText)
      .setSmallIcon(android.R.drawable.ic_popup_sync)
      .setOngoing(true)
      .build()
  }
}
