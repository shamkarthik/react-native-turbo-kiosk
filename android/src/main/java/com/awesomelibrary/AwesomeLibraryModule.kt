package com.awesomelibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.core.view.doOnLayout
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = AwesomeLibraryModule.NAME)
class AwesomeLibraryModule(reactContext: ReactApplicationContext) :
  NativeAwesomeLibrarySpec(reactContext) {

  companion object {
    const val NAME = "AwesomeLibrary"
    const val REQUEST_ADMIN_PERMISSION = 101
  }
  private var packageName: String? = null
  fun setHostAppPackageName(packageName: String) {
    this.packageName = packageName
  }

  fun getHostAppPackageName(): String? {
    return packageName
  }
  private val preferences: SharedPreferences = reactContext.getSharedPreferences(
    reactContext.packageName + ".settings",
    Context.MODE_PRIVATE
  )

  private val activityManager: ActivityManager = reactContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

  override fun getName(): String {
    return NAME
  }

  private val canExitByUnpinning: Boolean
    get() = preferences.getBoolean("canExitByUnpinning", false)

  @SuppressLint("ObsoleteSdkInt")
  private fun isLockTaskModeRunning(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_LOCKED ||
        activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_PINNED
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      @Suppress("DEPRECATION")
      activityManager.isInLockTaskMode
    } else {
      false
    }
  }

  override fun isAppInLockTaskMode():Boolean {
    return isLockTaskModeRunning()
  }

  // Correctly reference the reactContext from the superclass (NativeAwesomeLibrarySpec)
//  override fun clearDeviceOwnerApp() {
//    val devicePolicyManager = reactApplicationContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
//    devicePolicyManager.clearDeviceOwnerApp(reactApplicationContext.packageName)
//  }

  private val devicePolicyManager: DevicePolicyManager =
    reactContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
  private val componentName = ComponentName(reactContext, MyDeviceAdminReceiver::class.java)

  override fun requestDeviceAdminPermission() {
    val activity = currentActivity
    if (activity == null) {
      Log.e("AwesomeLibrary", "Activity is null. Cannot request admin permissions.")
      return
    }
    Log.d("AwesomeLibrary", "requestDeviceAdminPermission triggered " + devicePolicyManager.isAdminActive(componentName))
    // Check if already a device admin
    if (!devicePolicyManager.isAdminActive(componentName)) {
      Log.d("AwesomeLibrary", "Device Admin trigger")
      val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
      intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin rights");
      activity.startActivityForResult(intent, REQUEST_ADMIN_PERMISSION);
//      val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
//        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
//        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin rights are required.")
//      }
//      activity.startActivityForResult(intent, REQUEST_ADMIN_PERMISSION)
    } else {
      Log.d("AwesomeLibrary", "Already a device admin.")
    }
  }


  override fun clearDeviceOwnerApp() {
    Log.d("AwesomeLibrary", componentName.toString())
    if (devicePolicyManager.isAdminActive(componentName)) {
      devicePolicyManager.removeActiveAdmin(componentName)
      Log.d("AwesomeLibrary", "Device admin removed.")
    } else {
      Log.d("AwesomeLibrary", "Device admin not active.")
    }
  }

  override fun startLockTaskWith(additionalPackages: ReadableArray?) {
    val activity = currentActivity
    if (activity != null) {
      val devicePolicyManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
      val admin = ComponentName(activity, MyAdmin::class.java)
      Log.d("Kiosk","startLockTaskWith devicePolicyManager" + activity.packageName)
      Log.d("Kiosk","startLockTaskWith devicePolicyManager" + devicePolicyManager.isDeviceOwnerApp(activity.packageName) )

      // Save Kiosk mode state to SharedPreferences
      val prefs = activity.getSharedPreferences("KioskPrefs", Context.MODE_PRIVATE)
      val editor = prefs.edit()
      editor.putBoolean("isKioskEnabled", true)
      editor.apply() // Save changes asynchronously

      if (devicePolicyManager.isDeviceOwnerApp(activity.packageName)) {
        Log.d("Kiosk","startLockTaskWith if")
        val packages = mutableListOf(activity.packageName)
        additionalPackages?.let {
          for (i in 0 until it.size()) {
            packages.add(it.getString(i))
          }
        }
        devicePolicyManager.setLockTaskPackages(admin, packages.toTypedArray())
        activity.startLockTask()

        devicePolicyManager.lockNow()

      } else {
        activity.startLockTask()
      }
      // Disable power off button
//      val window = activity.window
//      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//      window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
//      window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
//      window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }
  }


  override fun startLockTask() {
    startLockTaskWith(null)
  }


  override fun stopLockTask() {
    val activity = currentActivity
    activity?.stopLockTask()

    // Remove Kiosk mode state from SharedPreferences
    activity?.let {
      val prefs = it.getSharedPreferences("KioskPrefs", Context.MODE_PRIVATE)
      val editor = prefs.edit()
      editor.remove("isKioskEnabled")
      editor.apply() // Remove changes asynchronously
    }

    // Re-enable power button functionality
//    val window = activity?.window
//    window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//    window?.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
//    window?.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
//    window?.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
  }

  override fun enableExitByUnpinning() {
    preferences.edit().putBoolean("canExitByUnpinning", true).apply()
  }

  override fun disableExitByUnpinning() {
    preferences.edit().putBoolean("canExitByUnpinning", false).apply()
  }

  private fun disableGestureNavigation(activity: Activity) {
    activity.window.decorView.systemUiVisibility = (
      View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_FULLSCREEN
      )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      activity.window.decorView.doOnLayout {
        activity.window.decorView.systemGestureExclusionRects = listOf()
      }
    }
  }

  // override fun pin() {
  //   currentActivity?.let { activity ->
  //     activity.runOnUiThread {
  //       activity.startLockTask()
  //       disableGestureNavigation(activity)
  //     }
  //   }
  // }

  // override fun unpin() {
  //   currentActivity?.let { activity ->
  //     activity.runOnUiThread {
  //       activity.stopLockTask()
  //     }
  //   }
  // }
}
