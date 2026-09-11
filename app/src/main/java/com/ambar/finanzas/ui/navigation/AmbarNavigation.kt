package com.ambar.finanzas.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ambar.finanzas.data.repository.FinanceRepository
import com.ambar.finanzas.ui.screens.home.HomeScreen
import com.ambar.finanzas.ui.screens.home.HomeViewModel
import com.ambar.finanzas.ui.screens.installments.InstallmentsScreen
import com.ambar.finanzas.ui.screens.installments.InstallmentsViewModel
import com.ambar.finanzas.ui.screens.settings.SettingsScreen
import com.ambar.finanzas.ui.screens.settings.SettingsViewModel
import com.ambar.finanzas.ui.screens.transactions.TransactionsScreen
import com.ambar.finanzas.ui.screens.transactions.TransactionsViewModel
import com.ambar.finanzas.ui.components.QuickAddSheet

import com.ambar.finanzas.ui.screens.planificacion.PlanificacionScreen

enum class AmbarScreen(val route: String, val label: String, val icon: ImageVector) {
    HOME("home", "Inicio", Icons.Default.Home),
    TRANSACTIONS("transactions", "Movimientos", Icons.Default.Receipt),
    INSTALLMENTS("installments", "Cuotas", Icons.Default.CreditCard),
    PLANIFICACION("planificacion", "Plan", Icons.Default.DateRange),
    SETTINGS("settings", "Ajustes", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbarNavigation(repository: FinanceRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showQuickAdd by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                AmbarScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
                        selected = currentRoute == screen.route,
                        alwaysShowLabel = false,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickAdd = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AmbarScreen.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AmbarScreen.HOME.route) {
                val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
                HomeScreen(viewModel = vm)
            }
            composable(AmbarScreen.TRANSACTIONS.route) {
                val vm: TransactionsViewModel = viewModel(factory = TransactionsViewModel.Factory(repository))
                TransactionsScreen(viewModel = vm)
            }
            composable(AmbarScreen.INSTALLMENTS.route) {
                val vm: InstallmentsViewModel = viewModel(factory = InstallmentsViewModel.Factory(repository))
                InstallmentsScreen(viewModel = vm)
            }
            composable(AmbarScreen.SETTINGS.route) {
                val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository))
                SettingsScreen(viewModel = vm)
            }
            composable(AmbarScreen.PLANIFICACION.route) {
                PlanificacionScreen()
            }
        }
    }

    if (showQuickAdd) {
        QuickAddSheet(
            repository = repository,
            onDismiss = { showQuickAdd = false },
            onShowSnackbar = { msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        )
    }
}
