package com.infected.ar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class NavSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun onboardingToHomeSmoke() {
        composeRule.onNodeWithText("INFECTED AR").assertIsDisplayed()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasText("Welcome to INFECTED AR")).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Start Infecting").performClick()

        composeRule.onNodeWithText("Live Infect").assertIsDisplayed()
        composeRule.onNodeWithText("Upload Photo Infect").assertIsDisplayed()
    }
}
