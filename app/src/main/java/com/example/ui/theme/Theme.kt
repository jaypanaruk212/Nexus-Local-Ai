package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Cyberpunk Dark Color Scheme (Primary Theme)
private val CyberDarkColorScheme = darkColorScheme(
  primary = BloodRed,
  onPrimary = Color.White,
  primaryContainer = BloodRedDark,
  onPrimaryContainer = Color.White,

  secondary = NeonGreen,
  onSecondary = CyberBlack,
  secondaryContainer = NeonGreenDark,
  onSecondaryContainer = Color.White,

  tertiary = CyberCyan,
  onTertiary = CyberBlack,
  tertiaryContainer = Color(0xFF004D5A),
  onTertiaryContainer = Color.White,

  background = CyberBlack,
  onBackground = TextPrimary,

  surface = CyberDarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberElevatedSurface,
  onSurfaceVariant = TextSecondary,

  error = BloodRed,
  onError = Color.White,
  errorContainer = BloodRedDark,
  onErrorContainer = Color.White,

  outline = Color(0x3364748B),
  outlineVariant = Color(0x1A00FF66)
)

// Cyberpunk Light Mode (High-contrast military sci-fi white/gray)
private val CyberLightColorScheme = lightColorScheme(
  primary = BloodRedCrimson,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFFCDD2),
  onPrimaryContainer = BloodRedDark,

  secondary = NeonGreenDark,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFB9F6CA),
  onSecondaryContainer = Color(0xFF003314),

  tertiary = Color(0xFF00838F),
  onTertiary = Color.White,

  background = Color(0xFFF1F5F9),
  onBackground = Color(0xFF0F172A),

  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFE2E8F0),
  onSurfaceVariant = Color(0xFF475569),

  outline = Color(0xFFCBD5E1)
)

@Composable
fun NexusTheme(
  darkTheme: Boolean = true, // Default to true for Cyberpunk aesthetics
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) CyberDarkColorScheme else CyberLightColorScheme
  val view = LocalView.current

  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window ?: return@SideEffect
      window.statusBarColor = CyberBlack.toArgb()
      window.navigationBarColor = CyberBlack.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
      WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = CyberTypography,
    shapes = CyberShapes,
    content = content
  )
}

// Backward-compatible alias for template references
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  NexusTheme(darkTheme = darkTheme, content = content)
}

// ---------------------------------------------------------------------------
// 3D Glassmorphism & Cyber UI Modifier Extensions
// ---------------------------------------------------------------------------

/**
 * Applies a 3D Glassmorphism panel effect with glowing tech borders
 * and subtle linear gradients.
 */
fun Modifier.cyberGlassPanel(
  shape: Shape = RoundedCornerShape(16.dp),
  borderColor: Color = CyberGlassBorder,
  backgroundColor: Color = Color(0x33111827),
  borderWidth: Dp = 1.dp
): Modifier = this
  .clip(shape)
  .background(backgroundColor)
  .border(borderWidth, borderColor, shape)

/**
 * Applies a 3D Neumorphism / Cyberpunk elevated shadow with glowing halo
 */
fun Modifier.cyber3DElevation(
  glowColor: Color = NeonGreenGlow,
  elevation: Dp = 6.dp,
  shape: Shape = RoundedCornerShape(16.dp)
): Modifier = this
  .shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = glowColor,
    spotColor = glowColor
  )

/**
 * 3D Cyber Chamfered Button Effect
 */
fun Modifier.cyberButton3D(
  brush: Brush = CyberGradients.BloodRedGradient,
  shape: Shape = RoundedCornerShape(12.dp),
  borderColor: Color = Color(0x66FFFFFF)
): Modifier = this
  .shadow(elevation = 8.dp, shape = shape, spotColor = BloodRed)
  .background(brush = brush, shape = shape)
  .border(width = 1.dp, color = borderColor, shape = shape)
