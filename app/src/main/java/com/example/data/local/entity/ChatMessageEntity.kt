package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
  tableName = "chat_messages",
  foreignKeys = [
    ForeignKey(
      entity = ChatSessionEntity::class,
      parentColumns = ["id"],
      childColumns = ["sessionId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("sessionId")]
)
data class ChatMessageEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val sessionId: String,
  val sender: String, // "USER" or "ASSISTANT"
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val tokensPerSec: Float? = null,
  val modelName: String,
  val isGroundingUsed: Boolean = false,
  val attachmentName: String? = null
)
