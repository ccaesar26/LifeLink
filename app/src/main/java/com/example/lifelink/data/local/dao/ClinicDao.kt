package com.example.lifelink.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lifelink.data.local.entity.Clinic
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicDao {
    @Query("SELECT * FROM ${Clinic.TABLE_NAME} ORDER BY ${Clinic.ARG_CLINIC_ID} ASC")
    fun getAll(): Flow<List<Clinic>>

    @Query("SELECT * FROM ${Clinic.TABLE_NAME} WHERE ${Clinic.ARG_CLINIC_NAME} = :name AND ${Clinic.ARG_CLINIC_ADDRESS} = :address LIMIT 1")
    suspend fun getClinicByNameAndAddress(name: String, address: String): Clinic?

    @Insert
    suspend fun insert(clinic: Clinic)
}