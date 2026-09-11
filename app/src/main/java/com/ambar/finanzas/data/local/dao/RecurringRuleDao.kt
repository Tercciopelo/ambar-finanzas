package com.ambar.finanzas.data.local.dao

import androidx.room.*
import com.ambar.finanzas.data.local.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringRuleDao {
    @Query("SELECT * FROM recurring_rules WHERE active = 1 ORDER BY dayOfMonth ASC")
    fun getActive(): Flow<List<RecurringRuleEntity>>

    @Query("SELECT * FROM recurring_rules ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RecurringRuleEntity>>

    @Query("SELECT * FROM recurring_rules WHERE isSubscription = 1 AND active = 1")
    fun getSubscriptions(): Flow<List<RecurringRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RecurringRuleEntity): Long

    @Delete
    suspend fun delete(rule: RecurringRuleEntity)

    @Update
    suspend fun update(rule: RecurringRuleEntity)

    @Query("UPDATE recurring_rules SET active = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
@Query("SELECT * FROM recurring_rules")
    fun getAllSync(): List<RecurringRuleEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(rules: List<RecurringRuleEntity>)
    @Query("DELETE FROM recurring_rules")
    fun deleteAll()

}
