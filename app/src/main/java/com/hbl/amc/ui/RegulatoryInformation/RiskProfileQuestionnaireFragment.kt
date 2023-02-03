package com.hbl.amc.ui.RegulatoryInformation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentRiskProfileQuestionnaireBinding
import com.hbl.amc.domain.RequestDTO.SaveRpqInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.API_ID
import com.hbl.amc.ui.EDIT_MODE
import com.hbl.amc.ui.STEP_ID
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.hbl.amc.utils.setupEditText
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class RiskProfileQuestionnaireFragment : Fragment(), KoinComponent {
    private var _binding: FragmentRiskProfileQuestionnaireBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val regulatoryViewModel by viewModel<RegulatoryInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    lateinit var activ : RegulatoryInfoActivity

    var keepInvestmentPlanList: List<KeepInvestmentPlan>? = null
    var enoughSavingsToSupportList: List<EnoughSavingsToSupport>? = null
    var financialGoalsToBeAttainedList: List<FinancialGoalsToBeAttained>? = null
    var relateMyselfBestStatementList: List<RelateMyselfBestStatement>? = null
    var furtherInvestmentIntendList: List<FurtherInvestmentIntend>? = null
    var substantialInitialLossList: List<SubstantialInitialLoss>? = null
    var usuallyInvestKeepMoneyList: List<UsuallyInvestKeepMoney>? = null


    var selectedKeepInvestmentPlan: Int = 0
    var selectedEnoughSavingsToSupport: Int = 0
    var selectedFinancialGoalsToBeAttained: Int = 0
    var selectedRelateMyselfBestStatement: Int = 0
    var selectedFurtherInvestmentIntend: Int = 0
    var selectedSubstantialInitialLoss: Int = 0
    var selectedUsuallyInvestKeepMoney: Int = 0

    var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activ = requireActivity() as RegulatoryInfoActivity

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                // block backward navigation from this section
                activ.finish()
            }
        }

        if(activ.toRPQ || activ.intent.hasExtra(STEP_ID))
        {
            requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initViewModel()

        arguments?.let {
            if (it.containsKey(EDIT_MODE)) {
                editMode = it[EDIT_MODE] as Boolean
            }
        }

        if (_binding == null) {
            _binding = FragmentRiskProfileQuestionnaireBinding.inflate(inflater, container, false)
            initViews()
            regulatoryViewModel.getRpqInfoLookups(HBLPreferenceManager.getToken(requireContext()))
        }

        return binding.root
    }

    private fun initViewModel() {

        val keepInvestmentPlan: ArrayList<String> = ArrayList()
        val enoughSavingsToSupport: ArrayList<String> = ArrayList()
        val financialGoalsToBeAttained: ArrayList<String> = ArrayList()
        val relateMyselfBestStatement: ArrayList<String> = ArrayList()
        val furtherInvestmentIntend: ArrayList<String> = ArrayList()
        val substantialInitialLoss: ArrayList<String> = ArrayList()
        val usuallyInvestKeepMoney: ArrayList<String> = ArrayList()

        regulatoryViewModel.rpqInfoLookUpsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
//                    if (it.response?.status == "success") {
                    if (it.response?.result != null) {
                        keepInvestmentPlanList = it.response?.result?.keepInvestmentPlanList
                        enoughSavingsToSupportList = it.response?.result?.enoughSavingsToSupportList
                        financialGoalsToBeAttainedList =
                            it.response?.result?.financialGoalsToBeAttainedList
                        relateMyselfBestStatementList =
                            it.response?.result?.relateMyselfBestStatementList
                        furtherInvestmentIntendList =
                            it.response?.result?.furtherInvestmentIntendList
                        substantialInitialLossList = it.response?.result?.substantialInitialLossList
                        usuallyInvestKeepMoneyList = it.response?.result?.usuallyInvestKeepMoneyList

                        keepInvestmentPlan.add(getString(R.string.select))
                        enoughSavingsToSupport.add(getString(R.string.select))
                        financialGoalsToBeAttained.add(getString(R.string.select))
                        relateMyselfBestStatement.add(getString(R.string.select))
                        furtherInvestmentIntend.add(getString(R.string.select))
                        substantialInitialLoss.add(getString(R.string.select))
                        usuallyInvestKeepMoney.add(getString(R.string.select))


                        keepInvestmentPlanList?.forEach { value ->
                            keepInvestmentPlan.add(value.name)
                        }
                        enoughSavingsToSupportList?.forEach { value ->
                            enoughSavingsToSupport.add(value.name)
                        }
                        financialGoalsToBeAttainedList?.forEach { value ->
                            financialGoalsToBeAttained.add(value.name)
                        }
                        relateMyselfBestStatementList?.forEach { value ->
                            relateMyselfBestStatement.add(value.name)
                        }
                        furtherInvestmentIntendList?.forEach { value ->
                            furtherInvestmentIntend.add(value.name)
                        }
                        substantialInitialLossList?.forEach { value ->
                            substantialInitialLoss.add(value.name)
                        }
                        usuallyInvestKeepMoneyList?.forEach { value ->
                            usuallyInvestKeepMoney.add(value.name)
                        }

                        setUpKeepInvestmentPlanSpinner(keepInvPlan = keepInvestmentPlan)
                        setUpEnoughSavingsToSupportSpinner(enoughSavings = enoughSavingsToSupport)
                        setUpFinancialGoalsToBeAttainedSpinner(financialGoals = financialGoalsToBeAttained)
                        setUpRelateMyselfBestStatementSpinner(relateMe = relateMyselfBestStatement)
                        setUpFurtherInvestmentIntendSpinner(furtherIntend = furtherInvestmentIntend)
                        setUpSubstantialInitialLossSpinner(subsInitialLoss = substantialInitialLoss)
                        setUpUsuallyInvestKeepMoneySpinner(usuallyInvestMoney = usuallyInvestKeepMoney)

                        regulatoryViewModel.resumeRPQ(
                            HBLPreferenceManager.getToken(requireContext()),
                            HBLPreferenceManager.getCustomerID(requireContext())
                        )
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
                    it.error?.message?.let { it1 -> Log.d("rpq lookups info error", it1) }
                }
            }
        })

        regulatoryViewModel.saveRpqInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.let { res ->
                            Log.d("test_ans", it.response.toString())
                        //save user in shared prefs
                        if (res.status == "success") {
                            when {
                                editMode -> {
                                    findNavController().navigateUp()
                                }
                                activ.toRPQ -> {
                                    activ.finish()
                                }
                                else -> {
                                    findNavController().navigate(R.id.action_riskProfileQuestionnaireFragment_to_fatcaChecklistFragment)
                                }
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
                                        val intent =
                                            Intent(requireContext(), MainActivity::class.java)
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
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 -> Log.d("RPQ save info error", it1) }
                }
            }
        })

        regulatoryViewModel.resumeRPQApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        it.response.result.let { rpq ->
                            binding.ageInYear.text = rpq.age.toString()

                            val kip =
                                keepInvestmentPlanList?.find { fnhi -> fnhi.id == rpq.keepInvestmentPlanId }
                            kip?.let { fhi ->
                                binding.riskTolerenceLevelSpinner.setSelection(
                                    keepInvestmentPlan.indexOf(fhi.name)
                                )
                            }
                            val ess =
                                enoughSavingsToSupportList?.find { fnhi -> fnhi.id == rpq.enoughSavingsToSupportId }
                            ess?.let { fhi ->
                                binding.currentMonthlySavingsSpinner.setSelection(
                                    enoughSavingsToSupport.indexOf(fhi.name)
                                )
                            }
                            val fga =
                                financialGoalsToBeAttainedList?.find { fnhi -> fnhi.id == rpq.financialGoalsToBeAttainedId }
                            fga?.let { fhi ->
                                binding.occupationSpinner.setSelection(
                                    financialGoalsToBeAttained.indexOf(fhi.name)
                                )
                            }
                            val rmbs =
                                relateMyselfBestStatementList?.find { fnhi -> fnhi.id == rpq.relateMyselfBestStatementId }
                            rmbs?.let { fhi ->
                                binding.investmentObjectiveSpinner.setSelection(
                                    relateMyselfBestStatement.indexOf(fhi.name)
                                )
                            }

                            val fhii =
                                furtherInvestmentIntendList?.find { fnhi -> fnhi.id == rpq.furtherInvestmentIntendId }
                            fhii?.let { fhi ->
                                binding.knowledgeOfInvestmentSpinner.setSelection(
                                    furtherInvestmentIntend.indexOf(fhi.name)
                                )
                            }
                            val sil =
                                substantialInitialLossList?.find { fnhi -> fnhi.id == rpq.substantialInitialLossId }
                            sil?.let { fhi ->
                                binding.investmentHorizonSpinner.setSelection(
                                    substantialInitialLoss.indexOf(fhi.name)
                                )
                            }
                            val uik =
                                usuallyInvestKeepMoneyList?.find { fnhi -> fnhi.id == rpq.usuallyInvestKeepMoneyId }
                            uik?.let { fhi ->
                                binding.furtherInvestmentSpinner.setSelection(
                                    usuallyInvestKeepMoney.indexOf(fhi.name)
                                )
                            }
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
                    it.error?.message?.let { it1 -> Log.d("RPQ save info error", it1) }
                }
            }
        })

        regulatoryViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        })
    }

    private fun setUpKeepInvestmentPlanSpinner(keepInvPlan: List<String>) {
        binding.riskTolerenceLevelSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, keepInvPlan)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    keepInvestmentPlanList?.let {
                        selectedKeepInvestmentPlan = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpEnoughSavingsToSupportSpinner(enoughSavings: List<String>) {
        binding.currentMonthlySavingsSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, enoughSavings)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    enoughSavingsToSupportList?.let {
                        selectedEnoughSavingsToSupport = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpFinancialGoalsToBeAttainedSpinner(financialGoals: List<String>) {
        binding.occupationSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, financialGoals)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    financialGoalsToBeAttainedList?.let {
                        selectedFinancialGoalsToBeAttained = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpRelateMyselfBestStatementSpinner(relateMe: List<String>) {
        binding.investmentObjectiveSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, relateMe)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    relateMyselfBestStatementList?.let {
                        selectedRelateMyselfBestStatement = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpFurtherInvestmentIntendSpinner(furtherIntend: List<String>) {
        binding.knowledgeOfInvestmentSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, furtherIntend)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    furtherInvestmentIntendList?.let {
                        selectedFurtherInvestmentIntend = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpSubstantialInitialLossSpinner(subsInitialLoss: List<String>) {
        binding.investmentHorizonSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, subsInitialLoss)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    substantialInitialLossList?.let {
                        selectedSubstantialInitialLoss = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpUsuallyInvestKeepMoneySpinner(usuallyInvestMoney: List<String>) {
        binding.furtherInvestmentSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, usuallyInvestMoney)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    usuallyInvestKeepMoneyList?.let {
                        selectedUsuallyInvestKeepMoney = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }


    private fun initViews() {
        binding.appBar.title.text = getString(R.string.regulatory_information)

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        if (editMode) {
            binding.appBar.stepTv.visibility = View.GONE
            binding.appBar.progressbarContainer.visibility = View.GONE
            binding.statusLayout.visibility = View.INVISIBLE
        }

        setupProgress()

//        binding.ageInYearEdittext.setupEditText(requireContext(), binding.ageInYearInput)

        binding.kycButton.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_riskProfileQuestionnaireFragment_to_KYCFragment, bundleOf(
                        Pair(EDIT_MODE, true)
                    )
                )
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }

        binding.continueBtn.setOnClickListener {

            val saveRpqInfoDTO = SaveRpqInfoDTO(
                binding.ageInYear.text.toString().toInt(),
                selectedKeepInvestmentPlan,
                selectedEnoughSavingsToSupport,
                selectedFinancialGoalsToBeAttained,
                selectedRelateMyselfBestStatement,
                selectedFurtherInvestmentIntend,
                selectedSubstantialInitialLoss,
                selectedUsuallyInvestKeepMoney,
                "98d82238-b799-4623-876b-4839a9b7b02b",
                HBLPreferenceManager.getCustomerID(requireContext())
            )
            regulatoryViewModel.saveRpqInfo(HBLPreferenceManager.getToken(requireContext()), saveRpqInfoDTO)
        }
        binding.appBar.backBtn.setOnClickListener {
            when {
                activ.toRPQ or activ.intent.hasExtra(STEP_ID)-> {
                    activ.finish()
                }
                else -> {
                    findNavController().navigateUp()
                }
            }
        }

        /*binding.ageInYearEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.ageInYearEdittext.length() < 1) {
                    binding.ageInYearInput.error = "Minimum length should be 1"
                } else {
                    binding.ageInYearInput.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })*/
    }

    private fun validateInput(): Boolean {
        /*return !(binding.ageInYearEdittext.length() < 1 ||*/
        return !(selectedKeepInvestmentPlan < 1 ||
                selectedSubstantialInitialLoss < 1 ||
                selectedEnoughSavingsToSupport < 1 ||
                selectedFinancialGoalsToBeAttained < 1 ||
                selectedRelateMyselfBestStatement < 1 ||
                selectedFurtherInvestmentIntend < 1 ||
                selectedSubstantialInitialLoss < 1 ||
                selectedUsuallyInvestKeepMoney < 1)
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 50
        binding.appBar.progressbar3.progress = 0
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "40%"
        binding.appBar.stepTv.text = "Step 2/4"
    }
}