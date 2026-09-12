package com.ambar.finanzas.utils

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
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
    val settings: List<SettingEntity>,
    val savingsGoals: List<SavingsGoalEntity>? = emptyList(),
    val savingContributions: List<SavingContributionEntity>? = emptyList()
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
                settings = database.settingDao().getAllSync(),
                savingsGoals = database.savingsGoalDao().getAllSync(),
                savingContributions = database.savingsGoalDao().getAllContributionsSync()
            )

            val json = gson.toJson(data)

            val output = context.contentResolver.openOutputStream(uri)
                ?: error("No se pudo abrir el archivo de destino")
            output.use { outputStream ->
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
            } ?: return@withContext Result.failure(Exception("No se pudo leer el archivo"))

            val data = gson.fromJson(json, BackupData::class.java)
                ?: return@withContext Result.failure(Exception("El respaldo está vacío"))
            require(data.schemaVersion == 1) { "Esta versión del respaldo no es compatible" }

            database.withTransaction {
                // Clear existing
                database.transactionDao().deleteAll()
                database.categoryDao().deleteAll()
                database.recurringRuleDao().deleteAll()
                database.installmentPlanDao().deleteAll()
                database.savingsGoalDao().deleteAllContributions()
                database.savingsGoalDao().deleteAll()
                database.budgetDao().deleteAll()
                database.settingDao().deleteAll()
                
                // Insert new
                if (data.categories.isNotEmpty()) database.categoryDao().insertAll(data.categories)
                if (data.transactions.isNotEmpty()) database.transactionDao().insertAll(data.transactions)
                if (data.recurringRules.isNotEmpty()) database.recurringRuleDao().insertAll(data.recurringRules)
                if (data.installmentPlans.isNotEmpty()) database.installmentPlanDao().insertAll(data.installmentPlans)
                if (data.savingsGoals.orEmpty().isNotEmpty()) database.savingsGoalDao().insertAll(data.savingsGoals.orEmpty())
                if (data.savingContributions.orEmpty().isNotEmpty()) {
                    database.savingsGoalDao().insertAllContributions(data.savingContributions.orEmpty())
                }
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

    suspend fun exportCSV(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val transactions = database.transactionDao().getAllSync()
            val categories = database.categoryDao().getAllSync().associateBy { it.id }

            val output = context.contentResolver.openOutputStream(uri)
                ?: error("No se pudo abrir el archivo de destino")
            output.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write("Fecha,Tipo,Categoría,Monto,Descripción\n")
                    transactions.forEach { tx ->
                        val date = java.time.Instant.ofEpochMilli(tx.date)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        val type = if (tx.type == "EXPENSE") "Gasto" else "Ingreso"
                        val category = tx.categoryId?.let { categories[it]?.name } ?: "Sin categoría"
                        
                        writer.write(listOf(date.toString(), type, csv(category), tx.amount.toString(), csv(tx.description)).joinToString(","))
                        writer.write("\n")
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun csv(value: String): String = "\"" + value.replace("\"", "\"\"") + "\""
}
