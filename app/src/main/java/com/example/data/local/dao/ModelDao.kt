package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DownloadedModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {

  @Query("SELECT * FROM downloaded_models ORDER BY downloadedAt DESC")
  fun getAllDownloadedModels(): Flow<List<DownloadedModelEntity>>

  @Query("SELECT * FROM downloaded_models WHERE id = :id LIMIT 1")
  suspend fun getModelById(id: String): DownloadedModelEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertModel(model: DownloadedModelEntity)

  @Query("DELETE FROM downloaded_models WHERE id = :id")
  suspend fun deleteModel(id: String)
}
