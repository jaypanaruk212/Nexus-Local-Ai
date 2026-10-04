package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Cyberpunk / Tech Palette - Core Foundations
val CyberBlack = Color(0xFF07090E)         // Deepest background
val CyberDarkSurface = Color(0xFF0F141F)   // Primary card surface
val CyberElevatedSurface = Color(0xFF161E2E) // Elevated card surface
val CyberGlassBorder = Color(0x3300FF66)    // Neon green glowing glass border
val CyberRedGlassBorder = Color(0x33FF1744)  // Blood red glowing glass border

// Blood Red Spectrum (Power, Warnings, Critical Stats, Combat Aesthetics)
val BloodRed = Color(0xFFFF1744)
val BloodRedDark = Color(0xFF99001A)
val BloodRedGlow = Color(0x66FF1744)
val BloodRedCrimson = Color(0xFFD50000)

// Neon Green Spectrum (Active AI, Success, Turbo Matrix, High-Tech Core)
val NeonGreen = Color(0xFF00FF66)
val NeonGreenDark = Color(0xFF00993D)
val NeonGreenGlow = Color(0x6600FF66)
val NeonGreenLight = Color(0xFF69F0AE)

// Cyber Accents & Neutrals
val CyberCyan = Color(0xFF00E5FF)
val CyberYellow = Color(0xFFFFD600)
val CyberPurple = Color(0xFF7C4DFF)

// Text & Monospace Tones
val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)
val TextDisabled = Color(0xFF475569)

// 3D Glassmorphism & Cyber Gradients
object CyberGradients {
  val BloodRedGradient = Brush.linearGradient(
    colors = listOf(BloodRed, BloodRedDark)
  )

  val NeonGreenGradient = Brush.linearGradient(
    colors = listOf(NeonGreenLight, NeonGreenDark)
  )

  val CyberCoreGradient = Brush.linearGradient(
    colors = listOf(BloodRed, CyberBlack, NeonGreen)
  )

  val CardGlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0x331E293B), Color(0x1A0F172A))
  )

  val HeroPanelGradient = Brush.radialGradient(
    colors = listOf(Color(0x3300FF66), Color(0x0507090E))
  )

  val RedPulseGradient = Brush.radialGradient(
    colors = listOf(Color(0x44FF1744), Color(0x0007090E))
  )
}
