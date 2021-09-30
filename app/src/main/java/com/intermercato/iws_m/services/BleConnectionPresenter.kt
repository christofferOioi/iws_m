package com.intermercato.iws_m.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.preference.PreferenceManager
import com.intermercato.iws_m.Constants.BLE_BOX_ID



class BleConnectionPresenter(val context: BleConnectionService) : BleConnectionContract.Presenter {

    private val TAG = "servieble"
    private var mBound: Boolean = false
    private var bleService: BluetoothLeService? = null

    init {
        Log.d(TAG, "init presenter")

    }


    override fun connect() {

        if (mBound) {
            if (bleService?.initialize() == false) {
                Log.d(TAG, "connect false")
            } else {
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                val address = preferences.getString(BLE_BOX_ID, null)
                Log.d(TAG, "connect initializing " + preferences.getString(BLE_BOX_ID, null))
                bleService?.connect(address)
            }
        }
    }

    override fun disconnect() {
        Log.d(TAG, "init presenter")
        if (mBound) {
            bleService?.disconnect()
        }
    }

    override fun doCommand(command: String?) {
        Log.d(TAG, "init doCommand ")
        if (mBound) {
            if(bleService!= null){


                    bleService?.WriteJson(command)

            }
        }
    }

    // BIND BLE connection service
    override fun doBind() {
        Log.d(TAG, "binding bluetooth le service")
        var intent = Intent(context, BluetoothLeService::class.java).also { intent ->
            context.bindService(intent, bleconnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun unBind() {
        Log.d(TAG, "unbinding bluetooth le service")
        if (mBound) {
            context.unbindService(bleconnection)
            mBound = false
        }

    }

    public fun getServiceConnection(): ServiceConnection {
        return bleconnection
    }

    private val bleconnection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothLeService.LocalBinder
            bleService = binder.getService()
            mBound = true
            //connect()
            Log.d(TAG, "LE onServiceConnected $mBound")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG, "LE onServiceDisconnected $mBound")
            mBound = false
        }
    }
}