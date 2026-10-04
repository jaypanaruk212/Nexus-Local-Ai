package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HuggingFaceModelDto(
  @Json(name = "_id") val id: String? = null,
  @Json(name = "id") val modelId: String,
  @Json(name = "author") val author: String? = null,
  @Json(name = "downloads") val downloads: Long? = 0,
  @Json(name = "likes") val likes: Long? = 0,
  @Json(name = "private") val isPrivate: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class WebSearchResultDto(
  @Json(name = "title") val title: String? = "",
  @Json(name = "link") val link: String? = "",
  @Json(name = "snippet") val snippet: String? = ""
)
