package com.intermercato.iws_m.ui.weighing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class WeighingFactoryViewModel(private val view : WeighingContract.View, private val mParam :String) : ViewModelProvider.Factory {

    /*private var view: WeighingContract.View? = null
    private var mParam: String? = null*/

/*    fun WeighingFactoryViewModel(v : WeighingContract.View,  p: String) {
        view = v
        mParam = p
    }*/

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeighingViewModel(view, mParam) as T
    }
}
