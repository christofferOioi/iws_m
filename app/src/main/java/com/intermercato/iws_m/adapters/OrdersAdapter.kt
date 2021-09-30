package com.intermercato.iws_m.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.intermercato.iws_m.R
import com.intermercato.iws_m.databinding.RowOrderLayoutBinding
import com.intermercato.iws_m.mApplication
import com.intermercato.iws_m.realmModels.Order


class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrderHolder>() {

    private var data : List<Order> = ArrayList()

    companion object {
        const val TAG ="api"
    }

    var listener : ClickCallBack? = null
    fun setClickListener(l :ClickCallBack){
        listener = l
    }

    interface ClickCallBack {
        fun onClickOrder(order: Order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {

        val v = RowOrderLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderHolder(v)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        Log.d(TAG,"onBindHolder")
        holder.bin(data[position],listener)
    }

    override fun getItemCount() = data.size

    fun submitData( _data : List<Order>){
        data= _data
    }

    class OrderHolder(private  val itemBinding: RowOrderLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root){


        fun bin(order : Order, listener : ClickCallBack?){
            itemBinding.apply {
                txtShipName.text = order.orderShipName
                txtOrderNumber.text = order.orderNumber
                txtArrivalDate.text = order.arrivalDate
                txtSpotChecks.text = order.usesSpotCheck.toString()
                txtOrderMessagesTitle.text = mApplication.applicationContext().getString(R.string.txtMessageAsTitle)
                txtOrderMessages.text = order.message
                itemView.setOnClickListener {
                    listener?.onClickOrder(order)
                }
            }
        }
    }
}
