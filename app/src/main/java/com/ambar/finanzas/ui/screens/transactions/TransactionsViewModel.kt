package com.ambar.finanzas.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TransactionsUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val searchQuery: String = "",
    val filterType: String = "ALL"
)

class TransactionsViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val filterType = MutableStateFlow("ALL")

    val uiState: StateFlow<TransactionsUiState> = combine(
        repository.getRecentTransactions(100),
        searchQuery,
        filterType
    ) { txs, query, type ->
        var filtered = txs
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.description.contains(query, ignoreCase = true) }
        }
        if (type != "ALL") {
            filtered = filtered.filter { it.type == type }
        }
        TransactionsUiState(
            transactions = filtered,
            searchQuery = query,
            filterType = type
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsUiState()
    )

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setFilterType(type: String) {
        filterType.value = type
    }

    class Factory(private val repository: FinanceRepository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return TransactionsViewModel(repository) as T
        }
    }
}
