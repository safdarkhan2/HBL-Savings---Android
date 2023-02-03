package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hbl.amc.databinding.FragmentFaqMutualfundsBinding

class FaqMutualFundsFragment : Fragment() {

    private var _binding: FragmentFaqMutualfundsBinding? = null

    private val binding get() = _binding!!

    val mainDocumentsList = listOf(
        "HBL Investment Fund",
        "HBL Growth Fund",
        "HBL Islamic Dedicated Equity Fund",
        "HBL Stock Fund"
    )

    private lateinit var faqMutualFundsMainAdapter: FaqMutualFundsMainItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaqMutualfundsBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()

        return view
    }

    fun initView() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        faqMutualFundsMainAdapter = FaqMutualFundsMainItemAdapter()
        binding.faqMutualfundsMainRv.apply {
            adapter = faqMutualFundsMainAdapter
            layoutManager = GridLayoutManager(this.context,1)
        }
        binding.faqMutualfundsMainRv.isNestedScrollingEnabled = false

        faqMutualFundsMainAdapter.updateData(mainDocumentsList)


    }
}