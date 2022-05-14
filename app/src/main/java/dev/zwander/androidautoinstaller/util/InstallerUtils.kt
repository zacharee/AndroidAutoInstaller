package dev.zwander.androidautoinstaller.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import dev.zwander.androidautoinstaller.R
import java.io.File

val Context.canInstall: Boolean
    get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        Settings.Secure.getInt(contentResolver, Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1
    } else {
        packageManager.canRequestPackageInstalls()
    }

fun Context.requestInstall() {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            data = Uri.parse("package:$packageName")
        }
    } else {
        Intent(Settings.ACTION_SECURITY_SETTINGS)
    }.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    startActivity(intent)
    Toast.makeText(
        this,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) R.string.allow_app_installs else R.string.allow_unknown_sources,
        Toast.LENGTH_SHORT
    ).show()
}

fun Context.createCacheUri(apkName: String): Uri {
    val apkFile = File("$cacheDir/apks", apkName)
    apkFile.parentFile?.mkdirs()
    apkFile.delete()

    return FileProvider.getUriForFile(
        this,
        "$packageName.provider",
        apkFile
    )
}

fun Context.copyApkToCache(input: Uri, output: Uri) {
    contentResolver.openInputStream(input).use { inStream ->
        contentResolver.openOutputStream(output).use { outStream ->
            inStream.copyTo(outStream)
        }
    }
}

fun Context.installApk(uri: Uri) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            data = uri
        }
    } else {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndTypeAndNormalize(uri, "application/vnd.android.package-archive")
        }
    }.apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, "com.android.vending")
    }

    startActivity(intent)
}