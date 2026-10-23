package com.ebydev.devpocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ebydev.devpocket.app.navigation.AppNavHost
import com.ebydev.devpocket.core.ui.DevPocketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevPocketTheme {
                AppNavHost()
            }
        }
    }
}
