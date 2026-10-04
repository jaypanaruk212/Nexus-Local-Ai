package com.example.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
  data object Splash : Screen("splash", "Welcome")
  data object Dashboard : Screen("dashboard", "Monitor", Icons.Default.Dashboard)
  data object Chat : Screen("chat", "Nexus AI", Icons.Default.Chat)
  data object ModelHub : Screen("model_hub", "Model Hub", Icons.Default.CloudDownload)
  data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
  data object About : Screen("about", "Developer", Icons.Default.Info)

  companion object {
    val bottomNavItems = listOf(
      Dashboard,
      Chat,
      ModelHub,
      Settings,
      About
    )
  }
}
