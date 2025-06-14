package com.example.lifelink.data.repository

import androidx.annotation.WorkerThread
import com.example.lifelink.data.local.dao.AppointmentDao
import com.example.lifelink.data.local.entity.Appointment
import com.example.lifelink.data.local.entity.AppointmentWithClinicName
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(private val appointmentDao: AppointmentDao) {

    val allAppointments: Flow<List<Appointment>> = appointmentDao.getAll()

    fun getMostRecentAppointmentByUserId(userId: String): Flow<AppointmentWithClinicName?> {
        return appointmentDao.getAppointmentByUserId(userId)
    }

    @WorkerThread
    suspend fun insertAppointment(appointment: Appointment) {
        appointmentDao.insert(appointment)
    }
}
