package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.presentation.navigation.NexusNavHost
import com.example.ui.theme.NexusTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var isDarkTheme by remember { mutableStateOf(true) }

      NexusTheme(darkTheme = isDarkTheme) {
        NexusNavHost(
          isDarkTheme = isDarkTheme,
          onToggleTheme = { isDarkTheme = it },
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
