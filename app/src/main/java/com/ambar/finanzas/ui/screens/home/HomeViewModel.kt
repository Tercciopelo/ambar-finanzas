package com.ambar.finanzas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.dao.CategoryTotal
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import com.ambar.finanzas.utils.CurrencyUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val monthKey: String = CurrencyUtils.currentMonthKey(),
    val monthDisplay: String = CurrencyUtils.monthKeyToDisplay(CurrencyUtils.currentMonthKey()),
    val totalIncome: Long = 0L,
    val totalExpenses: Long = 0L,
    val totalPending: Long = 0L,
    val available: Long = 0L,
    val canSpendToday: Long = 0L,
    val pendingPayments: List<TransactionEntity> = emptyList(),
    val expensesByCategory: List<CategoryTotal> = emptyList(),
    val unreadAlerts: Int = 0
)

class HomeViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _monthKey = MutableStateFlow(CurrencyUtils.currentMonthKey())

    val uiState: StateFlow<HomeUiState> = _monthKey.flatMapLatest { mk ->
        val financials = combine(
            repository.getMonthlyIncome(mk),
            repository.getMonthlyExpenses(mk),
            repository.getMonthlyPending(mk)
        ) { income, expenses, pending ->
            Triple(income ?: 0L, expenses ?: 0L, pending ?: 0L)
        }

        val details = combine(
            repository.getPendingPayments(mk),
            repository.getExpensesByCategory(mk),
            repository.getUnreadAlertCount()
        ) { payments, byCat, alerts ->
            Triple(payments, byCat, alerts)
        }

        combine(financials, details) { fin, det ->
            val (income, expenses, pending) = fin
            val (pendingList, byCat, alerts) = det

            val available = income - expenses - pending
            val daysLeft = CurrencyUtils.daysRemainingInMonth(mk).coerceAtLeast(1)
            val canSpend = (available / daysLeft).coerceAtLeast(0)

            HomeUiState(
                monthKey = mk,
                monthDisplay = CurrencyUtils.monthKeyToDisplay(mk),
                totalIncome = income,
                totalExpenses = expenses,
                totalPending = pending,
                available = available,
                canSpendToday = canSpend,
                pendingPayments = pendingList,
                expensesByCategory = byCat,
                unreadAlerts = alerts
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun previousMonth() {
        _monthKey.value = CurrencyUtils.previousMonthKey(_monthKey.value)
    }

    fun nextMonth() {
        _monthKey.value = CurrencyUtils.nextMonthKey(_monthKey.value)
    }

    fun currentMonth() { _monthKey.value = CurrencyUtils.currentMonthKey() }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
