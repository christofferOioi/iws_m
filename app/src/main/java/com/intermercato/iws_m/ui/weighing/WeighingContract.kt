package com.intermercato.iws_m.ui.weighing

import com.intermercato.iws_m.realmModels.Driver
import com.intermercato.iws_m.realmModels.Order


/**
 * Created by fredrik on 2017-12-11.
 */
interface WeighingContract {

    interface View {
        fun displayBattery(battStatus: Int?)
        fun displayWorkMode(workMode: Int?)
        fun displayLiveWeight(weight: Int?, containerMinWeight: Int?, rssi :Int?)
        fun displaySavedContainerWeights(brutto: Int, container: Int, netto: Int, numberOfTotalWeights: Int)
        fun displayStatus(status: String)
        fun displayCircularProgress(inProgress: Boolean)
        fun displayState(state: Int?)
        fun displayTare(progress: Int)
        fun displaySubtraction(subtract: Boolean)
        fun displayConnectionDown(isConnectionDown: Boolean)
        fun getCurrentOrder(order: Order?)
        fun updateBankWeights(weight: Int?)
        fun setMinMaxWeights(min: Int?, max: Int?)
    }

    interface Presenter {

        fun saveBankIndex(orderId:String?,bankId: String?)
        fun setCurrentBankByActive(orderId: String?)
        fun getCurrentOrderFromRepo()
        fun getCurrentDriverFromRepo(): Driver?
        fun doTare()
        fun doAction()
        fun doUndo()
        fun doExit()
        fun doSubtraction()
        fun isSubtractionActive(): Boolean
        fun getWorkMode(): Int?
        fun doClearResettableTotalWeight()
    }

}