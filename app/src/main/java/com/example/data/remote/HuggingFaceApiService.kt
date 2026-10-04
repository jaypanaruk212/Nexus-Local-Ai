package com.example.data.remote

import com.example.data.remote.model.HuggingFaceModelDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HuggingFaceApiService {

  @GET("api/models")
  suspend fun searchModels(
    @Query("search") query: String,
    @Query("filter") filter: String = "gguf",
    @Query("sort") sort: String = "downloads",
    @Query("direction") direction: Int = -1,
    @Query("limit") limit: Int = 20
  ): List<HuggingFaceModelDto>
}
