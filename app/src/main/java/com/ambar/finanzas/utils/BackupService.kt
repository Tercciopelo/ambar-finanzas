package com.ambar.finanzas.utils

import android.content.Context
import android.net.Uri
import com.ambar.finanzas.data.local.database.AmbarDatabase
import com.ambar.finanzas.data.local.entity.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.io.OutputStreamWriter

data class BackupData(
    val schemaVersion: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val transactions: List<TransactionEntity>,
    val categories: List<CategoryEntity>,
    val recurringRules: List<RecurringRuleEntity>,
    val installmentPlans: List<InstallmentPlanEntity>,
    val budgets: List<BudgetEntity>,
    val settings: List<SettingEntity>
)

class BackupService(private val context: Context, private val database: AmbarDatabase) {

    private val gson = Gson()

    suspend fun exportBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val data = BackupData(
                transactions = database.transactionDao().getAllSync(),
                categories = database.categoryDao().getAllSync(),
                recurringRules = database.recurringRuleDao().getAllSync(),
                installmentPlans = database.installmentPlanDao().getAllSync(),
                budgets = listOfNotNull(database.budgetDao().getBudgetSync()),
                settings = database.settingDao().getAllSync()
            )

            val json = gson.toJson(data)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    reader.readText()
                }
            } ?: return@withContext Result.failure(Exception("Cannot read file"))

            val data = gson.fromJson(json, BackupData::class.java)

            database.runInTransaction {
                // Clear existing
                database.transactionDao().deleteAll()
                database.categoryDao().deleteAll()
                database.recurringRuleDao().deleteAll()
                database.installmentPlanDao().deleteAll()
                database.budgetDao().deleteAll()
                
                // Insert new
                if (data.categories.isNotEmpty()) database.categoryDao().insertAll(data.categories)
                if (data.transactions.isNotEmpty()) database.transactionDao().insertAll(data.transactions)
                if (data.recurringRules.isNotEmpty()) database.recurringRuleDao().insertAll(data.recurringRules)
                if (data.installmentPlans.isNotEmpty()) database.installmentPlanDao().insertAll(data.installmentPlans)
                if (data.budgets.isNotEmpty()) database.budgetDao().insertBudget(data.budgets.first())
                if (data.settings.isNotEmpty()) {
                    data.settings.forEach { database.settingDao().setSync(it) }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
