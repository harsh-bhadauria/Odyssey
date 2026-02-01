package com.raven.odyssey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.raven.odyssey.ui.navigation.AppNavDisplay
import com.raven.odyssey.ui.theme.OdysseyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OdysseyTheme {
                //TodoListScreenNew()
                AppNavDisplay()
            }
        }
    }
}