package com.intermercato.iws_m.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.intermercato.intermercatocloudrequests.adapter.TopSpacingItemDecoration
import com.intermercato.iws_m.R
import com.intermercato.iws_m.adapters.OrdersAdapter
import com.intermercato.iws_m.databinding.FragmentOrdersBinding
import com.intermercato.iws_m.realmModels.Order
import com.intermercato.iws_m.utils.DataState


class FragmentOrders : Fragment(R.layout.fragment_orders) , OrdersAdapter.ClickCallBack{

    companion object {
        const val TAG = "api"
    }

    private var fragmentOrderBinding : FragmentOrdersBinding? = null
    private lateinit var ordersAdapter : OrdersAdapter
    private var orderList : List<Order> = ArrayList()
    private val viewModel : OrdersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOrdersBinding.bind(view)
        fragmentOrderBinding = binding

        setHasOptionsMenu(true)

        // init recyclerview for displaying incoming orders
        initRecyclerView(binding)

        setupObservers()
        // init mainStateEvent
        viewModel.mainStateEvent(OrdersViewModel.MainStateEvent.GetOrders)
    }

    private fun setupObservers(){

            viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

                    when(dataState){
                        is DataState.Loading -> {
                            displayProgressBar(true)
                        }

                        is DataState.Success -> {
                            Log.d(TAG,"data ${dataState.data.size}")
                            ordersAdapter.submitData(dataState.data)
                            displayProgressBar(false)
                        }

                        is DataState.Error -> {
                            displayError(dataState.exception.message)
                        }

                        else -> {}
                    }
            })

    }

    private fun initRecyclerView(binding : FragmentOrdersBinding){
        binding.apply {

            ordersAdapter = OrdersAdapter()
            ordersAdapter.setClickListener(this@FragmentOrders)
            ordersRecyclerView.setHasFixedSize(true)
            ordersRecyclerView.addItemDecoration(TopSpacingItemDecoration(20))
            ordersRecyclerView.adapter = ordersAdapter
        }
    }

    override fun onClickOrder(order: Order) {
        Log.d(TAG,"click ${order.id}")
        /*val materials: List<Order>? = order.b
        if (materials != null) {
            for(m in materials){
                Log.d(TAG,"material ${m.id} ${m.name} ${m.total }")
            }
        }*/
        val destinationId = FragmentOrdersDirections.actionFragmentOrdersToFragmentActiveOrder(order.id!!, order.orderShipName!!)
        findNavController().navigate(destinationId)
    }

    private fun displayError(message: String?){
        fragmentOrderBinding?.apply {
            if(message != null) Toast.makeText(requireContext(),getString(R.string.api_error),Toast.LENGTH_LONG).show()
        }

    }

    private fun displayProgressBar(isDisplayed: Boolean){
        fragmentOrderBinding?.apply {
            orderProgressBar.visibility = if(isDisplayed) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentOrderBinding = null
    }
}