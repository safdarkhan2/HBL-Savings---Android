package com.hbl.amc.ui.productInformation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentMutualFundsSelectionBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.API_ID
import com.hbl.amc.ui.Disclaimers.DisclaimersActivity
import com.hbl.amc.ui.FUNDS_RESULT
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.hbl.amc.utils.RiskIndicator
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import androidx.constraintlayout.widget.ConstraintSet

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoActivity
import com.hbl.amc.ui.STEP_ID
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.TO_RPQ


class MutualFundsSelectionFragment : Fragment() {

    private var toRpq = false
    private var _binding: FragmentMutualFundsSelectionBinding? = null
    private val binding get() = _binding!!
    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val productViewModel by viewModel<ProductInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var selectedFundsResult: GetSelectedFundsResult? = null
    var mutualFundsList: List<Fund>? = null
    var otherFundsList: List<Fund>? = null
    var selectedFund: Fund? = null
    lateinit var fundsAdapter: FundsAdapter
    var minRiskRange : Int? = null
    var maxRiskRange : Int? = null
    var riskLevels : List<RiskLevelsResult>? = null
    var userRiskLevel = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMutualFundsSelectionBinding.inflate(inflater, container, false)
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

        if (selectedFundsResult == null) {
            productViewModel.getSelectedMutualFunds(
                HBLPreferenceManager.getToken(requireContext()),
                HBLPreferenceManager.getCustomerID(requireContext())
            )
        } else {
            productViewModel.getRiskLevels(HBLPreferenceManager.getToken(requireContext()))
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

        binding.txtChange.setOnClickListener {
//            findNavController().navigate(R.id.riskProfileQuestionnaireFragment)
            val intent = Intent(requireContext(), RegulatoryInfoActivity::class.java)
            toRpq = true
            intent.putExtra(TO_RPQ, toRpq)
            startActivity(intent)
        }

        binding.continueBtn.setOnClickListener {
            saveFund()
        }

        binding.appBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.productsBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        binding.mfTv.setOnClickListener {
            if (binding.mutualFundsRv.isVisible) {
                binding.mutualFundsRv.visibility = GONE
                binding.mfTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_plus),
                    null
                )
            } else {
                binding.mutualFundsRv.visibility = VISIBLE
                binding.mfTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_minus),
                    null
                )
            }
        }

        binding.otherFundsTv.setOnClickListener {
            if (binding.otherFundsLayout.isVisible) {
                binding.otherFundsLayout.visibility = GONE
                binding.otherFundsTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_plus),
                    null
                )
            } else {
                binding.otherFundsLayout.visibility = VISIBLE
                binding.otherFundsTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_minus),
                    null
                )
            }
        }
    }

    private fun saveFund() {
        val saveMutualFundDTO = MutualFundSaveResult(
            HBLPreferenceManager.getCustomerID(requireContext()),
            "cddcbffe-5a95-4310-b8d4-fdf047130cb4",
            selectedFund!!.id,
            false
        )
        productViewModel.saveMutualFund(
            HBLPreferenceManager.getToken(requireContext()),
            saveMutualFundDTO
        )
    }

    private fun initViewModel() {
        productViewModel.getSelectedMutualFundsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { smr ->
                        selectedFundsResult = smr
                        productViewModel.getRiskLevels(HBLPreferenceManager.getToken(requireContext()))
                    } ?: run {
                        it?.response?.message?.let { msg ->
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
                    it?.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.getMutualFundsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { mf ->
                        showDetails(mf)
                    } ?: run {
                        it?.response?.message?.let { msg ->
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
                    it?.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.saveMutualFundApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let {
//                            findNavController().navigate(R.id.documentChecklistFragment)
                        val intent = Intent(requireContext(), DisclaimersActivity::class.java)
                        startActivity(intent)
                    } ?: run {
                        it?.response?.message?.let { msg ->
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
                    it?.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })

        productViewModel.getRiskLevelsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        it.response?.result?.let { rsk ->
                            minRiskRange = rsk[0].minimumScore
                            maxRiskRange = rsk[rsk.lastIndex].maximumScore
                            riskLevels = rsk

                            productViewModel.getMutualFunds(
                                HBLPreferenceManager.getToken(requireContext()),
                                HBLPreferenceManager.getCustomerID(requireContext())
                            )
                        }
                    } else {
                        it?.response?.message?.let { msg ->
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
                    it?.error?.message?.let { it1 -> Log.d("risk levels error", it1) }
                }
            }
        })
    }

    private fun showDetails(result: MutualFundsGetResult) {


        mutualFundsList = result.mutualFundsList
        otherFundsList = result.otherFundsList
//        binding.rpqScoreProgress.progress = result.rpqScore
        binding.txtRpqScore.text = result.rpqScore.toString()
        binding.rpqScoreMsg.text = "Based on your choices your score is ${result.rpqScore}. \nYou may invest in any of the suggested funds below OR You may select other funds by disagreeing to the RPQ consent shown upon any suggested funds selection."
        binding.txtInvestmentLimit.text = "Your investment limit is ${result.investmentLimit}."

        if (!selectedFundsResult!!.isOtherFund && !selectedFundsResult!!.fundId.isNullOrEmpty()) {
            for (i in mutualFundsList!!.indices) {
                if (mutualFundsList!![i].id == selectedFundsResult!!.fundId) {
                    mutualFundsList!![i].isSelected = true
                    selectedFund = mutualFundsList!![i]
                    toggleContinueBtn(validateInput())
                    break
                }
            }
        }


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
        //                binding.rpqScoreProgress.setIndicatorColor(requireContext().resources.getColor(R.color.hbl_main_green))
                }
                riskLevels!![1].riskLevel -> {
                    binding.riskIndicator.filledColor = R.color.yellow_theme
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.yellow_dot)
        //                binding.rpqScoreProgress.setIndicatorColor(requireContext().resources.getColor(R.color.yellow_theme))
                }
                else -> {
                    binding.riskIndicator.filledColor = R.color.red
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.red_dot)
        //                binding.rpqScoreProgress.setIndicatorColor(requireContext().resources.getColor(R.color.red))
                }
            }

            binding.riskIndicator.invalidate() //to redraw the risk indicator with updated progress

//            val layoutParams = ConstraintLayout.LayoutParams(
//                ConstraintLayout.LayoutParams.WRAP_CONTENT,
//                ConstraintLayout.LayoutParams.WRAP_CONTENT
//            )

            //start here
//            layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
//            layoutParams.endToEnd = ConstraintSet.PARENT_ID
//            layoutParams.startToStart = ConstraintSet.PARENT_ID
//            layoutParams.topToTop = ConstraintSet.PARENT_ID
//            layoutParams.setMargins(0, 150, 0, 0)
//            riskIndicator.layoutParams = layoutParams
//            binding.riskIndicatorLayout.addView(riskIndicator)
        }

        val onSelectOrUnSelectFund: (position: Int, isSelected: Boolean) -> Unit =
            { position: Int, isSelected: Boolean ->
                for (i in mutualFundsList!!.indices) {
                    if (i == position) {
                        if (isSelected) {
                            selectedFund = mutualFundsList!![i]
                            showDisclaimer(selectedFund!!.name)
                        } else {
                            selectedFund = null
                        }
                        mutualFundsList!![i].isSelected = isSelected
                    } else {
                        mutualFundsList!![i].isSelected = false
                    }

                    toggleContinueBtn(validateInput())
                }
                fundsAdapter.updateData(mutualFundsList!!)
            }

        fundsAdapter = FundsAdapter(onSelectOrUnSelectFund)
        binding.mutualFundsRv.apply {
            adapter = fundsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        fundsAdapter.updateData(mutualFundsList!!)

        if (!otherFundsList.isNullOrEmpty()) {
            setUpOtherFunds(otherFundsList)
        }
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

    private fun setUpOtherFunds(otherFundsList: List<Fund>?) {
        val otherFunds: ArrayList<String> = ArrayList()
        otherFunds.add("Select other Fund")
        otherFundsList?.forEach { fund ->
            otherFunds.add(fund.name)
        }

        binding.otherFundsSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, otherFunds)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    otherFundsList?.let {
                        if (p2 > 0) {
                            selectedFund = it[p2 - 1]
                            unSelectAllMutualFunds()
                        } else {
                            selectedFund = null
                        }
                    }

                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }

        if (selectedFundsResult!!.isOtherFund && !selectedFundsResult!!.fundId.isNullOrEmpty()) {
            for (i in otherFundsList!!.indices) {
                if (otherFundsList[i].id == selectedFundsResult!!.fundId) {
                    binding.otherFundsContainer.visibility = VISIBLE
                    binding.otherFundsSpinner.setSelection(i + 1)
                    selectedFund = otherFundsList[i]
                    toggleContinueBtn(validateInput())
                    break
                }
            }
        }
    }

    private fun unSelectAllMutualFunds() {
        for (i in mutualFundsList!!.indices) {
            mutualFundsList!![i].isSelected = false
        }
        fundsAdapter.updateData(mutualFundsList!!)
    }

    private fun validateInput(): Boolean {
        var isValid = true
        if (mutualFundsList.isNullOrEmpty()) {
            isValid = false
        } else if (selectedFund == null) {
            isValid = false
        }

        return isValid
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun showDisclaimer(fundName : String) {
        DialogUtils.showConfirmationDialog(
            requireActivity(),
            layoutInflater,
            getString(R.string.caution),
            "According to your RPQ score you have been suggested $fundName.\n\nIf you choose another fund you will choose it at your own risk.",
            R.drawable.ic_alert,
            onOkPressed = {
                binding.otherFundsContainer.visibility = GONE
                if (!otherFundsList.isNullOrEmpty()) {
                    binding.otherFundsSpinner.setSelection(0)
                }
            },
            onCancelPressed = {
                selectedFund = null
                binding.otherFundsContainer.visibility = VISIBLE
                unSelectAllMutualFunds()
                toggleContinueBtn(validateInput())

                binding.mutualFundsRv.visibility = GONE
                binding.otherFundsLayout.visibility = VISIBLE
                binding.mfTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_plus),
                    null
                )
                binding.otherFundsTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_minus),
                    null
                )
            }
        )
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 100
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "75%"
        binding.appBar.stepTv.text = "Step 3/4"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}