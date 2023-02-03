package com.hbl.amc.ui.ExtraFeatures

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentGuidesBinding
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.self_service.SelfServiceActivity

class GuidesFragment : Fragment() {

    private var _binding: FragmentGuidesBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        // Change this code and add extra features in new activity
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuidesBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()

        return view
    }

    fun initView() {

        binding.backIv.setOnClickListener {
            requireActivity().finish()
        }

        binding.calculatorLayout.setOnClickListener {
            if (!binding.calculatorSubLayout.isVisible) {
                binding.calculatorSubLayout.visibility = VISIBLE
                binding.calculatorDropdownBtn.rotation = 0f
            }
            else {
                binding.calculatorSubLayout.visibility = GONE
                binding.calculatorDropdownBtn.rotation = 270f
            }
        }

        binding.sipCalculator.setOnClickListener {
            findNavController().navigate(R.id.SIPCalculatorFragment)
        }

        binding.featureValueCalculator.setOnClickListener {
            findNavController().navigate(R.id.futureValueCalculatorFragment)
        }

        binding.mutualFundsTaxCalculator.setOnClickListener {
            findNavController().navigate(R.id.mutualFundsTaxSavingCalculatorFragment)
        }

        binding.vpsCalculator.setOnClickListener {
            findNavController().navigate(R.id.VPSFundsTaxSavingCalculator)
        }

        binding.retirementIncomeCalculator.setOnClickListener {
            findNavController().navigate(R.id.retirementIncomeCalculatorFragment)
        }

        binding.documents.setOnClickListener{
            findNavController().navigate(R.id.documentsFragment)
        }

        binding.faqs.setOnClickListener{
            findNavController().navigate(R.id.faqFragment)
        }
    }
}