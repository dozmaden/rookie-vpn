package com.dozmaden.rookievpn.model

import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.utils.VpnUtilities.getImgURL

data class VpnServer(
    val country: String,
    val filename: String,
    val username: String,
    val password: String,
    val flagUrl: String? = getImgURL(R.drawable.placeholderflag)
)