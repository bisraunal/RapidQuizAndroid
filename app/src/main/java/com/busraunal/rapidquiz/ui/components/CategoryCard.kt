package com.busraunal.rapidquiz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.busraunal.rapidquiz.data.model.CategoryDto
import com.busraunal.rapidquiz.ui.theme.*

@Composable
fun CategoryCard(
    category: CategoryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColor = getCategoryThemeColor(category.colorTheme)
    val iconVector = getCategoryIcon(category.icon)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        ),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Icon Container + Question Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeColor.copy(alpha = 0.12f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = category.name,
                        tint = themeColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CardDark,
                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "${category.questionCount} Soru",
                        color = themeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Name
            Text(
                text = category.name,
                color = TextWhite,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle meta
            Text(
                text = "⏱ Soru başına 5 saniye • ⚡ Refleks Testi",
                color = TextSecondary,
                fontSize = 12.sp
            )

            // Music info tag
            if (!category.musicTitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardDark.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Müzik",
                        tint = themeColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = category.musicTitle,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: "Hemen Başla ➔"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hemen Başla",
                    color = themeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(CardDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "code2", "code" -> Icons.Default.Code
        "bot", "smart_toy" -> Icons.Default.SmartToy
        "cpu", "memory" -> Icons.Default.Memory
        "globe", "public" -> Icons.Default.Public
        "atom", "science" -> Icons.Default.Science
        "trophy", "sports_soccer" -> Icons.Default.EmojiEvents
        "chefhat", "restaurant", "utensilscrossed" -> Icons.Default.Restaurant
        else -> Icons.Default.Quiz
    }
}
