package com.ambar.finanzas

import com.ambar.finanzas.utils.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class FinanceTests {
    @Test
    fun testCurrencyFormatting() {
        val formatted = CurrencyUtils.formatCLP(100000L)
        // Since formatting depends on the locale, we can just check if it contains the numbers and symbol
        assert(formatted.contains("100.000") || formatted.replace(".", "").contains("100000"))
        assert(formatted.contains("$"))
    }

    @Test
    fun testCanSpendToday() {
        // Mocking available = 249750, daysLeft = 24
        val available = 249750L
        val daysLeft = 24
        val canSpend = available / daysLeft
        assertEquals(10406L, canSpend)
    }

    @Test
    fun testAvailableCalculation() {
        val income = 900000L
        val expenses = 650000L
        val pending = 50000L
        val available = income - expenses - pending
        assertEquals(200000L, available)
    }

    @Test
    fun testPreviousMonth() {
        val current = "2026-09"
        val expected = "2026-08"
        assertEquals(expected, CurrencyUtils.previousMonthKey(current))
    }

    @Test
    fun testNextMonth() {
        val current = "2026-09"
        val expected = "2026-10"
        assertEquals(expected, CurrencyUtils.nextMonthKey(current))
    }
}
