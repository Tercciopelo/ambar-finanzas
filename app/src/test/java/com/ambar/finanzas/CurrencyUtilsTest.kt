package com.ambar.finanzas

import com.ambar.finanzas.utils.CurrencyUtils
import org.junit.Assert.*
import org.junit.Test

class CurrencyUtilsTest {

    @Test
    fun formatCLP_correctlyFormatsPositiveAmount() {
        val result = CurrencyUtils.formatCLP(100000)
        assertTrue(result.contains("100"))
        assertTrue(result.contains("000"))
    }

    @Test
    fun formatCLP_handlesZero() {
        val result = CurrencyUtils.formatCLP(0)
        assertNotNull(result)
    }

    @Test
    fun currentMonthKey_hasCorrectFormat() {
        val key = CurrencyUtils.currentMonthKey()
        assertTrue(key.matches(Regex("\\d{4}-\\d{2}")))
    }

    @Test
    fun previousMonthKey_decrementsMonth() {
        val prev = CurrencyUtils.previousMonthKey("2026-09")
        assertEquals("2026-08", prev)
    }

    @Test
    fun previousMonthKey_wrapsAroundYear() {
        val prev = CurrencyUtils.previousMonthKey("2026-01")
        assertEquals("2025-12", prev)
    }

    @Test
    fun nextMonthKey_incrementsMonth() {
        val next = CurrencyUtils.nextMonthKey("2026-09")
        assertEquals("2026-10", next)
    }

    @Test
    fun nextMonthKey_wrapsAroundYear() {
        val next = CurrencyUtils.nextMonthKey("2026-12")
        assertEquals("2027-01", next)
    }

    @Test
    fun monthKeyToDisplay_returnsSpanishMonthName() {
        val display = CurrencyUtils.monthKeyToDisplay("2026-09")
        assertTrue(display.contains("Septiembre"))
        assertTrue(display.contains("2026"))
    }

    @Test
    fun last6Months_returns6Elements() {
        val months = CurrencyUtils.last6Months()
        assertEquals(6, months.size)
    }

    @Test
    fun last6Months_elementsAreChronological() {
        val months = CurrencyUtils.last6Months()
        for (i in 0 until months.size - 1) {
            assertTrue(months[i] < months[i + 1])
        }
    }

    @Test
    fun daysRemainingInMonth_isPositive() {
        val days = CurrencyUtils.daysRemainingInMonth(CurrencyUtils.currentMonthKey())
        assertTrue(days >= 0)
    }
}
