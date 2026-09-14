package com.turovfy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.turovfy.app.network.ApiService
import com.turovfy.app.playback.PlayerController
import com.turovfy.app.ui.screens.*
import com.turovfy.app.ui.theme.TurovFyTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var playerController: PlayerController
    private val apiService = ApiService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerController = PlayerController(this)

        setContent {
            TurovFyTheme {
                val currentTrack by playerController.currentTrack.collectAsState()
                val isPlaying by playerController.isPlaying.collectAsState()
                val currentPosition by playerController.currentPosition.collectAsState()
                val duration by playerController.duration.collectAsState()

                var currentTab by remember { mutableStateOf("catalog") }
                var isFullscreenOpen by remember { mutableStateOf(false) }
                var isKaraokeOpen by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    while (true) {
                        playerController.updateProgress()
                        delay(500)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    when (currentTab) {
                        "catalog" -> CatalogScreen(
                            apiService = apiService,
                            onTrackSelect = { track, list ->
                                playerController.playTrack(track, list)
                            }
                        )
                        "eq" -> EqualizerScreen()
                        "settings" -> SettingsScreen()
                    }

                    // Плавающий док-плеер
                    currentTrack?.let { track ->
                        if (!isFullscreenOpen && !isKaraokeOpen) {
                            PlayerDock(
                                track = track,
                                isPlaying = isPlaying,
                                onTogglePlay = { playerController.togglePlayPause() },
                                onClick = { isFullscreenOpen = true },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 76.dp, start = 16.dp, end = 16.dp)
                            )
                        }
                    }

                    // Нижний навигационный бар
                    NavigationBar(
                        containerColor = Color(0xCC0C0C0E),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        NavigationBarItem(
                            selected = currentTab == "catalog",
                            onClick = { currentTab = "catalog" },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Каталог") },
                            label = { Text("Каталог") }
                        )
                        NavigationBarItem(
                            selected = currentTab == "eq",
                            onClick = { currentTab = "eq" },
                            icon = { Icon(Icons.Default.Equalizer, contentDescription = "Эквалайзер") },
                            label = { Text("EQ") }
                        )
                        NavigationBarItem(
                            selected = currentTab == "settings",
                            onClick = { currentTab = "settings" },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") },
                            label = { Text("Профиль") }
                        )
                    }

                    // Полноэкранный плеер
                    if (isFullscreenOpen && currentTrack != null) {
                        FullscreenPlayer(
                            track = currentTrack!!,
                            isPlaying = isPlaying,
                            currentPosition = currentPosition,
                            duration = duration,
                            onClose = { isFullscreenOpen = false },
                            onTogglePlay = { playerController.togglePlayPause() },
                            onNext = { playerController.nextTrack() },
                            onPrev = { playerController.prevTrack() },
                            onSeek = { playerController.seekTo(it) },
                            onOpenKaraoke = { isKaraokeOpen = true }
                        )
                    }

                    // Экран синхронного текста (Караоке)
                    if (isKaraokeOpen && currentTrack != null) {
                        KaraokeScreen(
                            track = currentTrack!!,
                            currentPositionMs = currentPosition,
                            apiService = apiService,
                            onClose = { isKaraokeOpen = false }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerController.release()
    }
}