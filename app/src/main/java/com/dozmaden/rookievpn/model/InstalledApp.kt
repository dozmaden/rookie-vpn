package com.dozmaden.rookievpn.model

import android.graphics.drawable.Drawable

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?
)