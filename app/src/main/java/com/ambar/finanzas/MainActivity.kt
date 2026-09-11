package com.ambar.finanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ambar.finanzas.ui.navigation.AmbarNavigation
import com.ambar.finanzas.ui.theme.AmbarFinanzasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AmbarApp

        setContent {
            AmbarFinanzasTheme {
                AmbarNavigation(repository = app.repository)
            }
        }
    }
}
