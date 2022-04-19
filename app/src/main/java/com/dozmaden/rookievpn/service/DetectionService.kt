package com.dozmaden.rookievpn.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dozmaden.rookievpn.MainActivity
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.preferences.SharedPreference
import de.blinkt.openvpn.OpenVpnApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

private const val NOTIFICATION_ID = 1

class DetectionService : AccessibilityService() {

    private lateinit var preferenceStorage: SharedPreference

    private var isForeground = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        preferenceStorage = SharedPreference(applicationContext)
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected() {
        super.onServiceConnected()
        scope.launch {
            preferenceStorage.focusModeStatusFlow.collect {
                updateForeground(it)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val focusModeOn = preferenceStorage.getFocusModeStatus()
        if (focusModeOn && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            event.packageName?.toString()?.let { runVpnIfAppInSelected(it) }
        }
//        Log.d("ACCESSIBILITYEVENT", "ENTERED HERE FIRST")
//        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            Log.d("ACCESSIBILITYEVENT", "ENTERED HERE")
//            event.packageName?.toString()?.let { runVpnIfAppInSelected(it) }
//        }
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateForeground(focusModeOn: Boolean) {
        if (focusModeOn && !isForeground) {
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
                    setContentTitle("UsageManager")
                    setContentText("FocusMode is active")
                    setSmallIcon(R.drawable.ic_home_black_24dp)
                    setContentIntent(pendingIntent)
                }.build()
            startForeground(NOTIFICATION_ID, notification)
            isForeground = true
        } else if (!focusModeOn && isForeground) {
            stopForeground(true)
            isForeground = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channel = NotificationChannel(
            "my_service",
            "My Background Service",
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return "my_service"
    }

    private fun runVpnIfAppInSelected(packageName: String) {
        if (preferenceStorage.isSelectedApp(packageName)) {
//            val launcherIntent = Intent(Intent.ACTION_MAIN)
//            launcherIntent.addCategory(Intent.CATEGORY_HOME)
//            launcherIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(launcherIntent)
//            (getSystemService(ACTIVITY_SERVICE) as ActivityManager).killBackgroundProcesses(
//                packageName
//            )
//            Toast.makeText(applicationContext, "NOT ALLOWED", Toast.LENGTH_SHORT).show()


            val server = preferenceStorage.server

            val conf = server?.vpn?.let { applicationContext.assets.open(it) }

            val isr = InputStreamReader(conf)
            val br = BufferedReader(isr)

            var config = ""
            var line: String?

            while (true) {
                line = br.readLine()
                if (line == null) break
                config += """
                            $line
                            
                            """.trimIndent()
            }

            br.readLine()

            OpenVpnApi.startVpn(
                applicationContext,
                config,
                server?.country,
                server?.vpnUsername,
                server?.vpnPassword
            )
        }
    }
}