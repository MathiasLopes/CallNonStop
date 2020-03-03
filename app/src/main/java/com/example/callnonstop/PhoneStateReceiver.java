package com.example.callnonstop;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {

    public static String TAG="PhoneStateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        //MainActivity mActivity = (MainActivity) context;

        //mActivity.addLog("TEST");

       /* if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG,"PhoneStateReceiver**Call State=" + state);

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                Log.d(TAG,"PhoneStateReceiver**Idle");
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                // Incoming call
                String incomingNumber =
                        intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG,"PhoneStateReceiver**Incoming call " + incomingNumber);

                if (!killCall(context)) { // Using the method defined earlier
                    Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
                }

            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d(TAG,"PhoneStateReceiver **Offhook");
                killCall(context);
            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG,"PhoneStateReceiver **Outgoing call " + outgoingNumber);

            setResultData(null); // Kills the outgoing call

        } else {
            Log.d(TAG,"PhoneStateReceiver **unexpected intent.action=" + intent.getAction());
        }*/
    }

    public boolean killCall(Context context) {

        /*Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
*/
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }

}