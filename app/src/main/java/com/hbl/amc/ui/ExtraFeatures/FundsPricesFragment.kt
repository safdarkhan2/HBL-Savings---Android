package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hbl.amc.databinding.FragmentFundsPricesBinding

class FundsPricesFragment : Fragment() {

    private var _binding: FragmentFundsPricesBinding? = null

    private val binding get() = _binding!!

    val mainDocumentsList = listOf(
        "HBL Investment Fund",
        "HBL Growth Fund",
        "HBL Islamic Dedicated Equity Fund",
        "HBL Stock Fund"
    )

    private  lateinit var fundsPricesMainAdapter : FundsPricesMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFundsPricesBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()

        return view
    }

    fun initView()
    {
        fundsPricesMainAdapter = FundsPricesMainAdapter()
        binding.fundsPricesMainRv.apply {
            adapter = fundsPricesMainAdapter
            layoutManager = GridLayoutManager(this.context,1)
        }

        fundsPricesMainAdapter.updateData(mainDocumentsList)
    }
}