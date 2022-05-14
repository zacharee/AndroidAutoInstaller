package dev.zwander.androidautoinstaller

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import dev.zwander.androidautoinstaller.ui.components.InstallView
import dev.zwander.androidautoinstaller.ui.components.PermissionsPrompt
import dev.zwander.androidautoinstaller.ui.theme.AndroidAutoInstallerTheme
import dev.zwander.androidautoinstaller.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MainContent {
                finish()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun MainContent(onCloseApp: () -> Unit = {}) {
    val context = LocalContext.current

    var isLoading by remember {
        mutableStateOf(false)
    }

    var canInstall by remember {
        mutableStateOf(context.canInstall)
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                canInstall = context.canInstall
            }
            else -> {}
        }
    }

    AndroidAutoInstallerTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = canInstall
                ) {
                    if (it) {
                        InstallView { loading ->
                            isLoading = loading
                        }
                    } else {
                        PermissionsPrompt(onCloseApp = onCloseApp)
                    }
                }

                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                            .zIndex(2f),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}