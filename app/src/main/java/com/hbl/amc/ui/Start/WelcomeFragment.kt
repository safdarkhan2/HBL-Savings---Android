package com.hbl.amc.ui.Start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentWelcomeBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.CustomerInformation.CustomerInformationActivity
import com.hbl.amc.ui.CustomerInformation.CustomerInformationMainFragment
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoActivity
import com.hbl.amc.ui.productInformation.ProductSelectionActivity
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
//    lateinit var adapter: RequirementsExpandableAdapter
    lateinit var list : ArrayList<ReqExpandableModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()

//        HBLPreferenceManager.clearPreferences(requireContext())

        return view
    }

    fun initViews() {

        setupDropDownList()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.dropdownList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = RequirementsExpandableAdapter(
                requireContext(),
                list
            )
        }

        binding.dropdownList.adapter?.notifyDataSetChanged()
        Log.d("test","test")

        binding.continueBtn.setOnClickListener {
            val intent = Intent(requireContext(), CustomerInformationMainFragment::class.java)
//            val intent = Intent(requireContext(), RegulatoryInfoActivity::class.java)
            startActivity(intent)
        }

        var accType = HBLPreferenceManager.getAccountType(requireContext())

        accType?.let {
            if (it.code == "01-KS") {
                binding.min1.text = "Identification Document"
                binding.min2.text = "Proof of Income"
//                binding.min3.text = "Other Documents for FATCA/CRS"
                binding.min3.visibility = GONE
            } else if (it.code == "01-BB") {
                binding.min1.text = "Identification Document"
                binding.min2.visibility = GONE
                binding.min3.visibility = GONE
            }
        }
    }

    private fun setupDropDownList() {
        list = ArrayList()
        val customer_information_list = ArrayList<String>()
        val regulatory_info_list = ArrayList<String>()
        val product_selection_list = ArrayList<String>()
        val disclaimers_investment_list = ArrayList<String>()

        customer_information_list.add("Personal")
        customer_information_list.add("Professional")
        customer_information_list.add("Bank Details")

        regulatory_info_list.add("Know Your Customer (KYC)")
        regulatory_info_list.add("Risk Profile Questionnaire")
        regulatory_info_list.add("FATCA Declaration")
        regulatory_info_list.add("CRS Declaration")

        product_selection_list.add("Funds/Scheme Selection")
//        product_selection_list.add("Funds Selection")
//        product_selection_list.add("Other Information")

        disclaimers_investment_list.add("Document Checklist")
        disclaimers_investment_list.add("Disclaimers and T&Cs")


        val customerInfo = ReqExpandableModel(
            ReqExpandableModel.PARENT,
            OnboardingRequirement.Requirement(
                "Customer Information",
                customer_information_list,
                1
            ),
            isExpanded = false,
            isCloseShown = true
        )

        val regulatoryInfo = ReqExpandableModel(
            ReqExpandableModel.PARENT,
            OnboardingRequirement.Requirement(
                "Regulatory Information",
                regulatory_info_list,
                2
            ),
            isExpanded = false,
            isCloseShown = true
        )

        val productSelection = ReqExpandableModel(
            ReqExpandableModel.PARENT,
            OnboardingRequirement.Requirement(
                "Product Selection",
                product_selection_list,
                3
            ),
            isExpanded = false,
            isCloseShown = true
        )

        val disclaimerInvestment = ReqExpandableModel(
            ReqExpandableModel.PARENT,
            OnboardingRequirement.Requirement(
                "Disclaimers",
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}