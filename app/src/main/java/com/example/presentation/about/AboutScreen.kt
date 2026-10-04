package com.example.presentation.about

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.common.CyberBadge
import com.example.presentation.common.CyberGlassCard
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.BloodRedGlow
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberElevatedSurface
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGradients
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.CyberRedGlassBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonGreenLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AboutScreen(
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "HoloGlow")
  val borderGlowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "BorderPulse"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .padding(horizontal = 16.dp)
      .testTag("about_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item { Spacer(modifier = Modifier.height(8.dp)) }

    // Header
    item {
      Column {
        Text(
          text = "CORE MANIFESTO & IDENTITY",
          style = CyberMonoType.BadgeText,
          color = NeonGreen,
          fontSize = 11.sp
        )
        Text(
          text = "About Nexus Local AI",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      }
    }

    // -------------------------------------------------------------
    // 3D PREMIUM DEVELOPER CARD (JAY PANARUK)
    // -------------------------------------------------------------
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(24.dp),
            spotColor = BloodRedGlow,
            ambientColor = NeonGreenGlow
          )
          .clip(RoundedCornerShape(24.dp))
          .background(
            brush = Brush.linearGradient(
              colors = listOf(
                Color(0xFF1E0A10),
                CyberDarkSurface,
                Color(0xFF0A1E14)
              )
            )
          )
          .border(
            width = 2.dp,
            brush = Brush.sweepGradient(
              listOf(
                BloodRed.copy(alpha = borderGlowAlpha),
                NeonGreen.copy(alpha = borderGlowAlpha),
                BloodRed.copy(alpha = borderGlowAlpha)
              )
            ),
            shape = RoundedCornerShape(24.dp)
          )
          .padding(20.dp)
          .testTag("developer_card_3d")
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          // Top Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            CyberBadge(text = "LEAD ARCHITECT", color = BloodRed)
            CyberBadge(text = "100% UNLOCKED", color = NeonGreen)
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 3D Developer Avatar Node
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(
                brush = Brush.radialGradient(
                  listOf(BloodRed, CyberBlack)
                )
              )
              .border(2.dp, NeonGreen, CircleShape)
              .shadow(16.dp, CircleShape, spotColor = BloodRedGlow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Terminal,
              contentDescription = null,
              tint = NeonGreen,
              modifier = Modifier.size(42.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Nama Besar Developer
          Text(
            text = "JAY PANARUK",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            letterSpacing = 2.sp,
            color = TextPrimary
          )

          Text(
            text = "Fullstack Android & Local AI Systems Engineer",
            color = NeonGreenLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Manifesto Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x6607090E))
              .border(1.dp, Color(0x33FF1744), RoundedCornerShape(12.dp))
              .padding(16.dp)
          ) {
            Text(
              text = "\"saya membuat aplikasi ini semata mata untuk kenyamanan kita bersama, sebab banyak aplikasi sejenis ini yang saya temui fitur fitur bagus nya terkunci alias premium harus berbayar, jadi di sini saya akan sampaikan\"",
              color = Color(0xFFE2E8F0),
              fontSize = 13.sp,
              lineHeight = 20.sp,
              fontStyle = FontStyle.Italic,
              textAlign = TextAlign.Center
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Teks Besar: "SAYA AKAN LAWAN"
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(CyberGradients.BloodRedGradient)
              .padding(horizontal = 24.dp, vertical = 10.dp)
              .shadow(12.dp, RoundedCornerShape(8.dp), spotColor = BloodRedGlow)
          ) {
            Text(
              text = "SAYA AKAN LAWAN",
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              letterSpacing = 3.sp,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Footer Kecil: "by ngab owi"
          Text(
            text = "by ngab owi",
            style = CyberMonoType.BadgeText,
            color = TextMuted,
            fontSize = 11.sp,
            letterSpacing = 1.sp
          )
        }
      }
    }

    // -------------------------------------------------------------
    // VISI, MISI, & KEUNGGULAN APLIKASI
    // -------------------------------------------------------------
    item {
      CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("NEXUS PHILOSOPHY & PILLARS", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          PillarRow(
            icon = Icons.Default.Lock,
            title = "100% Offline & Zero Telemetry",
            description = "Inferensi model AI GGUF sepenuhnya berjalan di CPU/NPU lokal perangkat. Tidak ada data chat atau prompt yang dikirim ke cloud."
          )

          Spacer(modifier = Modifier.height(12.dp))

          PillarRow(
            icon = Icons.Default.Favorite,
            title = "Gratis Selamanya Tanpa Kunci",
            description = "Semua fitur termasuk model hub, grounding search, cleaner, dan unmetered context tokens tersedia bebas tanpa paywall."
          )

          Spacer(modifier = Modifier.height(12.dp))

          PillarRow(
            icon = Icons.Default.Speed,
            title = "Hardware Acceleration",
            description = "Dukungan optimasi quant 4-bit (Q4_K_M) yang menghemat RAM hingga 60% dengan eksekusi multi-core threading."
          )
        }
      }
    }

    // Architecture & Tech Stack Card
    item {
      CyberGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Code, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("STACK ARCHITECTURE", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "• Kotlin 2.2 + Jetpack Compose Material 3\n" +
              "• MVVM + Clean Architecture + Repository Pattern\n" +
              "• Room Database for Persistence\n" +
              "• Retrofit 2 + OkHttp for HuggingFace Hub\n" +
              "• Foreground Services & WorkManager for Tasks\n" +
              "• Automated GitHub Actions Release Pipeline",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
          )
        }
      }
    }

    item { Spacer(modifier = Modifier.height(16.dp)) }
  }
}

@Composable
private fun PillarRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  description: String
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0x3300FF66)),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = description, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
    }
  }
}
