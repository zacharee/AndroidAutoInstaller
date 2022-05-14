package dev.zwander.androidautoinstaller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import dev.zwander.androidautoinstaller.R
import dev.zwander.androidautoinstaller.util.requestInstall

@Composable
fun PermissionsPrompt(
    onCloseApp: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .widthIn(max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.allow_installs_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.allow_installs_message),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(16.dp))

        FlowRow(
            mainAxisAlignment = FlowMainAxisAlignment.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onCloseApp) {
                Text(stringResource(id = R.string.close_app))
            }

            OutlinedButton(onClick = {
                context.requestInstall()
            }) {
                Text(stringResource(id = R.string.allow))
            }
        }
    }
}