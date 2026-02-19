package com.infected.ar.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InfectionEntity::class], version = 1, exportSchema = false)
abstract class InfectedDatabase : RoomDatabase() {
    abstract fun infectionDao(): InfectionDao

    companion object {
        fun create(context: Context): InfectedDatabase =
            Room.databaseBuilder(context, InfectedDatabase::class.java, "infected.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
