package com.example.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CyberShapes = Shapes(
  small = RoundedCornerShape(8.dp),
  medium = RoundedCornerShape(14.dp),
  large = RoundedCornerShape(20.dp),
  extraLarge = RoundedCornerShape(28.dp)
)

// Cyberpunk Tech Chamfered / Hexagonal Shapes
object TechShapes {
  val ChamferedCard = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
  val ChamferedBadge = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
  val TechTerminal = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
}
