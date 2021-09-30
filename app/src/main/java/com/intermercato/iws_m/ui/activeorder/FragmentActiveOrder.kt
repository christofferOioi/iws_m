package com.intermercato.iws_m.ui.activeorder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.intermercato.iws_m.Constants.FRAGMENT_REQUEST_KEY
import com.intermercato.iws_m.Constants.KEYNUMBER
import com.intermercato.iws_m.R
import com.intermercato.iws_m.adapters.BankOnClickCallback
import com.intermercato.iws_m.adapters.BanksRecyclerViewAdapter
import com.intermercato.iws_m.adapters.DividerDecoration
import com.intermercato.iws_m.databinding.FragmentActiveOrderBinding
import com.intermercato.iws_m.realmModels.Bank
import com.intermercato.iws_m.utils.BankUtils
import com.intermercato.iws_m.utils.ScreenHelp
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import org.greenrobot.eventbus.EventBus
import se.oioi.intelweighblelib.Constants
import se.oioi.intelweighblelib.events.scale.CommandEvent

class FragmentActiveOrder : Fragment(R.layout.fragment_active_order) , BankOnClickCallback{

    companion object {

        const val TAG = "active"
    }
    private val args : FragmentActiveOrderArgs by navArgs()
    private var fragmentActiveOrderBinding : FragmentActiveOrderBinding? = null
    private val viewModel : ActiveOrderViewModel by viewModels()
    private var realm : Realm? = null
    private var bankres: RealmResults<Bank>? = null

    private var mAdapter: BanksRecyclerViewAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding  = FragmentActiveOrderBinding.bind(view)
        fragmentActiveOrderBinding = binding
        realm = Realm.getDefaultInstance()

        binding.apply {
            btnStartWeighing.setOnClickListener {
                // compile time safe using action to navigate, safeargs is used
                val destinationId = FragmentActiveOrderDirections.actionFragmentActiveOrderToFragmentWeighing(args.orderId, args.orderName)
                findNavController().navigate(destinationId)
            }
            btnGetParams.setOnClickListener {
                EventBus.getDefault().post(CommandEvent(Constants.COMMAND_GET_PARAMETERS))
            }

            btnStopScale.setOnClickListener {
                EventBus.getDefault().post(CommandEvent(Constants.COMMAND_STOP_SCALE))
            }


            viewModel.currentOrderName.observe(viewLifecycleOwner, { name ->

            })
        }
        setHasOptionsMenu(true)
        viewModel.setCurrentOrderId(args.orderId,args.orderName)
        viewModel.getOrderById(args.orderId).let {
            binding.apply {
                Log.d(TAG,"${it?.orderShipName} ${it?.orderNumber}")
                txtShipName.text = it?.orderShipName
                txtOrderName.text = it?.orderNumber
                txtArrivalDate.text  = it?.arrivalDate
                txtOrderMessagesTitle.text = requireContext().getString(R.string.txtMessageAsTitle)
                txtOrderMessages.text = it?.message
            }

        }
        setUpListener()
        setUpRecyclerView(binding)


    }

    private fun setUpRecyclerView(binding: FragmentActiveOrderBinding){
        Log.d(TAG,"safeArgs ${args.orderId}")
        bankres = realm?.where(Bank::class.java)?.equalTo("orderId", args.orderId)?.findAllAsync()
        Log.d(TAG, "bankres " + bankres?.size)
        bankres?.addChangeListener(bankListener)

        mAdapter = BanksRecyclerViewAdapter(bankres, requireContext())
        mAdapter?.setShowBankArrow(true)
        mAdapter?.setClickListener(this)

        binding.apply {

            activeBankRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            activeBankRecyclerview.addItemDecoration(DividerDecoration(requireContext(), R.drawable.list_divider))
            activeBankRecyclerview.setHasFixedSize(true)
            activeBankRecyclerview.adapter = mAdapter
        }

    }

    override fun onBankClick(bankId: String?, totalBankWeight: Int, bankIndex: Int) {

    }

    override fun onActiveBankUpdated(totalBankWeight: Int) {

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

    fun setUpListener(){
        parentFragmentManager.setFragmentResultListener(
            FRAGMENT_REQUEST_KEY,
            this,
            { requestKey, result ->
                onFragmentResult(requestKey, result)
            })
    }
    @SuppressLint("RestrictedApi")
    private fun onFragmentResult(requestKey: String, result: Bundle) {
        Preconditions.checkState(FRAGMENT_REQUEST_KEY == requestKey)

        val number = result.getInt(KEYNUMBER)
        Log.d(TAG,"number $number")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentActiveOrderBinding = null
        realm?.close()
    }
}