package com.fhws.transport

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fhws.transport.ui.App
import com.fhws.transport.ui.theme.FhwsTransportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        enableEdgeToEdge()
        setContent {
            FhwsTransportTheme {
                App()
            }
        }
    }
}