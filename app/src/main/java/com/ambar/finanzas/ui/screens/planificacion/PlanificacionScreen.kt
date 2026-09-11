package com.ambar.finanzas.ui.screens.planificacion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Suscripciones", "Metas", "Apartadas", "Calendario")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Planificación") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (selectedTab) {
                    0 -> Text("Suscripciones Activas")
                    1 -> Text("Metas de Ahorro")
                    2 -> Text("Platas Apartadas")
                    3 -> Text("Calendario de Pagos")
                }
            }
        }
    }
}
