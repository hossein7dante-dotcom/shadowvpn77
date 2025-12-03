package com.example.submgr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val SUB_URL = "https://shadowmere.xyz/api/b64sub/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val repo = SubRepository(db.configDao())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ConfigViewModel(repo) as T
            }
        }

        setContent {
            val vm: ConfigViewModel = viewModel(factory = factory)
            val items by vm.configs.collectAsState(initial = emptyList())

            var loading by remember { mutableStateOf(false) }
            val ctx = LocalContext.current

            LaunchedEffect(Unit) {
                loading = true
                vm.refresh(SUB_URL) { err ->
                    Toast.makeText(ctx, "Error: $err", Toast.LENGTH_LONG).show()
                }
                loading = false
            }

            Scaffold(
                topBar = { TopAppBar(title = { Text("Sub Manager") }) },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        loading = true
                        vm.refresh(SUB_URL) { err ->
                            Toast.makeText(ctx, "Error: $err", Toast.LENGTH_LONG).show()
                        }
                        loading = false
                    }) {
                        Text("Update")
                    }
                }
            ) { padding ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(12.dp)
                ) {
                    if (loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items) { item ->
                            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(text = item.remark ?: item.type + " • " + item.raw.take(40))
                                        Spacer(Modifier.height(6.dp))
                                        Text(text = item.raw, style = MaterialTheme.typography.bodySmall)
                                    }

                                    Column {
                                        Switch(
                                            checked = item.enabled,
                                            onCheckedChange = { vm.toggle(item) }
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        val context = LocalContext.current
                                        Button(onClick = {
                                            // copy to clipboard
                                            copyToClipboard(context, item.raw)
                                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Text("Copy")
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Button(onClick = {
                                            // share via intent
                                            shareText(context, item.raw)
                                        }) {
                                            Text("Share")
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Button(onClick = {
                                            // try open in v2rayNG or shadowsocks if installed
                                            tryOpenInClient(context, item)
                                        }) {
                                            Text("Open in Client")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun copyToClipboard(ctx: Context, text: String) {
        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("config", text))
    }

    private fun shareText(ctx: Context, text: String) {
        val intent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }, null)
        ctx.startActivity(intent)
    }

    private fun tryOpenInClient(ctx: Context, item: ConfigItem) {
        val wellKnown = listOf(
            "com.v2ray.ang",
            "com.github.ishulx.v2ray",
            "com.github.shadowsocks"
        )

        for (pkg in wellKnown) {
            val pm = packageManager
            try {
                val intent = pm.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    val share = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, item.raw)
                    }
                    ctx.startActivity(share)
                    return
                }
            } catch (_: Exception) { /* continue */ }
        }

        val uri = Uri.parse("market://search?q=v2ray")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(goToMarket)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=v2ray")))
        }
    }
