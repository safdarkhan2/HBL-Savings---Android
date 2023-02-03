package com.hbl.amc.ui.CustomerInformation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentProfessionalInformationBinding
import com.hbl.amc.domain.RequestDTO.SaveProfessionalInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Disclaimers.DisclaimersViewModel
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.hbl.amc.utils.setupEditText
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf


class ProfessionalInformationFragment : AppCompatActivity(), KoinComponent {

    private lateinit var binding: FragmentProfessionalInformationBinding

//    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO
    var totalExperience: Int? = null
    var isStudent = false
    var accountTypes: AccountTypes? = null

    private val customerViewModel by viewModel<CustomerInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val disclaimersViewModel by viewModel<DisclaimersViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var occupationList: List<Occupation>? = null
    var sourceOfIncomeList: List<SourceOfIncome>? = null
    var avgAnnualIncomeList: List<AvgAnnualIncome>? = null
    var preciousMetalList: List<PreciousMetal>? = null
    var dividendPayoutList: List<DividendPayout>? = null
    var publicFigureList: List<PublicFigure>? = null
    var expectedInvestmentPerTransactionList: List<ExpectedInvestmentPerTransaction>? = null
    var yearlyExpectedInvestmentList: List<YearlyExpectedInvestment>? = null
    var financialAccountRefusalList: List<FinancialAccountRefusal>? = null

    var selectedOccupation: String? = null
    var selectedSourceOfIncome: String? = null
    var selectedAvgIncome: String? = null
    var selectedPreciousMetal = 0
    var selectedDividendPayout = 0
    var selectedPublicFigureEntity = 0
    var selectedExpectedInvestmentPerTransaction = 0
    var selectedYearlyExpectedInvestment = 0
    var selectedFinancialAccountRefusal = 0

    var selectedReligion: String? = null
    private val sharedPrefFile = "hblsharedpreference"

    val occupations: ArrayList<String> = ArrayList()
    val sourcesOfIncome: ArrayList<String> = ArrayList()
    val avgAnnualIncomes: ArrayList<String> = ArrayList()
    val preciousMetals: ArrayList<String> = ArrayList()
    val dividendPayouts: ArrayList<String> = ArrayList()
    val publicFigures: ArrayList<String> = ArrayList()
    val expectedInvestmentPerTransaction: ArrayList<String> = ArrayList()
    val yearlyExpectedInvestment: ArrayList<String> = ArrayList()
    val financialAccountRefusal: ArrayList<String> = ArrayList()

    var editMode = false
    var docType = ""
    var allDocsTypes: List<DocumentsTypesResult>? = null
    var proofOfIncome: String? = null
    var zakatAffidavit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfessionalInformationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        DialogUtils.createPogressDialog(context = this)

        accountTypes = HBLPreferenceManager.getAccountType(this)

        intent?.let {
            if (it.hasExtra(EDIT_MODE)) {
                editMode = it.getBooleanExtra(EDIT_MODE, false)
            }
        }

        HBLPreferenceManager.getDocumentsTypesResponse(this)?.let { docTypeRes ->
            docTypeRes.result.also { allDocsTypes = it }
        }

        initViewModel()
        initViews()

        customerViewModel.getProfessionalInfoLookups(
            HBLPreferenceManager.getToken(
                this
            )
        )
    }


    private fun initViewModel() {

        customerViewModel.professionalInfoLookUpsApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        occupationList = it.response?.result?.occupationList
                        sourceOfIncomeList = it.response?.result?.sourceOfIncomeList
                        avgAnnualIncomeList = it.response?.result?.avgAnnualIncomeList
                        preciousMetalList = it.response?.result?.preciousMetalList
                        dividendPayoutList = it.response?.result?.dividendPayoutList
                        publicFigureList = it.response?.result?.publicFigureList
                        expectedInvestmentPerTransactionList =
                            it.response?.result?.expectedInvestmentPerTransactionList
                        yearlyExpectedInvestmentList =
                            it.response?.result?.yearlyExpectedInvestmentList
                        financialAccountRefusalList =
                            it.response?.result?.financialAccountRefusalList


                        occupations.add(getString(R.string.select))
                        sourcesOfIncome.add(getString(R.string.select))
                        avgAnnualIncomes.add(getString(R.string.select))
                        preciousMetals.add(getString(R.string.select))
                        dividendPayouts.add(getString(R.string.select))
                        publicFigures.add(getString(R.string.select))
                        expectedInvestmentPerTransaction.add(getString(R.string.select))
                        yearlyExpectedInvestment.add(getString(R.string.select))
                        financialAccountRefusal.add(getString(R.string.select))

                        occupationList?.forEach { occupation ->
                            occupations.add(occupation.name)
                        }

                        sourceOfIncomeList?.forEach { soi ->
                            sourcesOfIncome.add(soi.name)
                        }

                        avgAnnualIncomeList?.forEach { aai ->
                            avgAnnualIncomes.add(aai.name)
                        }

                        preciousMetalList?.forEach { pm ->
                            preciousMetals.add(pm.name)
                        }

                        dividendPayoutList?.forEach { div_pay ->
                            dividendPayouts.add(div_pay.name)
                        }

                        publicFigureList?.forEach { pf ->
                            publicFigures.add(pf.name)
                        }

                        expectedInvestmentPerTransactionList?.forEach { eipt ->
                            expectedInvestmentPerTransaction.add(eipt.name)
                        }

                        yearlyExpectedInvestmentList?.forEach { yei ->
                            yearlyExpectedInvestment.add(yei.name)
                        }

                        financialAccountRefusalList?.forEach { far ->
                            financialAccountRefusal.add(far.name)
                        }

                        setUpOccupationSpinner(occupations = occupations)
                        setUpAvgAnnualIncomeSpinner(avgAnnualIncomes = avgAnnualIncomes)
                        setUpDividendPayoutSpinner(dividendPayouts = dividendPayouts)
                        setUpPreciousMetalSpinner(preciousMetals = preciousMetals)
                        setUpPublicFigureSpinner(publicFigures = publicFigures)
                        setUpSourceOfIncomeSpinner(sourcesOfIncome = sourcesOfIncome)
                        setUpExpectedInvestmentPerTransactionSpinner(expectedInvestment = expectedInvestmentPerTransaction)
                        setUpYearlyExpectedInvestmentSpinner(yearlyExpectedInvestment = yearlyExpectedInvestment)
                        setUpFinancialAccountRefusalSpinner(financialAccountRefusal = financialAccountRefusal)

                        //uncomment this api when response is correct
                        customerViewModel.resumeProfessionalInfo(
                            token = HBLPreferenceManager.getToken(
                                this
                            ), customerId = HBLPreferenceManager.getCustomerID(this)
                        )
                    } else {
                        it?.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    this,
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    this,
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


                    it.error?.message?.let { it1 ->
                        Log.d("professional info error", it1)
                        DialogUtils.showAlertDialog(
                            this,
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {
                            customerViewModel.getProfessionalInfoLookups(
                                HBLPreferenceManager.getToken(
                                    this
                                )
                            )
                        }
                    }
                }
            }
        })


        customerViewModel.saveProfessionalInfoApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        val user = it.response?.let { res -> res.result }
                        Log.d("test_ans", it.response.toString())
                        if (editMode) {
                            onBackPressed()
                        } else {
                            val intent = Intent(this, BankAccountInformationFragment::class.java)
                            startActivity(intent)
                        }
//                    findNavController().navigate(R.id.action_professionalInformationFragment_to_bankAccountInformationFragment)
                    } else {
                        it?.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    this,
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    this,
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
                    it.error?.message?.let { it1 -> Log.d("Professional info error", it1) }
                }
                LiveDataWrapper.Status.LOADING -> {

                }
            }
        })

        customerViewModel.resumeProfessionalInfoApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.let { res ->
                        res.result?.let { result ->
                            val occupation =
                                occupationList?.find { oc -> oc.id == result.customerOccupationId }
                            occupation?.let { oc ->
                                binding.occupationSpinner.setSelection(occupations.indexOf(oc.name))
                            }

                            val soi =
                                sourceOfIncomeList?.find { sc -> sc.id == result.sourceOfIncomeId }
                            soi?.let { sc ->
                                binding.sourceOfIncomeSpinner.setSelection(
                                    sourcesOfIncome.indexOf(
                                        sc.name
                                    )
                                )
                            }

                            val avgi =
                                avgAnnualIncomeList?.find { avi -> avi.id == result.avgAnnaulIncomeId }
                            avgi?.let { avi ->
                                binding.avgAnnualIncomeSpinner.setSelection(
                                    avgAnnualIncomes.indexOf(
                                        avi.name
                                    )
                                )
                            }


                            val dvp =
                                dividendPayoutList?.find { dv -> dv.id == result.dividendPayoutId }
                            dvp?.let { dv ->
                                binding.dividendPayoutSpinner.setSelection(
                                    dividendPayouts.indexOf(
                                        dv.name
                                    )
                                )
                            }

                            val ei =
                                expectedInvestmentPerTransactionList?.find { dv -> dv.id == result.expectedInvestmentId }
                            ei?.let { dv ->
                                binding.expectedInvestmentPerTransactionSpinner.setSelection(
                                    expectedInvestmentPerTransaction.indexOf(
                                        dv.name
                                    )
                                )
                            }

                            val yei =
                                yearlyExpectedInvestmentList?.find { dv -> dv.id == result.yearlyExpectedInvestmentId }
                            yei?.let { dv ->
                                binding.expectedInvestmentTransactionYearSpinner.setSelection(
                                    yearlyExpectedInvestment.indexOf(
                                        dv.name
                                    )
                                )
                            }

                            val far =
                                financialAccountRefusalList?.find { dv -> dv.id == result.financialAccountRefusalId }
                            far?.let { dv ->
                                binding.declarationOfAccountSpinner.setSelection(
                                    financialAccountRefusal.indexOf(
                                        dv.name
                                    )
                                )
                            }


                            result.customerDesignation?.let { cd ->
                                binding.designationInputEdittext.setText(cd)
                            }

                            result.businessEmployerName?.let { be ->
                                binding.employerInputEdittext.setText(be)
                            }

                            result.totalWorkingExp?.let { twe ->
                                binding.experienceInYearsInputEdittext.setText(twe.toString())
                            }

                            result.otherOccupation?.let { oc ->
                                binding.otherOccupationEdittext.setText(oc)
                            }

                            result.othersourceOfIncome?.let { osi ->
                                binding.otherSourceOfIncomeEdittext.setText(osi)
                            }

                            if (result.customerPublicFigure) {

                                binding.publicFigureSwitch.isChecked = result.customerPublicFigure

//                                val pbi =
//                                    publicFigureList?.find { pb -> pb.id == result.publicFigureId }
//                                pbi?.let { pb ->
//                                    binding.publicFigureEntitySpinner.setSelection(
//                                        publicFigures.indexOf(
//                                            pb.name
//                                        )
//                                    )
//                                }
                            }

                            if (result.highValueProfile) {

                                binding.dealHighValueItemsSwitch.isChecked = result.highValueProfile

                                val hmi =
                                    preciousMetalList?.find { hm -> hm.id == result.preciousMetalId }
                                hmi?.let { hm ->
                                    binding.dealHighValueItemsSpinner.setSelection(
                                        preciousMetals?.indexOf(
                                            hm.name
                                        )
                                    )
                                }
                            }

                            //zakat declarion document,
                            //source of income document, proof of income
                            binding.zakatDeductionSwitch.isChecked = result.zakatDeclaration
                        }
                    } ?: run {
                        it?.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                DialogUtils.showAlertDialog(
                                    this,
                                    layoutInflater,
                                    getString(R.string.oops),
                                    msg,
                                    R.drawable.ic_alert,
                                    getString(R.string.ok)
                                ) {
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            } else {
                                DialogUtils.showAlertDialog(
                                    this,
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
                    it.error?.message?.let { it1 ->
                        Log.d(
                            "Professional info resumption error",
                            it1
                        )
                    }
                }
                LiveDataWrapper.Status.LOADING -> {

                }
            }
        })

        customerViewModel.loadingLiveData.observe(this, Observer {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        disclaimersViewModel.uploadDocumentApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.result?.let { res ->
//                        if (res.isUploaded) {
                        if (res.documentName == ZAKAT_FUND) {
                            zakatAffidavit = res.id

                            DialogUtils.showAlertDialog(
                                this,
                                layoutInflater,
                                getString(R.string.success),
                                "Zakat Affidavit uploaded successfully!",
                                R.drawable.ic_check_yellow,
                                getString(R.string.ok)
                            ) {}
                        } else if (res.documentName == SOURCE_OF_INCOME) {
                            proofOfIncome = res.id
                            binding.uploadProofOfIncomeBtn.setImageResource(R.drawable.ic_edit)
                            DialogUtils.showAlertDialog(
                                this,
                                layoutInflater,
                                getString(R.string.success),
                                "Proof of Income uploaded successfully!",
                                R.drawable.ic_check_yellow,
                                getString(R.string.ok)
                            ) {}
                        }
                        /*} else {
                            showAlertDialog(
                                this,
                                layoutInflater,
                                getString(R.string.oops),
                                "Document upload error!",
                                R.drawable.ic_alert,
                                getString(R.string.ok)
                            ) {}
                        }*/
                    } ?: run {
                        it?.response?.message?.let { msg ->
                            DialogUtils.showAlertDialog(
                                this,
                                layoutInflater,
                                getString(R.string.oops),
                                msg,
                                R.drawable.ic_alert,
                                getString(R.string.ok)
                            ) {}
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("upload $docType error", it1) }
                }
            }
        })

        disclaimersViewModel.loadingLiveData.observe(this, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.downloadDocumentApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        if (it.response?.result?.extension.equals("PDF", true)) {
                            val uri = AppUtils.getPDFURI(this, it.response?.result.documentBase64)
                            AppUtils.viewPDF(this, layoutInflater, uri!!)
                        } else if (it.response?.result?.extension.equals("docx", true)) {
                            val uri =
                                AppUtils.getWordDocURI(this, it.response?.result.documentBase64)
                            AppUtils.viewWordDoc(this, layoutInflater, uri!!)
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })
    }

    private fun setUpOccupationSpinner(occupations: List<String>) {
        binding.occupationSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                occupations
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    occupationList?.let {
                        selectedOccupation = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            null
                        }

                        if (p2 > 0 && (it[p2 - 1].name.contains(
                                "Student",
                                true
                            ) || it[p2 - 1].name.contains("wife", true))
                        ) {
                            binding.designationInputLayout.visibility = GONE
                            binding.experienceInYearsInputLayout.visibility = GONE
                            binding.employerInputLayout.visibility = GONE
                            isStudent = true
                        } else {
                            if (accountTypes?.code == "01-BB") {
                                binding.designationInputLayout.visibility = GONE
                                binding.experienceInYearsInputLayout.visibility = GONE
                                binding.employerInputLayout.visibility = GONE
                                isStudent = true
                            } else {
                                binding.designationInputLayout.visibility = VISIBLE
                                binding.experienceInYearsInputLayout.visibility = VISIBLE
                                binding.employerInputLayout.visibility = VISIBLE
                                isStudent = false
                            }
                        }

                        if (p2 > 0 && it[p2 - 1].name.contains("Others", true)) {
                            binding.otherOccupationLayout.visibility = VISIBLE
                        } else {
                            binding.otherOccupationLayout.visibility = GONE
                        }
//                        val error = binding.occupationSpinner.selectedView as TextView
//
//                        if(selectedOccupation == null)
//                        {
//
//                            error.error = "Please Select!"
//                        }
//                        else
//                        {
//                            error.error = null
//                        }

                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpSourceOfIncomeSpinner(sourcesOfIncome: List<String>) {
        binding.sourceOfIncomeSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                sourcesOfIncome
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    sourceOfIncomeList?.let {
                        selectedSourceOfIncome = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            null
                        }
                        if (p2 > 0 && it[p2 - 1].name.contains("Others", true)) {
                            binding.otherSourceOfIncomeLayout.visibility = VISIBLE
                        } else {
                            binding.otherSourceOfIncomeLayout.visibility = GONE
                        }

                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpAvgAnnualIncomeSpinner(avgAnnualIncomes: List<String>) {
        binding.avgAnnualIncomeSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                avgAnnualIncomes
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    avgAnnualIncomeList?.let {
                        selectedAvgIncome = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            null
                        }
                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpPreciousMetalSpinner(preciousMetals: List<String>) {
        binding.dealHighValueItemsSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                preciousMetals
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    preciousMetalList?.let {
                        selectedPreciousMetal = if (p2 > 0) {
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

    private fun setUpDividendPayoutSpinner(dividendPayouts: List<String>) {
        binding.dividendPayoutSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                dividendPayouts
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    dividendPayoutList?.let {
                        selectedDividendPayout = if (p2 > 0) {
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

    private fun setUpPublicFigureSpinner(publicFigures: List<String>) {
        binding.publicFigureEntitySpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                publicFigures
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    publicFigureList?.let {
                        selectedPublicFigureEntity = if (p2 > 0) {
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


    private fun setUpExpectedInvestmentPerTransactionSpinner(expectedInvestment: List<String>) {
        binding.expectedInvestmentPerTransactionSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                expectedInvestment
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    expectedInvestmentPerTransactionList?.let {
                        selectedExpectedInvestmentPerTransaction = if (p2 > 0) {
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

    private fun setUpYearlyExpectedInvestmentSpinner(yearlyExpectedInvestment: List<String>) {
        binding.expectedInvestmentTransactionYearSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                yearlyExpectedInvestment
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    yearlyExpectedInvestmentList?.let {
                        selectedYearlyExpectedInvestment = if (p2 > 0) {
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

    private fun setUpFinancialAccountRefusalSpinner(financialAccountRefusal: List<String>) {
        binding.declarationOfAccountSpinner.apply {
            adapter = ArrayAdapter(
                this@ProfessionalInformationFragment,
                R.layout.dropdown_item,
                financialAccountRefusal
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    financialAccountRefusalList?.let {
                        selectedFinancialAccountRefusal = if (p2 > 0) {
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

        if (accountTypes?.code == "01-BB") {
            binding.designationInputLayout.visibility = GONE
            binding.employerInputLayout.visibility = GONE
            binding.experienceInYearsInputLayout.visibility = GONE
            binding.avgAnnualIncomeLayout.visibility = GONE
            binding.expectedInvestmentPerTransactionLayout.visibility = GONE
            binding.expectedInvestmentTransactionYearLayout.visibility = GONE
            binding.declarationOfAccountLayout.visibility = GONE
            binding.uploadProofOfIncomeLayout.visibility = GONE
        }

        if (editMode) {
            binding.appBar.progressbarContainer.visibility = GONE
            binding.appBar.stepTv.visibility = GONE
            binding.statusLayout.visibility = View.INVISIBLE
        }

        binding.appBar.title.text = getString(R.string.customer_information)

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(this)?.let {
                DialogUtils.showInfoPopup(this, layoutInflater, it.code)
            }
        }

        setupProgress()

        binding.personalInfoTab.setOnClickListener {
            /*findNavController().navigate(
                R.id.action_professionalInformationFragment_to_customerInformationDetailFragment,
                bundleOf(Pair(EDIT_MODE, true))
            )*/
            val intent = Intent(this, CustomerInformationDetailFragment::class.java)
            intent.putExtra(EDIT_MODE, true)
            startActivity(intent)
        }

//        selectedReligion = customerInfoDetailFragment?.arguments?.getString(SELECTED_RELIGION)
        val sharedPreferences: SharedPreferences = getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        selectedReligion = sharedPreferences.getString(SELECTED_RELIGION, "default")

        Log.d("Religion", selectedReligion.toString())
        if (selectedReligion != "Islam") {
            binding.zakatDeduction.visibility = GONE
            binding.zakatDeductionBottomBar.visibility = GONE
            binding.zakatAffidavit.visibility = GONE
            binding.zakatAffidavitBottombar.visibility = GONE
        } else {
            binding.zakatDeduction.visibility = VISIBLE
            binding.zakatDeductionBottomBar.visibility = VISIBLE
            binding.zakatAffidavit.visibility = VISIBLE
            binding.zakatAffidavitBottombar.visibility = VISIBLE
        }

        binding.continueBtn.setOnClickListener {
            val designation = if (isStudent) {
                null
            } else {
                binding.designationInputEdittext.text.toString()
            }

            val employer = if (isStudent) {
                null
            } else {
                binding.employerInputEdittext.text.toString()
            }

            if (isStudent) {
                totalExperience = null
            }

            val saveProfessionalInfoDTO = SaveProfessionalInfoDTO(
                HBLPreferenceManager.getCustomerID(this),
                selectedOccupation,
                designation,
                employer,
                totalExperience,
                binding.publicFigureSwitch.isChecked,
                selectedPublicFigureEntity,
                selectedSourceOfIncome.toString(),
                selectedAvgIncome,
                binding.dealHighValueItemsSwitch.isChecked,
                selectedPreciousMetal,
                binding.zakatDeductionSwitch.isChecked,
                selectedDividendPayout,
                zakatAffidavit,
                proofOfIncome,
                "30612306-dbfa-4ae3-8e6b-a937e839b1d0",
                binding.otherSourceOfIncomeEdittext.text.toString(),
                binding.otherOccupationEdittext.text.toString(),
                selectedExpectedInvestmentPerTransaction,
                selectedYearlyExpectedInvestment,
                selectedFinancialAccountRefusal
            )

            customerViewModel.saveProfessionalInfo(
                HBLPreferenceManager.getToken(this),
                saveProfessionalInfoDTO
            )
        }

        binding.appBar.backBtn.setOnClickListener {
            onBackPressed()
//            findNavController().navigateUp()
        }

        binding.designationInputEdittext.setupEditText(
            this,
            binding.designationInputLayout
        )
        binding.experienceInYearsInputEdittext.setupEditText(
            this,
            binding.experienceInYearsInputLayout
        )
        binding.employerInputEdittext.setupEditText(this, binding.employerInputLayout)

        binding.designationInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.designationInputEdittext.length() < 2) {
                    binding.designationInputLayout.error = "Minimum length should be 2"
                } else {
                    binding.designationInputLayout.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.employerInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.employerInputEdittext.length() < 3) {
                    binding.employerInputLayout.error = "Minimum length should be 3"
                } else {
                    binding.employerInputLayout.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.experienceInYearsInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.experienceInYearsInputEdittext.length() < 1) {
                    binding.experienceInYearsInputLayout.error = "Minimum length should be 1"
                } else {
                    binding.experienceInYearsInputLayout.error = null
                }

                if (!s.isNullOrEmpty()) {
                    totalExperience = s.toString().toInt()
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.publicFigureSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.publicFigureEntityLayoutMain.visibility = VISIBLE
            } else {
                binding.publicFigureEntityLayoutMain.visibility = GONE
                binding.publicFigureEntitySpinner.setSelection(0)
                selectedPublicFigureEntity = 0
            }

            toggleContinueBtn(validateInput())
        })

        binding.dealHighValueItemsSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.dealHighValueItemsLayoutMain.visibility = VISIBLE
            } else {
                binding.dealHighValueItemsLayoutMain.visibility = GONE
                binding.dealHighValueItemsSpinner.setSelection(0)
                selectedPreciousMetal = 0
            }

            toggleContinueBtn(validateInput())
        })

        binding.zakatDeductionSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.zakatAffidavit.visibility = GONE
                binding.zakatDeductionBottomBar.visibility = GONE
            } else {
                binding.zakatAffidavit.visibility = VISIBLE
                binding.zakatDeductionBottomBar.visibility = VISIBLE
            }
        })

        binding.uploadProofOfIncomeBtn.setOnClickListener {
            docType = SOURCE_OF_INCOME
            ImagePicker.with(this)
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.uploadZakatAffidavit.setOnClickListener {
            docType = ZAKAT_FUND
            ImagePicker.with(this)
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.downloadBtn.setOnClickListener {
            val docType = allDocsTypes?.find { dc -> dc.name == "ZakatDeclarationForm" }
            docType?.let { it1 ->
                disclaimersViewModel.getSampleDocument(
                    it1.id
                )
            }
        }

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!

                val file = FileUtil.getTempFile(this, uri)
                Log.d("file path", file?.name + "-----------" + file?.path)

                allDocsTypes?.let { ad ->
                    val doc = ad.find { dc -> dc.name == docType }
                    doc?.let { dc ->
                        file?.let { actualFile ->
                            val actualDoc = MultipartBody.Part.createFormData(
                                "file", actualFile.name,
                                actualFile.asRequestBody("image/*".toMediaTypeOrNull())
                            )
                            val docID = MultipartBody.Part.createFormData(
                                "documentTypeId",
                                dc.id.toString()
                            )

                            val customerID = MultipartBody.Part.createFormData(
                                "customerId",
                                HBLPreferenceManager.getCustomerID(this)
                            )
                            disclaimersViewModel.uploadDocumentByIds(
                                docFile = actualDoc,
                                docTypeId = docID,
                                customerID = customerID
                            )
                        }
                    }
                }
            }
        }

    private fun validateInput(): Boolean {
        return !((binding.designationInputLayout.isVisible && binding.designationInputEdittext.length() < 2) ||
                (binding.employerInputLayout.isVisible && binding.employerInputEdittext.length() < 3) ||
                (binding.experienceInYearsInputLayout.isVisible && binding.experienceInYearsInputEdittext.length() < 1)
                || (binding.avgAnnualIncomeLayout.isVisible && selectedAvgIncome == null)
                || selectedDividendPayout == 0
//                || (binding.dividendPayoutLayout.isVisible && selectedDividendPayout == 0)
                || (binding.occupationLayout.isVisible && selectedOccupation == null)
                || (binding.dealHighValueItemsSwitch.isChecked && selectedPreciousMetal == 0)
                || (binding.sourceOfIncomeLayout.isVisible && selectedSourceOfIncome == null)
                || (binding.publicFigureSwitch.isChecked && selectedPublicFigureEntity == 0)
                || (binding.otherOccupationLayout.isVisible && binding.otherOccupationEdittext.text!!.length <= 0)
                || (binding.otherSourceOfIncomeLayout.isVisible && binding.otherSourceOfIncomeEdittext.text!!.length <= 0)
                || (binding.expectedInvestmentPerTransactionLayout.isVisible && selectedExpectedInvestmentPerTransaction == 0)
                || (binding.expectedInvestmentTransactionYearLayout.isVisible && selectedYearlyExpectedInvestment == 0)
                || (binding.declarationOfAccountLayout.isVisible && selectedFinancialAccountRefusal == 0))
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 64
        binding.appBar.progressbar2.progress = 0
        binding.appBar.progressbar3.progress = 0
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "20%"
        binding.appBar.stepTv.text = "Step 1/4"
    }
}