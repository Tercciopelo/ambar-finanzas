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

    fun deletePlan(plan: InstallmentPlanEntity) { viewModelScope.launch { repository.deleteInstallmentPlan(plan) } }

    fun addPlan(name: String, amount: Long, current: Int, total: Int) {
        viewModelScope.launch {
            val plan = InstallmentPlanEntity(
                uuid = java.util.UUID.randomUUID().toString(),
                name = name,
                installmentAmount = amount,
                currentInstallment = current,
                totalInstallments = total,
                startMonth = com.ambar.finanzas.utils.CurrencyUtils.currentMonthKey(),
                status = "ACTIVE"
            )
            repository.addInstallmentPlan(plan)
        }
    }

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
