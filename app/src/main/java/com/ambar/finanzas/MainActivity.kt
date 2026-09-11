package com.ambar.finanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambar.finanzas.ui.navigation.AmbarNavigation
import com.ambar.finanzas.ui.theme.AmbarFinanzasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as AmbarApp
        // New months and fresh installs never create fictional financial records.
        setContent {
            val theme by remember { app.repository.observeSetting("theme") }.collectAsStateWithLifecycle(initialValue = null)
            val dark = when (theme) { "dark" -> true; "light" -> false; else -> isSystemInDarkTheme() }
            AmbarFinanzasTheme(darkTheme = dark) { AmbarNavigation(repository = app.repository) }
        }
    }
}
