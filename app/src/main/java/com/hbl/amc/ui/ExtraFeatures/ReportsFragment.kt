package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbl.amc.databinding.FragmentLetUsCallYouBinding
import com.hbl.amc.databinding.FragmentReportsBinding

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()

        return view
    }

    fun initViews()
    {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}