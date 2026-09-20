package com.fhws.transport.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

data class WebViewState(
    val url: String = "",
    val loading: Boolean = true,
    val progress: Int = 0,
    val error: String? = null,
)

/**
 * A Compose wrapper around a configured [WebView]. Internal navigation
 * (same host as the configured server) stays inside the WebView; any
 * other host falls through to the system browser.
 */
@Composable
fun FhwsWebView(
    baseUrl: String,
    state: MutableState<WebViewState>,
    webViewRef: MutableState<WebView?>,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.databaseEnabled = true
                settings.mediaPlaybackRequiresUserGesture = true
                settings.loadWithOverviewMode = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.userAgentString = "${settings.userAgentString} FHWSTransportApp/1.0"

                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        val target = request.url.toString()
                        val internal = baseHostMatches(baseUrl, target)
                        if (!internal) {
                            openInBrowser(context, target)
                        }
                        return !internal
                    }

                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        state.value = state.value.copy(url = url, loading = true, progress = 0, error = null)
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        state.value = state.value.copy(loading = false, progress = 100)
                    }

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: WebResourceError,
                    ) {
                        if (request.isForMainFrame) {
                            state.value = state.value.copy(
                                error = error.description?.toString() ?: "Could not load the page.",
                            )
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        state.value = state.value.copy(progress = newProgress)
                    }
                }

                webViewRef.value = this
                loadUrl(baseUrl)
            }
        },
        update = { web ->
            // Only reload when the configured server actually changed —
            // in-app navigation within the same host is left untouched.
            val current = web.url
            if (current == null || Uri.parse(baseUrl).host != Uri.parse(current).host) {
                web.loadUrl(baseUrl)
            }
        },
    )
}

/** Links only stay in-app when they point at the configured server host. */
internal fun baseHostMatches(baseUrl: String, target: String): Boolean =
    Uri.parse(baseUrl).host == Uri.parse(target).host

internal fun openInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // No browser available; give up silently.
    }
}