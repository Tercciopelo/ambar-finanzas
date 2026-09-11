package com.ambar.finanzas.ui.screens.installments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*

data class InstallmentsUiState(
    val activePlans: List<InstallmentPlanEntity> = emptyList(),
    val completedPlans: List<InstallmentPlanEntity> = emptyList()
)

class InstallmentsViewModel(private val repository: FinanceRepository) : ViewModel() {

    val uiState: StateFlow<InstallmentsUiState> = repository.getAllInstallmentPlans()
        .map { plans ->
            InstallmentsUiState(
                activePlans = plans.filter { it.status == "ACTIVE" },
                completedPlans = plans.filter { it.status == "COMPLETED" }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InstallmentsUiState())

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InstallmentsViewModel(repository) as T
        }
    }
}
