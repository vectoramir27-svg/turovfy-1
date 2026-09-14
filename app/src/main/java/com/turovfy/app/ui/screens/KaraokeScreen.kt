package com.turovfy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turovfy.app.model.LyricsLine
import com.turovfy.app.model.Track
import com.turovfy.app.network.ApiService
import kotlinx.coroutines.launch

@Composable
fun KaraokeScreen(
    track: Track,
    currentPositionMs: Long,
    apiService: ApiService,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var lines by remember { mutableStateOf<List<LyricsLine>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    LaunchedEffect(track.id) {
        isLoading = true
        try {
            val response = apiService.getLyrics(track.title, track.artist)
            lines = parseLyrics(response.lyrics)
        } catch (e: Exception) {
            lines = listOf(LyricsLine(0f, "Текст песни недоступен"))
        } finally {
            isLoading = false
        }
    }

    val currentSeconds = currentPositionMs / 1000f
    val activeIndex = lines.indexOfLast { currentSeconds >= it.timeSeconds }.coerceAtLeast(0)

    LaunchedEffect(activeIndex) {
        if (lines.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem((activeIndex - 2).coerceAtLeast(0))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141210))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(track.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Text(track.artist, color = Color.Gray, fontSize = 12.sp)
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Color.White)
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Загрузка текста...", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(lines) { index, line ->
                        val isActive = index == activeIndex
                        Text(
                            text = line.text,
                            fontSize = if (isActive) 28.sp else 22.sp,
                            fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                            color = if (isActive) Color.White else Color(0x40FFFFFF),
                            lineHeight = 36.sp
                        )
                    }
                }
            }
        }
    }
}

private fun parseLyrics(lrcContent: String): List<LyricsLine> {
    val list = mutableListOf<LyricsLine>()
    val timeRegex = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})\]""")

    lrcContent.lines().forEach { line ->
        val match = timeRegex.find(line)
        if (match != null) {
            val min = match.groupValues[1].toInt()
            val sec = match.groupValues[2].toInt()
            val totalSeconds = (min * 60 + sec).toFloat()
            val text = line.replace(timeRegex, "").trim()
            if (text.isNotBlank()) {
                list.add(LyricsLine(totalSeconds, text))
            }
        }
    }
    if (list.isEmpty()) {
        return lrcContent.lines().filter { it.isNotBlank() }.mapIndexed { i, s -> LyricsLine((i * 4).toFloat(), s) }
    }
    return list
}