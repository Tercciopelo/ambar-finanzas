package com.ambar.finanzas.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import com.ambar.finanzas.utils.CurrencyUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val searchQuery: String = "",
    val filterType: String? = null // null = all, INCOME, EXPENSE
)

class TransactionsViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow<String?>(null)
    private val _monthKey = MutableStateFlow(CurrencyUtils.currentMonthKey())

    val uiState: StateFlow<TransactionsUiState> = combine(
        _monthKey.flatMapLatest { repository.getTransactionsByMonth(it) },
        _searchQuery,
        _filterType
    ) { transactions, query, filter ->
        var filtered = transactions
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.description.contains(query, ignoreCase = true) }
        }
        if (filter != null) {
            filtered = filtered.filter { it.type == filter }
        }
        TransactionsUiState(
            transactions = filtered,
            searchQuery = query,
            filterType = filter
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionsUiState())

    fun setSearch(query: String) { _searchQuery.value = query }
    fun setFilter(type: String?) { _filterType.value = type }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch { repository.deleteTransaction(id) }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionsViewModel(repository) as T
        }
    }
}
