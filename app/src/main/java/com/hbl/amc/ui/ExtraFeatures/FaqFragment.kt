package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentFaqBinding

class FaqFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()

        return view
    }

    fun initView() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.mutualFundsLayout.setOnClickListener {
            if (!binding.mutualFundsSubLayout.isVisible) {
                binding.mutualFundsSubLayout.visibility = View.VISIBLE
                binding.mutualFundsDropdownBtn.rotation = 0f
            } else {
                binding.mutualFundsSubLayout.visibility = View.GONE
                binding.mutualFundsDropdownBtn.rotation = 270f
            }
        }
        binding.pensionFundsLayout.setOnClickListener {
            if (!binding.pensionFundsSubLayout.isVisible) {
                binding.pensionFundsSubLayout.visibility = View.VISIBLE
                binding.pensionFundsDropdown.rotation = 0f
            } else {
                binding.pensionFundsSubLayout.visibility = View.GONE
                binding.pensionFundsDropdown.rotation = 270f
            }
        }
        binding.digitalAccountOpeningLayout.setOnClickListener {
            if (!binding.digitalAccountOpeningSubLayout.isVisible) {
                binding.digitalAccountOpeningSubLayout.visibility = View.VISIBLE
                binding.digitalAccountOpeningDropdown.rotation = 0f
            } else {
                binding.digitalAccountOpeningSubLayout.visibility = View.GONE
                binding.digitalAccountOpeningDropdown.rotation = 270f
            }
        }

        binding.mutualFundsLayout.setOnClickListener {
            findNavController().navigate(R.id.faqMutualFundsFragment)
        }
    }
}