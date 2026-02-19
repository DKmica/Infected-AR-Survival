package com.infected.ar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ZombieSliders(
    val rot: Float = 0.5f,
    val veins: Float = 0.5f,
    val blood: Float = 0.5f,
    val bruises: Float = 0.5f,
    val eyeGlow: Float = 0.7f
)
