package com.example.data.remote

import com.example.data.remote.model.WebSearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WebSearchApiService {

  @GET("search")
  suspend fun searchWeb(
    @Query("q") query: String,
    @Query("format") format: String = "json"
  ): List<WebSearchResultDto>
}
