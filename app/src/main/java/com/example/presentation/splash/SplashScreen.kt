package com.example.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.presentation.common.CyberBadge
import com.example.ui.theme.BloodRed
import com.example.ui.theme.BloodRedGlow
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberMonoType
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onNavigateToDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Parallax / Translation offset animations
  val textParallax = remember { Animatable(50f) }
  val alphaAnim = remember { Animatable(0f) }
  val scaleAnim = remember { Animatable(0.7f) }

  val infiniteTransition = rememberInfiniteTransition(label = "SplashPulsing")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "CorePulse"
  )

  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "RingRotate"
  )

  LaunchedEffect(Unit) {
    alphaAnim.animateTo(1f, animationSpec = tween(800))
    scaleAnim.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    textParallax.animateTo(0f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
    delay(2000)
    onNavigateToDashboard()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Cyber Matrix Background Glow
    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawBehind {
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(NeonGreenGlow.copy(alpha = 0.2f), Color.Transparent),
              center = Offset(size.width / 2, size.height / 3),
              radius = size.width * 0.7f
            )
          )
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(BloodRedGlow.copy(alpha = 0.15f), Color.Transparent),
              center = Offset(size.width / 2, size.height * 0.75f),
              radius = size.width * 0.6f
            )
          )
        }
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .padding(24.dp)
        .graphicsLayer {
          alpha = alphaAnim.value
          scaleX = scaleAnim.value
          scaleY = scaleAnim.value
        }
    ) {
      // 3D Cyber Core Orb
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(160.dp)
      ) {
        // Outer rotating cyber ring
        Box(
          modifier = Modifier
            .size(150.dp)
            .rotate(rotationAngle)
            .border(
              width = 2.dp,
              brush = Brush.sweepGradient(
                listOf(NeonGreen, BloodRed, Color.Transparent, NeonGreen)
              ),
              shape = CircleShape
            )
        )

        // Pulsing Core
        Box(
          modifier = Modifier
            .size(110.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(
              brush = Brush.radialGradient(
                listOf(BloodRed, CyberBlack)
              )
            )
            .border(2.dp, NeonGreen, CircleShape)
            .shadow(24.dp, CircleShape, spotColor = NeonGreenGlow),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_nexus_logo_1791153709000),
            contentDescription = "Nexus AI Core Logo",
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
          )
        }
      }

      Spacer(modifier = Modifier.height(36.dp))

      // Parallax 3D Welcome Text
      Box(
        modifier = Modifier.offset {
          IntOffset(0, textParallax.value.toInt())
        },
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "WELCOME TO",
            style = CyberMonoType.BadgeText,
            color = NeonGreen,
            fontSize = 13.sp,
            letterSpacing = 4.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "NEXUS LOCAL AI",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            letterSpacing = 1.sp,
            color = TextPrimary
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Autonomous On-Device GGUF Intelligence",
            fontSize = 13.sp,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      CyberBadge(
        text = "INITIALIZING HARDWARE KERNEL...",
        color = NeonGreen
      )
    }
  }
}
