package com.infected.ar.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "infections")
data class InfectionEntity(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val sourceType: String,
    val style: String,
    val intensity: Int,
    val slidersJson: String,
    val thumbnailPath: String,
    val beforeImagePath: String?,
    val afterImagePath: String?,
    val revealVideoPath: String?
)
