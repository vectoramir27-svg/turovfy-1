package com.turovfy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.turovfy.app.model.Track
import com.turovfy.app.network.ApiService
import com.turovfy.app.ui.components.GlassCard
import kotlinx.coroutines.launch

@Composable
fun CatalogScreen(
    apiService: ApiService,
    onTrackSelect: (Track, List<Track>) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = apiService.searchTracks("Тренды Музыка 2026")
            tracks = response.results
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "TUROVFY",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поисковая строка
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (searchQuery.isEmpty()) {
                        Text("Поиск трека или исполнителя", color = Color.Gray, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            if (query.isNotBlank()) {
                                coroutineScope.launch {
                                    try {
                                        tracks = apiService.searchTracks(query).results
                                    } catch (_: Exception) {}
                                }
                            }
                        },
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        cursorBrush = SolidColor(Color.White),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Моя волна
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            cornerRadius = 24
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Моя\nволна", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 30.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Персональный поток", fontSize = 12.sp, color = Color(0xFFFFD60A))
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val wave = apiService.getWave()
                                if (wave.results.isNotEmpty()) {
                                    onTrackSelect(wave.results.first(), wave.results)
                                }
                            } catch (_: Exception) {}
                        }
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            if (searchQuery.isBlank()) "Рекомендации" else "Результаты поиска",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tracks) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onTrackSelect(track, tracks) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = track.cover,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1F1F1F))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(track.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(track.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(track.duration, color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
