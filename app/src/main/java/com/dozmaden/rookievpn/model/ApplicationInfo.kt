package com.dozmaden.rookievpn.model

import android.graphics.drawable.Drawable

data class ApplicationInfo(
    val packageName: String,
    val appLabel: String,
    val appIcon: Drawable?
)
