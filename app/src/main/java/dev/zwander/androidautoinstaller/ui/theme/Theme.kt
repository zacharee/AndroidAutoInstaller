package dev.zwander.androidautoinstaller.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import dev.zwander.androidautoinstaller.R

@Composable
fun AndroidAutoInstallerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            darkColorScheme(
                primary = colorResource(id = R.color.colorPrimaryLight),
                secondary = colorResource(id = R.color.colorSecondary)
            )
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            lightColorScheme(
                primary = colorResource(id = R.color.colorPrimaryDark),
                secondary = colorResource(id = R.color.colorSecondary)
            )
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}