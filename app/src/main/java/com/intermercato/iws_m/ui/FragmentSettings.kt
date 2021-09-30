package com.intermercato.iws_m.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.intermercato.iws_m.databinding.FragmentSettingsBinding

class FragmentSettings : Fragment() {

    private var binding : FragmentSettingsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _binding = FragmentSettingsBinding.bind(view)
        binding = _binding
    }

}
