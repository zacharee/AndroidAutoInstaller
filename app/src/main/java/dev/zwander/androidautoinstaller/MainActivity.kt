package dev.zwander.androidautoinstaller

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import dev.zwander.androidautoinstaller.ui.theme.AndroidAutoInstallerTheme
import dev.zwander.androidautoinstaller.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent {
                finish()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContent(onCloseApp: () -> Unit = {}) {
    val context = LocalContext.current

    var isLoading by remember {
        mutableStateOf(false)
    }
    var awaitingRequestResult by remember {
        mutableStateOf(false)
    }
    var canInstall by remember {
        mutableStateOf(context.canInstall)
    }

    val scope = rememberCoroutineScope()

    val apkPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it != null) {
            scope.launch {
                val export = context.createCacheUri("temp_apk.apk")

                withContext(Dispatchers.IO) {
                    context.copyApkToCache(it, export)
                }

                isLoading = false

                context.installApk(export)
            }
        } else {
            isLoading = false
        }
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (awaitingRequestResult) {
                    awaitingRequestResult = false
                    canInstall = context.canInstall
                }
            }
            else -> {}
        }
    }

    AndroidAutoInstallerTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }
                    canInstall -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.usage_desc),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.size(64.dp))

                            OutlinedButton(
                                onClick = {
                                    isLoading = true
                                    apkPicker.launch(arrayOf("application/vnd.android.package-archive"))
                                },
                                modifier = Modifier.heightIn(64.dp).fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.select_apk),
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                    else -> {
                        AlertDialog(
                            onDismissRequest = {},
                            buttons = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    OutlinedButton(onClick = onCloseApp) {
                                        Text(stringResource(id = R.string.close_app))
                                    }

                                    OutlinedButton(onClick = {
                                        awaitingRequestResult = true
                                        context.requestInstall()
                                    }) {
                                        Text(stringResource(id = R.string.allow))
                                    }
                                }
                            },
                            title = {
                                Text(stringResource(id = R.string.allow_installs_title))
                            },
                            text = {
                                Text(stringResource(id = R.string.allow_installs_message))
                            }
                        )
                    }
                }
            }
        }
    }
}