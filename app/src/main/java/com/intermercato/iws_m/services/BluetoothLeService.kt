package com.intermercato.iws_m.services

import android.app.Service
import android.bluetooth.*
import android.bluetooth.BluetoothProfile.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import org.greenrobot.eventbus.EventBus

import org.json.JSONException
import org.json.JSONObject
import se.oioi.intelweighblelib.Constants
import se.oioi.intelweighblelib.events.scale.ConnectionEvent
import se.oioi.intelweighblelib.utils.JsonParser
import java.util.*

class BluetoothLeService : Service() {

    private val TAG = "bluetooth"
    var SCALE_SERVICE: UUID = UUID.fromString("0f9d2e5f-f404-461c-83ef-02463376b85f")
    var SCALE_MEASURE: UUID = UUID.fromString("7411b4b3-e178-4854-9890-35d10524c83a")

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mCharacteristic: BluetoothGattCharacteristic? = null
    private var mConnectionState: Int = STATE_DISCONNECTED
    private var stringBuilder: StringBuilder? = null
    private var timeStampLastCommand: Long = -1
    val ACTION_GATT_CONNECTED = "com.intermercato.bluetooth.le.ACTION_GATT_CONNECTED"
    val ACTION_GATT_DISCONNECTED = "com.intermercato.bluetooth.le.ACTION_GATT_DISCONNECTED"
    val ACTION_GATT_SERVICES_DISCOVERED = "com.intermercato.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
    val ACTION_DATA_AVAILABLE = "com.intermercato.bluetooth.le.ACTION_DATA_AVAILABLE"
    val EXTRA_DATA = "com.intermercato.bluetooth.le.EXTRA_DATA"

    private val binder: IBinder? = LocalBinder()

    override fun onCreate() {
        Log.d(TAG, "onCreate bluetooth le service")
        super.onCreate()
    }


    fun initialize(): Boolean {
        // If bluetoothManager is null, try to set it
        stringBuilder = StringBuilder()

        if (mBluetoothManager == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothManager = getSystemService(BluetoothManager::class.java)
            }
            if (mBluetoothManager == null) {
                Log.d(TAG, "Unable to initialize BluetoothManager.")
                return false
            }
        }
        // For API level 18 and higher, get a reference to BluetoothAdapter through
        // BluetoothManager.
        mBluetoothManager?.let { manager ->
            mBluetoothAdapter = manager.adapter
            if (mBluetoothAdapter == null) {
                Log.d(TAG, "Unable to obtain a BluetoothAdapter.")
                return false
            }
            return true
        } ?: return false
    }

    fun disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt?.disconnect()
    }

    fun connect(address: String?): Boolean {

        if (mBluetoothAdapter == null || address == null) {
            Log.d(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        if(mConnectionState == STATE_CONNECTED){
            Log.d(TAG,"Device already connected")
            return false
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
            return if (mBluetoothGatt!!.connect()) {
                mConnectionState = STATE_CONNECTING
                true
            } else {
                false
            }
        }




        val device = mBluetoothAdapter?.getRemoteDevice(address)
        if (device == null) {
            Log.d(TAG, "Device not found.  Unable to connect.")
            return false
        }


        mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        Log.d(TAG, "Trying to create a new connection")
        mBluetoothDeviceAddress = address
        mConnectionState = STATE_CONNECTING

        return true
    }

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange $newState")
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                mConnectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG, "Connected to GATT server.")
                timeStampLastCommand  = System.currentTimeMillis() / 1000
                // Attempts to discover services after successful connection.
                EventBus.getDefault().postSticky(ConnectionEvent(Constants.ConnectionNewStates.CONNECTED_TO_BOX))
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt!!.discoverServices())
            } else if (newState == STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                EventBus.getDefault().postSticky(ConnectionEvent(Constants.ConnectionNewStates.DISCONNECTED))
                broadcastUpdate(intentAction)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "onServicesDiscovered")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                for (s in gatt.services) {
                    Log.d(TAG, "service " + s.uuid)
                    if (SCALE_SERVICE == s.uuid) {
                        Log.d(TAG, "service found")
                        val characteristic = s.getCharacteristic(SCALE_MEASURE)
                        Log.d(TAG, "service characteristic " + characteristic.uuid)
                        //gatt.requestMtu(247)
                        setCharacteristicNotification(characteristic, true)

                        return
                    }
                }

            } else {
                Log.d(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG,"onReadRemoteRssi "+rssi+"  status "+status)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.d(TAG,"ATT MTU changed to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}")

            /*if(status == BluetoothGatt.GATT_SUCCESS){
                for (s in gatt?.services!!) {
                    Log.d(TAG, "service " + s.uuid)
                    if (SCALE_SERVICE == s.uuid) {
                        Log.d(TAG, "service found")
                        mCharacteristic = s.getCharacteristic(SCALE_MEASURE)
                        setCharacteristicNotification(mCharacteristic,true)
                        Log.d(TAG, "service characteristic " + mCharacteristic?.uuid)
                        break
                    }
                }
            }*/
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.d(TAG, "onCharacteristicRead")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Log.d(TAG, "onCharacteristicWrite")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Log.d(TAG, "onCharacteristicChanged")
            mCharacteristic = characteristic
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)

            if((System.currentTimeMillis() / 1000) - timeStampLastCommand >= 4) {
                timeStampLastCommand = System.currentTimeMillis() / 1000
                mBluetoothGatt?.readRemoteRssi()
            }

        }
    }

    fun WriteJson(msg: String?): Boolean? {

        Log.d(TAG, "WriteJson $mConnectionState")

        if(mCharacteristic != null) {
            Log.d(TAG, "WriteJson $msg to characteristic ${mCharacteristic?.uuid} mConnectionState $mConnectionState")
            val writeType = when {

                mCharacteristic?.isWritable()!! -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                mCharacteristic?.isWritableWithoutResponse()!! -> BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                else -> error(" ${mCharacteristic?.uuid} cannot be written to")
            }

            mCharacteristic?.writeType = writeType
            mCharacteristic?.setValue(msg)
            mBluetoothGatt?.writeCharacteristic(mCharacteristic);
        }


        return true
    }

    fun BluetoothGattCharacteristic.isReadable(): Boolean? = containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

    fun BluetoothGattCharacteristic.isWritable(): Boolean? = containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

    fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean? = containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean? {
        return properties and property != 0
    }

    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic?, enabled: Boolean) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized")
            return
        }
        Log.d(TAG, "setCharacteristic to notify true " + characteristic?.uuid)
        mBluetoothGatt?.setCharacteristicNotification(characteristic, enabled)

        // This is specific to Heart Rate Measurement.
        if (SCALE_MEASURE == characteristic?.uuid) {
            Log.d(TAG, "setCharacteristicNotification --")
            val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            if (descriptor != null) {
                Log.d(TAG, "setCharacteristicNotification ----")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                mBluetoothGatt?.writeDescriptor(descriptor)
            }
        }
        /* if (enabled) {
             Log.d(TAG, "setCharacteristic to enable descriptor")
             val bluetoothGattDescriptor = characteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
             bluetoothGattDescriptor?.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
             mBluetoothGatt?.writeDescriptor(bluetoothGattDescriptor)
         } else {
             Log.d(TAG, "setCharacteristic to disable descriptor")
             val bluetoothGattDescriptor = characteristic?.getDescriptor(SCALE_MEASURE)
             bluetoothGattDescriptor?.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
             mBluetoothGatt?.writeDescriptor(bluetoothGattDescriptor)
         }
 */
    }

    fun broadcastUpdate(action: String?) {
        sendBroadcast(Intent(action))
    }

    var strHolder: String? = null
    fun broadcastUpdate(action: String?, characteristic: BluetoothGattCharacteristic?) {

        val intent = Intent(action)

        if (SCALE_MEASURE?.equals(characteristic?.uuid)) {
            val byte: ByteArray? = characteristic?.value
            if (byte != null && byte.isNotEmpty()) {
                var message: String? = String(byte)

                //Log.d(TAG, "broadcastUpdate $message")
                //Log.d(TAG, "cotains backlash n" + message?.contains("\n"))
                if (message?.contains("\n") == true) {
                    intent.putExtra(EXTRA_DATA, strHolder + message)
                    stringToJSON(strHolder + message)
                    strHolder = ""
                } else {

                    stringToJSON(strHolder)
                    strHolder = message
                    intent.putExtra(EXTRA_DATA, strHolder)

                }
            }
            sendBroadcast(intent)
        }
    }

    fun stringToJSON(str : String?){

        //Log.d(TAG,"json "+str)

        try{
            val obj: JSONObject? = JSONObject(str)

            val keys: MutableIterator<String>? = obj?.keys()
            // get key name
            val k = keys?.next()

            if (k?.equals(Constants.LIVE_PACKAGE)!!) {
                Log.d(TAG, "json " + str);
                val liveData = JsonParser.BuildLiveDataObject(obj.getJSONObject(Constants.LIVE_PACKAGE))
                if (liveData != null) {
       /*             Log.d(TAG, "state    : " + liveData.getState());
                    Log.d(TAG, "sub      : " + liveData.getSubstate());
                    Log.d(TAG, "status   : " + liveData.getStatestatus());
                    Log.d(TAG, "battery  : " + liveData.getBatterystate());
                    Log.d(TAG, "btnstate : " + liveData.getBtnstate());
                    Log.d(TAG, "weight   : " + liveData.getLiveweight());
                    Log.d(TAG, "workstate: " + liveData.getWorkstate());
                    Log.d(TAG, "workstate: " + liveData.getRssi());*/
                    EventBus.getDefault().post(liveData)
                }
            }else if (k?.equals(Constants.WEIGHT_PACKAGE)) {
                Log.d(TAG, "json weight " + str);
                val  autoWeight = JsonParser.BuildAutoWeightObject(obj.getJSONObject(Constants.WEIGHT_PACKAGE));
                EventBus.getDefault().post(autoWeight);
            } else if (k?.equals(Constants.PARAMETERS_PACKAGE)) {
                Log.d(TAG, "json param " + str);
                val parameters = JsonParser.BuildParametersObject(obj.getJSONObject(Constants.PARAMETERS_PACKAGE));
                EventBus.getDefault().postSticky(parameters);
            } else if (k?.equals(Constants.SCALE_INFO_PACKAGE)) {
                Log.d(TAG, "json scale info " + str);
                val scaleInformation = JsonParser.BuildScaleInformationObject(obj.getJSONObject(Constants.SCALE_INFO_PACKAGE));
                EventBus.getDefault().post(scaleInformation);
            }
        } catch (e : JSONException){


        }
    }


    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return if (mBluetoothGatt == null) null else mBluetoothGatt!!.services
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onBind(p: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }
    }


    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        if (mBluetoothGatt == null) {
            return
        }
        Log.d(TAG, "close gattServer ");
        mBluetoothGatt?.close()
        mBluetoothGatt = null
    }
}