package com.infected.ar.data.repository

import com.infected.ar.data.db.InfectionDao
import com.infected.ar.data.db.InfectionEntity
import kotlinx.coroutines.flow.Flow

class InfectionRepository(private val dao: InfectionDao) {
    fun observeInfections(): Flow<List<InfectionEntity>> = dao.observeAll()
    suspend fun save(infectionEntity: InfectionEntity) = dao.upsert(infectionEntity)
    suspend fun find(id: String): InfectionEntity? = dao.findById(id)
    suspend fun delete(infectionEntity: InfectionEntity) = dao.delete(infectionEntity)
}
