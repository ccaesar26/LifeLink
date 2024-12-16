package com.example.lifelink.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lifelink.data.local.dao.AppointmentDao
import com.example.lifelink.data.local.dao.ClinicDao
import com.example.lifelink.data.local.dao.ClinicWithAppointmentsDao
import com.example.lifelink.data.local.dao.ClinicWithEmergenciesDao
import com.example.lifelink.data.local.dao.EmergencyDao
import com.example.lifelink.data.local.entity.Appointment
import com.example.lifelink.data.local.entity.Clinic
import com.example.lifelink.data.local.entity.Emergency

@Database(entities = [Clinic::class, Appointment::class, Emergency::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clinicDao(): ClinicDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun emergencyDao(): EmergencyDao
    abstract fun clinicWithAppointmentsDao(): ClinicWithAppointmentsDao
    abstract fun clinicWithEmergenciesDao(): ClinicWithEmergenciesDao

    companion object {
        const val DATABASE_NAME = "bloodhub_database"
    }
}