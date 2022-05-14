package dev.zwander.androidautoinstaller.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import dev.zwander.androidautoinstaller.R
import dev.zwander.androidautoinstaller.util.copyApkToCache
import dev.zwander.androidautoinstaller.util.createCacheFile
import dev.zwander.androidautoinstaller.util.createCacheUri
import dev.zwander.androidautoinstaller.util.installApk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun InstallView(
    onLoadingChanged: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val installlauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onLoadingChanged(false)
    }

    val apkPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it != null) {
            scope.launch {
                val export = context.createCacheFile("temp_apk.apk")

                withContext(Dispatchers.IO) {
                    context.copyApkToCache(it, export)
                }

                context.installApk(export, installlauncher)
            }
        } else {
            onLoadingChanged(false)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .widthIn(max = 600.dp)
    ) {
        val (button, text, note) = createRefs()

        Text(
            text = stringResource(id = R.string.usage_desc),
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(text) {
                bottom.linkTo(button.top, margin = 32.dp)
            }
        )

        OutlinedButton(
            onClick = {
                onLoadingChanged(true)
                apkPicker.launch(arrayOf("application/vnd.android.package-archive"))
            },
            modifier = Modifier
                .heightIn(64.dp)
                .fillMaxWidth()
                .constrainAs(button) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Text(
                text = stringResource(id = R.string.select_apk),
                fontSize = 24.sp
            )
        }

        Text(
            text = stringResource(id = R.string.usage_note),
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(note) {
                top.linkTo(button.bottom, margin = 32.dp)
            }
        )
    }
}