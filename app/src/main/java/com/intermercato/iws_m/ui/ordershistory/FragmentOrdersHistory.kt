package com.intermercato.iws_m.ui.ordershistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.intermercato.iws_m.R
import com.intermercato.iws_m.databinding.FragmentOrdersHistoryBinding

class FragmentOrdersHistory : Fragment(R.layout.fragment_orders_history) {

    private var fragmentOrderBinding: FragmentOrdersHistoryBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOrdersHistoryBinding.bind(view)
        fragmentOrderBinding = binding
        binding.apply {

        }
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentOrderBinding = null
    }
}