package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelRepository
import com.example.data.models.LevelConfig
import com.example.data.models.SectorNavigationUtils
import com.example.ui.theme.ImmersiveBg
import com.example.ui.theme.ImmersiveCaption
import com.example.ui.theme.ImmersiveCard
import com.example.ui.theme.ImmersiveCardBorder
import com.example.ui.theme.ImmersiveDarkPurple
import com.example.ui.theme.ImmersiveLavender
import com.example.ui.theme.ImmersiveMidPurple
import com.example.ui.theme.ImmersiveMuted
import com.example.ui.theme.ImmersiveSoftLilac
import com.example.viewmodel.GameViewModel

@Composable
fun LevelSelectScreen(
    viewModel: GameViewModel,
    onLevelSelected: (levelNumber: Int) -> Unit,
    onBackClick: () -> Unit
) {
    val userGameData by viewModel.userGameData.collectAsState()
    val unlockedLevel = userGameData.unlockedLevel
    val starsMap = userGameData.levelStars

    val (highestUnlockedSector, _) = SectorNavigationUtils.getSectorAndLevel(unlockedLevel)
    var selectedSectorId by remember { mutableIntStateOf(highestUnlockedSector) }

    val totalSectorsToShow = (highestUnlockedSector + 1).coerceAtLeast(3)
    val sectors = (1..totalSectorsToShow).map { LevelRepository.getSectorInfo(it) }

    val currentSectorInfo = LevelRepository.getSectorInfo(selectedSectorId)
    val levelsCount = SectorNavigationUtils.getLevelsCountForSector(selectedSectorId)
    val levelsInSector = (1..levelsCount).map { lvlInSec ->
        val globalLvl = SectorNavigationUtils.getGlobalLevelNumber(selectedSectorId, lvlInSec)
        LevelRepository.getLevel(globalLvl)
    }

    Scaffold(
        containerColor = ImmersiveBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ImmersiveLavender.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(700f, 80f),
                        radius = 800f
                    )
                )
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Immersive UI Header with Round Pill Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ImmersiveCard)
                        .border(1.dp, ImmersiveCardBorder, CircleShape)
                        .clickable { onBackClick() }
                        .testTag("level_select_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ImmersiveSoftLilac,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "SECTOR MATRIX",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Endless Classified Operations • Unlocked: Level $unlockedLevel",
                        color = ImmersiveCaption,
                        fontSize = 11.sp
                    )
                }
            }

            // Sector Pill Selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sectors) { sector ->
                    val isSelected = sector.id == selectedSectorId
                    val isSectorUnlocked = sector.id <= highestUnlockedSector
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ImmersiveMidPurple
                                else if (isSectorUnlocked) ImmersiveCard
                                else Color(0xFF1E1D22)
                            )
                            .border(
                                1.dp,
                                if (isSelected) ImmersiveLavender
                                else if (isSectorUnlocked) ImmersiveCardBorder
                                else Color(0xFF2E2C33),
                                CircleShape
                            )
                            .clickable { selectedSectorId = sector.id }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (!isSectorUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = ImmersiveCaption,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = if (sector.id == 1) "SECTOR 1 (TUTORIAL)" else "SECTOR ${sector.id}",
                                color = if (isSelected) ImmersiveSoftLilac else if (isSectorUnlocked) ImmersiveMuted else ImmersiveCaption,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sector Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sector $selectedSectorId: ${currentSectorInfo.name}".uppercase(),
                        color = ImmersiveLavender,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = currentSectorInfo.subtitle,
                        color = ImmersiveMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Levels Grid in Sector
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(levelsInSector) { level ->
                    LevelGridCard(
                        level = level,
                        isUnlocked = level.levelNumber <= unlockedLevel,
                        stars = starsMap[level.levelNumber] ?: 0,
                        onClick = {
                            if (level.levelNumber <= unlockedLevel) {
                                onLevelSelected(level.levelNumber)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelGridCard(
    level: LevelConfig,
    isUnlocked: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    val isAvailable = isUnlocked

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) ImmersiveCard else Color(0xFF1E1D22)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .border(
                1.dp,
                if (isAvailable) ImmersiveCardBorder else Color(0xFF333038),
                RoundedCornerShape(24.dp)
            )
            .clickable(enabled = isAvailable) { onClick() }
            .testTag("level_card_${level.levelNumber}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!isAvailable) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = ImmersiveCaption,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lvl ${level.levelNumber}",
                        color = ImmersiveCaption,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${level.levelNumber}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = level.title,
                        color = ImmersiveMuted,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Stars
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            val earned = i <= stars
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (earned) ImmersiveLavender else ImmersiveCardBorder,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
