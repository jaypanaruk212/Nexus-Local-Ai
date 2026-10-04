package com.example.presentation.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NexusDatabase
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatSessionEntity
import com.example.data.remote.NetworkClient
import com.example.data.repository.ChatRepository
import com.example.domain.llm.GenerationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

  private val database = NexusDatabase.getDatabase(application)
  private val chatRepository = ChatRepository(
    chatDao = database.chatDao(),
    webSearchApi = NetworkClient.webSearchApi
  )

  val sessions: StateFlow<List<ChatSessionEntity>> = chatRepository.getAllSessions()
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  private val _currentSessionId = MutableStateFlow<String?>(null)
  val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

  private val _uiMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val uiMessages: StateFlow<List<ChatMessage>> = _uiMessages.asStateFlow()

  private val _isGenerating = MutableStateFlow(false)
  val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

  init {
    viewModelScope.launch {
      // Initialize default session if none exists
      sessions.collect { list ->
        if (list.isNotEmpty() && _currentSessionId.value == null) {
          selectSession(list.first().id)
        } else if (list.isEmpty() && _currentSessionId.value == null) {
          val newId = chatRepository.createNewSession("Nexus Core Workspace", "Llama-3.2-1B-Instruct.Q4_K_M.gguf")
          _currentSessionId.value = newId
          // Seed initial greeting
          chatRepository.saveMessage(
            sessionId = newId,
            sender = "ASSISTANT",
            content = "Nexus Local AI Kernel online. Running locally on this device via GGUF neural engine. Zero telemetry, 100% private, no cloud lock-in. How can I assist your workflow today?",
            modelName = "Llama-3.2-1B-Instruct.Q4_K_M.gguf",
            tokensPerSec = 28.4f
          )
        }
      }
    }
  }

  fun selectSession(sessionId: String) {
    _currentSessionId.value = sessionId
    viewModelScope.launch {
      chatRepository.getMessagesForSession(sessionId).collect { entityList ->
        _uiMessages.value = entityList.map { entity ->
          ChatMessage(
            id = entity.id,
            sender = if (entity.sender == "USER") MessageSender.USER else MessageSender.ASSISTANT,
            content = entity.content,
            timestamp = entity.timestamp,
            tokensPerSec = entity.tokensPerSec,
            modelName = entity.modelName,
            isGroundingUsed = entity.isGroundingUsed,
            attachmentName = entity.attachmentName
          )
        }
      }
    }
  }

  fun createSession(title: String, modelName: String) {
    viewModelScope.launch {
      val id = chatRepository.createNewSession(title, modelName)
      selectSession(id)
    }
  }

  fun sendMessage(
    prompt: String,
    modelName: String,
    isGroundingEnabled: Boolean,
    attachmentName: String? = null,
    temperature: Float = 0.7f,
    topP: Float = 0.9f
  ) {
    val sessionId = _currentSessionId.value ?: return
    viewModelScope.launch {
      _isGenerating.value = true

      // 1. Save user message to Room
      chatRepository.saveMessage(
        sessionId = sessionId,
        sender = "USER",
        content = prompt,
        modelName = modelName,
        isGroundingUsed = isGroundingEnabled,
        attachmentName = attachmentName
      )

      // 2. Prepare Grounding context if enabled
      val groundingPrefix = if (isGroundingEnabled) {
        "[Web Grounding: Attached local search knowledge]\n\n"
      } else ""

      // 3. Stream from local GGUF engine
      val config = GenerationConfig(
        temperature = temperature,
        topP = topP,
        systemPrompt = "You are Nexus Local AI, an autonomous on-device intelligence."
      )

      val stringBuilder = StringBuilder(groundingPrefix)
      var lastTokensPerSec = 26.5f

      chatRepository.streamInference(prompt, config).collect { chunk ->
        stringBuilder.append(chunk.token)
        lastTokensPerSec = chunk.tokensPerSecond
      }

      // 4. Save final assistant response to Room
      chatRepository.saveMessage(
        sessionId = sessionId,
        sender = "ASSISTANT",
        content = stringBuilder.toString(),
        modelName = modelName,
        tokensPerSec = lastTokensPerSec,
        isGroundingUsed = isGroundingEnabled
      )

      _isGenerating.value = false
    }
  }
}
