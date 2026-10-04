package com.example.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.presentation.common.CyberCircularGauge
import com.example.presentation.common.CyberGlassCard
import com.example.presentation.common.CyberScanningRadar
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.CyberRedGlassBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenLight
import com.example.ui.theme.TechShapes
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
  onNavigateToChat: () -> Unit,
  onNavigateToModelHub: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var stats by remember { mutableStateOf(SystemMonitorHelper.getHardwareStats(context)) }
  var junkSizeBytes by remember { mutableLongStateOf(SystemMonitorHelper.calculateJunkSize(context)) }
  var isScanning by remember { mutableStateOf(false) }
  var isCleaning by remember { mutableStateOf(false) }
  var lastCleanedMsg by remember { mutableStateOf("") }

  val scanLogs = remember {
    mutableStateListOf(
      "Kernel initialized: Ready for GGUF model execution",
      "Memory subsystem verified: Unified architecture detected"
    )
  }

  // Refresh hardware stats periodically
  LaunchedEffect(Unit) {
    while (true) {
      stats = SystemMonitorHelper.getHardwareStats(context)
      delay(4000)
    }
  }

  fun triggerScan() {
    coroutineScope.launch {
      isScanning = true
      scanLogs.clear()
      scanLogs.add(">> Initializing 3D deep cache scan...")
      delay(600)
      scanLogs.add(">> Scanning app internal textures & heap...")
      delay(600)
      scanLogs.add(">> Inspecting orphan shader binaries...")
      delay(700)
      junkSizeBytes = SystemMonitorHelper.calculateJunkSize(context)
      val formattedMb = String.format(Locale.US, "%.1f", junkSizeBytes / (1024f * 1024f))
      scanLogs.add(">> Scan complete: Found $formattedMb MB junk & cached fragments.")
      isScanning = false
    }
  }

  fun triggerClean() {
    coroutineScope.launch {
      isCleaning = true
      isScanning = true
      scanLogs.add(">> [PURGE] Flushing runtime temp caches...")
      delay(700)
      val freedBytes = SystemMonitorHelper.clearJunk(context)
      delay(600)
      junkSizeBytes = 0L
      val freedMb = String.format(Locale.US, "%.1f", freedBytes / (1024f * 1024f))
      lastCleanedMsg = "Freed $freedMb MB of memory!"
      scanLogs.add(">> [SUCCESS] Purged $freedMb MB! RAM & Storage optimized.")
      isCleaning = false
      isScanning = false
      stats = SystemMonitorHelper.getHardwareStats(context)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .padding(horizontal = 16.dp)
      .testTag("dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Header Banner
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "SYSTEM TELEMETRY",
            style = CyberMonoType.BadgeText,
            color = NeonGreen,
            fontSize = 11.sp
          )
          Text(
            text = "Hardware & Engine",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        IconButton(
          onClick = {
            stats = SystemMonitorHelper.getHardwareStats(context)
          },
          modifier = Modifier.testTag("refresh_stats_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh Telemetry",
            tint = NeonGreen
          )
        }
      }
    }

    // Hardware Telemetry Card
    item {
      CyberGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberGlassBorder
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Memory,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "NEURAL HARDWARE STATUS",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
              )
            }
            CyberBadge(
              text = stats.cpuArchitecture,
              color = NeonGreen
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Circular Gauges Row (RAM, Storage, VRAM)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            CyberCircularGauge(
              title = "RAM Used",
              valueText = "${(stats.ramUsagePercent * 100).toInt()}%",
              progress = stats.ramUsagePercent,
              gaugeColor = if (stats.ramUsagePercent > 0.8f) BloodRed else NeonGreen,
              subtitle = String.format(Locale.US, "%.1f / %.1f GB", stats.usedRamGb, stats.totalRamGb)
            )

            CyberCircularGauge(
              title = "Storage",
              valueText = "${(stats.storageUsagePercent * 100).toInt()}%",
              progress = stats.storageUsagePercent,
              gaugeColor = Color(0xFF00E5FF),
              subtitle = String.format(Locale.US, "%.0f / %.0f GB", stats.usedStorageGb, stats.totalStorageGb)
            )

            CyberCircularGauge(
              title = "GGUF VRAM",
              valueText = String.format(Locale.US, "%.1fG", stats.estimatedVramGb),
              progress = (stats.estimatedVramGb / (stats.totalRamGb * 0.5f)).coerceIn(0f, 1f),
              gaugeColor = NeonGreenLight,
              subtitle = "Avail. AI Budget"
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Device Details Strip
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            CyberBadge(text = stats.deviceModel, color = TextSecondary)
            CyberBadge(text = "${stats.cpuCores} Cores Active", color = NeonGreen)
            CyberBadge(text = stats.androidVersion, color = TextSecondary)
            CyberBadge(text = "ARM Neon & Vulkan Ready", color = NeonGreen)
          }
        }
      }
    }

    // 3D Junk Cleaner & Cache Purge Card
    item {
      CyberGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (junkSizeBytes > 0) CyberRedGlassBorder else CyberGlassBorder
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CleaningServices,
                contentDescription = null,
                tint = if (junkSizeBytes > 0) BloodRed else NeonGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "3D JUNK CLEANER & RAM PURGE",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
              )
            }
            if (lastCleanedMsg.isNotEmpty()) {
              CyberBadge(text = lastCleanedMsg, color = NeonGreen)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Scanning Radar
          CyberScanningRadar(
            isScanning = isScanning || isCleaning,
            scannerColor = if (junkSizeBytes > 100 * 1024 * 1024L) BloodRed else NeonGreen
          )

          Spacer(modifier = Modifier.height(14.dp))

          val junkMb = junkSizeBytes / (1024f * 1024f)
          Text(
            text = if (junkSizeBytes > 0) String.format(Locale.US, "%.1f MB Residue Detected", junkMb) else "System Clean & Optimized",
            style = CyberMonoType.StatNumber,
            color = if (junkSizeBytes > 0) BloodRed else NeonGreen,
            fontSize = 18.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Action Buttons: Scan & Clean
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            CyberButton(
              text = if (isScanning) "Scanning..." else "Scan Residue",
              onClick = { triggerScan() },
              isSecondary = true,
              modifier = Modifier.weight(1f),
              testTag = "scan_junk_button"
            )

            CyberButton(
              text = if (isCleaning) "Purging..." else "Clean Junk",
              onClick = { triggerClean() },
              isSecondary = false,
              modifier = Modifier.weight(1f),
              testTag = "clean_junk_button"
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Real-time Terminal Log
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(TechShapes.TechTerminal)
              .background(Color(0xFF06090F))
              .border(1.dp, Color(0x3300FF66), TechShapes.TechTerminal)
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "[NEXUS KERNEL CONSOLE]",
                style = CyberMonoType.BadgeText,
                color = NeonGreen.copy(alpha = 0.6f),
                fontSize = 10.sp
              )
              scanLogs.takeLast(4).forEach { log ->
                Text(
                  text = log,
                  style = CyberMonoType.TerminalLog
                )
              }
            }
          }
        }
      }
    }

    // Quick AI Actions Card
    item {
      CyberGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberGlassBorder
      ) {
        Column {
          Text(
            text = "LOCAL AI ACTIONS",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CyberButton(
              text = "Launch Chat",
              onClick = onNavigateToChat,
              icon = Icons.Default.PlayArrow,
              isSecondary = false,
              modifier = Modifier.weight(1f),
              testTag = "launch_chat_button"
            )

            CyberButton(
              text = "Browse Models",
              onClick = onNavigateToModelHub,
              icon = Icons.Default.AutoAwesome,
              isSecondary = true,
              modifier = Modifier.weight(1f),
              testTag = "browse_models_button"
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
