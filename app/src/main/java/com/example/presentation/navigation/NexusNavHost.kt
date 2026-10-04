package com.example.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.about.AboutScreen
import com.example.presentation.chat.ChatScreen
import com.example.presentation.dashboard.DashboardScreen
import com.example.presentation.modelhub.ModelHubScreen
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.splash.SplashScreen
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedGlow
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberElevatedSurface
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NexusNavHost(
  isDarkTheme: Boolean,
  onToggleTheme: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

  // Handle system back button
  BackHandler(enabled = currentScreen != Screen.Dashboard && currentScreen != Screen.Splash) {
    currentScreen = Screen.Dashboard
  }

  // Responsive layout: detect width for NavigationRail vs BottomBar
  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isExpandedScreen = maxWidth >= 600.dp
    val showNav = currentScreen != Screen.Splash

    if (isExpandedScreen && showNav) {
      // -------------------------------------------------------------
      // DESKTOP / TABLET DUAL-PANE (NavigationRail)
      // -------------------------------------------------------------
      Row(modifier = Modifier.fillMaxSize().background(CyberBlack)) {
        NavigationRail(
          containerColor = CyberDarkSurface,
          contentColor = TextPrimary,
          modifier = Modifier
            .fillMaxHeight()
            .border(1.dp, CyberGlassBorder)
            .testTag("desktop_nav_rail"),
          header = {
            Box(
              modifier = Modifier.padding(vertical = 16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "NEXUS",
                style = CyberMonoType.BadgeText,
                color = NeonGreen,
                fontSize = 13.sp
              )
            }
          }
        ) {
          Screen.bottomNavItems.forEach { screen ->
            val isSelected = currentScreen.route == screen.route
            NavigationRailItem(
              selected = isSelected,
              onClick = { currentScreen = screen },
              icon = {
                screen.icon?.let {
                  Icon(
                    imageVector = it,
                    contentDescription = screen.title,
                    tint = if (isSelected) NeonGreen else TextSecondary
                  )
                }
              },
              label = {
                Text(
                  text = screen.title,
                  fontSize = 10.sp,
                  color = if (isSelected) NeonGreen else TextSecondary
                )
              },
              colors = NavigationRailItemDefaults.colors(
                indicatorColor = Color(0x3300FF66)
              ),
              modifier = Modifier.testTag("nav_rail_${screen.route}")
            )
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        ) {
          RenderScreenContent(
            screen = currentScreen,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            onNavigate = { currentScreen = it }
          )
        }
      }
    } else {
      // -------------------------------------------------------------
      // MOBILE COMPACT (Bottom Navigation Bar)
      // -------------------------------------------------------------
      Scaffold(
        bottomBar = {
          if (showNav) {
            NavigationBar(
              containerColor = CyberDarkSurface,
              contentColor = TextPrimary,
              modifier = Modifier
                .border(1.dp, CyberGlassBorder)
                .testTag("mobile_bottom_nav")
            ) {
              Screen.bottomNavItems.forEach { screen ->
                val isSelected = currentScreen.route == screen.route
                NavigationBarItem(
                  selected = isSelected,
                  onClick = { currentScreen = screen },
                  icon = {
                    screen.icon?.let {
                      Icon(
                        imageVector = it,
                        contentDescription = screen.title,
                        tint = if (isSelected) NeonGreen else TextMuted,
                        modifier = Modifier.size(22.dp)
                      )
                    }
                  },
                  label = {
                    Text(
                      text = screen.title,
                      fontSize = 10.sp,
                      color = if (isSelected) NeonGreen else TextMuted
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0x2E00FF66)
                  ),
                  modifier = Modifier.testTag("nav_bottom_${screen.route}")
                )
              }
            }
          }
        },
        containerColor = CyberBlack,
        modifier = Modifier.fillMaxSize()
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          RenderScreenContent(
            screen = currentScreen,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            onNavigate = { currentScreen = it }
          )
        }
      }
    }
  }
}

@Composable
private fun RenderScreenContent(
  screen: Screen,
  isDarkTheme: Boolean,
  onToggleTheme: (Boolean) -> Unit,
  onNavigate: (Screen) -> Unit
) {
  when (screen) {
    Screen.Splash -> {
      SplashScreen(
        onNavigateToDashboard = { onNavigate(Screen.Dashboard) }
      )
    }
    Screen.Dashboard -> {
      DashboardScreen(
        onNavigateToChat = { onNavigate(Screen.Chat) },
        onNavigateToModelHub = { onNavigate(Screen.ModelHub) }
      )
    }
    Screen.Chat -> {
      ChatScreen()
    }
    Screen.ModelHub -> {
      ModelHubScreen()
    }
    Screen.Settings -> {
      SettingsScreen(
        isDarkTheme = isDarkTheme,
        onToggleTheme = onToggleTheme
      )
    }
    Screen.About -> {
      AboutScreen()
    }
  }
}
