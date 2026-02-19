package com.infected.ar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.infected.ar.data.db.InfectedDatabase
import com.infected.ar.data.db.InfectionEntity
import com.infected.ar.data.repository.InfectionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

class InfectionRepositoryTest {
    private lateinit var db: InfectedDatabase
    private lateinit var repository: InfectionRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, InfectedDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = InfectionRepository(db.infectionDao())
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun saveAndReadInfection() = runBlocking {
        val infection = InfectionEntity(UUID.randomUUID().toString(), 1L, "UPLOAD", "CLASSIC", 88, "{}", "thumb", null, null, null)
        repository.save(infection)
        val results = repository.observeInfections().first()
        assertEquals(1, results.size)
        assertEquals("CLASSIC", results.first().style)
    }
}
