package com.raven.odyssey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.raven.odyssey.ui.navigation.AppNavDisplay
import com.raven.odyssey.ui.theme.OdysseyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use dark status bar icons (black/grey) for light backgrounds.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                /* scrim = */ android.graphics.Color.TRANSPARENT,
                /* darkScrim = */ android.graphics.Color.TRANSPARENT,
            ),
        )

        setContent {
            OdysseyTheme {
                AppNavDisplay()
            }
        }
    }
}