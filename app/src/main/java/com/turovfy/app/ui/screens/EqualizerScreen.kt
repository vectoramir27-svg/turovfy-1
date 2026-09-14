package com.turovfy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turovfy.app.ui.components.GlassCard

@Composable
fun EqualizerScreen() {
    var bassLevel by remember { mutableFloatStateOf(0f) }
    var midLevel by remember { mutableFloatStateOf(0f) }
    var trebleLevel by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("DSP Equalizer", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text("Параметрические фильтры звукового тракта", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { bassLevel = 0f; midLevel = 0f; trebleLevel = 0f },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Flat", color = Color.White)
            }
            Button(
                onClick = { bassLevel = 6f; midLevel = 1f; trebleLevel = -1f },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Bass Boost", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EqualizerBand(name = "Низкие (60 Hz)", value = bassLevel) { bassLevel = it }
                EqualizerBand(name = "Средние (1 kHz)", value = midLevel) { midLevel = it }
                EqualizerBand(name = "Высокие (12 kHz)", value = trebleLevel) { trebleLevel = it }
            }
        }
    }
}

@Composable
fun EqualizerBand(name: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, color = Color.White, fontSize = 14.sp)
            Text("${value.toInt()} dB", color = Color.Gray, fontSize = 13.sp)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -10f..10f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color(0x33FFFFFF)
            )
        )
    }
}