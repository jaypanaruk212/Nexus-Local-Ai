package com.example.presentation.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.presentation.common.CyberBadge
import com.example.presentation.common.CyberButton
import com.example.presentation.common.CyberGlassCard
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedGlow
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberElevatedSurface
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGradients
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.CyberRedGlassBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TechShapes
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val sender: MessageSender,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val tokensPerSec: Float? = null,
  val modelName: String = "Llama-3.2-1B-Instruct.Q4_K_M.gguf",
  val isGroundingUsed: Boolean = false,
  val attachmentName: String? = null
)

enum class MessageSender {
  USER,
  ASSISTANT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  viewModel: ChatViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val listState = rememberLazyListState()

  // Native TTS engine
  val ttsHelper = remember { TtsHelper(context) }
  DisposableEffect(Unit) {
    onDispose { ttsHelper.release() }
  }

  // Reactive Room State
  val messages by viewModel.uiMessages.collectAsStateWithLifecycle()
  val sessions by viewModel.sessions.collectAsStateWithLifecycle()
  val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()

  // Model Selection state
  val availableModels = remember {
    listOf(
      "Llama-3.2-1B-Instruct.Q4_K_M.gguf",
      "DeepSeek-R1-Distill-1.5B-Q4.gguf",
      "Qwen2.5-Coder-1.5B-Instruct.gguf",
      "Phi-3.5-mini-instruct-Q4_K_M.gguf",
      "Mistral-7B-Instruct-v0.3-Q4.gguf"
    )
  }
  var selectedModel by remember { mutableStateOf(availableModels[0]) }
  var isModelDropdownOpen by remember { mutableStateOf(false) }

  // Grounding & Web Search
  var isGroundingEnabled by remember { mutableStateOf(false) }

  // Attachments
  var attachedFileName by remember { mutableStateOf<String?>(null) }
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    uri?.let {
      attachedFileName = it.lastPathSegment ?: "document.txt"
      Toast.makeText(context, "Attached: $attachedFileName", Toast.LENGTH_SHORT).show()
    }
  }

  // Projects / History Drawer
  var showHistorySheet by remember { mutableStateOf(false) }

  // Input state
  var inputText by remember { mutableStateOf("") }

  // Auto-scroll when new messages arrive
  LaunchedEffect(messages.size, isGenerating) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  fun onSendClick() {
    val prompt = inputText.trim()
    if (prompt.isEmpty() && attachedFileName == null) return

    val currentAttached = attachedFileName
    inputText = ""
    attachedFileName = null

    viewModel.sendMessage(
      prompt = prompt,
      modelName = selectedModel,
      isGroundingEnabled = isGroundingEnabled,
      attachmentName = currentAttached
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .testTag("chat_screen")
  ) {
    // -------------------------------------------------------------
    // TOP BAR: Model Selector Dropdown & History Button
    // -------------------------------------------------------------
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(CyberDarkSurface)
        .border(1.dp, CyberGlassBorder)
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Model Picker Dropdown Button
        Box {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(CyberElevatedSurface)
              .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
              .clickable { isModelDropdownOpen = true }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("model_picker_dropdown"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(NeonGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = selectedModel.substringBefore(".gguf").take(18),
              style = CyberMonoType.BadgeText,
              color = TextPrimary,
              fontSize = 11.sp
            )
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Select GGUF Model",
              tint = NeonGreen
            )
          }

          DropdownMenu(
            expanded = isModelDropdownOpen,
            onDismissRequest = { isModelDropdownOpen = false },
            modifier = Modifier
              .background(CyberDarkSurface)
              .border(1.dp, CyberGlassBorder)
          ) {
            availableModels.forEach { model ->
              DropdownMenuItem(
                text = {
                  Column {
                    Text(text = model, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Local GGUF (Quantized 4-bit)", color = TextSecondary, fontSize = 10.sp)
                  }
                },
                onClick = {
                  selectedModel = model
                  isModelDropdownOpen = false
                  Toast.makeText(context, "Loaded: $model", Toast.LENGTH_SHORT).show()
                }
              )
            }
          }
        }

        // Action Buttons: Grounding Toggle & History Projects
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Web Search Grounding Toggle
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isGroundingEnabled) BloodRed.copy(alpha = 0.2f) else CyberElevatedSurface)
              .border(
                1.dp,
                if (isGroundingEnabled) BloodRed else Color(0x3364748B),
                RoundedCornerShape(8.dp)
              )
              .clickable {
                isGroundingEnabled = !isGroundingEnabled
                Toast.makeText(
                  context,
                  if (isGroundingEnabled) "Web Grounding Search: ENABLED" else "Offline Local Mode: STRICT",
                  Toast.LENGTH_SHORT
                ).show()
              }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("grounding_toggle_button"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Web Grounding",
              tint = if (isGroundingEnabled) BloodRed else TextSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isGroundingEnabled) "NET ON" else "OFFLINE",
              style = CyberMonoType.BadgeText,
              color = if (isGroundingEnabled) BloodRed else TextSecondary,
              fontSize = 10.sp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // History / Projects
          IconButton(
            onClick = { showHistorySheet = true },
            modifier = Modifier.testTag("history_button")
          ) {
            Icon(
              imageVector = Icons.Default.Folder,
              contentDescription = "Projects History",
              tint = NeonGreen
            )
          }
        }
      }
    }

    // -------------------------------------------------------------
    // CHAT MESSAGES AREA
    // -------------------------------------------------------------
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item { Spacer(modifier = Modifier.height(8.dp)) }

      items(messages, key = { it.id }) { msg ->
        ChatMessageBubble(
          message = msg,
          onCopy = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Nexus AI Message", msg.content))
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
          },
          onEdit = {
            inputText = msg.content
          },
          onSpeak = {
            ttsHelper.speak(msg.content)
          }
        )
      }

      // Typing Indicator
      if (isGenerating) {
        item {
          CyberTypingIndicator(modelName = selectedModel)
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }

    // Attachment preview chip if any
    if (attachedFileName != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDarkSurface)
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NeonGreen.copy(alpha = 0.15f))
            .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AttachFile,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = attachedFileName ?: "",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove attachment",
            tint = BloodRed,
            modifier = Modifier
              .size(16.dp)
              .clickable { attachedFileName = null }
          )
        }
      }
    }

    // -------------------------------------------------------------
    // INPUT AREA: Attachment +, Text Field, Send Button
    // -------------------------------------------------------------
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(CyberDarkSurface)
        .border(1.dp, CyberGlassBorder)
        .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Attachment Button
        IconButton(
          onClick = { filePickerLauncher.launch("*/*") },
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CyberElevatedSurface)
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(10.dp))
            .testTag("attachment_button")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add context file or document",
            tint = NeonGreen
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Cyber Text Input
        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = {
            Text(
              text = if (isGenerating) "AI is generating output..." else "Message Nexus Local AI...",
              color = TextMuted,
              fontSize = 13.sp
            )
          },
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF090D17),
            unfocusedContainerColor = Color(0xFF090D17),
            focusedBorderColor = NeonGreen,
            unfocusedBorderColor = Color(0x3364748B),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          shape = RoundedCornerShape(12.dp),
          maxLines = 4
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 3D Send Button
        Box(
          modifier = Modifier
            .size(48.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = BloodRedGlow)
            .clip(RoundedCornerShape(12.dp))
            .background(CyberGradients.BloodRedGradient)
            .clickable(
              enabled = !isGenerating && (inputText.isNotBlank() || attachedFileName != null),
              onClick = { onSendClick() }
            )
            .testTag("send_message_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message to Local LLM",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }

  // -------------------------------------------------------------
  // HISTORY / PROJECTS BOTTOM SHEET (Backed by Room Database)
  // -------------------------------------------------------------
  if (showHistorySheet) {
    ModalBottomSheet(
      onDismissRequest = { showHistorySheet = false },
      containerColor = CyberDarkSurface,
      sheetState = rememberModalBottomSheetState()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SAVED ROOM SESSIONS (${sessions.size})",
            style = CyberMonoType.BadgeText,
            color = NeonGreen,
            fontSize = 12.sp
          )

          CyberButton(
            text = "+ New Chat",
            isSecondary = true,
            onClick = {
              viewModel.createSession("Project Workspace ${sessions.size + 1}", selectedModel)
              showHistorySheet = false
              Toast.makeText(context, "New chat workspace initialized", Toast.LENGTH_SHORT).show()
            }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (sessions.isEmpty()) {
          Text("No saved sessions yet.", color = TextSecondary, fontSize = 12.sp)
        } else {
          sessions.forEach { session ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CyberElevatedSurface)
                .border(1.dp, CyberGlassBorder, RoundedCornerShape(10.dp))
                .clickable {
                  viewModel.selectSession(session.id)
                  showHistorySheet = false
                }
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = session.title,
                  color = TextPrimary,
                  fontWeight = FontWeight.Medium,
                  fontSize = 14.sp
                )
                Text(
                  text = "Model: ${session.modelUsed}",
                  color = TextSecondary,
                  fontSize = 10.sp
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

/**
 * Premium Chat Bubble for User and Local AI with action toolbar (Copy, Edit, TTS)
 */
@Composable
fun ChatMessageBubble(
  message: ChatMessage,
  onCopy: () -> Unit,
  onEdit: () -> Unit,
  onSpeak: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isUser = message.sender == MessageSender.USER
  val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
  val timeStr = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    // Sender label
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
      Text(
        text = if (isUser) "YOU" else "NEXUS AI",
        style = CyberMonoType.BadgeText,
        color = if (isUser) BloodRed else NeonGreen,
        fontSize = 10.sp
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = timeStr,
        color = TextMuted,
        fontSize = 10.sp
      )
      if (!isUser && message.tokensPerSec != null) {
        Spacer(modifier = Modifier.width(6.dp))
        CyberBadge(
          text = "${String.format(Locale.US, "%.1f", message.tokensPerSec)} tok/s",
          color = NeonGreen
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Bubble Body
    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp
          )
        )
        .background(
          if (isUser) {
            Brush.linearGradient(listOf(BloodRed.copy(alpha = 0.25f), Color(0xFF14070A)))
          } else {
            Brush.linearGradient(listOf(Color(0xFF0F1522), Color(0xFF090D14)))
          }
        )
        .border(
          width = 1.dp,
          color = if (isUser) CyberRedGlassBorder else CyberGlassBorder,
          shape = RoundedCornerShape(16.dp)
        )
        .padding(14.dp)
    ) {
      Column {
        if (message.attachmentName != null) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0x3300FF66))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AttachFile,
              contentDescription = null,
              tint = NeonGreen,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = message.attachmentName,
              color = TextPrimary,
              fontSize = 11.sp
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
          text = message.content,
          color = TextPrimary,
          fontSize = 14.sp,
          lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message Actions Toolbar: Copy, Edit (user), TTS (assistant)
        Row(
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Copy Button
          IconButton(
            onClick = onCopy,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy message",
              tint = TextSecondary,
              modifier = Modifier.size(14.dp)
            )
          }

          // Edit Button (if User)
          if (isUser) {
            IconButton(
              onClick = onEdit,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit prompt",
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
              )
            }
          }

          // TTS Speech Button (if Assistant)
          if (!isUser) {
            IconButton(
              onClick = onSpeak,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Read aloud via Text to Speech",
                tint = NeonGreen,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Cyberpunk Animated Typing Waveform / Indicator
 */
@Composable
fun CyberTypingIndicator(modelName: String) {
  val infiniteTransition = rememberInfiniteTransition(label = "TypingDots")
  val dot1Scale by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "Dot1"
  )
  val dot2Scale by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(tween(600, delayMillis = 200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "Dot2"
  )
  val dot3Scale by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(tween(600, delayMillis = 400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "Dot3"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF0F1522))
      .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Text(
      text = "Inference streaming ($modelName)",
      style = CyberMonoType.BadgeText,
      color = NeonGreen,
      fontSize = 11.sp
    )
    Spacer(modifier = Modifier.width(12.dp))

    Box(
      modifier = Modifier
        .size(8.dp)
        .scale(dot1Scale)
        .clip(CircleShape)
        .background(NeonGreen)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Box(
      modifier = Modifier
        .size(8.dp)
        .scale(dot2Scale)
        .clip(CircleShape)
        .background(NeonGreen)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Box(
      modifier = Modifier
        .size(8.dp)
        .scale(dot3Scale)
        .clip(CircleShape)
        .background(NeonGreen)
    )
  }
}
