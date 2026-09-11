package com.ambar.finanzas.data.local.dao

import androidx.room.*
import com.ambar.finanzas.data.local.entity.BudgetEntity
import com.ambar.finanzas.data.local.entity.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets LIMIT 1")
    fun getBudget(): Flow<BudgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Query("SELECT * FROM category_budgets WHERE monthKey = :monthKey")
    fun getCategoryBudgets(monthKey: String): Flow<List<CategoryBudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryBudget(budget: CategoryBudgetEntity)
}
