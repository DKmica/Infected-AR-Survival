package com.infected.ar.domain.usecase

import com.infected.ar.data.db.InfectionEntity
import com.infected.ar.data.repository.InfectionRepository

class SaveInfectionUseCase(private val repository: InfectionRepository) {
    suspend operator fun invoke(entity: InfectionEntity) = repository.save(entity)
}
