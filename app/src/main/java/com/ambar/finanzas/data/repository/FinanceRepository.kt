package com.ambar.finanzas.data.repository

import com.ambar.finanzas.data.local.dao.*
import com.ambar.finanzas.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val recurringRuleDao: RecurringRuleDao,
    private val installmentPlanDao: InstallmentPlanDao,
    private val budgetDao: BudgetDao,
    private val alertDao: AlertDao,
    private val settingDao: SettingDao
) {
    // ===== TRANSACTIONS =====

    fun getTransactionsByMonth(monthKey: String): Flow<List<TransactionEntity>> =
        transactionDao.getByMonth(monthKey)

    fun getRecentTransactions(limit: Int = 50): Flow<List<TransactionEntity>> =
        transactionDao.getRecent(limit)

    fun getMonthlyIncome(monthKey: String): Flow<Long?> =
        transactionDao.getMonthlyIncome(monthKey)

    fun getMonthlyExpenses(monthKey: String): Flow<Long?> =
        transactionDao.getMonthlyExpenses(monthKey)

    fun getMonthlyPending(monthKey: String): Flow<Long?> =
        transactionDao.getMonthlyPending(monthKey)

    fun getPendingPayments(monthKey: String): Flow<List<TransactionEntity>> =
        transactionDao.getPendingPayments(monthKey)

    fun getExpensesByCategory(monthKey: String): Flow<List<CategoryTotal>> =
        transactionDao.getExpensesByCategory(monthKey)

    fun getMonthlyTotals(months: List<String>): Flow<List<MonthTotal>> =
        transactionDao.getMonthlyTotals(months)

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> =
        transactionDao.search(query)

    suspend fun getFrequentDescriptions(): List<FrequentItem> =
        transactionDao.getFrequentDescriptions()

    suspend fun addTransaction(transaction: TransactionEntity): Long =
        transactionDao.insert(transaction)

    suspend fun addQuickExpense(amount: Long, description: String, categoryId: Long? = null, note: String = "", isRecurring: Boolean = false, isSubscription: Boolean = false): Long {
        val tx = TransactionEntity(
            uuid = UUID.randomUUID().toString(),
            type = "EXPENSE",
            amount = amount,
            description = description,
            categoryId = categoryId,
            date = System.currentTimeMillis(),
            monthKey = com.ambar.finanzas.utils.CurrencyUtils.currentMonthKey(),
            status = "PAID"
        )
        return transactionDao.insert(tx)
    }

    suspend fun addQuickIncome(amount: Long, description: String, categoryId: Long? = null, note: String = "", isRecurring: Boolean = false): Long {
        val tx = TransactionEntity(
            uuid = UUID.randomUUID().toString(),
            type = "INCOME",
            amount = amount,
            description = description,
            categoryId = categoryId,
            date = System.currentTimeMillis(),
            monthKey = com.ambar.finanzas.utils.CurrencyUtils.currentMonthKey(),
            status = "PAID"
        )
        return transactionDao.insert(tx)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.update(transaction)

    suspend fun deleteTransaction(id: Long) =
        transactionDao.deleteById(id)

    // ===== CATEGORIES =====

    fun getCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAll()

    suspend fun getCategoryById(id: Long): CategoryEntity? =
        categoryDao.getById(id)

    suspend fun addCategory(category: CategoryEntity): Long =
        categoryDao.insert(category)

    suspend fun updateCategory(category: CategoryEntity) =
        categoryDao.update(category)

    suspend fun initDefaultCategories() {
        if (categoryDao.count() == 0) {
            val defaults = listOf(
                CategoryEntity(name = "Familia", icon = "family_restroom", color = 0xFFE53935, order = 0),
                CategoryEntity(name = "Servicios", icon = "receipt_long", color = 0xFF1E88E5, order = 1),
                CategoryEntity(name = "Compras", icon = "shopping_bag", color = 0xFF8E24AA, order = 2),
                CategoryEntity(name = "Cuotas", icon = "credit_card", color = 0xFFFF8F00, order = 3),
                CategoryEntity(name = "Transporte", icon = "directions_car", color = 0xFF43A047, order = 4),
                CategoryEntity(name = "Salud", icon = "favorite", color = 0xFFD81B60, order = 5),
                CategoryEntity(name = "Comida", icon = "restaurant", color = 0xFFFF7043, order = 6),
                CategoryEntity(name = "Ocio", icon = "sports_esports", color = 0xFF5C6BC0, order = 7),
                CategoryEntity(name = "Suscripciones", icon = "subscriptions", color = 0xFF00ACC1, order = 8),
                CategoryEntity(name = "Otros", icon = "category", color = 0xFF78909C, order = 9),
            )
            categoryDao.insertAll(defaults)
        }
    }

    // ===== RECURRING RULES =====

    fun getActiveRecurringRules(): Flow<List<RecurringRuleEntity>> =
        recurringRuleDao.getActive()

    fun getSubscriptions(): Flow<List<RecurringRuleEntity>> =
        recurringRuleDao.getSubscriptions()

    suspend fun deleteRecurringRule(rule: RecurringRuleEntity) = recurringRuleDao.delete(rule)

    suspend fun addRecurringRule(rule: RecurringRuleEntity): Long =
        recurringRuleDao.insert(rule)

    // ===== INSTALLMENT PLANS =====

    fun getActiveInstallmentPlans(): Flow<List<InstallmentPlanEntity>> =
        installmentPlanDao.getActive()

    fun getAllInstallmentPlans(): Flow<List<InstallmentPlanEntity>> =
        installmentPlanDao.getAll()

    suspend fun deleteInstallmentPlan(plan: InstallmentPlanEntity) = installmentPlanDao.delete(plan)

    suspend fun addInstallmentPlan(plan: InstallmentPlanEntity): Long =
        installmentPlanDao.insert(plan)

    // ===== BUDGET =====

    fun getBudget(): Flow<BudgetEntity?> =
        budgetDao.getBudget()

    suspend fun setBudget(budget: BudgetEntity) =
        budgetDao.insertBudget(budget)

    // ===== ALERTS =====

    fun getActiveAlerts(): Flow<List<AlertEntity>> =
        alertDao.getActive()

    fun getUnreadAlertCount(): Flow<Int> =
        alertDao.getUnreadCount()

    suspend fun addAlert(alert: AlertEntity): Long =
        alertDao.insert(alert)

    suspend fun markAlertRead(id: Long) =
        alertDao.markRead(id)

    suspend fun dismissAlert(id: Long) =
        alertDao.dismiss(id)

    // ===== SETTINGS =====

    suspend fun getSetting(key: String): String? =
        settingDao.get(key)

    fun observeSetting(key: String): Flow<String?> =
        settingDao.observe(key)

    suspend fun setSetting(key: String, value: String) =
        settingDao.set(SettingEntity(key, value))
}


