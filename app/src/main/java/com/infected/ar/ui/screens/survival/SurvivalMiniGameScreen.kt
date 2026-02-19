package com.infected.ar.ui.screens.survival

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SurvivalMiniGameScreen(nav: NavController) {
    var hp by remember { mutableIntStateOf(12) }
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(1) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var scale by remember { mutableFloatStateOf(0.2f) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
            scale = (scale + 0.08f).coerceAtMost(1.2f)
        }
    }

    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().background(Color.Black)) {
        Canvas(Modifier.fillMaxSize().clickable {
            if (timeLeft > 0) {
                hp -= 1
                score += 10 * combo
                combo++
                if (hp <= 0) {
                    hp = 12
                    scale = 0.2f
                }
            }
        }) {
            drawCircle(Color.Red.copy(alpha = 0.5f), radius = size.minDimension * scale, center = Offset(size.width / 2, size.height / 2))
        }
        Column(Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("Time: $timeLeft")
            Text("Score: $score")
            Text("Combo: x$combo")
            Text("HP: $hp")
        }
        if (timeLeft == 0) {
            Column(Modifier.align(Alignment.Center).background(Color.Black.copy(alpha = 0.6f)).padding(16.dp)) {
                Text("Run Over. Score $score")
                PrimaryAction("Share Result") {}
                PrimaryAction("Back Home") { nav.navigate(Routes.Home) }
            }
        }
    }
}
