package com.infected.ar.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InfectionDao {
    @Query("SELECT * FROM infections ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<InfectionEntity>>

    @Query("SELECT * FROM infections WHERE id = :id")
    suspend fun findById(id: String): InfectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: InfectionEntity)

    @Delete
    suspend fun delete(item: InfectionEntity)
}
