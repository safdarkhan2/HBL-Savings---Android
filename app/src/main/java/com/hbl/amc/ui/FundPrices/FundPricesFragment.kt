package com.hbl.amc.ui.FundPrices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.databinding.FragmentFundPricesBinding

class FundPricesFragment : Fragment() {

    private var _binding: FragmentFundPricesBinding? = null
    private val binding get() = _binding!!
    lateinit var list: ArrayList<FundPricesExpandableModel>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFundPricesBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root
        initView()
        return rootView
    }

    private fun initView() {
        setupDropDownList()
        binding.fundPricecsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        val adapter = FundPricesExpandableAdapter(requireContext(), list)
        binding.fundPricecsRecyclerView.adapter = adapter
    }

    private fun setupDropDownList() {
        list = ArrayList()
        val customer_information_list = ArrayList<String>()
        val regulatory_info_list = ArrayList<String>()
        val product_selection_list = ArrayList<String>()
        val disclaimers_investment_list = ArrayList<String>()

        customer_information_list.add("Personal Information")
        customer_information_list.add("Professional Information")
        customer_information_list.add("Bank Account Information")

        regulatory_info_list.add("Know Your Customer (KYC)")
        regulatory_info_list.add("Risk Profile Questionnaire")
        regulatory_info_list.add("FATCA Checklist")
        regulatory_info_list.add("CRS")

        product_selection_list.add("Products")
        product_selection_list.add("Funds Selection")
        product_selection_list.add("Other Information")

        disclaimers_investment_list.add("Document Checklist")
        disclaimers_investment_list.add("Disclaimer")


        val customerInfo = FundPricesExpandableModel(
            FundPricesExpandableModel.PARENT,
            FundPricesParent.FundPricesChild(
                "Customer Information",
                customer_information_list,
                1
            ),
            isExpanded = false,
            isCloseShown = true
        )

        val regulatoryInfo = FundPricesExpandableModel(
            FundPricesExpandableModel.PARENT,
            FundPricesParent.FundPricesChild(
                "Regulatory Information",
                regulatory_info_list,
                2
            ),
            isExpanded = false,
            isCloseShown = true
        )

        val productSelection = FundPricesExpandableModel(
            FundPricesExpandableModel.PARENT,
            FundPricesParent.FundPricesChild(
                "Product Selection",
                product_selection_list,
                3
            ),
            isExpanded = false,
            isCloseShown = true
        )

        val disclaimerInvestment = FundPricesExpandableModel(
            FundPricesExpandableModel.PARENT,
            FundPricesParent.FundPricesChild(
                "Disclaimers & Investment",
                disclaimers_investment_list,
                4
            ),
            isExpanded = false,
            isCloseShown = true
        )

        list.add(customerInfo)
        list.add(regulatoryInfo)
        list.add(productSelection)
        list.add(disclaimerInvestment)
    }


}