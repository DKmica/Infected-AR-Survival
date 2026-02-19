package com.infected.ar

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class NavSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun onboardingToHomeSmoke() {
        composeRule.onNodeWithText("INFECTED AR").assertExists()
        // Skeleton only: extend with deterministic dispatchers/clock for stable nav assertions.
    }
}
