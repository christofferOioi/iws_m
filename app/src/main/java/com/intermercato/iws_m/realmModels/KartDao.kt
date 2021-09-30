package com.intermercato.iws_m.realmModels

import android.util.Log
import io.realm.Realm


open class KartDao(val realm: Realm) {

    companion object {
        const val TAG = "realm"
    }

    fun saveOrders(list: List<OtdOrder>) {
        //Log.d("realm", "saveOrder ${list.size}")

        for (order in list) {
            realm.beginTransaction()


            var _order = realm.where(Order::class.java).equalTo("id", order.id).findFirst()

            if (_order == null) {
                var newOrder = realm.createObject(Order::class.java, order.id)
                Log.d("realm", "newOrderId  ${newOrder.id}  with ${order.id} images array ${order.images?.size}")
                newOrder.orderNumber = order.orderName
                newOrder.arrivalDate = order.arrivalDate
                newOrder.orderShipName = order.shipName
                newOrder.message = order.message
                newOrder.usesSpotCheck = order.usesSpotCheck

                val imagesList: List<OtdOrder.ShipImages>? = order.images

                    imagesList?.forEach { image ->
                        var orderImage = realm.where(OrderImage::class.java).equalTo("id", image.id).findFirst()
                        if (orderImage == null) {
                            Log.d("realm", "xxxx")
                            var newOrderImage = realm.createObject(OrderImage::class.java, image.id)
                            newOrderImage.description = image.description
                            newOrderImage.url = image.url
                            newOrder.orderImages.add(newOrderImage)
                        } else {
                            Log.d("realm", "finns")
                            //newOrder?.orderImages?.add(orderImage)
                        }

                    }

                    val materials: List<OtdOrder.Material>? = order.materials

                    materials?.forEachIndexed {index, material ->
                        Log.d("realm", "xxxxbbxx")
                        val bank = realm.where(Bank::class.java).equalTo("id", material.id).findFirst()
                        if (bank == null) {
                            var newBank = realm.createObject(Bank::class.java, material.id)
                            if(index == 0){
                                newBank.active = true
                            }

                            newBank.alias = material.name
                            newBank.totalWeight = material.total!!.toInt()
                            newOrder.banks.add(newBank)
                            newBank.orderId = newOrder.id
                        }
                    }
                realm.insert(newOrder)
                Log.d("realm", "ending")
            } else {
                Log.d("realm", "order exist  ${_order.id}  with ${order.id}")
            }


            realm.commitTransaction()
        }
    }

    fun getOrders() : List<Order>{
        return realm.where(Order::class.java).findAll().also {
             realm.copyFromRealm(it)
        }
    }

    fun getOrderById(orderId : String?) : Order? {
        Log.d(TAG,"listenToOrder $orderId")

        val res = realm.where(Order::class.java).equalTo("id",orderId).findFirst()
        return realm.copyFromRealm(res)
    }
}



/*
.also { newOrder ->
Log.d("realm","order ${_order?.id}")

val materials : List<OtdOrder.Material>? = order.materials
materials?.forEach { material ->
val bank  = it.where(Bank::class.java).equalTo("id",material.id).findFirst() ?: it.createObject(Bank::class.java,material.id).also {
    it.alias = material.name
    it.totalWeight = material.total!!.toInt()
    newOrder.banks.add(it)
}
Log.d("realm"," banks ${bank.toString()}")
}

val imagesList : List<OtdOrder.ShipImages>? = order.images
imagesList?.forEach { image ->
var orderImage  = it.where(OrderImage::class.java).equalTo("id",image.id).findFirst() ?: it.createObject(OrderImage::class.java).also {
    it.description = image.description
    it.url = image.url
    newOrder.orderImages.add(it)
}
 Log.d("realm","orderImage ${orderImage.toString()}")
}

}*/