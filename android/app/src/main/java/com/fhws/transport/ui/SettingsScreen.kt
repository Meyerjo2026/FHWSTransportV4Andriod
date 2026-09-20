package com.fhws.transport.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fhws.transport.ServerSettings

@Composable
fun SettingsScreen(
    currentUrl: String,
    onSaveUrl: (String) -> Unit,
    onClearCache: () -> Unit,
) {
    val context = LocalContext.current
    var url by remember { mutableStateOf(currentUrl) }
    var message by remember { mutableStateOf<String?>(null) }
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text("Point the app at your deployed FHWS Transport server.", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Server URL") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { onSaveUrl(url.trim().trimEnd('/')) }) {
                Text("Save & connect")
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = {
                url = ServerSettings.DEFAULT_BASE_URL
                onSaveUrl(ServerSettings.DEFAULT_BASE_URL)
            }) {
                Text("Reset to default")
            }
        }

        if (message != null) {
            Spacer(Modifier.height(8.dp))
            Text(message!!, color = primary, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(28.dp))

        OutlinedButton(onClick = {
            onClearCache()
            message = "Cookies and cache cleared."
        }) {
            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Clear cookies & cache")
        }

        Spacer(Modifier.height(28.dp))

        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Text("Sign in with one of the FHWS Transport demo accounts:", style = MaterialTheme.typography.bodyMedium)
                Text("Admin    admin@cput.ac.za  /  admin123", style = MaterialTheme.typography.bodySmall)
                Text("Staff     staff@cput.ac.za  /  staff123", style = MaterialTheme.typography.bodySmall)
                Text("Student   student@mycput.ac.za  /  student123", style = MaterialTheme.typography.bodySmall)
                Text(
                    "The student, staff and admin areas appear automatically after you sign in.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}