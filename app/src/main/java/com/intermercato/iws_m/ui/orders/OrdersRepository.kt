package com.intermercato.iws_m.ui.orders

import android.util.Log
import com.intermercato.iws_m.realmModels.Order
import com.intermercato.iws_m.realmModels.OtdOrder
import com.intermercato.iws_m.realmModels.kartDao
import com.intermercato.iws_m.utils.DataSourceTemp
import com.intermercato.iws_m.utils.DataState
import io.realm.Realm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class OrdersRepository {

    val realm = Realm.getDefaultInstance()
    fun getOrders() : Flow<DataState<List<Order>>> = flow {
        emit(DataState.Loading)

        try {
            // JUST temporary
            val tempData = DataSourceTemp.createDataSet()



      /*      val order = realm.where(Order::class.java).findAll()
            Log.d("realm"," order s ${order.size} ")
            for(o in order){
                Log.d("realm"," order ${o?.id} imagesList ${o?.orderImages?.get(0)?.id}  material ${o?.banks} ")
            }*/

            //Log.d("realm"," order ${order?.orderImages }   ${tempData[0].images?.get(0)?.url}")
            realm.kartDao().saveOrders(tempData)
            emit(DataState.Loading)
            val listOfOrders = realm.kartDao().getOrders()
         /*   listOfOrders[0].id = "yoo"
            Log.d("realm","slut ${listOfOrders[0].id}")*/


            emit(DataState.Success(listOfOrders))

        }catch (e : Exception){
            emit(DataState.Error(e))
        }
    }
}