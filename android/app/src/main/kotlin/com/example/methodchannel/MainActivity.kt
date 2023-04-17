package com.example.methodchannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {
     private val CHANNEL = "samples.flutter.dev/battery"

     override  fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
                if (call.method == "getBatteryLevl") {
                    var msg : SmS = SmS()
                    if (batteryLevel != -1) {
                    result.success(batteryLevel)
                  } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                 }
                } else {
                    result.notImplemented()
                }
            }

        }
    }

    private fun getBatteryLevel(): Int{
        val batteryLevel: Int
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP){
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        }
        else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }
    
}

class SmS : BroadcastReceiver() {
    var msgs_list : MutableList<String> = mutableListOf()
    override fun onReceive(Context :Context?, Intent: Intent?) {
    if(Intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION){
        val bundle: Bundle? = Intent.getExtras() //---get the SMS message passed in---

        var msgs: Array<SmsMessage?>? = null
        var msg_from: String
        if (bundle != null) {
            //---retrieve the SMS message received---
            try {
                val pdus = bundle["pdus"] as Array<Any>?
                msgs = arrayOfNulls<SmsMessage>(pdus!!.size)
                for (i in msgs.indices) {
                    msgs!![i] = SmsMessage.createFromPdu(pdus!![i] as ByteArray)
                    val msgBody: String? = msgs!![i]?.getMessageBody()
                    if (msgBody != null) {
                        msgs_list.add(msgBody)
                    }

                }
            } catch (e: Exception) {
//                            Log.d("Exception caught",e.getMessage());
            }
        }
    }
    }
    fun getMessages(): MutableList<String>{
        return msgs_list
    }

}


