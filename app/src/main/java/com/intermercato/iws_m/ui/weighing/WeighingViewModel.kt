package com.intermercato.iws_m.ui.weighing

import android.util.Log
import androidx.lifecycle.ViewModel
import com.intermercato.iws_m.realmModels.Driver
import com.intermercato.iws_m.repositories.weighing.WeighingRealmContract
import com.intermercato.iws_m.repositories.weighing.WeighingRealmRepository
import se.oioi.blestandardapp.repositories.weighing.WeighingComboxInteractor
import se.oioi.blestandardapp.repositories.weighing.WeighingComboxInteractorContract
import se.oioi.intelweighblelib.models.AutoWeight


class WeighingViewModel(private val view: WeighingContract.View?, private val mParam :String?) : ViewModel() , WeighingContract.Presenter, WeighingRealmContract.Presenter, WeighingComboxInteractorContract.Presenter {

    private var comboxInteractor : WeighingComboxInteractorContract.Interactor? = null
    private var realmRepository : WeighingRealmContract.Repository? = null
    private var currentOrderId : String? = mParam

    companion object {
        val TAG = "weighing"
    }



    init {

        comboxInteractor = WeighingComboxInteractor(this)

        realmRepository = WeighingRealmRepository(this)
        Log.d(TAG,"init  comboxInteractor ${currentOrderId}  $mParam")
        comboxInteractor?.initConnection()
        Log.d(TAG,"-----------------------------------------")
    }

    fun starInteractorInit(){
        Log.d(TAG,"init  comboxInteractor")
    }

    override fun onCleared() {
        Log.d(TAG,"onClear")
        comboxInteractor?.deInitConnection()
        super.onCleared()
    }

    //
    override fun presentLiveWeight(weight: Int?, containerMinWeight: Int?, rssi: Int?) {
        view?.displayLiveWeight(weight,containerMinWeight,rssi)
    }

    override fun presentSavedContainerWeights(brutto: Int, container: Int, netto: Int, numberOfTotalWeights: Int) {
        view?.displaySavedContainerWeights(brutto,container,netto,numberOfTotalWeights)
    }

    override fun presentStatusMessage(status: String) {
       Log.d(TAG,"presentStatusMessage")
        view?.displayStatus(status)
    }

    override fun presentWeighingInProgress(progress: Boolean) {
        view?.displayCircularProgress(progress)
    }


    override fun presentState(state: Int?) {
        Log.d(TAG,"presentState")
        view?.displayState(state)
    }

    override fun presentWorkMode(mode: Int?) {
        Log.d(TAG,"presentWorkMode")
        view?.displayWorkMode(mode)
    }

    override fun presentTare(cur: Int) {
        view?.displayTare(cur)
    }

    override fun presentBatteryStatus(battStatus: Int?) {
       view?.displayBattery(battStatus)
    }

    override fun presentConnectionDown(isConnectionDown: Boolean) {
        Log.d(TAG,"presentConnectionDown")
        view?.displayConnectionDown(isConnectionDown)
    }

    override fun weightReceived(weight: AutoWeight?) {
        realmRepository?.saveWeight(weight,currentOrderId)
    }

    override fun doUndo() {
        // realm remove las weight
        realmRepository?.undoLastWeight()
    }

    override fun setMinMaxWeights(min: Int?, max: Int?) {
        Log.d(TAG,"setMinMaxWeights")
        realmRepository?.setMinMaxWeights(min,max)
        view?.setMinMaxWeights(min,max)
    }




    //   FROM REALM REPOSITORY

    override fun maxWeightReached() {
        comboxInteractor?.maxWeightReached()
    }


    // FROM THE VIEW
    override fun setCurrentBankByActive(orderId: String?) {
        realmRepository?.setCurrentBankByActive(orderId)
    }

    override fun saveBankIndex(orderId: String?, bankId: String?) {
       realmRepository?.saveBankIndex(orderId,bankId)
    }

    override fun getCurrentOrderFromRepo() {

    }

    override fun getCurrentDriverFromRepo(): Driver? {
        return null
    }

    override fun doTare() {
       comboxInteractor?.performTare()
    }

    override fun doAction() {
       comboxInteractor?.performAction(false)
    }

    override fun doExit() {
        Log.d(TAG,"doExit")
       comboxInteractor?.deInitConnection()
    }

    override fun doSubtraction() {
        realmRepository?.subtractionMode = !realmRepository?.subtractionMode!!
        view?.displaySubtraction(realmRepository?.subtractionMode!!)
    }

    override fun isSubtractionActive(): Boolean {
        return realmRepository?.subtractionMode!!
    }

    override fun getWorkMode(): Int? {
        return comboxInteractor?.getWorkMode()
    }

    override fun doClearResettableTotalWeight() {

    }
}