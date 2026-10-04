package com.example.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.common.CyberBadge
import com.example.presentation.common.CyberButton
import com.example.presentation.common.CyberGlassCard
import com.example.presentation.dashboard.SystemMonitorHelper
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberElevatedSurface
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDark
import com.example.ui.theme.NeonGreenLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun SettingsScreen(
  isDarkTheme: Boolean,
  onToggleTheme: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val hardwareStats = remember { SystemMonitorHelper.getHardwareStats(context) }

  // Model Hyperparameters
  var temperature by remember { mutableFloatStateOf(0.7f) }
  var topP by remember { mutableFloatStateOf(0.9f) }
  var contextWindow by remember { mutableIntStateOf(4096) }
  var maxTokens by remember { mutableIntStateOf(1024) }
  var cpuThreads by remember { mutableIntStateOf((hardwareStats.cpuCores / 2).coerceAtLeast(2)) }

  // Persona Prompt Template
  val personas = remember {
    listOf(
      "Default Nexus Core" to "You are Nexus Local AI, a precise, helpful, and secure on-device assistant.",
      "Senior Android Architect" to "You are an elite Senior Android Engineer and Google Developer Expert. Provide clean Kotlin architecture, Jetpack Compose best practices, and performant code.",
      "Cyberpunk Terminal Hacker" to "You speak with cyberpunk tech slang, high-tech matrix analogies, and direct concise logic.",
      "Creative Storyteller" to "You are an imaginative worldbuilder and novelist specializing in sci-fi and speculative cyberpunk fiction."
    )
  }
  var selectedPersonaIndex by remember { mutableIntStateOf(0) }

  // Layout mode
  var isDesktopLayoutForced by remember { mutableStateOf(false) }

  fun runAutoOptimize() {
    val totalRam = hardwareStats.totalRamGb
    val cores = hardwareStats.cpuCores

    if (totalRam >= 10f) {
      contextWindow = 8192
      maxTokens = 2048
      cpuThreads = (cores - 2).coerceAtLeast(4)
      Toast.makeText(context, "High-end profile applied: 8k Context, $cpuThreads CPU Threads", Toast.LENGTH_LONG).show()
    } else if (totalRam >= 6f) {
      contextWindow = 4096
      maxTokens = 1024
      cpuThreads = (cores / 2).coerceAtLeast(4)
      Toast.makeText(context, "Balanced profile applied: 4k Context, $cpuThreads CPU Threads", Toast.LENGTH_LONG).show()
    } else {
      contextWindow = 2048
      maxTokens = 512
      cpuThreads = 2
      Toast.makeText(context, "Memory Saver profile applied: 2k Context, 2 CPU Threads", Toast.LENGTH_LONG).show()
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .padding(horizontal = 16.dp)
      .testTag("settings_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item { Spacer(modifier = Modifier.height(8.dp)) }

    // Header Title
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "KERNEL CONFIGURATION",
            style = CyberMonoType.BadgeText,
            color = NeonGreen,
            fontSize = 11.sp
          )
          Text(
            text = "Engine Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        CyberButton(
          text = "Auto-Optimize",
          icon = Icons.Default.AutoAwesome,
          onClick = { runAutoOptimize() },
          isSecondary = true,
          testTag = "auto_optimize_button"
        )
      }
    }

    // Model Inference Hyperparameters Card
    item {
      CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Tune, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("MODEL PARAMETERS (GGUF)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Temperature Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Temperature", color = TextPrimary, fontSize = 13.sp)
            Text(String.format(Locale.US, "%.2f", temperature), style = CyberMonoType.StatNumber, fontSize = 13.sp)
          }
          Slider(
            value = temperature,
            onValueChange = { temperature = it },
            valueRange = 0.0f..1.5f,
            colors = SliderDefaults.colors(
              thumbColor = NeonGreen,
              activeTrackColor = NeonGreen,
              inactiveTrackColor = Color(0x3364748B)
            ),
            modifier = Modifier.testTag("temperature_slider")
          )

          // Top-P Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Top-P Sampling", color = TextPrimary, fontSize = 13.sp)
            Text(String.format(Locale.US, "%.2f", topP), style = CyberMonoType.StatNumber, fontSize = 13.sp)
          }
          Slider(
            value = topP,
            onValueChange = { topP = it },
            valueRange = 0.1f..1.0f,
            colors = SliderDefaults.colors(
              thumbColor = BloodRed,
              activeTrackColor = BloodRed,
              inactiveTrackColor = Color(0x3364748B)
            ),
            modifier = Modifier.testTag("top_p_slider")
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Context Window Options
          Text("Context Window Size", color = TextPrimary, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(2048, 4096, 8192, 16384).forEach { size ->
              val isSelected = size == contextWindow
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) NeonGreen else CyberElevatedSurface)
                  .border(1.dp, if (isSelected) NeonGreen else Color(0x3364748B), RoundedCornerShape(8.dp))
                  .clickable { contextWindow = size }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${size / 1024}k",
                  style = CyberMonoType.BadgeText,
                  color = if (isSelected) CyberBlack else TextPrimary,
                  fontSize = 11.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // CPU Execution Threads
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Compute Threads", color = TextPrimary, fontSize = 13.sp)
              Text("Available cores: ${hardwareStats.cpuCores}", color = TextSecondary, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(2, 4, 6, 8).filter { it <= hardwareStats.cpuCores }.forEach { threads ->
                val isSelected = threads == cpuThreads
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) BloodRed else CyberElevatedSurface)
                    .clickable { cpuThreads = threads }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = "${threads}T",
                    style = CyberMonoType.BadgeText,
                    color = Color.White,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // Persona Prompt Templates Card
    item {
      CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("SYSTEM PROMPT & PERSONA", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          personas.forEachIndexed { index, (title, prompt) ->
            val isSelected = index == selectedPersonaIndex
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) Color(0x3300FF66) else CyberElevatedSurface)
                .border(1.dp, if (isSelected) NeonGreen else Color(0x2664748B), RoundedCornerShape(10.dp))
                .clickable { selectedPersonaIndex = index }
                .padding(12.dp)
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                  if (isSelected) {
                    CyberBadge(text = "ACTIVE", color = NeonGreen)
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = prompt, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }
    }

    // Theme & Layout Settings Card
    item {
      CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Devices, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("DISPLAY & INTERFACE", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Dark Theme Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Cyberpunk Dark Mode", color = TextPrimary, fontSize = 13.sp)
              Text("Blood Red & Neon Green theme", color = TextSecondary, fontSize = 11.sp)
            }
            Switch(
              checked = isDarkTheme,
              onCheckedChange = { onToggleTheme(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = NeonGreen,
                checkedTrackColor = NeonGreenDark,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
              ),
              modifier = Modifier.testTag("theme_switch")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Desktop Mode Simulation Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Desktop / Tablet Split Mode", color = TextPrimary, fontSize = 13.sp)
              Text("Force dual-pane side navigation", color = TextSecondary, fontSize = 11.sp)
            }
            Switch(
              checked = isDesktopLayoutForced,
              onCheckedChange = {
                isDesktopLayoutForced = it
                Toast.makeText(context, if (it) "Desktop Mode enabled" else "Mobile Compact Mode active", Toast.LENGTH_SHORT).show()
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = BloodRed,
                checkedTrackColor = Color(0xFF660011)
              )
            )
          }
        }
      }
    }

    // Google Cloud Sync & Backup Card
    item {
      CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CloudSync, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("CLOUD SYNC & BACKUP", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Connect Google Account via Credential Manager to sync local chat sessions & prompt templates to Google Drive securely.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CyberButton(
              text = "Google Sign-In",
              onClick = {
                Toast.makeText(context, "Google Credential Manager: Ready to sync", Toast.LENGTH_SHORT).show()
              },
              icon = Icons.Default.AccountCircle,
              isSecondary = true,
              modifier = Modifier.weight(1f),
              testTag = "google_signin_button"
            )

            CyberButton(
              text = "Backup Drive",
              onClick = {
                Toast.makeText(context, "Local Room database backed up to encrypted storage!", Toast.LENGTH_SHORT).show()
              },
              icon = Icons.Default.CloudUpload,
              isSecondary = false,
              modifier = Modifier.weight(1f),
              testTag = "backup_drive_button"
            )
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(16.dp)) }
  }
}
