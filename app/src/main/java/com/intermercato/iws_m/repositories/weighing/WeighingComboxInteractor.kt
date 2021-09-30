package se.oioi.blestandardapp.repositories.weighing

import android.os.Handler
import android.util.Log
import com.intermercato.iws_m.R
import com.intermercato.iws_m.mApplication
import com.intermercato.iws_m.utils.DateUtils

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


import se.oioi.blestandardapp.utils.ContainerUtils


import se.oioi.intelweighblelib.Constants
import se.oioi.intelweighblelib.events.other.DialogBusEvent
import se.oioi.intelweighblelib.events.scale.*
import se.oioi.intelweighblelib.models.ContainerWeights
import se.oioi.intelweighblelib.models.LiveData
import se.oioi.intelweighblelib.models.Parameters
import se.oioi.intelweighblelib.services.ResultController


class WeighingComboxInteractor(private var presenter: WeighingComboxInteractorContract.Presenter) : WeighingComboxInteractorContract.Interactor {

    private var resultController: ResultController? = null
    private var workState: Int? = null
    private var maxWeight: Int? = 0
    private var minWeight: Int? = 0
    private var containerMinWeight: Int? = 0
    private var isShuttingDown: Boolean = false
    private var didReceiveParameters: Boolean = false
    private var isStarting = true
    private var TAG: String = "WeighingComboxInteractor"
    private var liveSubState: Int? = -1
    val counter: Int

    init {
        counter = nextId++
        println("${this::class.java.simpleName} init COUNTINSTANCE $counter")
    }

    private companion object {
        var nextId = 1
    }

    override fun initConnection() {

        EventBus.getDefault().register(this)
        if(resultController == null) {
            resultController = ResultController()
            presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.connecting_commbox))
        }

        Log.d(TAG,"init connection")
        EventBus.getDefault().post(StartComboxEvent(true))
    }

    override fun deInitConnection() {
        isStarting = false
        EventBus.getDefault().post(CommandEvent(Constants.COMMAND_STOP_SCALE))
        isShuttingDown = true
        resultController?.stopController()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)

        Log.d("weighingact","ExitRoutine deInitConnection")
    }

    override fun performTare() {
        EventBus.getDefault().post(CommandEvent(Constants.COMMAND_TARE))
    }

    var lastCall = 0L
    //var currentSubstate:Int? = -1
    override fun performAction(send: Boolean) {
        //Log.d(TAG, "perform action "+currentSubstate)
        //if ((System.currentTimeMillis() - lastCall) >= 2000) {
        //if(currentSubstate != 7 )
            //Log.d(TAG, "perform action .. "+currentSubstate)
            EventBus.getDefault().post(CommandEvent(Constants.COMMAND_ACTION))
            EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.LONG, 0, 0)))
          //  lastCall = System.currentTimeMillis()
        //}
    }

    override fun maxWeightReached() {


    /*    doAsync {
            //Wait 1,5 seconds to distinguish "Save weight"-beep from maxweight beep
            Thread.sleep(1500)
            EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.SHORT, 500, Constants.BeepTime.SHORT)))
        }*/
    }

    override fun getWorkMode(): Int? {
        return workState
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   BUTTONSTATE CHANGE EVENT  */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ButtonStateChangeEvent) {

        //Log.d("WeghingCombox", "event " + event.whichButton + " is pressed " + event.isPressed)
        if (event.whichButton == Constants.ButtonStates.ADD_WEIGHT && event.isPressed) {
            when (workState) {
                Constants.WorkStates.STATIC -> performAction(true)
                Constants.WorkStates.NORMAL ->  performAction(true)
                Constants.WorkStates.SEMI -> performAction(true)
                Constants.WorkStates.FULL -> performAction(true)
            }
        } else if (event.whichButton == Constants.ButtonStates.SUBTRACT_WEIGHT && event.isPressed) {
            when (workState) {
                Constants.WorkStates.STATIC -> presenter.doUndo()
                Constants.WorkStates.FULL -> presenter.doUndo()
                Constants.WorkStates.SEMI -> presenter.doUndo()
                Constants.WorkStates.NORMAL -> presenter.doUndo()
            }
        } else if (event.whichButton == Constants.ButtonStates.UNUSED) {
            //println("button unused pressed")
        }

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   PARAMETERS EVENT  */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    fun onEvent(event: Parameters?) {
        workState = event?.workstate
        presenter.presentWorkMode(event?.workstate)
        maxWeight = event?.maxweight // set value for maxWeight pie view
        minWeight = event?.minweight
        presenter.setMinMaxWeights(minWeight, maxWeight)
        containerMinWeight = event?.minContainerWeight
        didReceiveParameters = true
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   STATE CHANGE EVENT  */

    var liveCount = 0
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: StateChangeEvent?) {

        Log.d(TAG, "state -> " + event?.newState)
        presenter.presentState(event?.newState)
        if (isShuttingDown) {
            resultController?.stopController()
            resultController = null
            return
        }

        if(event?.newState == Constants.States.INIT){
            isStarting = true
        }


        if (event?.newState == Constants.States.OFF) {
            //Log.d(TAG, "onEvent StateChanged " + isStarting + " minWeight " + minWeight + " ")
            if (isStarting) {
                isStarting = false

                presenter.presentConnectionDown(false)
                Handler().postDelayed({
                    Log.d(TAG,"state -> OFF trying to start")
                    EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_START_SCALE, System.currentTimeMillis())))
                }, 600)
            }
        }

        if (event?.newState == Constants.States.LIVE) {
            isStarting = false
            liveCount = 0
            //Answers.getInstance().logCustom(CustomEvent("SCALE_LIVE"))
            Log.d(TAG, "SCALE_LIVE")
            if(!didReceiveParameters) {
                Log.d(TAG, "state ${event?.newState} didn't receive paramters -> send command get params")
                EventBus.getDefault().post(CommandEvent(Constants.COMMAND_GET_PARAMETERS))
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: ContainerWeights) {

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   NEW WEIGHT EVENT  */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 1)
    fun onEvent(event: NewWeightEvent?) {

        if (event != null) {
            presenter.weightReceived(event.newweight)
            var aw = event.newweight
            Log.d(TAG, "liveWeight " + aw.autotimestamp + " DateUtils " + DateUtils.getPreFormattedDateDetailed(aw.autotimestamp.toLong()))
            event.newweight.autoweights.forEachIndexed{index, w->

                Log.d(TAG, " i : $index  w: $w")

            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(event: ConnectionEvent) {
        Log.d(TAG, "ConnectionEvent " + event.connectionState + " ")

        if (event.connectionState == Constants.ConnectionNewStates.DISCONNECTED) {

            presenter.presentConnectionDown(true)
            isStarting = true

        } else if (event.connectionState == Constants.ConnectionNewStates.CONNECTED_TO_BOX) {

            isStarting = true
            /*resultController?.stopController()
            resultController = null
            resultController = f*/

            EventBus.getDefault().post(DialogBusEvent(DialogBusEvent.HIDE, ""))
            presenter.presentConnectionDown(false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 1)
    fun onEvent(event: ValueChangeEvent?) {

        val result = event?.liveData
        //currentSubstate = result?.substate
        Log.d("ComboxInteractor", "result statestatus " + result?.statestatus + " state " + result?.state + " substate: " + result?.substate)
        when (result?.state) {
            Constants.States.OVERLOAD -> onErrorState(result)
            Constants.States.INIT -> onInitState(result)
            Constants.States.TARA -> onTareState(result)
            Constants.States.OFF -> onOffState()
            Constants.States.LIVE -> onLiveState(result)
        }
    }

    private fun onTareState(result: LiveData?) {
        val prog = result?.statestatus!!.toInt()

        if (prog > 0) {
            presenter.presentTare(prog)
        }

        when (result?.substate) {
            Constants.Substates.Tara.IN_PROGRESS -> {
                presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_tara_in_progress))
            }
            Constants.Substates.Tara.DONE -> {
                presenter.presentStatusMessage("")
                presenter.presentTare(0)
            }
            //Constants.Substates.Tara.FAILED -> presenter.presentStatusMessage(BleStandardApplication.getContext().getString(R.string.tara_failed))
        }

    }

    private fun onInitState(result: LiveData?) {

        when (result?.substate) {

            1 -> presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_init_establishing)+" "+result?.statestatus+"%")
            2 -> presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_init_warming_up))
            0 -> presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_init_done))
            255 -> presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_timeout))
        }
    }

    private fun onErrorState(result: LiveData?) {
        Log.d(TAG, "onErrorState: " + result?.substate)
        val message: String = when (result?.substate) {
            Constants.Substates.Overload.OVERLOAD -> mApplication.applicationContext().getString(R.string.txt_scale_status_overload)
            Constants.Substates.Overload.LOWBATTERY -> mApplication.applicationContext().getString(R.string.txt_scale_status_no_battery)
            Constants.Substates.Overload.LINK_OFF -> mApplication.applicationContext().getString(R.string.txt_scale_status_broken_link)
            4 -> mApplication.Companion.applicationContext().getString(R.string.txt_scale_status_confused)
            else -> mApplication.Companion.applicationContext().getString(R.string.error)
        }

        if(result?.substate == 255){
            isStarting = true
        }
        presenter.presentStatusMessage(message)
    }

    private fun onOffState() {
        presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.txt_off))
    }

    private fun onLiveState(result: LiveData?) {
        //Log.d(TAG, "workstate " + result?.liveweight + "  " + result?.state+"  "+result?.substate)

        presenter.presentLiveWeight(result?.liveweight,containerMinWeight,result?.rssi)

        val cBundle = ContainerUtils.filterIndata(result?.liveweight,result?.autoweights,containerMinWeight)

        presenter.presentSavedContainerWeights(cBundle.brutto,cBundle.container,cBundle.netto,cBundle.numberOfTotalWeights)

        presenter.presentBatteryStatus(result?.batterystate)

        val oldSubstate = liveSubState

        if (oldSubstate != result?.substate) {
            if (!(oldSubstate == Constants.Substates.Live.WEIGHING && liveSubState == Constants.Substates.Live.IDLE)) {
                liveSubState = result?.substate
                //Log.d(TAG, "live substate "+liveSubState)

                workState.let {
                    if (workState == Constants.WorkStates.SEMI) {

                        beepControlSemi(liveSubState)

                    } else if (workState == Constants.WorkStates.NORMAL) {

                        beepControlSemi(liveSubState)

                    } else {

                        beepControlAutomatic(liveSubState)

                    }
                }
            }
        }

        //Log.d(TAG,"presentLive: "+result?.liveWeight)

        //if (oldSubstate != result?.substate) {


            when (result?.substate) {

                Constants.Substates.Live.WEIGHING -> {
                    //presenter.presentWeighingInProgress(true)
                    presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_weighing))
                }
                Constants.Substates.Live.WEIGHT_READY -> {
                    //presenter.presentWeighingInProgress(false)
                    //presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_weightready))
                }
                Constants.Substates.Live.PAUSED -> {
                    //presenter.presentWeighingInProgress(false)
                    presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_paused))
                }
                Constants.Substates.Live.UNLOAD -> {
                    //presenter.presentWeighingInProgress(false)
                    //presenter.presentStatusMessage(BleStandardApplication.getContext().getString(R.string.substate_weightready))
                    /*Handler().postDelayed({

                    }, 1000)*/
                    presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_unload))
                }
                Constants.Substates.Live.TIMEOUT -> {
                    //presenter.presentWeighingInProgress(false)
                    presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_timeout))
                }
                Constants.Substates.Live.WAITING -> {
                    //presenter.presentWeighingInProgress(false)
                    presenter.presentStatusMessage(mApplication.applicationContext().getString(R.string.substate_waiting))
                }
                Constants.Substates.Live.READY -> {
                    //presenter.presentWeighingInProgress(false)
                    //presenter.presentStatusMessage(BleStandardApplication.getContext().getString(R.string.substate_ready))
                    presenter.presentStatusMessage("")
                }
                10 -> {
                    presenter.presentStatusMessage("checking link..")
                    //BleStandardApplication.getContext().getString(R.string.txt_scale_status_broken_link)
                }
                else -> {
                    //presenter.presentWeighingInProgress(false)
                    presenter.presentStatusMessage("")
                }
            }
        //}
    }

    private fun onConnected(status: String?) {
        presenter.presentStatusMessage("$status")
    }

    var lastAutoCall = 0L
    private fun beepControlAutomatic(sessionState: Int?) {
        //Log.d(TAG, "subState auto: " + sessionState)

        when (sessionState) {

            Constants.Substates.Live.WEIGHING -> {
                if ((System.currentTimeMillis() - lastAutoCall) >= 2000) {
                    EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.LONG, 0, 0)))
                    //Log.d(TAG, "subState weighing")
                    lastAutoCall = System.currentTimeMillis()
                }
            }

            Constants.Substates.Live.WEIGHT_READY -> {
                //Log.d(TAG, "BEEP WEIGHT READY")
                //EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.LONG, 0,0)))
            }

            Constants.Substates.Live.UNLOAD -> {
                //Log.d(TAG, "BEEP UNLOAD")
                EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.LONG, 0, 0)))
            }

            Constants.Substates.Live.READY -> {
                //Log.d(TAG, "BEEP READY")
                //EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, quickBeepLength, 0)))
            }
            else -> Unit
        }
    }


    private fun beepControlSemi(sessionState: Int?) {
        //Log.d(TAG, "subState semi: " + sessionState)

        when (sessionState) {

            Constants.Substates.Live.WAITING -> {
                //EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.LONG, 0, 0)))
                //Log.d(TAG, "subState waiting")
            }

            Constants.Substates.Live.WEIGHING -> { }

            Constants.Substates.Live.WEIGHT_READY -> { }

            Constants.Substates.Live.UNLOAD -> {
                EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.LONG, 0, 0)))
                //Log.d(TAG, "subState unload")
            }

            Constants.Substates.Live.READY -> {

            }

            else -> Unit
        }
    }
}