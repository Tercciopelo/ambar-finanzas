package com.ambar.finanzas.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ambar.finanzas.R
import com.ambar.finanzas.data.local.database.AmbarDatabase
import com.ambar.finanzas.data.repository.FinanceRepository
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.first

class AlertWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AmbarDatabase.getDatabase(applicationContext)
        val repository = FinanceRepository(
            database.transactionDao(), database.categoryDao(),
            database.recurringRuleDao(), database.installmentPlanDao(),
            database.budgetDao(), database.alertDao(), database.settingDao()
        )

        val today = LocalDate.now()
        // Here we would check for pending payments and notify.
        // For simplicity, we just send a notification if they have pending payments today.
        
        val monthKey = com.ambar.finanzas.utils.CurrencyUtils.currentMonthKey()
        val pending = repository.getPendingPayments(monthKey).first()
        
        if (pending.isNotEmpty()) {
            val count = pending.size
            sendNotification(
                "Pagos Próximos",
                "Tienes $count pagos pendientes para este mes."
            )
        }

        return Result.success()
    }

    private fun sendNotification(title: String, content: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ambar_alerts",
                "Alertas Financieras",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, "ambar_alerts")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
