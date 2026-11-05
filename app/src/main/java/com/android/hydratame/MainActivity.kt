package com.android.hydratame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.hydratame.ui.HydrataMEApp
import com.android.hydratame.ui.theme.HydrataMETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HydrataMETheme {
                HydrataMEApp()
            }
        }
    }
}
