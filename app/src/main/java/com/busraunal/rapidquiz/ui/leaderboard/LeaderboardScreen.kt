package com.busraunal.rapidquiz.ui.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.busraunal.rapidquiz.ui.components.LeaderboardTable
import com.busraunal.rapidquiz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lider Tablosu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Header Banner
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AmberGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Lider Tablosu (Top 10)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "En hızlı düşünen ve en yüksek puanı toplayan yarışmacılar",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Horizontal Category Filter Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.tabs) { tab ->
                    val isSelected = selectedTab == tab
                    val tabColor = if (tab.slug == "global") AmberGold else CyanLight

                    Surface(
                        onClick = { viewModel.selectTab(tab) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) tabColor else SurfaceDark,
                        border = BorderStroke(1.dp, if (isSelected) tabColor else CardBorder),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.title,
                                color = if (isSelected) Color(0xFF030712) else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Table Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (val state = uiState) {
                    is LeaderboardUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = CyanLight)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Skorlar getiriliyor...", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                    is LeaderboardUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Yüklenemedi", color = WrongRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = state.message, color = TextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.loadLeaderboard(selectedTab.slug) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF030712))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Yenile", color = Color(0xFF030712), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    is LeaderboardUiState.Success -> {
                        LeaderboardTable(
                            scores = state.scores
                        )
                    }
                }
            }

            // 4. Quick Action Button (Hemen Bir Kategori Seç ve Yarış)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
            ) {
                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF030712))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hemen Bir Kategori Seç ve Yarış",
                    color = Color(0xFF030712),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
