package com.ambar.finanzas

import com.ambar.finanzas.utils.PlanningCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class PlanningCalculatorTest {
    @Test
    fun availableForSavings_subtractsExpensesPendingAndUnregisteredDebt() {
        assertEquals(250_000L, PlanningCalculator.availableForSavings(
            income = 1_000_000L,
            paidExpenses = 500_000L,
            pendingPayments = 150_000L,
            unpaidDebtInstallments = 75_000L,
            savedThisMonth = 25_000L
        ))
    }

    @Test
    fun monthlyAmount_spreadsRemainingMoneyAcrossAvailableMonths() {
        val target = LocalDate.of(2026, 12, 15)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals(100_000L, PlanningCalculator.monthlyAmount(
            remaining = 400_000L,
            targetDate = target,
            today = LocalDate.of(2026, 9, 11)
        ))
    }

    @Test
    fun monthlyAmount_roundsUpAndTreatsPastDeadlineAsCurrentMonth() {
        val target = LocalDate.of(2026, 8, 1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals(100_001L, PlanningCalculator.monthlyAmount(
            remaining = 100_001L,
            targetDate = target,
            today = LocalDate.of(2026, 9, 11)
        ))
    }
}
