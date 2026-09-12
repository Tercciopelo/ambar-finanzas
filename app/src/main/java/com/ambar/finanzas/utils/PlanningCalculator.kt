package com.ambar.finanzas.utils

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object PlanningCalculator {
    fun availableForSavings(
        income: Long,
        paidExpenses: Long,
        pendingPayments: Long,
        unpaidDebtInstallments: Long,
        savedThisMonth: Long = 0L
    ): Long = income - paidExpenses - pendingPayments - unpaidDebtInstallments - savedThisMonth

    fun monthlyAmount(remaining: Long, targetDate: Long?, today: LocalDate = LocalDate.now()): Long {
        if (remaining <= 0L) return 0L
        if (targetDate == null) return 0L
        val target = Instant.ofEpochMilli(targetDate).atZone(ZoneId.systemDefault()).toLocalDate()
        val months = (ChronoUnit.MONTHS.between(YearMonth.from(today), YearMonth.from(target)) + 1L)
            .coerceAtLeast(1L)
        return (remaining + months - 1L) / months
    }
}
