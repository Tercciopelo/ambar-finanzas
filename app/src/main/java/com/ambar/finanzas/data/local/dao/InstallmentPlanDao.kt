package com.ambar.finanzas.data.local.dao

import androidx.room.*
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentPlanDao {
    @Query("SELECT * FROM installment_plans WHERE status = 'ACTIVE' ORDER BY name ASC")
    fun getActive(): Flow<List<InstallmentPlanEntity>>

    @Query("SELECT * FROM installment_plans ORDER BY createdAt DESC")
    fun getAll(): Flow<List<InstallmentPlanEntity>>

    @Query("SELECT * FROM installment_plans WHERE id = :id")
    suspend fun getById(id: Long): InstallmentPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: InstallmentPlanEntity): Long

    @Update
    suspend fun update(plan: InstallmentPlanEntity)

    @Query("UPDATE installment_plans SET status = 'COMPLETED' WHERE id = :id")
    suspend fun markCompleted(id: Long)
}
