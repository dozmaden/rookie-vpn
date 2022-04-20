package com.dozmaden.rookievpn.service

import android.accessibilityservice.AccessibilityService
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dozmaden.rookievpn.MainActivity
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.preferences.AppPreferences
import com.dozmaden.rookievpn.preferences.VpnPreferences
import com.dozmaden.rookievpn.utils.VpnUtilities.connectToVpn
import com.dozmaden.rookievpn.utils.VpnUtilities.isVpnConnectionActive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetectionService : AccessibilityService() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var vpnPreferences: VpnPreferences

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var isForeground = false

    override fun onCreate() {
        super.onCreate()
        appPreferences = AppPreferences(applicationContext)
        vpnPreferences = VpnPreferences(applicationContext)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected() {
        super.onServiceConnected()
        scope.launch {
            appPreferences.autoModeStatusFlow.collect {
                updateForeground(it)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val autoModeOn = appPreferences.getAutoStatus()
        if (autoModeOn && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            event.packageName?.toString()?.let { runVpnIfAppInSelected(it) }
        }
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateForeground(autoModeOn: Boolean) {
        if (!isForeground && autoModeOn) {
            val pendingIntent: PendingIntent =
                Intent(this, MainActivity::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
            val notification = Notification.Builder(this, createNotificationChannel())
                .apply {
                    setContentTitle("Rookie VPN")
                    setContentText("Auto-connect on selected apps feature is on")
                    setSmallIcon(R.drawable.ic_baseline_vpn_key_24)
                    setContentIntent(pendingIntent)
                }.build()
            startForeground(1, notification)
            isForeground = true
        } else if (isForeground && !autoModeOn) {
            stopForeground(true)
            isForeground = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channel = NotificationChannel(
            "rookie_vpn_autoconnect_service",
            "VPN Auto-connect Background Service",
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.GRAY
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return "rookie_vpn_autoconnect_service"
    }

    private fun runVpnIfAppInSelected(packageName: String) {
        val activeVpn = isVpnConnectionActive(applicationContext)
        if (!activeVpn && appPreferences.isSelectedApp(packageName)) {
            vpnPreferences.vpnServer?.let {
                connectToVpn(applicationContext, it)
                Toast.makeText(applicationContext, "VPN Launched!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}