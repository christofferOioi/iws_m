package com.intermercato.iws_m.ui.activeorder

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intermercato.iws_m.realmModels.Order
import com.intermercato.iws_m.realmModels.kartDao
import io.realm.Realm

class ActiveOrderViewModel : ViewModel() {

    var currentOrderId: MutableLiveData<String> = MutableLiveData()
    var currentOrderName: MutableLiveData<String> = MutableLiveData()
    var realm : Realm = Realm.getDefaultInstance()

/*    val orderId = currentOrderId.switchMap {  id ->

        //Log.d("active","id $id")
    }*/

    init {

    }

    fun getOrderById(id : String) : Order? {
        return realm.kartDao().getOrderById(currentOrderId.value)
    }

    fun setCurrentOrderId(id: String, name: String) {
        currentOrderId.value = id
        currentOrderName.value = name

    }

    override fun onCleared() {
        super.onCleared()
        Log.d("active","viewModel onCleared")
    }
}

