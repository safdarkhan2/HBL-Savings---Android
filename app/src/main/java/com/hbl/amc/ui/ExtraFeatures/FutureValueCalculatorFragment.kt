package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentFutureValueCalculatorBinding
import com.hbl.amc.databinding.FragmentRetirementIncomeCalculatorBinding
import com.hbl.amc.utils.setupEditText

class FutureValueCalculatorFragment : Fragment() {

    private var _binding: FragmentFutureValueCalculatorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFutureValueCalculatorBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()

        return view
    }

    fun initView()
    {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.periodicContributionAmountEt.setupEditText(requireContext(), binding.periodicContributionAmountLayout)
        binding.savingsFrequencyEt.setupEditText(requireContext(), binding.savingsFrequencyLayout)
        binding.assumedAnnualReturnEt.setupEditText(requireContext(), binding.assumedAnnualReturnLayout)
        binding.savingsTenureEt.setupEditText(requireContext(), binding.savingsTenureLayout)

        binding.expandTextView.text =
            resources.getString(R.string.dummy_text)
    }
}