package com.busraunal.rapidquiz.ui.theme

import androidx.compose.ui.graphics.Color

// Web Dark & Glass Theme Palette (Tailwind Slate / Dark)
val BackgroundDark = Color(0xFF030712) // slate-950
val SurfaceDark = Color(0xFF0F172A)    // slate-900
val CardDark = Color(0xFF1E293B)       // slate-800
val CardBorder = Color(0xFF334155)     // slate-700
val GlassBorder = Color(0x3338BDF8)    // cyan-500/20

// Brand Gradient & Accent Colors
val CyanPrimary = Color(0xFF06B6D4)    // cyan-500
val CyanLight = Color(0xFF22D3EE)      // cyan-400
val SkyAccent = Color(0xFF38BDF8)      // sky-400
val IndigoAccent = Color(0xFF6366F1)   // indigo-500
val PurpleAccent = Color(0xFFA855F7)   // purple-500
val AmberGold = Color(0xFFF59E0B)      // amber-500
val EmeraldGreen = Color(0xFF10B981)   // emerald-500
val RoseRed = Color(0xFFF43F5E)        // rose-500
val BlueAccent = Color(0xFF3B82F6)     // blue-500
val OrangeAccent = Color(0xFFF97316)   // orange-500

// Functional Colors
val CorrectGreen = Color(0xFF10B981)
val WrongRed = Color(0xFFF43F5E)
val TimerGreen = Color(0xFF10B981)
val TimerYellow = Color(0xFFF59E0B)
val TimerRed = Color(0xFFEF4444)

// Text Colors
val TextWhite = Color(0xFFF8FAFC)      // slate-50
val TextPrimary = Color(0xFFF1F5F9)    // slate-100
val TextSecondary = Color(0xFF94A3B8)  // slate-400
val TextMuted = Color(0xFF64748B)      // slate-500

// Helper to get category color
fun getCategoryThemeColor(theme: String): Color {
    return when (theme.lowercase()) {
        "cyan" -> CyanLight
        "purple" -> PurpleAccent
        "amber", "yellow" -> AmberGold
        "emerald" -> EmeraldGreen
        "rose" -> RoseRed
        "blue" -> BlueAccent
        "orange" -> OrangeAccent
        else -> CyanLight
    }
}