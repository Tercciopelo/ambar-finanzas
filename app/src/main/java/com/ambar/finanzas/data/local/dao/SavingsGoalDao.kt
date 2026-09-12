package com.ambar.finanzas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ambar.finanzas.data.local.entity.SavingContributionEntity
import com.ambar.finanzas.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals ORDER BY completed ASC, targetDate IS NULL, targetDate ASC, createdAt DESC")
    fun getAll(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :id")
    suspend fun getById(id: Long): SavingsGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: SavingsGoalEntity): Long

    @Update
    suspend fun update(goal: SavingsGoalEntity)

    @Delete
    suspend fun delete(goal: SavingsGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: SavingContributionEntity): Long

    @Query("SELECT SUM(amount) FROM saving_contributions WHERE date >= :start AND date < :end")
    fun getContributionsBetween(start: Long, end: Long): Flow<Long?>

    @Query("SELECT * FROM savings_goals")
    fun getAllSync(): List<SavingsGoalEntity>

    @Query("SELECT * FROM saving_contributions")
    fun getAllContributionsSync(): List<SavingContributionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(goals: List<SavingsGoalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContributions(contributions: List<SavingContributionEntity>)

    @Query("DELETE FROM saving_contributions")
    fun deleteAllContributions()

    @Query("DELETE FROM savings_goals")
    fun deleteAll()
}
