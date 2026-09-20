package com.fhws.transport

import android.content.Context

/**
 * Server URL persistence. The app is a WebView shell around the FHWS
 * Transport web app, so the server can be pointed at any deployed
 * instance (the live Render app by default).
 */
object ServerSettings {

    private const val PREFS = "fhws_prefs"
    private const val KEY_BASE_URL = "base_url"

    const val DEFAULT_BASE_URL = "https://fhwstransportv2-1.onrender.com"

    fun baseUrl(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_BASE_URL, DEFAULT_BASE_URL)
            ?: DEFAULT_BASE_URL

    fun setBaseUrl(context: Context, url: String) {
        val trimmed = url.trim().trimEnd('/')
        val cleaned = if (trimmed.isEmpty()) DEFAULT_BASE_URL else trimmed
        val http = if (!cleaned.startsWith("https://") && !cleaned.startsWith("http://")) {
            "https://$cleaned"
        } else {
            cleaned
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BASE_URL, http.trimEnd('/'))
            .apply()
    }
}