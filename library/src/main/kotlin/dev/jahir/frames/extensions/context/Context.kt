package dev.jahir.frames.extensions.context

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.annotation.XmlRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import dev.jahir.frames.R
import dev.jahir.frames.data.Preferences
import dev.jahir.frames.extensions.resources.deleteEverything
import dev.jahir.frames.extensions.resources.dirSize
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.isLink
import dev.jahir.frames.ui.activities.base.BaseThemedActivity
import java.io.File


@Suppress("DEPRECATION")
fun Context.isNetworkAvailable(): Boolean {
    try {
        var connectivityManager: ConnectivityManager? = try {
            ContextCompat.getSystemService(this, ConnectivityManager::class.java)
        } catch (ignored: Exception) {
            null
        }
        if (connectivityManager == null)
            try {
                connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
            } catch (ignored: Exception) {
            }
        connectivityManager ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            capabilities?.let {
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                        || it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } ?: false
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo ?: return false
            val connected =
                activeNetworkInfo.isAvailable && activeNetworkInfo.isConnectedOrConnecting
            return connected || connectivityManager.allNetworkInfo.any { it.isConnectedOrConnecting }
        }
    } catch (ignored: Exception) {
        return false
    }
}

@Suppress("DEPRECATION")
inline val Context.isWifiConnected: Boolean
    get() {
        try {
            var connectivityManager: ConnectivityManager? = try {
                ContextCompat.getSystemService(this, ConnectivityManager::class.java)
            } catch (ignored: Exception) {
                null
            }
            if (connectivityManager == null)
                try {
                    connectivityManager =
                        getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
                } catch (ignored: Exception) {
                }
            connectivityManager ?: return false

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo ?: return false
                val connected =
                    activeNetworkInfo.isAvailable && activeNetworkInfo.isConnectedOrConnecting
                return connected && activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
            }
        } catch (ignored: Exception) {
            return false
        }
    }

fun Context.resolveColor(@AttrRes attr: Int, @ColorInt fallback: Int = 0): Int {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getColor(0, fallback)
    } finally {
        a.recycle()
    }
}

fun Context.stringArray(@ArrayRes resId: Int, fallback: Array<String> = arrayOf()): Array<String> =
    try {
        resources.getStringArray(resId)
    } catch (e: Exception) {
        fallback
    }

fun Context.string(@StringRes resId: Int, vararg formatArgs: Any? = arrayOf()): String =
    try {
        getString(resId, *formatArgs.map { it.toString() }.toTypedArray())
    } catch (e: Exception) {
        ""
    }

fun Context.color(@ColorRes id: Int, @ColorInt fallback: Int = 0): Int =
    try {
        ContextCompat.getColor(this, id)
    } catch (e: Exception) {
        fallback
    }

fun Context.boolean(@BoolRes id: Int, fallback: Boolean = false): Boolean =
    try {
        resources.getBoolean(id)
    } catch (e: Exception) {
        fallback
    }

fun Context.integer(@IntegerRes id: Int, fallback: Int = 0): Int =
    try {
        resources.getInteger(id)
    } catch (e: Exception) {
        fallback
    }

fun Context.dimen(@DimenRes id: Int, fallback: Float = 0F): Float =
    try {
        resources.getDimension(id)
    } catch (e: Exception) {
        fallback
    }

fun Context.dimenPixelSize(@DimenRes id: Int, fallback: Int = 0): Int =
    try {
        resources.getDimensionPixelSize(id)
    } catch (e: Exception) {
        fallback
    }

fun Context.drawable(@DrawableRes resId: Int, fallback: Drawable? = null): Drawable? =
    try {
        AppCompatResources.getDrawable(this, resId)
    } catch (e: Exception) {
        fallback
    }

fun Context.drawable(name: String?): Drawable? {
    name ?: return null
    if (!name.hasContent()) return null
    return try {
        drawable(resources.getIdentifier(name, "drawable", packageName))
    } catch (e: Exception) {
        null
    }
}

fun Context.xml(@XmlRes xmlRes: Int): XmlResourceParser? =
    try {
        resources.getXml(xmlRes)
    } catch (e: Exception) {
        null
    }

fun Context.withXml(@XmlRes xmlRes: Int, what: (parser: XmlResourceParser) -> Unit) {
    try {
        xml(xmlRes)?.use(what)
    } catch (e: Exception) {
    }
}

fun Context.toast(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, res, duration).show()
}

fun Context.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, content, duration).show()
}

@ColorInt
fun Context.getRightNavigationBarColor(): Int {
    val mode = (getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager)?.nightMode
    val isNightMode = mode == UiModeManager.MODE_NIGHT_YES
            && preferences.currentTheme != Preferences.ThemeKey.LIGHT
    val shouldBeBlack =
        !preferences.shouldColorNavbar || (isNightMode && preferences.usesAmoledTheme)
    return if (!shouldBeBlack) {
        try {
            resolveColor(R.attr.colorSurface, color(R.color.surface))
        } catch (e: Exception) {
            Color.parseColor("#000000")
        }
    } else {
        Color.parseColor("#000000")
    }
}

@Suppress("DEPRECATION")
val Context.currentVersionCode: Long
    get() = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, 0).longVersionCode
        } else {
            packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
        }
    } catch (e: Exception) {
        -1L
    }

@Suppress("DEPRECATION")
val Context.currentVersionName: String?
    get() = try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: Exception) {
        "Unknown"
    }

fun Context.getAppName(): String {
    var name: String = try {
        (packageManager?.getApplicationLabel(applicationInfo) ?: "").toString()
    } catch (e: Exception) {
        ""
    }
    if (name.hasContent()) return name

    val stringRes = applicationInfo?.labelRes ?: 0
    name = if (stringRes == 0) {
        applicationInfo?.nonLocalizedLabel?.toString() ?: ""
    } else {
        string(stringRes)
    }
    if (name.hasContent()) return name

    val def = string(R.string.app_name)
    return if (def.hasContent()) def else "Unknown"
}

val Context.isFirstRun: Boolean
    get() {
        return try {
            preferences.isFirstRun
        } catch (e: Exception) {
            true
        }
    }

val Context.isUpdate: Boolean
    get() {
        val prevVersion = preferences.lastVersion
        preferences.lastVersion = currentVersionCode
        return currentVersionCode > prevVersion
    }

fun Context.compliesWithMinTime(time: Long): Boolean =
    System.currentTimeMillis() - firstInstallTime > time

val Context.firstInstallTime: Long
    get() {
        return try {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        } catch (e: Exception) {
            -1
        }
    }

@SuppressLint("QueryPermissionsNeeded")
fun Context.openLink(url: String?) {
    val link = url ?: return
    val actualLink = if (link.isLink()) link else "http://$link"
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(actualLink))
    if (browserIntent.resolveActivity(packageManager) != null) {
        startActivity(browserIntent)
    } else {
        try {
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Cannot find a browser")
        }
    }
}

val Context.dataCacheSize: String
    get() {
        var cache: Long = 0
        var extCache: Long = 0

        try {
            cacheDir?.listFiles()?.forEach {
                cache += if (it.isDirectory) it.dirSize else it.length()
            }
        } catch (ignored: Exception) {
        }

        try {
            externalCacheDir?.listFiles()?.forEach {
                extCache += if (it.isDirectory) it.dirSize else it.length()
            }
        } catch (ignored: Exception) {
        }

        val finalResult = ((cache + extCache) / 1024).toDouble()

        return if (finalResult > 1024) String.format("%.2f", finalResult / 1024) + " MB"
        else String.format("%.2f", finalResult) + " KB"
    }

fun Context.clearDataAndCache() {
    val appDir = File(cacheDir?.parent ?: return)
    appDir.let {
        if (it.exists()) {
            it.list()?.forEach { fl ->
                if (!fl.equals("lib", true)) File(appDir, fl).deleteEverything()
            }
        }
    }
    clearCache()
}

fun Context.clearCache() {
    try {
        cacheDir?.deleteEverything()
    } catch (ignored: Exception) {
    }
}

val Context.preferences: Preferences
    get() = (this as? BaseThemedActivity<*>)?.preferences ?: Preferences(this)

internal fun Context.setDefaultDashboardTheme() {
    try {
        AppCompatDelegate.setDefaultNightMode(
            when (preferences.currentTheme) {
                Preferences.ThemeKey.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                Preferences.ThemeKey.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY or AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
        (this as? AppCompatActivity)?.delegate?.applyDayNight()
    } catch (e: Exception) {
    }
}