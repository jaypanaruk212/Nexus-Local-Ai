package com.example.data.repository

import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatSessionEntity
import com.example.data.remote.WebSearchApiService
import com.example.domain.llm.GenerationChunk
import com.example.domain.llm.GenerationConfig
import com.example.domain.llm.GgufInferenceEngine
import kotlinx.coroutines.flow.Flow
import java.io.File

class ChatRepository(
  private val chatDao: ChatDao,
  private val webSearchApi: WebSearchApiService,
  val llmEngine: GgufInferenceEngine = GgufInferenceEngine()
) {

  fun getAllSessions(): Flow<List<ChatSessionEntity>> = chatDao.getAllSessions()

  fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>> =
    chatDao.getMessagesForSession(sessionId)

  suspend fun createNewSession(title: String, modelName: String): String {
    val session = ChatSessionEntity(
      title = title,
      modelUsed = modelName
    )
    chatDao.insertSession(session)
    return session.id
  }

  suspend fun saveMessage(
    sessionId: String,
    sender: String,
    content: String,
    modelName: String,
    tokensPerSec: Float? = null,
    isGroundingUsed: Boolean = false,
    attachmentName: String? = null
  ) {
    val message = ChatMessageEntity(
      sessionId = sessionId,
      sender = sender,
      content = content,
      modelName = modelName,
      tokensPerSec = tokensPerSec,
      isGroundingUsed = isGroundingUsed,
      attachmentName = attachmentName
    )
    chatDao.insertMessage(message)
  }

  suspend fun deleteSession(sessionId: String) {
    chatDao.deleteSession(sessionId)
  }

  /**
   * Executes inference through GGUF Engine
   */
  fun streamInference(
    prompt: String,
    config: GenerationConfig = GenerationConfig()
  ): Flow<GenerationChunk> {
    return llmEngine.generateStream(prompt, config)
  }

  suspend fun loadLocalModel(modelName: String): Boolean {
    return llmEngine.loadModel(File(modelName))
  }
}
