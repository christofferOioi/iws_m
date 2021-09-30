package com.intermercato.iws_m.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intermercato.iws_m.realmModels.Order
import com.intermercato.iws_m.utils.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrdersViewModel: ViewModel(){

    private val repository : OrdersRepository = OrdersRepository()
    private val _dataState : MutableLiveData<DataState<List<Order>>> = MutableLiveData()
    val dataState : LiveData<DataState<List<Order>>> get() = _dataState


    fun mainStateEvent(event : MainStateEvent){

        viewModelScope.launch {

                when(event){

                    is MainStateEvent.GetOrders -> {
                            repository.getOrders().onEach { dataState ->

                                _dataState.value = dataState

                            }.launchIn(viewModelScope)
                    }

                    else ->{}
                }
        }
    }

    sealed class MainStateEvent {
        object Loading : MainStateEvent()
        object GetOrders : MainStateEvent()
        object None : MainStateEvent()
    }

}