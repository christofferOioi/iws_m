package se.oioi.blestandardapp.utils



import android.util.Log
import com.intermercato.iws_m.mApplication
import com.intermercato.iws_m.repositories.PrefsRepo
import org.greenrobot.eventbus.EventBus.*

import se.oioi.intelweighblelib.Constants

import se.oioi.intelweighblelib.events.scale.CommandEvent
import se.oioi.intelweighblelib.helpers.Help

class ContainerUtils(val weight: Int?, val weights: IntArray?, val containerMinNettWeight: Int?) {

    companion object Factory {

        var lastAutoCall = 0
        var firstRunForContainer = true
        val numberOfChambers = (PrefsRepo.getContainerMode() + 1)
        var brutto = 0
        var netto = 0
        var container = 0
        val TAG:String="containerutils"

        data class Container(val brutto: Int, val container: Int, val netto: Int, val numberOfTotalWeights:Int)



        fun filterIndata(weight: Int?, weights: IntArray?, containerMinNettWeight: Int?): Container {

            Log.d(TAG, "counter " + lastAutoCall+"  size "+weights?.size+" prefsRe "+numberOfChambers+" "+containerMinNettWeight)

            if (firstRunForContainer && weights != null && weights?.size >= 1) {
                Log.d(TAG, "COMMAND_RESET " + firstRunForContainer + "  " + Help.isContainerEnabled(mApplication.Companion.applicationContext()))
                getDefault().post(CommandEvent(Constants.COMMAND_RESET))
            }
            firstRunForContainer = false

            val w: Int? = weight


            if (w != null && w < containerMinNettWeight!! && weights?.size!! == 1) {
                lastAutoCall++
                if (lastAutoCall >= 120) {
                    lastAutoCall = 0
                }
            } else {
                lastAutoCall = 0
            }


            if (w != null && weights?.size == 1 && (containerMinNettWeight!! > w!! && w >= -50) && lastAutoCall == 100) {
                Log.d(TAG, "weights COMMAND_RESET - _ -> $containerMinNettWeight  $w ")
                getDefault().post(CommandEvent(Constants.COMMAND_RESET))
                return Container(0, 0, 0, 0)

                lastAutoCall = 0

            }
            //Log.d(TAG, "counter " + lastAutoCall+"  "+weights?.size+" prefsRe "+numberOfChambers+" "+containerMinWeight)
            weights?.let {

                if (it.size == 1) {
                    brutto = weights.last()
                    return Container(brutto, 0, 0, weights?.size)

                } else if (it.size <= 0) {
                    brutto = 0
                    return Container(brutto, 0, 0, weights?.size)
                } else {
                }

                if (it.size > 1) {


                    brutto = weights.last()
                    var container: Int = 0
                    container = weights[1] - ((weights[1] - weights[0]))
                    val n = (weights[1] - weights[0])

                    //displaySessionWeight.setWeightsArray(b, c, n, weights.size)

                    if (w!! > 0 && w!! < containerMinNettWeight!! && (weights?.size >= numberOfChambers)) {
                        Log.d(TAG, "counter " + lastAutoCall + "  " + weights?.size + " prefs " + numberOfChambers + " " + containerMinNettWeight)
                        Log.d(TAG, "weights COMMAND_RESET - done -> $containerMinNettWeight  $w ")
                        getDefault().post(CommandEvent(Constants.COMMAND_RESET))
                       return Container(0, 0, 0, 0)

                    }
                    return Container(brutto, container, n, weights.size)
                }
            }

            return Container(0,0,0,0)
        }
    }
}