package com.example.lifelink

import android.app.Application
import androidx.room.Room
import com.example.lifelink.data.local.database.AppDatabase

class ApplicationController : Application() {
    companion object {
        lateinit var instance: ApplicationController
            private set
    }

    lateinit var appDatabase: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        setUpDatabase()
    }

    private fun setUpDatabase() {
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
}