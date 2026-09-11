package com.ambar.finanzas

import android.app.Application
import com.ambar.finanzas.data.local.database.AmbarDatabase
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AmbarApp : Application() {

    val database by lazy { AmbarDatabase.getInstance(this) }

    val repository by lazy {
        FinanceRepository(
            transactionDao = database.transactionDao(),
            categoryDao = database.categoryDao(),
            recurringRuleDao = database.recurringRuleDao(),
            installmentPlanDao = database.installmentPlanDao(),
            budgetDao = database.budgetDao(),
            alertDao = database.alertDao(),
            settingDao = database.settingDao()
        )
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Initialize default categories on first launch
        applicationScope.launch {
            repository.initDefaultCategories()
        }
    }
}
