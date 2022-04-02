package com.dozmaden.rookievpn.dto

data class NetworkInfo(
    val status: String,
    val continent: String,
    val country: String,
    val countryCode: String,
    val regionName: String,
    val city: String,
    val zip: String,
    val isp: String,
    val org: String,
    val reverse: String,
    val mobile: String,
    val proxy: String,
    val ip: String
)