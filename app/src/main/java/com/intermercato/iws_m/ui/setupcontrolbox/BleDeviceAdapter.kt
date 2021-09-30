package com.intermercato.iws_m.ui.setupcontrolbox

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.intermercato.iws_m.databinding.ScannedBleDevicesBinding
import com.intermercato.iws_m.entities.BleItem


class BleDeviceAdapter(private var devices: ArrayList<BleItem>?) : RecyclerView.Adapter<BleDeviceHolder>() {

    private var listener: onClickCallBack? = null

    fun setListener(l: onClickCallBack?) {
        listener = l
    }

    interface onClickCallBack {
        fun onClickItem(device: BleItem?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceHolder {
      /*  val v = LayoutInflater.from(parent.context).inflate(R.layout.scanned_ble_devices, parent, false)
        return BleDeviceHolder(v)*/
        val biding = ScannedBleDevicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return BleDeviceHolder(biding,listener!!)
    }

    override fun getItemCount(): Int {
        return devices?.size!!
    }

    override fun onBindViewHolder(holder: BleDeviceHolder, position: Int) {
        val device: BleItem? = devices?.get(position)
        device?.let {
            holder.onBind(it)
        }

        if(device?.isActive == true){

        }

        holder.itemView.setOnClickListener {
            Log.d("adapter", "click")
            listener?.onClickItem(device,position)
        }
    }


    fun addItem(device: BleItem) {
        Log.d("adapter", "add device")
        devices?.add(device)
        var p: Int? = (devices?.size!! -1) //devices?.size?.minus(-1))

        this.notifyItemInserted(p!!)
    }
}