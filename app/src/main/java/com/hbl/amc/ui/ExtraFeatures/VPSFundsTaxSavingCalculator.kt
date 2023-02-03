package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentVpsFundsTaxSavingCalculatorBinding
import com.hbl.amc.utils.setupEditText

class VPSFundsTaxSavingCalculator : Fragment() {

    private var _binding: FragmentVpsFundsTaxSavingCalculatorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentVpsFundsTaxSavingCalculatorBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()

        return view
    }

    fun initViews() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.investmentAmountEt.setupEditText(requireContext(), binding.investmentAmountLayout)
        binding.totalMonhlyIncomeEt.setupEditText(requireContext(), binding.totalMonhlyIncomeLayout)

        binding.expandTextView.text =
            resources.getString(R.string.dummy_text)
    }
}