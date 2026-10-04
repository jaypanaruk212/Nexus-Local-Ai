package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.ModelDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatSessionEntity
import com.example.data.local.entity.DownloadedModelEntity

@Database(
  entities = [
    ChatSessionEntity::class,
    ChatMessageEntity::class,
    DownloadedModelEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class NexusDatabase : RoomDatabase() {

  abstract fun chatDao(): ChatDao
  abstract fun modelDao(): ModelDao

  companion object {
    @Volatile
    private var INSTANCE: NexusDatabase? = null

    fun getDatabase(context: Context): NexusDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          NexusDatabase::class.java,
          "nexus_local_ai.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
