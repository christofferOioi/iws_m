package com.intermercato.iws_m.ui.setupcontrolbox


import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.intermercato.iws_m.R
import com.intermercato.iws_m.databinding.ScannedBleDevicesBinding
import com.intermercato.iws_m.entities.BleItem


class BleDeviceHolder(private val binding : ScannedBleDevicesBinding, l : BleDeviceAdapter.onClickCallBack) : RecyclerView.ViewHolder(binding.root) {

/*
    private var deviceText : TextView? =  v.findViewById(R.id.txtBleDevice)
    private var adressText : TextView? =  v.findViewById(R.id.txtAdress)
    private var btIcon : ImageView? = v.findViewById(R.id.imgIcon)*/

    init {
        binding.root.setOnClickListener {

        }

    }

    fun onBind(d : BleItem){
        Log.d("adapter", "d " + d.name +" " +d.adress+"  "+ d.isActive)

        binding.apply {

            txtBleDevice.text = d.name
            txtAdress.text = d._adress

            when(d.isActive) {
                true -> imgIcon?.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24)
                else -> imgIcon?.setImageResource(R.drawable.ic_baseline_bluetooth_24)
            }
        }
    }
}