package com.dozmaden.rookievpn.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import bot.box.appusage.utils.UsageUtils
import com.dozmaden.rookievpn.R

object AppUtilities {

    fun Context.isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED

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
}