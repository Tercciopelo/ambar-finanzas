package com.ambar.finanzas.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import com.ambar.finanzas.utils.CurrencyUtils
import kotlinx.coroutines.flow.*

data class TransactionsUiState(val transactions: List<TransactionEntity> = emptyList(), val searchQuery: String = "",
    val filterType: String = "ALL", val monthKey: String = CurrencyUtils.currentMonthKey())

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TransactionsViewModel(private val repository: FinanceRepository) : ViewModel() {
    private val search = MutableStateFlow("")
    private val type = MutableStateFlow("ALL")
    private val month = MutableStateFlow(CurrencyUtils.currentMonthKey())
    val uiState = month.flatMapLatest { mk ->
        combine(repository.getTransactionsByMonth(mk), search, type) { rows, query, filter ->
            TransactionsUiState(rows.filter {
                (it.description.contains(query, true) || it.note.contains(query, true)) &&
                    when (filter) { "ALL" -> true; "PENDING" -> it.status != "PAID"; else -> it.type == filter }
            }, query, filter, mk)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionsUiState())
    fun setSearchQuery(query: String) { search.value = query }
    fun setFilterType(filter: String) { type.value = filter }
    fun previousMonth() { month.value = CurrencyUtils.previousMonthKey(month.value) }
    fun nextMonth() { month.value = CurrencyUtils.nextMonthKey(month.value) }
    fun currentMonth() { month.value = CurrencyUtils.currentMonthKey() }
    suspend fun delete(tx: TransactionEntity) { check(tx.installmentPlanId == null); repository.deleteTransaction(tx.id) }
    suspend fun markPaid(tx: TransactionEntity) { repository.markPaid(tx.id) }
    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TransactionsViewModel(repository) as T
    }
}
