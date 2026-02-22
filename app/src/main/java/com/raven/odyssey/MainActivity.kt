package com.raven.odyssey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import com.raven.odyssey.ui.navigation.AppNavDisplay
import com.raven.odyssey.ui.theme.OdysseyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDark = isSystemInDarkTheme()

            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        // Light icons for dark theme.
                        SystemBarStyle.dark(
                            /* scrim = */ android.graphics.Color.TRANSPARENT,
                        )
                    } else {
                        // Dark icons for light theme.
                        SystemBarStyle.light(
                            /* scrim = */ android.graphics.Color.TRANSPARENT,
                            /* darkScrim = */ android.graphics.Color.TRANSPARENT,
                        )
                    },
                )
            }

            OdysseyTheme(darkTheme = isDark) {
                AppNavDisplay()
            }
        }
    }
}