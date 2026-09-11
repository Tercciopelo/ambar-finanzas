package com.ambar.finanzas.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ambar.finanzas.data.repository.FinanceRepository
import com.ambar.finanzas.ui.components.LocalHideAmounts
import com.ambar.finanzas.ui.components.QuickAddSheet
import com.ambar.finanzas.ui.screens.home.HomeScreen
import com.ambar.finanzas.ui.screens.home.HomeViewModel
import com.ambar.finanzas.ui.screens.installments.InstallmentsScreen
import com.ambar.finanzas.ui.screens.installments.InstallmentsViewModel
import com.ambar.finanzas.ui.screens.planificacion.PlanificacionScreen
import com.ambar.finanzas.ui.screens.planificacion.PlanificacionViewModel
import com.ambar.finanzas.ui.screens.settings.SettingsScreen
import com.ambar.finanzas.ui.screens.settings.SettingsViewModel
import com.ambar.finanzas.ui.screens.transactions.TransactionsScreen
import com.ambar.finanzas.ui.screens.transactions.TransactionsViewModel
import kotlinx.coroutines.launch

enum class AmbarScreen(val route: String, val label: String, val icon: ImageVector) {
    HOME("home", "Inicio", Icons.Default.Home),
    TRANSACTIONS("transactions", "Movimientos", Icons.Default.Receipt),
    INSTALLMENTS("installments", "Cuotas", Icons.Default.CreditCard),
    PLANIFICACION("planificacion", "Plan", Icons.Default.DateRange),
    SETTINGS("settings", "Ajustes", Icons.Default.Settings)
}

private data class QuickAddRequest(
    val expense: Boolean = true,
    val recurringRuleId: Long? = null,
    val saveAsRecurring: Boolean = false
)

@Composable
fun AmbarNavigation(repository: FinanceRepository) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val hideAmounts by remember { repository.observeSetting("hide_amounts") }
        .collectAsStateWithLifecycle(initialValue = null)
    var quickAdd by remember { mutableStateOf<QuickAddRequest?>(null) }

    CompositionLocalProvider(LocalHideAmounts provides (hideAmounts == "true")) {
        Scaffold(snackbarHost = { SnackbarHost(snackbar) }, bottomBar = {
            NavigationBar {
                AmbarScreen.entries.forEach { screen ->
                    NavigationBarItem(selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }, icon = { Icon(screen.icon, screen.label) }, label = { Text(screen.label) }, alwaysShowLabel = true)
                }
            }
        }) { innerPadding ->
            NavHost(navController, startDestination = AmbarScreen.HOME.route, modifier = Modifier.padding(innerPadding)) {
                composable(AmbarScreen.HOME.route) {
                    val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
                    HomeScreen(vm, onAdd = { expense -> quickAdd = QuickAddRequest(expense) },
                        onTransactions = { navController.navigate(AmbarScreen.TRANSACTIONS.route) },
                        onPrivacy = {
                            scope.launch { repository.setSetting("hide_amounts", (hideAmounts != "true").toString()) }
                        })
                }
                composable(AmbarScreen.TRANSACTIONS.route) {
                    val vm: TransactionsViewModel = viewModel(factory = TransactionsViewModel.Factory(repository))
                    TransactionsScreen(vm, onAdd = { quickAdd = QuickAddRequest() })
                }
                composable(AmbarScreen.INSTALLMENTS.route) {
                    val vm: InstallmentsViewModel = viewModel(factory = InstallmentsViewModel.Factory(repository))
                    InstallmentsScreen(vm)
                }
                composable(AmbarScreen.PLANIFICACION.route) {
                    val vm: PlanificacionViewModel = viewModel(factory = PlanificacionViewModel.Factory(repository))
                    PlanificacionScreen(vm,
                        onAdd = { quickAdd = QuickAddRequest(saveAsRecurring = true) },
                        onRegister = { id -> quickAdd = QuickAddRequest(recurringRuleId = id) })
                }
                composable(AmbarScreen.SETTINGS.route) {
                    val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository))
                    SettingsScreen(vm)
                }
            }
        }

        quickAdd?.let { request ->
            QuickAddSheet(repository = repository, onDismiss = { quickAdd = null },
                onShowSnackbar = { message -> scope.launch { snackbar.showSnackbar(message) } },
                initialExpense = request.expense, initialRuleId = request.recurringRuleId,
                initialRepeat = request.saveAsRecurring)
        }
    }
}
