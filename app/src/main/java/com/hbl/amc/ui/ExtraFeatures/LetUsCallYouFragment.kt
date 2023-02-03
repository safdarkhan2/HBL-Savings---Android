package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.databinding.FragmentLetUsCallYouBinding
import com.hbl.amc.utils.TimePickerFragment
import com.hbl.amc.utils.setupEditText

class LetUsCallYouFragment : Fragment() {

    private var _binding: FragmentLetUsCallYouBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLetUsCallYouBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()

        return view
    }

    fun initViews() {
        binding.mobileNumberEt.setupEditText(requireContext(), binding.mobileNumberLayout)

        binding.selectPreferredTimeLayout.setEndIconOnClickListener{
            TimePickerFragment(binding.selectPreferredTimeEt).show(childFragmentManager,"timePicker")
        }

        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}