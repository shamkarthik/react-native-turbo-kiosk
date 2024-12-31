package com.awesomelibrary

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


class BootReceiver : BroadcastReceiver() {

//  private var packageName: String? = null
//
//  // You may want to initialize the package name when setting up the receiver in the application.
//  fun setPackageName(packageName: String) {
//    this.packageName = packageName
//  }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
//        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
//            Log.d("BootReceiver", "Device boot completed. Starting Foreground Service...")
//
//            // Start Foreground Service
//            val serviceIntent = Intent(context, ForegroundService::class.java)
//            context.startForegroundService(serviceIntent) // Required for Android 8.0+ (API 26+)
//          Log.d("BootReceiver", "packageName ${context.packageName}")
//          // Launch Main Activity (to lock the app into kiosk mode)
//          if (context.packageName != null) {
//            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
//            launchIntent?.let {
//              it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//              context.startActivity(it)  // Launch the host app's main activity
//            }
//          }
//        }
      if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
        val sharedPreferences = context.getSharedPreferences("KioskPrefs", Context.MODE_PRIVATE)

        val isKioskEnabled = sharedPreferences.getBoolean("isKioskEnabled", false)

        Log.d("BootReceiver", "Boot completed, checking Kiosk Mode: $isKioskEnabled")

        if (isKioskEnabled) {
          // Start the foreground service
          val serviceIntent = Intent(context, ForegroundService::class.java)
          context.startForegroundService(serviceIntent)

          // Launch the app
          val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
          context.startActivity(launchIntent)
        }
      }
    }


}
