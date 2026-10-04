package com.example.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedDark
import com.example.ui.theme.BloodRedGlow
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberElevatedSurface
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGradients
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.TechShapes
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * 3D Glassmorphic Card Container with glowing perimeter and deep depth
 */
@Composable
fun CyberGlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(16.dp),
  borderColor: Color = CyberGlassBorder,
  backgroundColor: Color = Color(0x33111827),
  elevation: Dp = 8.dp,
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .shadow(elevation = elevation, shape = shape, spotColor = borderColor)
      .clip(shape)
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            backgroundColor,
            CyberElevatedSurface.copy(alpha = 0.6f)
          )
        )
      )
      .border(1.dp, borderColor, shape)
      .padding(16.dp),
    content = content
  )
}

/**
 * 3D Holographic Cyber Button with active tactile feedback and gradient fill
 */
@Composable
fun CyberButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  isSecondary: Boolean = false,
  testTag: String = "cyber_button"
) {
  val brush = if (isSecondary) {
    CyberGradients.NeonGreenGradient
  } else {
    CyberGradients.BloodRedGradient
  }
  val textColor = if (isSecondary) CyberBlack else TextPrimary
  val glowColor = if (isSecondary) NeonGreenGlow else BloodRedGlow

  Box(
    modifier = modifier
      .testTag(testTag)
      .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), spotColor = glowColor)
      .clip(RoundedCornerShape(12.dp))
      .background(brush = brush)
      .border(1.dp, Color(0x4DFFFFFF), RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color.White),
        onClick = onClick
      )
      .padding(horizontal = 20.dp, vertical = 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = textColor,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        color = textColor,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
      )
    }
  }
}

/**
 * Cyber Badge with tech chamfered border for model tags, status & memory indicators
 */
@Composable
fun CyberBadge(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = NeonGreen,
  backgroundColor: Color = color.copy(alpha = 0.15f)
) {
  Box(
    modifier = modifier
      .clip(TechShapes.ChamferedBadge)
      .background(backgroundColor)
      .border(1.dp, color.copy(alpha = 0.4f), TechShapes.ChamferedBadge)
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text.uppercase(),
      style = CyberMonoType.BadgeText,
      color = color,
      fontSize = 10.sp
    )
  }
}

/**
 * 3D Tech Hardware Circular Gauge (Used for RAM, CPU, Storage)
 */
@Composable
fun CyberCircularGauge(
  title: String,
  valueText: String,
  progress: Float, // 0.0f to 1.0f
  modifier: Modifier = Modifier,
  gaugeColor: Color = NeonGreen,
  subtitle: String = ""
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(96.dp)
    ) {
      // Glow backplate
      CircularProgressIndicator(
        progress = { 1f },
        modifier = Modifier.size(86.dp),
        color = gaugeColor.copy(alpha = 0.1f),
        strokeWidth = 8.dp,
        strokeCap = StrokeCap.Round
      )
      // Active meter
      CircularProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = Modifier.size(86.dp),
        color = gaugeColor,
        strokeWidth = 8.dp,
        strokeCap = StrokeCap.Round
      )

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = valueText,
          style = CyberMonoType.StatNumber,
          fontSize = 15.sp,
          color = gaugeColor
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      color = TextPrimary,
      fontSize = 13.sp
    )
    if (subtitle.isNotEmpty()) {
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        fontSize = 11.sp
      )
    }
  }
}

/**
 * Cyber Animated Scanning Radar for Junk Cleaner
 */
@Composable
fun CyberScanningRadar(
  isScanning: Boolean,
  modifier: Modifier = Modifier,
  scannerColor: Color = NeonGreen
) {
  val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "SweepRotation"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "PulseScale"
  )

  Box(
    modifier = modifier
      .size(160.dp)
      .clip(RoundedCornerShape(80.dp))
      .background(Color(0xFF0A0F1D))
      .border(2.dp, scannerColor.copy(alpha = 0.4f), RoundedCornerShape(80.dp))
      .drawBehind {
        val center = Offset(size.width / 2, size.height / 2)
        // Radar concentric circles
        drawCircle(
          color = scannerColor.copy(alpha = 0.15f),
          radius = size.width * 0.25f,
          center = center
        )
        drawCircle(
          color = scannerColor.copy(alpha = 0.25f),
          radius = size.width * 0.40f,
          center = center
        )
        // Grid crosshair
        drawLine(
          color = scannerColor.copy(alpha = 0.2f),
          start = Offset(0f, center.y),
          end = Offset(size.width, center.y),
          strokeWidth = 1.dp.toPx()
        )
        drawLine(
          color = scannerColor.copy(alpha = 0.2f),
          start = Offset(center.x, 0f),
          end = Offset(center.x, size.height),
          strokeWidth = 1.dp.toPx()
        )
      },
    contentAlignment = Alignment.Center
  ) {
    if (isScanning) {
      Box(
        modifier = Modifier
          .size(60.dp)
          .scale(pulseScale)
          .clip(RoundedCornerShape(30.dp))
          .background(scannerColor.copy(alpha = 0.2f))
      )
    }

    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(if (isScanning) BloodRed else scannerColor)
        .shadow(12.dp, RoundedCornerShape(12.dp), spotColor = if (isScanning) BloodRed else scannerColor)
    )
  }
}
