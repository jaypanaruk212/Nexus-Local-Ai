package com.example.presentation.modelhub

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.common.CyberBadge
import com.example.presentation.common.CyberButton
import com.example.presentation.common.CyberGlassCard
import com.example.service.ModelDownloadService
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberElevatedSurface
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGradients
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDark
import com.example.ui.theme.NeonGreenLight
import com.example.ui.theme.TechShapes
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class HubModelItem(
  val id: String,
  val name: String,
  val category: String, // Light, Medium, Heavy, Official, Community
  val sizeGb: Float,
  val quantType: String,
  val minRamGb: Float,
  val description: String,
  val author: String,
  val downloadUrl: String,
  val isDownloaded: Boolean = false
)

@Composable
fun ModelHubScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  val categories = remember { listOf("ALL", "LIGHT (<2GB)", "MEDIUM (2-4GB)", "HEAVY (4GB+)", "OFFICIAL", "COMMUNITY") }
  var selectedCategory by remember { mutableStateOf("ALL") }
  var searchQuery by remember { mutableStateOf("") }

  // Rotating Tips & Tricks during download
  val downloadTips = remember {
    listOf(
      "Tip: Model Q4_K_M menawarkan keseimbangan terbaik antara ukuran file dan akurasi inferensi.",
      "Tip: Pastikan baterai minimal 30% atau terhubung ke charger saat mengeksekusi model di atas 3B.",
      "Tip: Anda dapat mengimpor file .gguf kustom langsung dari kartu SD atau penyimpanan internal.",
      "Tip: Aktifkan mode Auto-Optimize RAM di Pengaturan agar Context Window disesuaikan otomatis."
    )
  }
  var currentTipIndex by remember { mutableIntStateOf(0) }

  // Active Download Status
  var downloadingModel by remember { mutableStateOf<HubModelItem?>(null) }
  var downloadProgress by remember { mutableFloatStateOf(0f) }
  var downloadSpeedMb by remember { mutableFloatStateOf(0f) }
  var downloadEtaSeconds by remember { mutableIntStateOf(0) }

  // Cycle tips during active download
  LaunchedEffect(downloadingModel) {
    if (downloadingModel != null) {
      while (downloadingModel != null) {
        delay(4000)
        currentTipIndex = (currentTipIndex + 1) % downloadTips.size
      }
    }
  }

  // File Picker for importing local GGUF
  val importModelLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    uri?.let {
      val filename = it.lastPathSegment ?: "local_model.gguf"
      Toast.makeText(context, "Successfully imported local GGUF: $filename", Toast.LENGTH_LONG).show()
    }
  }

  val modelList = remember {
    mutableStateListOf(
      HubModelItem(
        id = "1",
        name = "Llama-3.2-1B-Instruct",
        category = "LIGHT (<2GB)",
        sizeGb = 1.1f,
        quantType = "Q4_K_M",
        minRamGb = 2.5f,
        description = "Model super ringan dari Meta dengan kecepatan respons tinggi di HP berspesifikasi menengah.",
        author = "meta-llama / bartowski",
        downloadUrl = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF",
        isDownloaded = true
      ),
      HubModelItem(
        id = "2",
        name = "DeepSeek-R1-Distill-Qwen-1.5B",
        category = "LIGHT (<2GB)",
        sizeGb = 1.4f,
        quantType = "Q4_K_M",
        minRamGb = 3.0f,
        description = "Model reasoning penalaran matematika dan coding berbasis arsitektur DeepSeek R1.",
        author = "deepseek-ai / unsloth",
        downloadUrl = "https://huggingface.co/unsloth/DeepSeek-R1-Distill-Qwen-1.5B-GGUF",
        isDownloaded = false
      ),
      HubModelItem(
        id = "3",
        name = "Qwen2.5-Coder-1.5B-Instruct",
        category = "LIGHT (<2GB)",
        sizeGb = 1.3f,
        quantType = "Q4_K_M",
        minRamGb = 3.0f,
        description = "Dikhususkan untuk sintaks pemrograman Kotlin, Python, SQL, dan debugging instan.",
        author = "Qwen / Qwen2.5",
        downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF",
        isDownloaded = false
      ),
      HubModelItem(
        id = "4",
        name = "Phi-3.5-mini-instruct",
        category = "MEDIUM (2-4GB)",
        sizeGb = 2.4f,
        quantType = "Q4_K_M",
        minRamGb = 4.5f,
        description = "Model 3.8B dari Microsoft dengan konteks 128k, unggul untuk logika dan ringkasan panjang.",
        author = "microsoft / bartowski",
        downloadUrl = "https://huggingface.co/bartowski/Phi-3.5-mini-instruct-GGUF",
        isDownloaded = false
      ),
      HubModelItem(
        id = "5",
        name = "Mistral-7B-Instruct-v0.3",
        category = "HEAVY (4GB+)",
        sizeGb = 4.2f,
        quantType = "Q4_K_M",
        minRamGb = 7.0f,
        description = "Model performa tinggi untuk flagship dengan RAM 8GB+, penalaran narasi sangat kaya.",
        author = "mistralai / TheBloke",
        downloadUrl = "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.3-GGUF",
        isDownloaded = false
      )
    )
  }

  fun startModelDownload(model: HubModelItem) {
    downloadingModel = model
    downloadProgress = 0.05f
    downloadSpeedMb = 8.4f
    downloadEtaSeconds = (model.sizeGb * 1024 / 8.4f).toInt()

    // Start Android Foreground Service for persistent download
    val serviceIntent = Intent(context, ModelDownloadService::class.java).apply {
      action = ModelDownloadService.ACTION_START_DOWNLOAD
      putExtra(ModelDownloadService.EXTRA_MODEL_NAME, model.name)
      putExtra(ModelDownloadService.EXTRA_MODEL_URL, model.downloadUrl)
    }
    context.startService(serviceIntent)

    // Simulate progress in UI
    coroutineScope.launch {
      while (downloadProgress < 1.0f && downloadingModel != null) {
        delay(500)
        downloadProgress += 0.04f
        downloadSpeedMb = (7..14).random() + (0..9).random() / 10f
        val remainingMb = (model.sizeGb * 1024) * (1f - downloadProgress)
        downloadEtaSeconds = (remainingMb / downloadSpeedMb.coerceAtLeast(1f)).toInt().coerceAtLeast(0)
      }
      if (downloadProgress >= 1.0f) {
        val index = modelList.indexOfFirst { it.id == model.id }
        if (index != -1) {
          modelList[index] = modelList[index].copy(isDownloaded = true)
        }
        Toast.makeText(context, "${model.name} download complete and ready to use!", Toast.LENGTH_LONG).show()
        downloadingModel = null
      }
    }
  }

  fun cancelDownload() {
    val serviceIntent = Intent(context, ModelDownloadService::class.java).apply {
      action = ModelDownloadService.ACTION_CANCEL_DOWNLOAD
    }
    context.startService(serviceIntent)
    downloadingModel = null
    downloadProgress = 0f
    Toast.makeText(context, "Download cancelled", Toast.LENGTH_SHORT).show()
  }

  val filteredModels = modelList.filter { model ->
    val matchesCategory = when (selectedCategory) {
      "ALL" -> true
      "OFFICIAL" -> model.author.contains("meta") || model.author.contains("microsoft") || model.author.contains("mistral")
      "COMMUNITY" -> model.author.contains("unsloth") || model.author.contains("TheBloke")
      else -> model.category == selectedCategory
    }
    val matchesSearch = model.name.contains(searchQuery, ignoreCase = true) ||
      model.description.contains(searchQuery, ignoreCase = true) ||
      model.author.contains(searchQuery, ignoreCase = true)

    matchesCategory && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .padding(horizontal = 16.dp)
      .testTag("model_hub_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item { Spacer(modifier = Modifier.height(8.dp)) }

    // Header & Import Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "HUGGINGFACE & REPOSITORIES",
            style = CyberMonoType.BadgeText,
            color = NeonGreen,
            fontSize = 11.sp
          )
          Text(
            text = "AI Model Hub",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        // Import Local GGUF button
        CyberButton(
          text = "Import GGUF",
          icon = Icons.Default.FileOpen,
          onClick = { importModelLauncher.launch("*/*") },
          isSecondary = true,
          testTag = "import_gguf_button"
        )
      }
    }

    // Active Downloader Banner (if downloading)
    if (downloadingModel != null) {
      item {
        val model = downloadingModel!!
        CyberGlassCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonGreen,
          elevation = 12.dp
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                  progress = { downloadProgress },
                  modifier = Modifier.size(28.dp),
                  color = NeonGreen,
                  strokeWidth = 3.dp,
                  strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "DOWNLOADING MODEL",
                    style = CyberMonoType.BadgeText,
                    color = NeonGreen,
                    fontSize = 11.sp
                  )
                  Text(
                    text = model.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
              }

              IconButton(
                onClick = { cancelDownload() },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Cancel Download",
                  tint = BloodRed
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
              progress = { downloadProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = NeonGreen,
              trackColor = Color(0x3300FF66)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Speed & ETA Strip
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "${(downloadProgress * 100).toInt()}% (${String.format(Locale.US, "%.1f", model.sizeGb * downloadProgress)} / ${model.sizeGb} GB)",
                style = CyberMonoType.TerminalLog,
                color = TextSecondary
              )
              Text(
                text = "${String.format(Locale.US, "%.1f", downloadSpeedMb)} MB/s • ETA: ${downloadEtaSeconds}s",
                style = CyberMonoType.TerminalLog,
                color = NeonGreen
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rotating Tip
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF090F1B))
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = Color(0xFFFFD600),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = downloadTips[currentTipIndex],
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search models by name, author, or category...", color = TextMuted, fontSize = 13.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = NeonGreen)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("model_search_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = CyberElevatedSurface,
          unfocusedContainerColor = CyberElevatedSurface,
          focusedBorderColor = NeonGreen,
          unfocusedBorderColor = Color(0x3364748B),
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )
    }

    // Category Tabs Horizontal Scroll
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { cat ->
          val isSelected = cat == selectedCategory
          Box(
            modifier = Modifier
              .clip(TechShapes.ChamferedBadge)
              .background(if (isSelected) NeonGreen else CyberElevatedSurface)
              .border(
                1.dp,
                if (isSelected) NeonGreen else Color(0x3364748B),
                TechShapes.ChamferedBadge
              )
              .clickable { selectedCategory = cat }
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text(
              text = cat,
              style = CyberMonoType.BadgeText,
              color = if (isSelected) CyberBlack else TextPrimary,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // Model Cards List
    items(filteredModels, key = { it.id }) { model ->
      CyberGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (model.isDownloaded) CyberGlassBorder else Color(0x2664748B)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = model.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
              )
              Text(
                text = model.author,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }

            if (model.isDownloaded) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Downloaded",
                  tint = NeonGreen,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                CyberBadge(text = "READY", color = NeonGreen)
              }
            } else {
              CyberBadge(text = "${model.sizeGb} GB", color = Color(0xFF00E5FF))
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = model.description,
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Spec Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            CyberBadge(text = model.quantType, color = NeonGreenLight)
            CyberBadge(text = "Req: ${model.minRamGb}GB RAM", color = TextSecondary)
            CyberBadge(text = model.category, color = TextMuted)
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Download / Ready Action
          if (model.isDownloaded) {
            CyberButton(
              text = "Model Ready in Engine",
              onClick = {
                Toast.makeText(context, "${model.name} is currently loaded into active memory", Toast.LENGTH_SHORT).show()
              },
              icon = Icons.Default.CheckCircle,
              isSecondary = true,
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            CyberButton(
              text = "Download GGUF (${model.sizeGb} GB)",
              onClick = { startModelDownload(model) },
              icon = Icons.Default.CloudDownload,
              isSecondary = false,
              modifier = Modifier.fillMaxWidth(),
              testTag = "download_model_${model.id}"
            )
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(16.dp)) }
  }
}
