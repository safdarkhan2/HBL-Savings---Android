package com.hbl.amc.ui.Generic

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.databinding.FragmentSettingsBinding
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.Start.MainActivity

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()
        return view
    }

    fun initViews()
    {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.logoutLayout.setOnClickListener {
            HBLPreferenceManager.clearPreferences(requireContext())
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}