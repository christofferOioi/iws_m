package com.intermercato.iws_m.repositories.weighing

import android.content.Intent

import android.os.Handler
import android.util.Log
import com.intermercato.iws_m.Constants.NOT_SENT
import com.intermercato.iws_m.Constants.TYPE_CONTAINER_ROW
import com.intermercato.iws_m.Constants.TYPE_WEIGHT_ROW
import com.intermercato.iws_m.mApplication
import com.intermercato.iws_m.realmModels.Bank
import com.intermercato.iws_m.realmModels.Driver
import com.intermercato.iws_m.realmModels.Order
import com.intermercato.iws_m.realmModels.Row
import com.intermercato.iws_m.repositories.PrefsRepo
import com.intermercato.iws_m.utils.DateUtils

import io.realm.Realm
import org.greenrobot.eventbus.EventBus

import se.oioi.intelweighblelib.Constants

import se.oioi.intelweighblelib.events.scale.CommandEvent
import se.oioi.intelweighblelib.helpers.Help
import se.oioi.intelweighblelib.models.AutoWeight
import java.util.*

/**
 * Created by fredrik on 2017-12-11.
 * Repository for WeighingActivity
 *
 */

class WeighingRealmRepository(private val mPresenter: WeighingRealmContract.Presenter) : WeighingRealmContract.Repository {



    override var subtractionMode: Boolean = false
    private var lastRowId: String? = null
    private var lastBankId: String? = null
    private var lastOrderId: String? = null
    private var minWeight: Int? = 0
    private var maxWeight: Int? = 0
    private val TAG: String = "repo"
    private var currentBankId : String? = "xx"


    init {



    }


    override fun undoLastWeight() {

        Realm.getDefaultInstance().use {
            it.executeTransactionAsync({ db ->

                lastRowId?.let {

                    val row = db.where(Row::class.java).equalTo("id", it).findFirst()
                    val bank = db.where(Bank::class.java).equalTo("id", lastBankId).findFirst()
                    val order = db.where(Order::class.java).equalTo("id", lastOrderId).findFirst()

                    order?.totalWeight?.let {
                        row?.let { order.totalWeight -= it.weight }
                    }
                    bank?.totalWeight?.let {
                        row?.let { bank.totalWeight -= it.weight }
                    }
                    row?.deleteFromRealm()
                }
            }, {
                Log.d(TAG,"Undo success")
                //getCurrentOrder()
            }, {
                Log.d(TAG,"Undo error")

            })
        }

    }

    override fun setMinMaxWeights(min: Int?, max: Int?) {
        minWeight = min
        maxWeight = max
    }

    override fun saveWeight(newWeight: AutoWeight?, currentOrderId: String?) {
        Log.d(TAG, "saveWeight - bank id : $currentBankId  currentOrderId:  $currentOrderId")


            newWeight?.let { w ->

                var weightFormatted = w.autoweight
                var autoid = w.autoid
                var autostderror = w.autostderror
                var devicemode = w.devicemode
                var taravalue = w.taravalue
                var controlsum = w.controlsum
                var boxtimestamp = w.autotimestamp
                var scalemode = w.scalemode
                var taraacc = w.taraacc
                Log.d(TAG, "taraaacc $currentOrderId")
                Log.d(TAG, "saveWeight autoid $autoid")
                Log.d(TAG, "autostderror $autostderror")
                Log.d(TAG, "devicemode $devicemode")
                Log.d(TAG, "taravalue $taravalue")
                Log.d(TAG, "controlsum $controlsum")
                Log.d(TAG, "boxtimestamp $boxtimestamp")
                Log.d(TAG, "scalemode $scalemode")
                Log.d(TAG, "taraaacc $taraacc")

                if (subtractionMode == true) weightFormatted *= -1

                Realm.getDefaultInstance().use { realm ->
                    realm.executeTransactionAsync({ bgRealm ->
                        if(currentBankId !=null && currentOrderId !=null) {
                            val order = bgRealm.where(Order::class.java).equalTo("id", currentOrderId).findFirst()

                            val bank = bgRealm.where(Bank::class.java).equalTo("id", this.currentBankId).findFirst()


                            Log.d(TAG, "bank id " + bank?.id)
          /*                  val lat: Double = if (gpsTracker?.location != null) gpsTracker?.location?.latitude!! else 0.0
                            val lon: Double = if (gpsTracker?.location != null) gpsTracker?.location?.longitude!! else 0.0
                            val acc: Double = if (gpsTracker?.location != null) gpsTracker?.location?.accuracy!!.toDouble() else 0.0
                            val alt: Double = if (gpsTracker?.location != null) gpsTracker?.location?.altitude!!.toDouble() else 0.0*/

                            var row = bgRealm.createObject(Row::class.java, UUID.randomUUID().toString())//Row()
                            // add weight data
                            // update order index 1....
                            order?.index = order?.index?.plus(1)

                            row.IndexOfRow = order?.index

                            if (Help.isContainerEnabled(mApplication.applicationContext())) {
                                row.type = TYPE_CONTAINER_ROW
                            } else {
                                row.type = TYPE_WEIGHT_ROW
                            }
                            row.didGetSent = NOT_SENT
                            row.isSpotCheck = false
                            row.weight = weightFormatted
                            row.autoid = autoid
                            row.autostderror = autostderror
                            row.devicemode = devicemode
                            row.taravalue = taravalue
                            row.controlsum = controlsum
                            row.boxtimestamp = boxtimestamp
                            row.scalemode = scalemode
                            row.taraacc = taraacc

                            var leTime = row.boxtimestamp!!

                            Log.d(TAG, "- autoid       " + row.autoid)
                            Log.d(TAG, "- autostderror " + row.autostderror)
                            Log.d(TAG, "- devicemode   " + row.devicemode)
                            Log.d(TAG, "- taravalue    ${row.taravalue}")
                            Log.d(TAG, "- controlsum   " + row.controlsum)
                            Log.d(TAG, "- boxtimestamp " + row.boxtimestamp + " the time " + DateUtils.getPreFormattedDateDetailed(leTime))
                            Log.d(TAG, "- scalemode    " + row.scalemode)
                            Log.d(TAG, "- taraaacc     " + row.taraacc)


                            // add weight meta data
                            row.bankAlias = bank?.alias
                            row.bank_id = bank?.id
                            row.bankOrderIndex = bank?.bankOrderIndex


                            row.orderNumber = order?.orderNumber
                            row.order_id = order?.id
                            row.massUnit = PrefsRepo.getWeightUnit()
                            Log.d(TAG, "- row.massUnit      " + row.massUnit+"  apiOrderReturnId "+row.apiOrderReturnId+" apiRowReturnId "+row.apiRowReturnId)
                            if (Help.isContainerEnabled(mApplication.applicationContext())) {
                                if (newWeight.autoContainer > 0 && newWeight.autoGross > 0) {
                                    row.containerWeight = newWeight.autoContainer
                                    row.bruttoWeight = newWeight.autoGross
                                    Log.d(TAG, "container    :  " + row.containerWeight)
                                    Log.d(TAG, "brutto       :  " + row.bruttoWeight)
                                    Log.d(TAG, "netto weight :  " + row.weight)
                                }
                            }



                            row.timeStart = System.currentTimeMillis()
                            row.collectionsite_id = order?.collectionSite?.id

                            bank?.let { it ->
                                it.totalWeight += row.weight
                                bank.rows.add(row)
                                bgRealm.insertOrUpdate(bank)
                                lastRowId = row.id
                                lastBankId = bank?.id
                                lastOrderId = order?.id
                            }

                            order?.let {
                                it.totalWeight += row.weight
                                Log.d(TAG, "orderIndex: " + order?.index)
                                if (PrefsRepo.isTripTotalMeter(mApplication.applicationContext())) {
                                    it.tripTotalWeight = it.tripTotalWeight?.plus(row.weight)
                                }

                                if (it.firstWeightDateTaken == 0L) {
                                    it.firstWeightDateTaken = row.timeStart
                                }

                                maxWeight?.let {
                                    Log.d("LOAD", "maxweight " + it + "   total " + order.totalWeight + " trip " + order.tripTotalWeight)
                                    if (order.totalWeight > it && it != 0) {
                                        mPresenter.maxWeightReached()
                                    }
                                }
                                bgRealm.insertOrUpdate(order)
                            }
                            //println("order" + order)
                            //println("$javaClass Saving ${row.id} ${row.weight}")
                        }
                    }, {

                        if (Help.isContainerEnabled(mApplication.applicationContext())) {
                            val h = Handler()
                            h.postDelayed(Runnable {
                                EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.QUICK, 0, 0)))
                                //EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_ACK_AUTOID)))
                            }, 1600)
                        } else {
                            EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, Constants.BeepTime.QUICK, 0, 0)))
                            //EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_ACK_AUTOID)))
                            // TODO kolla denna
                        }
                        //getCurrentOrder()

                       /* if(false){
                            Log.d(TAG, "Weight was saved - upload to server.\nbank id: $currentBankId\ncurrentOrderId:  $currentOrderId")
                            val postIntent =  Intent(mApplication.applicationContext(), PostCloudIntent::class.java)
                            postIntent.putExtra("currentOrderId",currentOrderId)
                            mApplication.applicationContext().startService(postIntent)
                        }*/

                        /*doAsync {
                            EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, 750, 0,0)))
                            Thread.sleep(1050)
                            EventBus.getDefault().post(CommandEvent(String.format(Constants.COMMAND_BEEP, 1000, 0,0)))
                        }*/

                    }, {})

            }
        }
    }


    override fun saveBankIndex(orderId: String?, bankId: String?) {

        Log.d("WeighingRepo", "orderId:  $orderId  BankIndex$bankId")

        Realm.getDefaultInstance().use {
            it.executeTransactionAsync { bgRealm ->
                if (orderId != null) {
                    val order = bgRealm.where(Order::class.java).equalTo("id", orderId).findFirst()
                    order?.let {
                        it.selectedBankIndex = 0
                        this.currentBankId = bankId
                        bgRealm.insertOrUpdate(it)
                    }

                }
            }
        }
    }

    override fun setCurrentBankByActive(orderId: String?) {
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                var bank = it.where(Bank::class.java).equalTo("orderId",orderId).equalTo("active",true).findFirst()
                Log.d(TAG,"total "+bank?.totalWeight)

                this.currentBankId = bank?.id

            }
        }
    }
}