package com.dozmaden.rookievpn.model

import android.graphics.drawable.Drawable

data class App(
    val packageName: String,
    val appLabel: String,
    val appIcon: Drawable?
)