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

    @Delete
    suspend fun delete(plan: InstallmentPlanEntity)

    @Update
    suspend fun update(plan: InstallmentPlanEntity)

    @Query("UPDATE installment_plans SET status = 'COMPLETED' WHERE id = :id")
    suspend fun markCompleted(id: Long)
@Query("SELECT * FROM installment_plans")
    fun getAllSync(): List<InstallmentPlanEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(plans: List<InstallmentPlanEntity>)
    @Query("DELETE FROM installment_plans")
    fun deleteAll()

}
