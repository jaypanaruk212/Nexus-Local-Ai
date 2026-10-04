package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NexusApplication : Application() {

  companion object {
    const val CHANNEL_DOWNLOAD_ID = "nexus_model_download_channel"
    const val CHANNEL_CLEANER_ID = "nexus_system_cleaner_channel"
    const val CHANNEL_SYSTEM_ID = "nexus_system_alerts_channel"

    lateinit var instance: NexusApplication
      private set
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    createNotificationChannels()
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      val downloadChannel = NotificationChannel(
        CHANNEL_DOWNLOAD_ID,
        "Nexus Model Downloader",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Shows real-time progress for downloading GGUF AI models"
      }

      val cleanerChannel = NotificationChannel(
        CHANNEL_CLEANER_ID,
        "Nexus System Cleaner",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Shows real-time junk scan and cleaning status"
      }

      val systemChannel = NotificationChannel(
        CHANNEL_SYSTEM_ID,
        "Nexus System Alerts",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Notifications for model completion and memory warnings"
      }

      notificationManager.createNotificationChannels(
        listOf(downloadChannel, cleanerChannel, systemChannel)
      )
    }
  }
}
