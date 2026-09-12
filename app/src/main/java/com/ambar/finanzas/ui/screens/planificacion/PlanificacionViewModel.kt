package com.ambar.finanzas.ui.screens.planificacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.data.local.entity.RecurringRuleEntity
import com.ambar.finanzas.data.local.entity.SavingsGoalEntity
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.data.repository.FinanceRepository
import com.ambar.finanzas.utils.CurrencyUtils
import com.ambar.finanzas.utils.PlanningCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import java.time.ZoneId

data class PlanningUiState(
    val income: Long = 0,
    val paidExpenses: Long = 0,
    val pendingPayments: Long = 0,
    val unpaidDebtInstallments: Long = 0,
    val totalDebtRemaining: Long = 0,
    val savedThisMonth: Long = 0,
    val availableForSavings: Long = 0,
    val requiredForGoals: Long = 0,
    val goals: List<SavingsGoalEntity> = emptyList(),
    val recurringRules: List<RecurringRuleEntity> = emptyList()
)

private data class FinancialPlan(
    val income: Long,
    val paidExpenses: Long,
    val pendingPayments: Long,
    val unpaidDebtInstallments: Long,
    val totalDebtRemaining: Long,
    val savedThisMonth: Long
)

class PlanificacionViewModel(private val repository: FinanceRepository) : ViewModel() {
    private val monthKey = CurrencyUtils.currentMonthKey()
    private val currentMonth = YearMonth.parse(monthKey)
    private val monthStart = currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val nextMonthStart = currentMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private val debtPlan = combine(
        repository.getTransactionsByMonth(monthKey),
        repository.getActiveInstallmentPlans()
    ) { transactions, plans -> buildDebtPlan(transactions, plans) }

    private val financialPlan = combine(
        repository.getMonthlyIncome(monthKey),
        repository.getMonthlyExpenses(monthKey),
        repository.getMonthlyPending(monthKey),
        repository.getSavingContributionsBetween(monthStart, nextMonthStart),
        debtPlan
    ) { income, expenses, pending, contributions, debts ->
        FinancialPlan(
            income = income ?: 0L,
            paidExpenses = expenses ?: 0L,
            pendingPayments = pending ?: 0L,
            unpaidDebtInstallments = debts.first,
            totalDebtRemaining = debts.second,
            savedThisMonth = contributions ?: 0L
        )
    }

    val uiState = combine(
        financialPlan,
        repository.getSavingsGoals(),
        repository.getActiveRecurringRules()
    ) { finances, goals, rules ->
        val required = goals.filterNot { it.completed }.sumOf { goal ->
            PlanningCalculator.monthlyAmount((goal.targetAmount - goal.savedAmount).coerceAtLeast(0L), goal.targetDate)
        }
        PlanningUiState(
            income = finances.income,
            paidExpenses = finances.paidExpenses,
            pendingPayments = finances.pendingPayments,
            unpaidDebtInstallments = finances.unpaidDebtInstallments,
            totalDebtRemaining = finances.totalDebtRemaining,
            savedThisMonth = finances.savedThisMonth,
            availableForSavings = PlanningCalculator.availableForSavings(
                finances.income, finances.paidExpenses, finances.pendingPayments,
                finances.unpaidDebtInstallments, finances.savedThisMonth
            ),
            requiredForGoals = required,
            goals = goals,
            recurringRules = rules
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlanningUiState())

    suspend fun addGoal(name: String, targetAmount: Long, targetDate: Long) =
        repository.addSavingsGoal(name, targetAmount, targetDate)

    suspend fun addContribution(goal: SavingsGoalEntity, amount: Long) =
        repository.addSavingContribution(goal.id, amount)

    suspend fun deleteGoal(goal: SavingsGoalEntity) = repository.deleteSavingsGoal(goal)

    suspend fun deleteRule(rule: RecurringRuleEntity) = repository.deleteRecurringRule(rule)

    private fun buildDebtPlan(
        transactions: List<TransactionEntity>,
        plans: List<InstallmentPlanEntity>
    ): Pair<Long, Long> {
        val plansAlreadyRegistered = transactions.mapNotNull { it.installmentPlanId }.toSet()
        val installmentsStillDue = plans.filterNot { it.id in plansAlreadyRegistered }.sumOf { it.installmentAmount }
        val debtRemaining = plans.sumOf { plan ->
            (plan.totalInstallments - plan.currentInstallment).coerceAtLeast(0).toLong() * plan.installmentAmount
        }
        return installmentsStillDue to debtRemaining
    }

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
