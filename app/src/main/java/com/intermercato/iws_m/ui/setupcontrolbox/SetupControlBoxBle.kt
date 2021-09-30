package com.intermercato.iws_m.ui.setupcontrolbox


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.intermercato.iws_m.Constants.BLE_BOX_ID
import com.intermercato.iws_m.Constants.REQUEST_ENABLE_BT
import com.intermercato.iws_m.R
import com.intermercato.iws_m.databinding.FragmentSetupControlboxBleBinding
import com.intermercato.iws_m.entities.BleItem


class SetupControlBoxBle : Fragment(R.layout.fragment_setup_controlbox_ble), BleDeviceAdapter.onClickCallBack {

    private var setupControlBoxBle : FragmentSetupControlboxBleBinding? = null
    private val binding get() = setupControlBoxBle!!
    private var devices: ArrayList<BleItem>? = arrayListOf()
    private var bleAdapter : BleDeviceAdapter? = null
    private val TAG = "blue"

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var mScanning = false
    private var mHandler: Handler? = null
    private val SCAN_PERIOD: Long = 10000


/*    class BleItem(name: String?, adress: String?, isActive: Boolean?) {
        var isActive: Boolean? = isActive
        var name: String? = name
        var adress: String? = adress
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupControlBoxBle = FragmentSetupControlboxBleBinding.bind(view)


        binding.apply {

            // init adapter, add devices
            bleAdapter = BleDeviceAdapter(devices)


            // add adapter to recyclerview
            rvScanForDevices.setHasFixedSize(true)
            rvScanForDevices.adapter = bleAdapter
            btnScan.setOnClickListener {

                scanLeDevice(true)
            }

        }

        bleAdapter?.setListener(this)

        mHandler = Handler()


        // init bluetooth adapter

        var manager: BluetoothManager? = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        mBluetoothAdapter = manager?.adapter
        bluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner
        if (mBluetoothAdapter == null) {
            Toast.makeText(activity, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val address = preferences.getString(BLE_BOX_ID, null)

        Log.d(TAG,"prefs mac address $address")
        if(!address.isNullOrBlank()) {
            binding.savedMac.text = address
        }
    }

    override fun onResume() {
        super.onResume()
        if (mBluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity?.startActivityFromFragment(this,enableBtIntent, REQUEST_ENABLE_BT)
        }

        scanLeDevice(true)
    }



    fun scanLeDevice(enable: Boolean) {

        if (enable) {

            // Stops scanning after a pre-defined scan period.
            mHandler!!.postDelayed({
                mScanning = false
                bluetoothLeScanner?.stopScan(mscanCallback)
                //invalidateOptionsMenu()
            }, SCAN_PERIOD)

            mScanning = true
            val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("0f9d2e5f-f404-461c-83ef-02463376b85f")).build()
            val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
            val scanFilters: MutableList<ScanFilter> = java.util.ArrayList()
            scanFilters.add(filter)
            bluetoothLeScanner?.startScan(scanFilters, settings, mscanCallback)
            Log.d(TAG,"startScan  --->")

        } else {
            Log.d(TAG,"scanLeDevice   $enable")
            mScanning = false
            bluetoothLeScanner!!.stopScan(mscanCallback)

        }
    }

    private val mscanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            try {
                //ScanRecord scanRecord = result.getScanRecord();
                Log.d(TAG,"onScanResult "+result.device.address)

                activity?.runOnUiThread {

                    if(devices?.size == 0){
                        bleAdapter?.addItem(BleItem(result.device.name, result.device.address, checkActiveStatus(result.device.address)))

                    }

                    var accept = false
                    devices?.forEach {
                        if(it.adress.equals(result.device.address)){
                            Log.d(TAG, "found break loop")
                            accept= true
                            return@forEach
                        }
                    }

                    if(!accept) {
                        Log.d(TAG, "device add " + result.device.address )
                        bleAdapter?.addItem(BleItem(result.device.name, result.device.address, false))
                    }
                    devices?.forEach {
                        Log.d(TAG, "d -> " + it?.adress + "  " + it?.name)
                    }
                }
            } catch (e: Exception) {
                val error_message = e.message
            }
        }
    }

    fun checkActiveStatus(adress : String?) : Boolean? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        Log.d(TAG, "preference box id " + preferences.getString(BLE_BOX_ID, null)+"  "+adress)
        val storedAddress = preferences.getString(BLE_BOX_ID, null)
        binding.savedMac.text = storedAddress
        return adress?.equals(storedAddress)

    }

    override fun onPause() {
        super.onPause()
        devices?.clear()
        bleAdapter?.notifyDataSetChanged()
    }

    override fun onClickItem(device: BleItem?, position: Int) {
        Log.d(TAG, "clicked " + device?.name)
        val alertBuilder = AlertDialog.Builder(requireContext())
        val str = getString(R.string.ble_select_device)

        alertBuilder.setMessage(str + "\n\n" + device?.name + " " + device?.adress)
        alertBuilder.setTitle(getString(R.string.ble_connection_device))
        alertBuilder.setIcon(resources.getDrawable(R.drawable.outline_bluetooth_searching_black_24))
        alertBuilder.setPositiveButton(getString(R.string.txt_positive_button_title_ok), DialogInterface.OnClickListener { dialogInterface, i ->

            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = preferences.edit()
            editor.putString(BLE_BOX_ID, device?.adress)
            editor.commit()

            for(d in devices!!){
                d.isActive = false

            }
            device?.isActive = true
            bleAdapter?.notifyDataSetChanged()

        })

        alertBuilder.create().show()
    }
}