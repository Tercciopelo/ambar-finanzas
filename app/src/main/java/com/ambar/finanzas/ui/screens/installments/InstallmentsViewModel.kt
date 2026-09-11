package com.ambar.finanzas.ui.screens.installments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class InstallmentsUiState(
    val activePlans: List<InstallmentPlanEntity> = emptyList()
)

class InstallmentsViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    val uiState: StateFlow<InstallmentsUiState> = repository.getActiveInstallmentPlans()
        .map { InstallmentsUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InstallmentsUiState()
        )

    class Factory(private val repository: FinanceRepository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return InstallmentsViewModel(repository) as T
        }
    }
}
