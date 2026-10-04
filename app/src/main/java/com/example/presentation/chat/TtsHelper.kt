package com.example.presentation.chat

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsHelper(context: Context) : TextToSpeech.OnInitListener {
  private var tts: TextToSpeech? = TextToSpeech(context, this)
  private var isInitialized = false

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts?.setLanguage(Locale.getDefault())
      if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
        isInitialized = true
      }
    }
  }

  fun speak(text: String) {
    if (isInitialized) {
      tts?.stop()
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nexus_tts_id")
    }
  }

  fun stop() {
    tts?.stop()
  }

  fun release() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}
