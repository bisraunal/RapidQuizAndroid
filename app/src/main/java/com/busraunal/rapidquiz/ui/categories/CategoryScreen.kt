package com.busraunal.rapidquiz.ui.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.busraunal.rapidquiz.ui.components.CategoryCard
import com.busraunal.rapidquiz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onCategoryClick: (categorySlug: String) -> Unit,
    onLeaderboardClick: () -> Unit,
    viewModel: CategoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = CyanLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rapid Quiz",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLeaderboardClick) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Lider Tablosu",
                            tint = AmberGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is CategoryUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = CyanLight)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Kategoriler yükleniyor...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                is CategoryUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bağlantı Hatası",
                            color = WrongRed,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadCategories() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = BackgroundDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tekrar Dene", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is CategoryUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp)
                    ) {
                        // 1. Hero Banner (Web HomeView ile aynı)
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Sparkles pill
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = CyanPrimary.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, CyanLight.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = CyanLight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Hızlı Düşün, Anlık Cevapla!",
                                            color = CyanLight,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Main Title
                                Text(
                                    text = buildAnnotatedString {
                                        append("5 Saniyede Bil,\n")
                                        withStyle(style = SpanStyle(color = CyanLight)) {
                                            append("Zirveye Yerleş!")
                                        }
                                    },
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 34.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Subtitle
                                Text(
                                    text = "Her soru için tam 5 saniye süren var. Doğru cevabı ne kadar hızlı verirsen o kadar yüksek bonus kazanırsın!",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Feature Pills
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    FeaturePill(icon = Icons.Default.Timer, text = "5s Kısıtı", tint = CyanLight)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FeaturePill(icon = Icons.Default.Bolt, text = "Hız Bonusu", tint = AmberGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FeaturePill(icon = Icons.Default.EmojiEvents, text = "Top 10", tint = PurpleAccent)
                                }
                            }
                        }

                        // 2. Section Header
                        item {
                            Column {
                                Text(
                                    text = "Kategoriler",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Yarışmak istediğin kategoriyi seçerek hemen başla",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // 3. Category Cards
                        items(state.categories, key = { it.id }) { category ->
                            CategoryCard(
                                category = category,
                                onClick = { onCategoryClick(category.slug) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: ImageVector,
    text: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
