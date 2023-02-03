package com.hbl.amc.ui.productInformation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentVpsFundsSelectionBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.FUNDS_RESULT
import com.hbl.amc.ui.LIFECYCLE_ALLOCATION
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoActivity
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.TO_RPQ
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VPSFundsSelectionFragment : Fragment() {

    private var toRpq = false
    private var _binding: FragmentVpsFundsSelectionBinding? = null
    private val binding get() = _binding!!
    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val productViewModel by viewModel<ProductInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var selectedFundsResult: GetSelectedFundsResult? = null
    var vpsFundCategoriesList: List<VPSFundCategory>? = null
    var vpsFundsList: List<Fund>? = null
    var allocationSchemes: List<AllocationScheme>? = null
    var selectedFundCategory: VPSFundCategory? = null
    var selectedFund: Fund? = null
    var selectedAllocationScheme: AllocationScheme? = null
    lateinit var fundsAdapter: FundsAdapter
    lateinit var allocationSchemesAdapter: AllocationSchemesAdapter
    var minRiskRange : Int? = null
    var maxRiskRange : Int? = null
    var riskLevels : List<RiskLevelsResult>? = null
    var userRiskLevel = ""
    private var age: Int = 0
    private var equityFund = 0
    private var debitFund = 0
    private var marketFund = 0
    lateinit var vpsCategoriesAdapter : VPSFundsCategoriesAdapter
    var filteredAllocations : ArrayList<AllocationScheme>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVpsFundsSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (it.containsKey(FUNDS_RESULT)) {
                selectedFundsResult = it.getSerializable(FUNDS_RESULT) as GetSelectedFundsResult
            }
        }

        initViews()
        initViewModel()
        setupProgress()

        if (selectedFundsResult != null) {
            productViewModel.getRiskLevels(HBLPreferenceManager.getToken(requireContext()))
        } else {
            productViewModel.getSelectedVPSFunds(
                HBLPreferenceManager.getToken(requireContext()),
                HBLPreferenceManager.getCustomerID(requireContext())
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (toRpq) {
            productViewModel.getRiskLevels(HBLPreferenceManager.getToken(requireContext()))
        }
    }

    private fun initViews() {
        binding.appBar.title.text = getString(R.string.product_selection)

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        vpsCategoriesAdapter = VPSFundsCategoriesAdapter (requireContext()) { vca ->

            vpsFundCategoriesList?.let { vfcList ->
                for (i in vfcList.indices) {
                    vfcList[i].isSelected = vfcList[i].id == vca.id

                    if (vfcList[i].isSelected) {
                        selectedFundCategory = vfcList[i]
                    }
                }

                vpsCategoriesAdapter.setCategoriesData(vfcList)

                if (selectedFundCategory != null) {
                    binding.txtInvestmentLimit.text = "Your investment limit is ${selectedFundCategory!!.cumulativeInvLimit}."
                } else {
                    selectedFundCategory = null
                    binding.vpsFundsRv.visibility = View.GONE
                    binding.txtInvestmentLimit.text = "Your investment limit is XYZ."
                }

                toggleContinueBtn(validateInput())
            }

        }

        binding.vpsFundsCategoriesRv.apply {
            adapter = vpsCategoriesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.txtChange.setOnClickListener {
//            findNavController().navigate(R.id.riskProfileQuestionnaireFragment)
            val intent = Intent(requireContext(), RegulatoryInfoActivity::class.java)
            toRpq = true
            intent.putExtra(TO_RPQ, toRpq)
            startActivity(intent)
        }

        binding.continueBtn.setOnClickListener {
            val saveVPSFundDTO = VPSFundSaveResult(
                HBLPreferenceManager.getCustomerID(requireContext()),
                "bf29cffe-f7e2-46eb-9ad2-0142287d4176",
//                "CDDCBFFE-5A95-4310-B8D4-FDF047130CB4",
                null,
//                selectedFund!!.id,
                selectedAllocationScheme!!.id,
                selectedFundCategory!!.id,
                equityFund,
                debitFund,
                marketFund,
                false
            )

            productViewModel.saveVPSFund(
                HBLPreferenceManager.getToken(requireContext()),
                saveVPSFundDTO
            )
        }

        binding.appBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.productsBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showConfirmationDialog() {
        DialogUtils.showConfirmationDialog(
            requireActivity(),
            layoutInflater,
            getString(R.string.caution),
            "According to your RPQ score you have been suggested XYZ Fund.\n\nIf you choose another fund you will choose it at your own risk.",
            R.drawable.ic_alert,
            onOkPressed = {
                findNavController().navigate(R.id.productOtherInfoFragment)
            },
            onCancelPressed = {}
        )
    }

    private fun initViewModel() {
        productViewModel.getSelectedVPSFundsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { svps ->
                        selectedFundsResult = svps
                        productViewModel.getRiskLevels(HBLPreferenceManager.getToken(requireContext()))
                    } ?: run {
                        it.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.getRiskLevelsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        it.response.result.let { rsk ->
                            minRiskRange = rsk[0].minimumScore
                            maxRiskRange = rsk[rsk.lastIndex].maximumScore
                            riskLevels = rsk

                            productViewModel.getVPSFundsData(
                                HBLPreferenceManager.getToken(requireContext()),
                                HBLPreferenceManager.getCustomerID(requireContext())
                            )
                        }
                    } else {
                        it.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 -> Log.d("risk levels error", it1) }
                }
            }
        })

        productViewModel.getVPSFundsDataApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { vps ->
                        showDetails(vps)
                    } ?: run {
                        it.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.getVPSFundsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { funds ->
                        showFunds(funds)
                    } ?: run {
                        it.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.saveVPSFundApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let {
                        findNavController().navigate(R.id.action_vpsFundsSelectionFragment_to_productOtherInfoFragment)
                    } ?: run {
                        it.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    requireContext(),
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    private fun showDetails(result: VPSFundsDataGetResult) {
        vpsFundCategoriesList = result.vpsCategoriesList
        allocationSchemes = result.allocationSchemeList
        binding.txtRpqScore.text = result.rpqScore.toString()
        binding.rpqMsgTxt.text = "Based on your choices your score is ${result.rpqScore}. \nYou may invest in any of the suggested funds below OR You may select other funds by disagreeing to the RPQ consent shown upon any suggested funds selection."

        if (minRiskRange != null && maxRiskRange != null) {
            val progress = AppUtils.mapProgressIndicatorValues(
                Range(minRiskRange!!, maxRiskRange!!),
                Range(0, 100),
                result.rpqScore
            )

//            val riskIndicator = RiskIndicator(requireContext())
            binding.riskIndicator.progress = progress

            getRiskLevel(result.rpqScore)

            when (userRiskLevel) {
                riskLevels!![0].riskLevel -> {
                    binding.riskIndicator.filledColor = R.color.progress_light_green
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.light_green_dot)
                }
                riskLevels!![1].riskLevel -> {
                    binding.riskIndicator.filledColor = R.color.yellow_theme
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.yellow_dot)
                }
                else -> {
                    binding.riskIndicator.filledColor = R.color.red
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.red_dot)
                }
            }

            binding.riskIndicator.invalidate() //to redraw the risk indicator with updated progress
        }


        //this spinner data and spinner below is not usable for now but I don't commented its code and functionality
        //instead of this now we're using recyclerview and adpater to display these funds and spinner visibilty is gone for now
        val fundCategories: ArrayList<String> = ArrayList()
        fundCategories.add(getString(R.string.select_vps_fund_category))
        vpsFundCategoriesList?.forEach { fundCategory ->
            fundCategories.add(fundCategory.name)
        }

//        binding.vpsFundCategoryTitle.visibility = View.VISIBLE
        binding.vpsFundCategories.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fundCategories)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    vpsFundCategoriesList?.let {
                        selectedFund = null
                        if (p2 > 0) {
                            selectedFundCategory = it[p2 - 1]
                            binding.txtInvestmentLimit.text = "Your investment limit is ${selectedFundCategory!!.cumulativeInvLimit}."

                            productViewModel.getVPSFunds(
                                HBLPreferenceManager.getToken(requireContext()),
                                selectedFundCategory!!.id
                            )
                        } else {
                            selectedFundCategory = null
                            binding.vpsFundsRv.visibility = View.GONE
                            binding.txtInvestmentLimit.text = "Your investment limit is XYZ."
                        }
                    }

                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }

        //recyclerview adapter is setting here and above spinner and data is not showing for now
        vpsFundCategoriesList?.let { vfc ->
            vpsCategoriesAdapter.setCategoriesData(vfc)
        }



        if (!selectedFundsResult!!.vpsFundCategoryId.isNullOrEmpty()) {
            for (i in vpsFundCategoriesList!!.indices) {
                if (vpsFundCategoriesList!![i].id == selectedFundsResult!!.vpsFundCategoryId) {
                    binding.vpsFundCategories.setSelection(i + 1)
                    selectedFundCategory = vpsFundCategoriesList!![i]
                    toggleContinueBtn(validateInput())
                    break
                }
            }
        }

        if (!selectedFundsResult!!.allocationSchemeId.isNullOrEmpty()) {
            for (i in allocationSchemes!!.indices) {
                if (allocationSchemes!![i].id == selectedFundsResult!!.allocationSchemeId) {

                    allocationSchemes!![i].isSelected = true
                    allocationSchemes!![i].equitySubFund = selectedFundsResult!!.equitySubFund
                    allocationSchemes!![i].debitSubFund = selectedFundsResult!!.debtSubFund
                    allocationSchemes!![i].moneyMarketSubFund =
                        selectedFundsResult!!.moneyMarketSubFund

                    selectedAllocationScheme = allocationSchemes!![i]
                    toggleContinueBtn(validateInput())
                    break
                }
            }
        }

        // TODO Will get from the API
        age = 30

        val onSelectOrUnSelectAllocation: (position: Int, isSelected: Boolean) -> Unit =
            { position: Int, isSelected: Boolean ->
                if (filteredAllocations.isNullOrEmpty()) {
                    for (i in allocationSchemes!!.indices) {
                        if (i == position) {
                            selectedAllocationScheme = if (isSelected) {
                                allocationSchemes!![i]
                            } else {
                                null
                            }
                            allocationSchemes!![i].isSelected = isSelected
                        } else {
                            allocationSchemes!![i].isSelected = false
                        }

                        toggleContinueBtn(validateInput())
                    }
                    allocationSchemesAdapter.updateData(allocationSchemes!!)
                } else {
                    showDisclaimer(allocationSchemes!![position].name, position, isSelected)
                }
            }

        val onSubFundChange: (position: Int, allocationScheme: AllocationScheme) -> Unit =
            { position: Int, allocationScheme: AllocationScheme ->
                if (selectedAllocationScheme != null) {
                    selectedAllocationScheme!!.equitySubFund = allocationScheme.equitySubFund
                    selectedAllocationScheme!!.debitSubFund = allocationScheme.debitSubFund
                    selectedAllocationScheme!!.moneyMarketSubFund =
                        allocationScheme.moneyMarketSubFund

                    toggleContinueBtn(validateInput())
                }
            }

        binding.allocationSchemesRv.isNestedScrollingEnabled = false
        binding.allocationSchemesRv.setHasFixedSize(false)
        allocationSchemesAdapter = AllocationSchemesAdapter(
            requireContext(),
            onSelectOrUnSelectAllocation,
            onSubFundChange
        )
        binding.allocationSchemesRv.apply {
            adapter = allocationSchemesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        filteredAllocations = ArrayList()

        when (result.rpqScore) {
            in -8..-2 -> {//-9 && result.rpqScore < -1) {
                allocationSchemes?.find { asd -> asd.code == "Low"}?.let { filteredAllocations!!.add(it) }
                allocationSchemes?.find { asd -> asd.code == "Lower"}?.let { filteredAllocations!!.add(it) }

                allocationSchemesAdapter.updateData(filteredAllocations!!)
            }
            in -2..3 -> {// > -3 && result.rpqScore < 4) {
                allocationSchemes?.find { asd -> asd.code == "Medium"}?.let { filteredAllocations!!.add(it) }

                allocationSchemesAdapter.updateData(filteredAllocations!!)
            }
            in 4..8 -> {
                allocationSchemes?.find { asd -> asd.code == "High"}?.let { filteredAllocations!!.add(it) }

                allocationSchemesAdapter.updateData(filteredAllocations!!)
            }
        }

    }

    private fun showDisclaimer(fundName : String, position : Int, isSelected: Boolean) {
        DialogUtils.showConfirmationDialog(
            requireActivity(),
            layoutInflater,
            getString(R.string.caution),
            "According to your RPQ score you have been suggested $fundName.\n\nIf you choose another fund you will choose it at your own risk.",
            R.drawable.ic_alert,
            onOkPressed = {
                filteredAllocations?.let { fa ->

                    for (i in fa.indices) {
                        if (i == position) {
                            selectedAllocationScheme = if (isSelected) {
                                fa[i]
                            } else {
                                null
                            }
                            fa[i].isSelected = isSelected
                        } else {
                            fa[i].isSelected = false
                        }

                        toggleContinueBtn(validateInput())
                    }
                    allocationSchemesAdapter.updateData(fa)
                }
            },
            onCancelPressed = {
                allocationSchemesAdapter.updateData(allocationSchemes!!)
                filteredAllocations = null
            }
        )
    }

    private fun showFunds(result: List<Fund>?) {
        vpsFundsList = result
        binding.vpsFundsRv.visibility = View.VISIBLE

        val onSelectOrUnSelectFund: (position: Int, isSelected: Boolean) -> Unit =
            { position: Int, isSelected: Boolean ->
                for (i in vpsFundsList!!.indices) {
                    if (i == position) {
                        selectedFund = if (isSelected) {
                            vpsFundsList!![i]
                        } else {
                            null
                        }
                        vpsFundsList!![i].isSelected = isSelected
                    } else {
                        vpsFundsList!![i].isSelected = false
                    }

                    toggleContinueBtn(validateInput())
                }
                fundsAdapter.updateData(vpsFundsList!!)
            }

        fundsAdapter = FundsAdapter(onSelectOrUnSelectFund)
        binding.vpsFundsRv.apply {
            adapter = fundsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        if (!selectedFundsResult!!.fundId.isNullOrEmpty()) {
            for (i in vpsFundsList!!.indices) {
                if (vpsFundsList!![i].id == selectedFundsResult!!.fundId) {
                    vpsFundsList!![i].isSelected = true
                    selectedFund = vpsFundsList!![i]
                    toggleContinueBtn(validateInput())
                    break
                }
            }
        }

        fundsAdapter.updateData(vpsFundsList!!)
    }

    fun getRiskLevel(rpqScore : Int) {
        if (!riskLevels.isNullOrEmpty()) {
            for (rsk in riskLevels!!) {
                if (rpqScore >= rsk.minimumScore && rpqScore <= rsk.maximumScore) {
                    userRiskLevel = rsk.riskLevel
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (selectedFundCategory == null) {
            isValid = false
        } else if (selectedAllocationScheme == null) {
            isValid = false
        }

        /*if (vpsFundsList.isNullOrEmpty()) {
            isValid = false
        } else if (selectedFundCategory == null) {
            isValid = false
        } else if (selectedFund == null) {
            isValid = false
        } else if (selectedAllocationScheme == null) {
            isValid = false
        }*/

        if (selectedAllocationScheme != null) {
            equityFund = if (selectedAllocationScheme!!.isEditMinEquitySubFund) {
                selectedAllocationScheme!!.equitySubFund
            } else {
                selectedAllocationScheme!!.minEquitySubFund
            }

            debitFund = if (selectedAllocationScheme!!.isEditMinDebtSubFund) {
                selectedAllocationScheme!!.debitSubFund
            } else {
                selectedAllocationScheme!!.minDebtSubFund
            }

            marketFund = if (selectedAllocationScheme!!.isEditMinMoneyMarketSubFund) {
                selectedAllocationScheme!!.moneyMarketSubFund
            } else {
                selectedAllocationScheme!!.minMoneyMarketSubFund
            }

            // Fund values will be fixed for Lifecycle allocations
            // And for other Funds the total should be equals to 100
            if (selectedAllocationScheme!!.code != LIFECYCLE_ALLOCATION
                && (equityFund + debitFund + marketFund) != 100
            ) {
                isValid = false
            }
        }

        return isValid
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 66
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "75%"
        binding.appBar.stepTv.text = "Step 3/4"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}