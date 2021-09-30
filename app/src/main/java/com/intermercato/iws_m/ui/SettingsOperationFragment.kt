package com.intermercato.iws_m.ui


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View

import androidx.preference.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import se.oioi.intelweighblelib.events.other.DialogBusEvent
import se.oioi.intelweighblelib.events.scale.StartComboxEvent
import se.oioi.intelweighblelib.services.ResultController
import com.intermercato.iws_m.R
import se.oioi.intelweighblelib.Constants
import se.oioi.intelweighblelib.events.scale.CommandEvent
import se.oioi.intelweighblelib.events.scale.StateChangeEvent
import se.oioi.intelweighblelib.events.scale.ValueChangeEvent
import se.oioi.intelweighblelib.models.Parameters


class SettingsOperationFragment : BaseFragmentCompat(), Preference.OnPreferenceChangeListener {

    private val TAG: String = "prefs-"
    private var resultController: ResultController? = null
    private var preferences: SharedPreferences? = null
    private var initParamsFirstTime: Boolean = true
    private var arrKeys: Int? = null
    private var arrVals: Int? = null
    private val BOX_WORK_MODE_CONTAINER = 3
    private val BOX_WORK_MODE_FULL_ATOMATIC = 3
    private val BOX_WORK_MODE_SEMI_AUTOMATIC = 2
    private val BOX_WORK_MODE_NORMAL = 1
    private val BOX_WORK_MODE_STATIC = 0
    private var doUpdate: Int = -1

    private var feedbackKey: String = ""
    private var feedbackVal: String = ""

    companion object {
        private val ARG_NAMES = "NAMES"
        private val ARG_VALUES = "VALUES"
        fun newInstance(n: Int, v: Int): SettingsOperationFragment {
            val args: Bundle = Bundle()
            args.putInt(ARG_NAMES, n)
            args.putInt(ARG_VALUES, v)
            val fragment = SettingsOperationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(TAG, "onCreatePreferences ")
        setPreferencesFromResource(R.xml.fragment_new_operationmode_settings,rootKey)
        // new code
        val context = preferenceManager.context


        preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        arrKeys = arguments?.getInt(ARG_NAMES)
        arrVals = arguments?.getInt(ARG_VALUES)
        EventBus.getDefault().register(this)
        // ReadSettingsForOperationUpdated.createKeyMap(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        Log.d(TAG, "onViewCreated " + arguments?.getInt(ARG_NAMES) + "\n" + arguments?.getInt(ARG_VALUES))
        resultController = ResultController()
        EventBus.getDefault().post(StartComboxEvent(true))
        EventBus.getDefault().post(DialogBusEvent(DialogBusEvent.SHOW, getString(R.string.txt_fetching_params)))

        setDivider(ColorDrawable(Color.LTGRAY))
        setDividerHeight(2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resultController?.stopController()
        EventBus.getDefault().post(DialogBusEvent(DialogBusEvent.HIDE, ""))
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }

    }

    val LIST = 0
    val EDIT = 1

    private fun setSummary(type:Int, key: String, index: Int) {
        Log.d(TAG, " workmode key: $key        $index")

        when(type){
            LIST -> {
                    val list = findPreference<ListPreference>(key)
                    list?.summary = index.toString()
            }
            EDIT -> {
                val editText = findPreference<EditTextPreference>(key)
                editText?.summary = index.toString()
            }

        }

        val editor = preferences?.edit()
        editor?.putString(key, index.toString())
        editor?.apply()
    }


    private fun hasValue(boxval: Int): Int {
        // helper method for looping through the local arrays
        // to see if the workmode value from the box has the same value
        // if not we set the first value fromt the first element in the values array

        // TODO - make this dynamic alla prefs array should be loaded to the preference fragment at creation
        //var keys = arrKeys?.let { getResources().getStringArray(it) }
        //var vals = arrVals?.let { getResources().getStringArray(it) }

        val keys = resources.getStringArray(R.array.intel_ble_settings_setup_operationMode_array)
        val vals = resources.getStringArray(R.array.intel_ble_settings_setup_operationMode_values)

        for (i in keys!!.indices) {
            Log.d(TAG, " arrKeys: " + keys!![i] + "  arrVals: " + vals!![i])
        }

        var b: Int = 0

        for (i in vals!!.indices) {
            if (boxval == vals[i].toInt()) {
                Log.d(TAG, " : " + vals[i].toInt())
                b = boxval
                return b
            }
        }
        Log.d(TAG, " :  no val found in local arrays - set semi automatic workmode")
        return vals[2].toInt()
    }

    private fun setUpPrefs(params: Parameters) {




       // CONTAINER MODE


        setSummary(LIST,"setContainerMode",params.containerMode)
        bindPreferenceSummaryToValue(findPreference<ListPreference>("setContainerMode") as ListPreference)

        // CONTAINER MIN WEIGHT
        setSummary(EDIT,"setContainerMinWeight", params.minContainerWeight)
        bindPreferenceSummaryToValue(findPreference<EditTextPreference>("setContainerMinWeight") as EditTextPreference)

        // WORK MODE
        setSummary(LIST,"setOperationModeKey", hasValue(params.workstate))
        bindPreferenceSummaryToValue(findPreference<ListPreference>("setOperationModeKey") as ListPreference)
        //ReadSettingsForOperationUpdated.ReadOperation(params.workstate, this)


        // MASS UNIT
        bindPreferenceSummaryToValue(findPreference<ListPreference>("setMassUnitKey") as ListPreference)

        // STABLE LIMIT
        setSummary(EDIT,"setStableLimitKey", params.stablelimit)
        bindPreferenceSummaryToValue(findPreference<EditTextPreference>("setStableLimitKey") as EditTextPreference)
        var stableP:Preference = findPreference<EditTextPreference>("setStableLimitKey") as EditTextPreference
        //stableP.shouldDisableView = true
        //stableP.isEnabled = false

        Log.d(TAG,"stableLimit isEnabled "+stableP.isEnabled)

        // MIN WEIGHT
        setSummary(EDIT,"setMinWeightKey", params.minweight)
        bindPreferenceSummaryToValue(findPreference<EditTextPreference>("setMinWeightKey") as EditTextPreference)

        // PLAY TIME
        setSummary(EDIT,"setPlayTimeKey", params.time)
        bindPreferenceSummaryToValue(findPreference<EditTextPreference>("setPlayTimeKey") as EditTextPreference)

        // DELAY TIME
        setSummary(EDIT,"setDelayTimeKey", params.delay)
        bindPreferenceSummaryToValue(findPreference<EditTextPreference>("setDelayTimeKey") as EditTextPreference)

        // MAX WEIGHT
        setSummary(EDIT,"setMaxWeightKey", params.maxweight)
        bindPreferenceSummaryToValue(findPreference<EditTextPreference>("setMaxWeightKey") as EditTextPreference)

        // MIN NET WEIGHT
       //bindPreferenceSummaryToValue(findPreference<EditTextPreference>("setMinNetWeightKey"))

        EventBus.getDefault().post(DialogBusEvent(DialogBusEvent.HIDE, ""))
    }

    private fun bindPreferenceSummaryToValue(preference: Preference) {
        Log.d(TAG, "Summary")
        preference.onPreferenceChangeListener = this
        onPreferenceChange(preference,
                PreferenceManager.
                        getDefaultSharedPreferences(preference.context).
                        getString(preference.key, ""))

    }

    override fun onPreferenceChange(p: Preference?, value: Any?): Boolean {

        val holder = JSONObject()
        val params = JSONObject()

        var stringValue:String = value.toString()
        Log.d(TAG, "onPreferenceChange " + stringValue)
        val k = p?.key
        if (p is ListPreference) {

            val listPreference = p
            val prefIndex = listPreference.findIndexOfValue(stringValue)
            Log.d(TAG, "is list, index: " + prefIndex + "  key: " + k)

            if (k == "setOperationModeKey" && !initParamsFirstTime) {
                params.put(Constants.PARAMETER_WORKSTATE, stringValue?.toLong())
                val ss = holder.put(Constants.RETURN_PARAMETERS, params)
                //Log.d(TAG, "setOperationMode  $ss")
                EventBus.getDefault().post(CommandEvent(String.format(ss.toString() + "\r\n")))

            } else if(k == "setContainerMode"){
                Log.d(TAG,"setContainerMode")
                if(stringValue.toInt() >=1) {
                    p.getSharedPreferences().edit().putBoolean("boolContainer", true).commit()
                }else{
                    p.getSharedPreferences().edit().putBoolean("boolContainer", false).commit()
                }
                params.put(Constants.PARAMETER_CONTAINER_MODE, stringValue?.toLong())
                val ss = holder.put(Constants.RETURN_PARAMETERS, params)

                EventBus.getDefault().post(CommandEvent(String.format(ss.toString() +"\r\n")))


            } else if (k == "setMassUnitKey" && prefIndex>=0/* && !initParamsFirstTime*/) {

                p?.summary = listPreference.entries[prefIndex]
                listener.valueWasUpdated(getKeyString(k), "")
            }
            if (prefIndex >= 0) {
                p.setSummary(listPreference.entries[prefIndex])
            }
            feedbackKey = k.toString()
            feedbackVal = stringValue

        } else if (p is EditTextPreference) {
            Log.d(TAG, "is EditText ")

            Log.d(TAG, "----> " + stringValue.isNullOrBlank() + "  " + stringValue.isNotEmpty())
            var nval: String
            if (stringValue.isNullOrBlank()) {
                nval = "0"
            } else {
                nval = stringValue
            }

            if(k == "setContainerMinWeight"){

                Log.d(TAG,"setContainerMinWeight")
                params.put(Constants.PARAMETER_CONTAINER_MINWEIGHT, nval)
                val addedVals = holder.put(Constants.RETURN_PARAMETERS, params)
                EventBus.getDefault().post(CommandEvent(String.format(addedVals.toString()+ "\r\n")))

            }else if (k == "setStableLimitKey" && !initParamsFirstTime) {

                params.put(Constants.PARAMETER_STABLELIMIT, nval)
                val addedVals = holder.put(Constants.RETURN_PARAMETERS, params)
                EventBus.getDefault().post(CommandEvent(String.format(addedVals.toString() + "\r\n")))

            } else if (k == "setMinWeightKey" && !initParamsFirstTime) {
                params.put(Constants.PARAMETER_MINWEIGHT, nval)
                val addedVals = holder.put(Constants.RETURN_PARAMETERS, params)
                EventBus.getDefault().post(CommandEvent(String.format(addedVals.toString() + "\r\n")))

            } else if (k == "setPlayTimeKey" && !initParamsFirstTime) {

                params.put(Constants.PARAMETER_PLAYTIME, nval)
                val addedVals = holder.put(Constants.RETURN_PARAMETERS, params)
                EventBus.getDefault().post(CommandEvent(String.format(addedVals.toString() + "\r\n")))

            } else if (k == "setDelayTimeKey" && !initParamsFirstTime) {

                params.put(Constants.PARAMETER_DELAY, nval)
                val addedVals = holder.put(Constants.RETURN_PARAMETERS, params)
                EventBus.getDefault().post(CommandEvent(String.format(addedVals.toString() + "\r\n")))

            } else if (k == "setMaxWeightKey" && !initParamsFirstTime) {

                params.put(Constants.PARAMETER_MAXWEIGHT, nval)
                val addedVals = holder.put(Constants.RETURN_PARAMETERS, params)
                EventBus.getDefault().post(CommandEvent(String.format(addedVals.toString() + "\r\n")))

            } else if (k == "setMassUnitKey" && !initParamsFirstTime) {

                listener.valueWasUpdated(getKeyString(k), "")
            } else if (k == "setMinNetWeightKey" && !initParamsFirstTime) {
                listener.valueWasUpdated(getKeyString(k), "")
            }

            p?.summary = nval

            feedbackKey = k.toString()
            feedbackVal = stringValue


        } else if (p is SwitchPreferenceCompat) {
            Log.d(TAG, "is SwitchPreferenceCompat")
            p?.summary = stringValue
        } else {
            p?.summary = stringValue
        }
        return true
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Log.d(TAG, "TreeClick " + preference.key)
        return when (preference.key) {
            "setOperationModeKey" -> {
                true
            }
            getString(R.string.pref_key_zipcode) -> {
                true
            }
            getString(R.string.pref_key_unit) -> {
                true
            }
            else -> {
                super.onPreferenceTreeClick(preference)
            }
        }
    }

    /**
     * Eventbus onEvents, STATE CHANGE EVEN & PARAMETERS EVENT
     *
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(stateChangeEvent: StateChangeEvent) {
        Log.d(TAG, "onEvent StateChanged ")
        EventBus.getDefault().post(DialogBusEvent(DialogBusEvent.SHOW, getString(R.string.txt_loading_parames)))
        if (stateChangeEvent.newState == Constants.States.OFF) {
            // EventBus.getDefault().post(DialogBusEvent(DialogBusEvent.HIDE, ""))
        } else {
            EventBus.getDefault().post(CommandEvent(Constants.COMMAND_STOP_SCALE))
        }
        EventBus.getDefault().post(CommandEvent(Constants.COMMAND_GET_PARAMETERS))
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 1)
    fun onEvent(event: ValueChangeEvent?) {
        val live = event?.liveData

        val liveTime:Int = live?.liveweight!!

        val num6 = (liveTime / 1) % 10;
        val num5 = (liveTime / 10) % 10;
        val num4 = (liveTime / 100) % 10;
        val num3 = (liveTime / 1000) % 10;
        val num2 = (liveTime / 10000) % 10;
        val num1 = (liveTime / 100000) % 10;



        //Log.d(TAG,"live time "+num1+num2+":"+num3+num4+": "+num5+num6+"     "+live?.liveweight)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(parameters: Parameters) {
        //Log.d(TAG, "onEvent Parameters cmode "+parameters.containerMode+" mincon " +parameters.minContainerWeight+" containerMode "+Help.isContainerEnabled(activity))
        if (initParamsFirstTime) {
            // initParamsFirstTime is only used for doing setUpPrefs once after parameters are loaded.
            setUpPrefs(parameters)
            initParamsFirstTime = false
        }

        if (doUpdate != -1) {
            Log.d(TAG, "value was updated")
            listener.valueWasUpdated(getKeyString(feedbackKey), feedbackVal)
            feedbackKey = ""
            feedbackVal = ""
        }
        doUpdate = 0
    }

    fun getKeyString(key: String): String {

        return when (key) {

            "setOperationModeKey" -> {
                getString(R.string.workmode)
            }
            "setMassUnitKey" -> {
                return getString(R.string.massunit)
            }
            "setStableLimitKey" -> {
                return getString(R.string.stablelimit)
            }
            "setMinNetWeightKey" -> {
                return getString(R.string.minnetweight)
            }
            "setMinWeightKey" -> {
                return getString(R.string.minweight)
            }
            "setPlayTimeKey" -> {
                return getString(R.string.playtime)
            }
            "setDelayTimeKey" -> {
                return getString(R.string.delaytime)
            }
            "setMaxWeightKey" -> {
                return getString(R.string.maxweight)
            }
            else -> {
                return ""
            }
        }
    }

    interface SnackBarEvent {
        fun valueWasUpdated(key: String?, v: String?)
    }

    private lateinit var listener: SnackBarEvent



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SnackBarEvent) {
            listener = context
        } else {
            throw RuntimeException(requireContext().toString() + " must implement FragmentEvent")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {

            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
