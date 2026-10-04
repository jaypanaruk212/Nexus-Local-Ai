package com.example.data.repository

import com.example.data.local.dao.ModelDao
import com.example.data.local.entity.DownloadedModelEntity
import com.example.data.remote.HuggingFaceApiService
import com.example.data.remote.model.HuggingFaceModelDto
import kotlinx.coroutines.flow.Flow

class ModelRepository(
  private val modelDao: ModelDao,
  private val huggingFaceApi: HuggingFaceApiService
) {

  fun getDownloadedModels(): Flow<List<DownloadedModelEntity>> =
    modelDao.getAllDownloadedModels()

  suspend fun registerDownloadedModel(
    id: String,
    name: String,
    filePath: String,
    sizeBytes: Long,
    quantType: String,
    author: String
  ) {
    val model = DownloadedModelEntity(
      id = id,
      name = name,
      filePath = filePath,
      sizeBytes = sizeBytes,
      quantType = quantType,
      author = author
    )
    modelDao.insertModel(model)
  }

  suspend fun deleteModel(id: String) {
    modelDao.deleteModel(id)
  }

  suspend fun searchHuggingFace(query: String): List<HuggingFaceModelDto> {
    return try {
      huggingFaceApi.searchModels(query = query)
    } catch (e: Exception) {
      emptyList()
    }
  }
}
