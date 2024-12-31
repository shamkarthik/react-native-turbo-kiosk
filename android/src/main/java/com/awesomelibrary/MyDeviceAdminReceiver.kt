package com.awesomelibrary

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        Log.d("DeviceAdminReceiver", "Device Admin enabled.")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Log.d("DeviceAdminReceiver", "Device Admin disabled.")
    }
}
