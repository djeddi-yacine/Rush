package com.shub39.rush.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shub39.rush.core.data.RushDatastore
import com.shub39.rush.lyrics.data.listener.MediaListener
import com.shub39.rush.core.presentation.theme.RushTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        MediaListener.init(this)

        enableEdgeToEdge()
        setContent {

            val datastore: RushDatastore = koinInject()

            val theme by datastore.getToggleThemeFlow()
                .collectAsState(initial = "")

            RushTheme(
                theme = theme
            ) {
                KoinContext {
                    RushApp()
                }
            }

            splashScreen.setKeepOnScreenCondition {
                theme == ""
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        MediaListener.destroy()
    }

    override fun onPause() {
        super.onPause()
        MediaListener.destroy()
    }

    override fun onResume() {
        super.onResume()
        MediaListener.init(this)
    }

}