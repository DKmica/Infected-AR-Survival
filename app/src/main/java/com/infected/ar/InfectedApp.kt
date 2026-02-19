package com.infected.ar

import android.app.Application
import com.infected.ar.data.db.InfectedDatabase
import com.infected.ar.data.datastore.SettingsDataStore
import com.infected.ar.data.repository.InfectionRepository

class InfectedApp : Application() {
    val database by lazy { InfectedDatabase.create(this) }
    val settingsDataStore by lazy { SettingsDataStore(this) }
    val infectionRepository by lazy { InfectionRepository(database.infectionDao()) }
}
