package com.ambar.finanzas.utils

import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object CurrencyUtils {
    private val clpFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
        maximumFractionDigits = 0
    }

    fun formatCLP(amount: Long): String {
        return clpFormat.format(amount)
    }

    fun currentMonthKey(): String {
        return YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    fun monthKeyToDisplay(monthKey: String): String {
        val ym = YearMonth.parse(monthKey)
        val months = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return "${months[ym.monthValue - 1]} ${ym.year}"
    }

    fun previousMonthKey(monthKey: String): String {
        return YearMonth.parse(monthKey).minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    fun nextMonthKey(monthKey: String): String {
        return YearMonth.parse(monthKey).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    fun daysRemainingInMonth(monthKey: String): Int {
        val ym = YearMonth.parse(monthKey)
        val today = LocalDate.now()
        return if (today.year == ym.year && today.monthValue == ym.monthValue) {
            ym.lengthOfMonth() - today.dayOfMonth
        } else {
            ym.lengthOfMonth()
        }
    }

    fun last6Months(): List<String> {
        val current = YearMonth.now()
        return (5 downTo 0).map {
            current.minusMonths(it.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM"))
        }
    }
}
