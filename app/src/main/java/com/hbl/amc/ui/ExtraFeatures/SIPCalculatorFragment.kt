package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentSipCalculatorBinding
import com.hbl.amc.utils.setupEditText

class SIPCalculatorFragment : Fragment() {

    private var _binding: FragmentSipCalculatorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSipCalculatorBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()
        return view
    }

    fun  initView()
    {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.savingTargetAmountEt.setupEditText(requireContext(), binding.savingTargetAmountLayout)
        binding.assumedAnnualReturnEt.setupEditText(requireContext(), binding.assumedAnnualReturnLayout)

        binding.expandTextView.text =
            resources.getString(R.string.dummy_text)
    }
}