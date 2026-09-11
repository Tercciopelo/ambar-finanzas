package com.ambar.finanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.ui.navigation.AmbarNavigation
import com.ambar.finanzas.ui.theme.AmbarFinanzasTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AmbarApp

        lifecycleScope.launch(Dispatchers.IO) {
            val monthKey = com.ambar.finanzas.utils.CurrencyUtils.currentMonthKey()
            val existing = app.repository.getTransactionsByMonth(monthKey).first()
            if (existing.isEmpty()) {
                val db = com.ambar.finanzas.data.local.database.AmbarDatabase.getInstance(this@MainActivity)
                val txDao = db.transactionDao()
                
                txDao.insert(
                    TransactionEntity(
                        uuid = UUID.randomUUID().toString(),
                        type = "INCOME",
                        amount = 1000000L,
                        description = "Sueldo",
                        date = System.currentTimeMillis() - 86400000L * 2,
                        monthKey = monthKey
                    )
                )

                txDao.insert(
                    TransactionEntity(
                        uuid = UUID.randomUUID().toString(),
                        type = "EXPENSE",
                        amount = 15000L,
                        description = "Uber",
                        date = System.currentTimeMillis() - 86400000L * 1,
                        monthKey = monthKey
                    )
                )

                txDao.insert(
                    TransactionEntity(
                        uuid = UUID.randomUUID().toString(),
                        type = "EXPENSE",
                        amount = 8990L,
                        description = "Almuerzo",
                        date = System.currentTimeMillis(),
                        monthKey = monthKey
                    )
                )

                txDao.insert(
                    TransactionEntity(
                        uuid = UUID.randomUUID().toString(),
                        type = "EXPENSE",
                        amount = 65000L,
                        description = "Supermercado Lider",
                        date = System.currentTimeMillis(),
                        monthKey = monthKey
                    )
                )
            }
        }

        setContent {
            AmbarFinanzasTheme {
                AmbarNavigation(repository = app.repository)
            }
        }
    }
}
