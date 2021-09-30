package se.oioi.blestandardapp.repositories.weighing

import se.oioi.intelweighblelib.models.AutoWeight

/**
 * Created by fredrik on 2017-12-11.
 */

interface WeighingComboxInteractorContract {

    interface Interactor {
        fun performTare()
        fun performAction(send: Boolean)
        fun maxWeightReached()
        fun initConnection()
        fun deInitConnection()
        fun getWorkMode(): Int?
    }

    interface Presenter {
        fun presentLiveWeight(weight: Int?, containerMinWeight: Int?, rssi : Int?)
        fun presentSavedContainerWeights(brutto: Int, container: Int, netto: Int, numberOfTotalWeights: Int)
        fun presentStatusMessage(status: String)
        fun presentWeighingInProgress(progress: Boolean)
        fun presentState(state: Int?)
        fun presentWorkMode(mode: Int?)
        fun presentTare(cur: Int)
        fun presentBatteryStatus(battStatus: Int?)
        fun presentConnectionDown(isConnectionDown: Boolean)
        fun weightReceived(weight: AutoWeight?)
        fun doUndo()
        fun setMinMaxWeights(min: Int?, max: Int?)
    }
}