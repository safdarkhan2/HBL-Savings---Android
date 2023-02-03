package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentContactUsBinding
import com.hbl.amc.databinding.FragmentLoginBinding
import com.hbl.amc.utils.setupEditText

class ContactUsFragment : Fragment() {

    private var _binding : FragmentContactUsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()
        return view
    }

    private fun initViews() {
        binding.yourNameEt.setupEditText(requireContext(), binding.yourName)
        binding.yourEmailEt.setupEditText(requireContext(), binding.yourEmail)
        binding.subjectEt.setupEditText(requireContext(), binding.subject)
        binding.contactNoEt.setupEditText(requireContext(), binding.contactNo)
        binding.cityEt.setupEditText(requireContext(), binding.city)
        binding.folioNumberEt.setupEditText(requireContext(), binding.folioNumber)

        binding.backIv.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}