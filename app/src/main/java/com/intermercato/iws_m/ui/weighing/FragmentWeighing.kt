package com.intermercato.iws_m.ui.weighing

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intermercato.iws_m.Constants.FRAGMENT_REQUEST_KEY
import com.intermercato.iws_m.Constants.KEYNUMBER
import com.intermercato.iws_m.R
import com.intermercato.iws_m.adapters.BankOnClickCallback
import com.intermercato.iws_m.adapters.BanksRecyclerViewAdapter
import com.intermercato.iws_m.adapters.DividerDecoration
import com.intermercato.iws_m.components.ProgressBarWithTextProgress
import com.intermercato.iws_m.databinding.FragmentWeighingBinding
import com.intermercato.iws_m.realmModels.Bank
import com.intermercato.iws_m.realmModels.Order
import com.intermercato.iws_m.ui.dialogfragments.WeighingButtonsFragment
import com.intermercato.iws_m.utils.BankUtils
import com.intermercato.iws_m.utils.ContainerHelper
import com.intermercato.iws_m.utils.ScreenHelp
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import se.oioi.intelweighblelib.Constants
import se.oioi.intelweighblelib.helpers.Help


class FragmentWeighing : Fragment(R.layout.fragment_weighing), WeighingContract.View,
    BankOnClickCallback,WeighingButtonsFragment.OnClickWeighingActions, ProgressBarWithTextProgress.OnViewFinished {

    private val TAG = "weighing"
    private lateinit var binding : FragmentWeighingBinding
    private var realm : Realm? = null
    private var bankres: RealmResults<Bank>? = null
    private var bankRecyclerview: RecyclerView? = null
    private var mAdapter: BanksRecyclerViewAdapter? = null
    private var progressBarShowMaxWeight : ProgressBarWithTextProgress?=null
    private val args : FragmentWeighingArgs by navArgs()


    private val viewModel: WeighingViewModel by viewModels { WeighingFactoryViewModel(this, args.orderId) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        binding = FragmentWeighingBinding.bind(view)

        binding.apply {

            btnExit.setOnClickListener {

            }


            progressBarShowMaxWeight?.apply {
                setListener(this@FragmentWeighing)
            }

            // weighing buttons add, subtract, tare, undo
            rootView.setOnClickListener {
                val controlButtons = WeighingButtonsFragment.newInstance(2,false)
                controlButtons.setListener(this@FragmentWeighing)
                controlButtons.show(parentFragmentManager,"weighing_buttons")

            }
        }


        setupRecyclerView(args.orderId)
        //EventBus.getDefault().post(StartComboxEvent(true))
        viewModel?.starInteractorInit()
        viewModel?.setCurrentBankByActive(args.orderId)
        setResult(66)

    }



    fun setupRecyclerView(orderId : String){
        val wh = ScreenHelp.getScreenWidthAndHeight(requireActivity())

        bankres = realm?.where(Bank::class.java)?.equalTo("orderId", orderId)?.findAllAsync()
        Log.d(TAG, "bankres " + bankres?.size)
        bankres?.addChangeListener(bankListener)

        mAdapter = BanksRecyclerViewAdapter(bankres, requireContext())
        mAdapter?.setShowBankArrow(true)
        mAdapter?.setClickListener(this)

        binding.apply {

            bankRecyclerview?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            bankRecyclerview?.addItemDecoration(DividerDecoration(requireContext(), R.drawable.list_divider))
            bankRecyclerview?.adapter = mAdapter
            bankRecyclerview?.setHasFixedSize(true)
        }
    }



    private val bankListener: RealmChangeListener<RealmResults<Bank>> = RealmChangeListener { it ->
        Log.d(TAG, "object " + it.size)
        if (mAdapter != null) {
            updateLayoutParams(it.size)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    private fun updateLayoutParams(numberOfBanks: Int) {
        // get with hight from phone screen
        val wh = ScreenHelp.getScreenWidthAndHeight(requireActivity())
        Log.d(TAG, "wh  w" + wh[0] + " h " + wh[1])
        // get the calculatedwith based on number of banks and screen width
        val calculatedWidth = BankUtils.getBanksWidth(numberOfBanks, wh[0])
        //Log.d(TAG, "calculatedWidth: " +calculatedWidth);
        // create Constraint layoutparams
        val params = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params.width = calculatedWidth
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        // load layout params and width and high to adapter
        mAdapter!!.setLayoutParamsForHolder(params, wh[0], wh[1])
    }

    override fun onBankClick(bankId: String?, totalBankWeight: Int, bankIndex: Int) {
        Log.d(TAG, "onBankClick $bankId totalBankWeight $totalBankWeight bankIndex $bankIndex")
        //presenter?.saveBankIndex(currentOrderId, bankId)
        viewModel?.saveBankIndex(args.orderId,bankId)
    }

    override fun WeighingButtonsClicked(v: View) {
        Log.d(TAG,"WeighingButtonsClicked ${v.id}")
        when(v?.id) {

            R.id.btnAction -> viewModel.doAction()
            R.id.btnUndo -> viewModel.doUndo()
            R.id.btnTare -> viewModel.doTare()
            R.id.btnSubtract -> viewModel.doSubtraction()
        }
    }

    override fun onActiveBankUpdated(totalBankWeight: Int) {
        /*
        weight?.let {
                  tvBankWeight.text = String.format("%s %s", WeightUtils.formatWeight(false, it), PrefsRepo.getWeightUnit())
              }
              */
    }

    private fun setResult(number: Int) {
        parentFragmentManager.setFragmentResult(
            FRAGMENT_REQUEST_KEY,
            bundleOf(KEYNUMBER to number)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()

    }

    override fun displayBattery(battStatus: Int?) {
        binding.apply {
            activity?.runOnUiThread {

            }
        }
    }

    override fun displayWorkMode(mode: Int?) {
        binding.apply {
            activity?.runOnUiThread {

                mode?.let {
                    when (it) {
                        Constants.WorkStates.SEMI -> {
                            if (Help.isContainerEnabled(requireContext())) {
                                tvWorkMode.text = getString(R.string.txt_containermode)
                            } else {
                                tvWorkMode.text = getString(R.string.txt_weightModeSemiAutomatic)
                            }
                        }
                        Constants.WorkStates.FULL -> tvWorkMode.text = getString(R.string.txt_weightModeFullAutomatic)
                        Constants.WorkStates.NORMAL -> tvWorkMode.text = ContainerHelper.isContainerActive()
                        Constants.WorkStates.STATIC -> tvWorkMode.text = getString(R.string.txt_weightModeStatic)
                    }
                }
            }
        }
    }

    override fun displayLiveWeight(weight: Int?, containerMinWeight: Int?, rssi: Int?) {
        binding.apply {
            activity?.runOnUiThread {
                liveWeight.text = weight.toString()
                tvRssiValue.text = rssi.toString()
                Log.d(TAG,"weight v $weight")
            }
        }
    }

    override fun displaySavedContainerWeights(brutto: Int, container: Int, netto: Int, numberOfTotalWeights: Int) {

    }

    override fun displayStatus(status: String) {
        Log.d(TAG,"displayStatus v $status")
        binding.apply {
            activity?.runOnUiThread {
                txtStatus.text = status
            }
        }
    }


    override fun displayCircularProgress(inProgress: Boolean) {

    }

    override fun displayState(state: Int?) {
        Log.d(TAG,"displayState v $state")
    }

    override fun displayTare(progress: Int) {
        binding.apply {
            Log.d(TAG, "displayTare")
            var p:Int = progress
            progressTare.max = 96

            Log.d(TAG, "displayTare progress: $progress")
            when (progress) {
                in 1..100 -> {
                    progressTare.visibility = View.VISIBLE
                    progressTare.progress = progress
                }
                else -> progressTare.visibility = View.INVISIBLE
            }
        }
    }

    override fun displaySubtraction(subtract: Boolean) {
        binding.apply {
            Log.d(TAG, "displaySubtraction")
            if (subtract) {
                liveWeight.setBackgroundColor(resources.getColor(R.color.red))
                liveWeight.setTextColor(resources.getColor(R.color.white))
            } else {
                liveWeight.setBackgroundColor(resources.getColor(R.color.cream_white))
                liveWeight.setTextColor(resources.getColor(R.color.black))
            }
        }
    }

    override fun displayConnectionDown(isConnectionDown: Boolean) {

    }

    override fun getCurrentOrder(order: Order?) {

    }

    override fun updateBankWeights(weight: Int?) {

    }

    override fun setMinMaxWeights(min: Int?, max: Int?) {
        Log.d(TAG, "setMinMaxWeights max $max min $min")
        binding.apply {
            max.let {

                if (it != null) {
                    progressBarShowMaxWeight?.setMaxTotalWeight(it)
                }
                if (it!! > 0) {

                    //showProgress = true

                } else {

                }
            }
        }
    }
    // interface of ProgressBarWithTextProgress
    override fun onProgressMaxWeightViewFinished() {
        Log.d(TAG, "onProgressMaxWeightViewFinished")
    }
}