package com.dozmaden.rookievpn.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import bot.box.appusage.utils.UsageUtils
import com.dozmaden.rookievpn.R

//fun Context.isAccessibilitySettingsOn(): Boolean {
//    try {
//        val accessibilityEnabled = Settings.Secure.getInt(
//            contentResolver,
//            Settings.Secure.ACCESSIBILITY_ENABLED
//        )
//        if (accessibilityEnabled == 1) {
//            val services = Settings.Secure.getString(
//                contentResolver,
//                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
//            )
//            if (services != null) {
//                return services.contains(packageName, ignoreCase = true)
//            }
//        }
//    } catch (e: Throwable) {
//    }
//    return false
//}

fun Context.getAppsList(): List<ResolveInfo> {
    val main = Intent(Intent.ACTION_MAIN, null)
    main.addCategory(Intent.CATEGORY_LAUNCHER);
    return packageManager.queryIntentActivities(main, 0)
        .distinctBy {
            it.activityInfo.packageName
        }
        .filter {
            it.activityInfo.packageName != packageName
        }
}

fun appLabel(context: Context, packageName: String): String =
    UsageUtils.parsePackageName(context.packageManager, packageName)

fun appIcon(packageName: String): Drawable? =
    UsageUtils.parsePackageIcon(packageName, R.drawable.ic_home_black_24dp)