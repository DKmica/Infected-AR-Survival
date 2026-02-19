package com.infected.ar.ui.screens.legal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.infected.ar.ui.components.GlitchHeader

@Composable
fun LegalScreen(title: String) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GlitchHeader(title)
        Text("Local placeholder legal text for $title. Replace with production legal copy.")
    }
}
