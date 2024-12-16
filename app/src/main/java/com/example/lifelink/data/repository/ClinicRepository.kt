package com.example.lifelink.data.repository

import androidx.annotation.WorkerThread
import com.example.lifelink.data.local.dao.ClinicDao
import com.example.lifelink.data.local.entity.Clinic
import kotlinx.coroutines.flow.Flow

class ClinicRepository(
    private val clinicDao: ClinicDao
) {
    val allClinics : Flow<List<Clinic>> = clinicDao.getAll()

    @WorkerThread
    suspend fun insertClinic(clinic: Clinic) {
        clinicDao.insert(clinic)
    }

    @WorkerThread
    suspend fun getClinicByNameAndAddress(name: String, address: String): Clinic? {
        return clinicDao.getClinicByNameAndAddress(name, address)
    }
}