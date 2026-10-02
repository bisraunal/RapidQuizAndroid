package com.busraunal.rapidquiz.ui.theme

import androidx.compose.ui.graphics.Color

// Core Theme Colors
val BackgroundDark = Color(0xFF0B0F19)
val SurfaceDark = Color(0xFF111827)
val CardDark = Color(0xFF1F2937)
val CardBorder = Color(0xFF374151)

// Neon Accent Colors
val NeonCyan = Color(0xFF00F0FF)
val NeonPurple = Color(0xFFA855F7)
val NeonPink = Color(0xFFEC4899)
val NeonAmber = Color(0xFFF59E0B)
val NeonEmerald = Color(0xFF10B981)
val NeonRose = Color(0xFFF43F5E)
val NeonBlue = Color(0xFF3B82F6)
val NeonOrange = Color(0xFFF97316)

// Functional Colors
val CorrectGreen = Color(0xFF10B981)
val WrongRed = Color(0xFFEF4444)
val TimerGreen = Color(0xFF10B981)
val TimerYellow = Color(0xFFF59E0B)
val TimerRed = Color(0xFFEF4444)

// Text Colors
val TextPrimary = Color(0xFFF9FAFB)
val TextSecondary = Color(0xFF9CA3AF)
val TextMuted = Color(0xFF6B7280)

// Helper to get category color
fun getCategoryThemeColor(theme: String): Color {
    return when (theme.lowercase()) {
        "cyan" -> NeonCyan
        "purple" -> NeonPurple
        "amber" -> NeonAmber
        "emerald" -> NeonEmerald
        "rose" -> NeonRose
        "blue" -> NeonBlue
        "orange" -> NeonOrange
        else -> NeonCyan
    }
}