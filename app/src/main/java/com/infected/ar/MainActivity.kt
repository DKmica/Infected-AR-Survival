package com.infected.ar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.infected.ar.ui.navigation.InfectedNavHost
import com.infected.ar.ui.theme.InfectedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfectedTheme {
                InfectedNavHost()
            }
        }
    }
}
