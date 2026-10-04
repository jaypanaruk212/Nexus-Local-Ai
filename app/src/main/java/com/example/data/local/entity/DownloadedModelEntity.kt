package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_models")
data class DownloadedModelEntity(
  @PrimaryKey val id: String,
  val name: String,
  val filePath: String,
  val sizeBytes: Long,
  val quantType: String,
  val author: String,
  val downloadedAt: Long = System.currentTimeMillis()
)
