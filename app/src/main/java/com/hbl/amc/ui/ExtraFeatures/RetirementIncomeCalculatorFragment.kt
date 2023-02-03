package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentRetirementIncomeCalculatorBinding
import com.hbl.amc.utils.setupEditText

class RetirementIncomeCalculatorFragment : Fragment() {

    private var _binding: FragmentRetirementIncomeCalculatorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRetirementIncomeCalculatorBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()

        return view
    }

    fun initView()
    {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.annualContributionEt.setupEditText(requireContext(), binding.annualContributionLayout)
        binding.currentAgeEt.setupEditText(requireContext(), binding.currentAgeLayout)
        binding.monthlyContributionEt.setupEditText(requireContext(), binding.monthlyContributionLayout)
        binding.assumedAnnualReturnEt.setupEditText(requireContext(), binding.assumedAnnualReturnLayout)
        binding.plannedRetirementAgeEt.setupEditText(requireContext(), binding.plannedRetirementAgeLayout)
        binding.retirementPeriodEt.setupEditText(requireContext(), binding.retirementPeriodLayout)
        binding.totalRetirementSavingsEt.setupEditText(requireContext(), binding.totalRetirementSavingsLayout)

        binding.expandTextView.text =
            resources.getString(R.string.dummy_text)
    }
}