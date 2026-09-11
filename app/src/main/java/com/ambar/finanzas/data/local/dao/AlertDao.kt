package com.ambar.finanzas.data.local.dao

import androidx.room.*
import com.ambar.finanzas.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts WHERE dismissed = 0 ORDER BY date DESC")
    fun getActive(): Flow<List<AlertEntity>>

    @Query("SELECT COUNT(*) FROM alerts WHERE read = 0 AND dismissed = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity): Long

    @Query("UPDATE alerts SET read = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("UPDATE alerts SET dismissed = 1 WHERE id = :id")
    suspend fun dismiss(id: Long)

    @Query("DELETE FROM alerts WHERE dismissed = 1")
    suspend fun clearDismissed()
}
