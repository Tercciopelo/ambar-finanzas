package com.ambar.finanzas.ui.screens.installments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import com.ambar.finanzas.utils.CurrencyUtils
import kotlinx.coroutines.flow.*
import java.util.UUID

data class InstallmentsUiState(val activePlans: List<InstallmentPlanEntity> = emptyList())
class InstallmentsViewModel(private val repository: FinanceRepository) : ViewModel() {
    val uiState = repository.getActiveInstallmentPlans().map { InstallmentsUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InstallmentsUiState())
    suspend fun deletePlan(plan: InstallmentPlanEntity) = repository.deleteInstallmentPlan(plan)
    suspend fun pay(plan: InstallmentPlanEntity) = repository.payInstallment(plan.id, plan.currentInstallment)
    suspend fun addPlan(name: String, amount: Long, paid: Int, total: Int) {
        require(name.isNotBlank() && amount in 1..99_999_999_999L && total in 1..1200 && paid in 0 until total)
        repository.addInstallmentPlan(InstallmentPlanEntity(uuid = UUID.randomUUID().toString(),
            name = name.trim(), installmentAmount = amount, currentInstallment = paid,
            totalInstallments = total, startMonth = CurrencyUtils.currentMonthKey()))
    }
    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = InstallmentsViewModel(repository) as T
    }
}
