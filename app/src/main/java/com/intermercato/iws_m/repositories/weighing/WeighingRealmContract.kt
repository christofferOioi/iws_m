package com.intermercato.iws_m.repositories.weighing

import com.intermercato.iws_m.realmModels.Order

import se.oioi.intelweighblelib.models.AutoWeight

/**
 * Created by Christoffer on 2021-09-05.
 */
interface WeighingRealmContract {
    interface Repository  {
        var subtractionMode: Boolean
        fun saveWeight(newWeight: AutoWeight?, currentOrderId: String?)
        fun saveBankIndex(orderId: String?,bankId: String?)
        fun setCurrentBankByActive(orderId: String?)
        fun undoLastWeight()
        fun setMinMaxWeights(min: Int?, max: Int?)
    }
    interface Presenter {
        fun maxWeightReached()
    }
}