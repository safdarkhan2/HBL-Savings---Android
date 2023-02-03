package com.hbl.amc.ui.ThankYou

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentCrsBinding
import com.hbl.amc.databinding.FragmentThankyouBinding
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.DialogUtils

class ThankYouFragment : Fragment() {

    private var _binding: FragmentThankyouBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                // block backward navigation from this section
//                Toast.makeText(requireContext(), "abc", LENGTH_LONG).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThankyouBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()
        setupProgress()

        return view
    }

    private fun initViews() {

        binding.appBar.title.isVisible = false
        binding.appBar.stepTv.isVisible = false
        binding.appBar.backBtn.isVisible = false

        binding.continueBtn.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

//        binding.backBtn.setOnClickListener {
//            findNavController().navigateUp()

//        }
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 100
        binding.appBar.progressbar4.progress = 100
        binding.appBar.progress.text = "100%"
    }
}