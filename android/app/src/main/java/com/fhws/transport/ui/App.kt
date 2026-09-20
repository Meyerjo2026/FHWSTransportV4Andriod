package com.fhws.transport.ui

import android.app.Activity
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.fhws.transport.ServerSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val activity = context as? Activity

    var baseUrl by rememberSaveable { mutableStateOf(ServerSettings.baseUrl(context)) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    val webViewState = remember { mutableStateOf(WebViewState()) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    BackHandler {
        if (showSettings) {
            showSettings = false
        } else {
            val web = webViewRef.value
            if (web != null && web.canGoBack()) {
                web.goBack()
            } else {
                activity?.finish()
            }
        }
    }

    Scaffold(
        topBar = {
            if (showSettings) {
                TopAppBar(
                    title = { Text("Server settings") },
                    navigationIcon = {
                        IconButton(onClick = { showSettings = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            } else {
                TopAppBar(
                    title = { Text("FHWS Transport") },
                    actions = {
                        IconButton(onClick = { webViewRef.value?.reload() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reload")
                        }
                    },
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !showSettings,
                    onClick = {
                        showSettings = false
                        webViewRef.value?.reload()
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("Home") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { webViewRef.value?.reload() },
                    icon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
                    label = { Text("Reload") },
                )
                NavigationBarItem(
                    selected = showSettings,
                    onClick = { showSettings = true },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text("Server") },
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (showSettings) {
                SettingsScreen(
                    currentUrl = baseUrl,
                    onSaveUrl = { saved ->
                        ServerSettings.setBaseUrl(context, saved)
                        baseUrl = saved
                        showSettings = false
                    },
                    onClearCache = {
                        CookieManager.getInstance().removeAllCookies(null)
                        webViewRef.value?.clearCache(true)
                    },
                )
            } else {
                FhwsWebView(
                    baseUrl = baseUrl,
                    state = webViewState,
                    webViewRef = webViewRef,
                )

                if (webViewState.value.loading) {
                    LinearProgressIndicator(
                        progress = { webViewState.value.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                    )
                }

                if (webViewState.value.error != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Couldn't reach ${baseUrl.removePrefix("https://").removePrefix("http://")}")
                        TextButton(onClick = { webViewRef.value?.reload() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}