package com.awesomelibrary

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.facebook.react.ReactActivity


class CloseSystemDialogsIntentReceiver : BroadcastReceiver() {
//  override fun onReceive(context: Context, intent: Intent) {
//    Log.i("HERE", "------------------------------------HERE")
//    Toast.makeText(context, "OFFFFFFFFFFFFFFFF", Toast.LENGTH_LONG).show()
//  }

  override fun onReceive(context: Context?, intent: Intent) {
    val action = intent.action
    Log.d("Kiosk", "CloseSystemDialogsIntentReceivers")
//    when (action) {
////      Intent.ACTION_SCREEN_OFF -> ReactActivity.unlockScreen()
////      Intent.ACTION_SCREEN_ON ->                 // and do whatever you need to do here
////        BaseActivity.clearScreen()
//    }
  }
}
