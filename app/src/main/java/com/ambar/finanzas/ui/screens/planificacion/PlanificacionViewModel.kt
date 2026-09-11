package com.ambar.finanzas.ui.screens.planificacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.RecurringRuleEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PlanificacionViewModel(private val repository: FinanceRepository) : ViewModel() {
    val subscriptions: StateFlow<List<RecurringRuleEntity>> = repository.getActiveRecurringRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun deleteRule(rule: RecurringRuleEntity) = repository.deleteRecurringRule(rule)

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlanificacionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlanificacionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
