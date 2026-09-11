package com.ambar.finanzas.data.local.dao

import androidx.room.*
import com.ambar.finanzas.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE monthKey = :monthKey ORDER BY date DESC")
    fun getByMonth(monthKey: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 50): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND monthKey = :monthKey AND status = 'PAID'")
    fun getMonthlyIncome(monthKey: String): Flow<Long?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND monthKey = :monthKey AND status = 'PAID'")
    fun getMonthlyExpenses(monthKey: String): Flow<Long?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND monthKey = :monthKey AND status IN ('PENDING', 'OVERDUE')")
    fun getMonthlyPending(monthKey: String): Flow<Long?>

    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE' AND monthKey = :monthKey AND status IN ('PENDING', 'OVERDUE') ORDER BY date ASC")
    fun getPendingPayments(monthKey: String): Flow<List<TransactionEntity>>

    @Query("SELECT categoryId, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' AND monthKey = :monthKey GROUP BY categoryId ORDER BY total DESC")
    fun getExpensesByCategory(monthKey: String): Flow<List<CategoryTotal>>

    @Query("SELECT monthKey, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' AND monthKey IN (:months) GROUP BY monthKey ORDER BY monthKey")
    fun getMonthlyTotals(months: List<String>): Flow<List<MonthTotal>>

    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :query || '%' ORDER BY date DESC")
    fun search(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT description, categoryId, COUNT(*) as freq FROM transactions WHERE type = 'EXPENSE' GROUP BY description ORDER BY freq DESC LIMIT 10")
    suspend fun getFrequentDescriptions(): List<FrequentItem>

    @Query("SELECT DISTINCT recurringRuleId || '-' || monthKey FROM transactions WHERE recurringRuleId = :ruleId AND monthKey = :monthKey")
    suspend fun hasRecurringForMonth(ruleId: Long, monthKey: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM transactions")
    fun getAllSync(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions")
    fun deleteAll()
}

data class CategoryTotal(val categoryId: Long?, val total: Long)
data class MonthTotal(val monthKey: String, val total: Long)
data class FrequentItem(val description: String, val categoryId: Long?, val freq: Int)
