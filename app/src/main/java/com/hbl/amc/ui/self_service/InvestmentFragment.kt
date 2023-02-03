package com.hbl.amc.ui.self_service

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentInvestmentBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.self_service.SelfServiceViewModel
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.FileUtils
import com.hbl.amc.utils.mergeLists
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.internal.notifyAll
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class InvestmentFragment : Fragment(), KoinComponent {

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val selfServiceViewModel by viewModel<SelfServiceViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var billNumber: Long? = 0

    private var _binding: FragmentInvestmentBinding? = null
    private val binding get() = _binding!!

    var fundsList: List<InvestmentFunds>? = null
    var incomePlanList: List<InvestmentIncomePlan>? = null
    var paymentFrequencyList: List<InvestmentPaymentFrequency>? = null
    var paymentTypeList: List<InvestmentPaymentTypes>? = null
    var specifyAmountList: List<InvestmentSpecifyAmount>? = null
    var unitTypeList: List<InvestmentUnitTypes>? = null
    var instructionsList: List<InvestmentInstructions>? = null
    var otherFundslist: List<InvestmentFunds>? = null
    var allFundslist: List<InvestmentFunds>? = null
    var disclaimerList: List<InvestmentDisclaimer>? = null

    var selectedFund: String = ""
    var selectedIncomePlan: Int = 0
    var selectedPaymentFrequency: Int = 0
    var selectedPaymentType: String = ""
    var selectedSpecifyAmount: Int = 0
    var selectedUnitType: Int = 0

    var isSelectedSpecialFund: Boolean = false
    var amount: Double = 0.0

    var ibftReciept: File? = null
    var isFileUploaded = false

    //    val otherFundsArrayList: ArrayList<String> = ArrayList()
    val fundsArrayList: ArrayList<String> = ArrayList()
    var otherFundsCheck = false
    var paymentMethod = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()
        initViews()

        /*selfServiceViewModel.getInvestmentInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            "DB4D798B-B8C0-4105-EB25-08D9A4F5A935"
        )*/

        selfServiceViewModel.getInvestmentInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            HBLPreferenceManager.getCustomerID(
                requireContext()
            )
        )

        return view
    }


    private val niftActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val intent = result.data
            // Handle the Intent
            try {
                intent?.let {
                    val payment = it.getStringExtra(PAYMENT)
//                    var url = HBLPreferenceManager.getPayment(requireContext())

                    payment?.let { paymentUrl ->
                        var url = paymentUrl
                        if (url.isNotEmpty()) {
                            url = url.replace("%3D", "=")
                            url = url.replace("%3F", "?")

                            val uri = Uri.parse(url)
                            uri.getQueryParameter("refNo")?.let { payment_id ->
                                Log.d("payment ref no", payment_id)

                                var ibft: MultipartBody.Part? = null
                                val customerID = MultipartBody.Part.createFormData(
                                    "customerId",
                                    HBLPreferenceManager.getCustomerID(requireContext())
//                        "1DF93F23-087E-4FA1-E166-08D9B2F21152"
                                )
                                val transactionTypeId = MultipartBody.Part.createFormData(
                                    "transactionTypeId",
                                    "E3BBD22E-BE96-4AB5-A0DD-50D2ECCD0D92"
                                )
                                val amount =
                                    MultipartBody.Part.createFormData("amount", amount.toString())
                                val paymentTypeId =
                                    MultipartBody.Part.createFormData(
                                        "paymentTypeId",
                                        selectedPaymentType
                                    )
                                val fundId =
                                    MultipartBody.Part.createFormData("fundId", selectedFund)
                                val unitTypeId =
                                    MultipartBody.Part.createFormData(
                                        "unitTypeId",
                                        selectedUnitType.toString()
                                    )
                                val incomeTypeId =
                                    MultipartBody.Part.createFormData(
                                        "incomeTypeId",
                                        selectedIncomePlan.toString()
                                    )
                                val incomeSpecifyId = MultipartBody.Part.createFormData(
                                    "incomeSpecifyId",
                                    selectedSpecifyAmount.toString()
                                )
                                val incomePaymentFrequency = MultipartBody.Part.createFormData(
                                    "incomePaymentFrequency",
                                    selectedPaymentFrequency.toString()
                                )
                                val paymentReference = MultipartBody.Part.createFormData(
                                    "paymentReference",
                                    payment_id
                                )
                                val IBFTReferenceNumber = MultipartBody.Part.createFormData(
                                    "IBFTReferenceNumber",
                                    ""
                                )
                                val isAccepted =
                                    MultipartBody.Part.createFormData("isAccepted", true.toString())
                                val billNumber = MultipartBody.Part.createFormData("billNumber", "")

                                ibftReciept?.let {
                                    ibft = MultipartBody.Part.createFormData(
                                        "IBFTRecepit", ""
                                    )
                                }
                                val paymentDate =
                                    MultipartBody.Part.createFormData("paymentDate", "")
                                val niftStatus =
                                    MultipartBody.Part.createFormData("niftStatus", "true")

                                val fundBankAccountNumber =
                                    MultipartBody.Part.createFormData("fundBankAccountNumber", "")


                                selfServiceViewModel.saveInvestmentInfo(
                                    HBLPreferenceManager.getToken(requireContext()),
                                    customerID,
                                    transactionTypeId,
                                    amount,
                                    paymentTypeId,
                                    fundId,
                                    unitTypeId,
                                    incomeTypeId,
                                    incomeSpecifyId,
                                    incomePaymentFrequency,
                                    paymentReference,
                                    isAccepted,
                                    billNumber,
                                    fundBankAccountNumber,
                                    ibft,
                                    paymentDate,
                                    niftStatus,
                                    IBFTReferenceNumber
                                )
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun initViewModel() {

        val specifyAmountsArrayList: ArrayList<String> = ArrayList()
        val paymentFrequencyArrayList: ArrayList<String> = ArrayList()


        selfServiceViewModel.getInvestmentInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        fundsList = it.response?.result?.fundsList
                        incomePlanList = it.response?.result?.incomePlanList
                        specifyAmountList = it.response?.result?.specifyAmountList
                        paymentFrequencyList = it.response?.result?.paymentFrequencyList
                        unitTypeList = it.response?.result?.unitTypesList
                        paymentTypeList = it.response?.result?.paymentTypesList
                        instructionsList = it.response?.result?.instructionsList
                        otherFundslist = it.response?.result?.otherFundsList
                        disclaimerList = it.response?.result?.disclaimerList

                        binding.accountNumberTv.text = it.response?.result?.bankFundAccountNumber

                        fundsArrayList.add(getString(R.string.select))
                        specifyAmountsArrayList.add(getString(R.string.select))
                        paymentFrequencyArrayList.add(getString(R.string.select))

                        fundsList?.forEach { fund ->
                            fundsArrayList.add(fund.name)
                        }

                        specifyAmountList?.forEach { sa ->
                            specifyAmountsArrayList.add(sa.name)
                        }

                        paymentFrequencyList?.forEach { pf ->
                            paymentFrequencyArrayList.add(pf.name)
                        }



                        setUpFundsSpinner(fundsArrayList)
                        setUpSpecifyAmountSpinner(specifyAmountsArrayList)
                        setUpPaymentFrequencySpinner(paymentFrequencyArrayList)

                        binding.userNameTv.text = it.response?.result?.bankInfo?.name
                        binding.bankNameTv.text = it.response?.result?.bankInfo?.bankName
                        binding.userIbanTv.text = it.response?.result?.bankInfo?.ibanNumber
                        val maxLimit = it.response?.result?.bankInfo?.maximumInvestmentLimit
                        val nf = NumberFormat.getNumberInstance(Locale.US)
                        binding.maxLimitTv.text = "Rs. ${nf.format(maxLimit)}"
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
                    it.error?.message?.let { it1 ->
                        Log.d("investment lookups info error", it1)
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {}
                    }
                }
            }
        })

        selfServiceViewModel.saveInvestmentInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    Log.d("investment_response", it?.response.toString())
                    if (it.response?.status == "success") {

                        HBLPreferenceManager.savePaymentSuccess(requireContext(), "")

                        it.response.let { it2 ->
                            Log.d(
                                "investment save info success",
                                it2.toString()
                            )
                        }
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.success),
                            "Investment Completed!\nReference No. ${it.response.result.darNumber}",
                            R.drawable.ic_check,
                            getString(R.string.ok)
                        ) {
                            findNavController().popBackStack()
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
                    it.error?.message?.let { it1 ->
                        Log.d("investment save info error", it1)
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {}
                    }
                }
            }
        })

        selfServiceViewModel.generateBillNumberApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        billNumber = it?.response?.result?.billNumber

                        // will show generate bill number dialog
                        billNumber?.let { it1 ->
                            DialogUtils.showBillNumberGeneratorDialog(
                                requireContext(),
                                getString(R.string.unique_bill_number_label),
                                getString(R.string.bill_number_dialog_msg_label),
                                it1,
                                getString(R.string.screen_shot_label),
                                onInvestClicked = {
                                    disclaimerList?.get(0)?.let { it1 ->
                                        DialogUtils.showDisclaimerDialog(
                                            requireContext(),
                                            it1.description,
                                            onAcceptChecked = {
                                                SaveBillGenerationInvestmentInfo()
                                            })
                                    }
                                }
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
                    it.error?.message?.let { it1 ->
                        Log.d("Generate bill error", it1)
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {}
                    }
                }
            }
        })

        selfServiceViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    private fun setUpFundsSpinner(fromFunds: List<String>) {
        binding.fundSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fromFunds)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (otherFundsCheck) {
                        allFundslist?.let {
                            selectedFund = if (p2 > 0) {
                                it[p2 - 1].id
                            } else {
                                ""
                            }

                            if (p2 > 0) {
                                binding.amount.visibility = VISIBLE
                                isSelectedSpecialFund = allFundslist!![p2 - 1].isOfferingIncomeUnit
                            } else {
                                binding.amount.visibility = View.GONE
                                binding.amountEt.text = null
                                isSelectedSpecialFund = false
                            }
                        }
                    } else {
                        fundsList?.let {
                            selectedFund = if (p2 > 0) {
                                it[p2 - 1].id
                            } else {
                                ""
                            }

                            if (p2 > 0) {
                                showFundSelectionDisclaimer(fromFunds[p2])
                                binding.amount.visibility = VISIBLE
                                isSelectedSpecialFund = fundsList!![p2 - 1].isOfferingIncomeUnit
                            } else {
                                binding.amount.visibility = View.GONE
                                binding.amountEt.text = null
                                isSelectedSpecialFund = false
                            }
                        }
                    }
                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpSpecifyAmountSpinner(amounts: List<String>) {
        binding.specifyAmountSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, amounts)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    specifyAmountList?.let {
                        selectedSpecifyAmount = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                    }
                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpPaymentFrequencySpinner(paymentFrequency: List<String>) {
        binding.paymentFrequencySpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, paymentFrequency)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    paymentFrequencyList?.let {
                        selectedPaymentFrequency = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                    }
                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    fun initViews() {

        binding.amount.visibility = View.GONE
        binding.rpoScoreTv.visibility = View.GONE
        binding.unitType.visibility = View.GONE
        binding.incomePlanLayout.visibility = View.GONE

        binding.secondLayout.visibility = View.GONE
        binding.onlineBankingExpandableLayout.visibility = View.GONE

        binding.amountEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.amountEt.text.toString().isNotEmpty()) {
                    amount = binding.amountEt.text.toString().toDouble()
                    val accountType = requireActivity().intent.getSerializableExtra(
                        ACCOUNT_TYPE_RESULT
                    ) as AccountType
                    if (amount < 1000 || amount > accountType.transactionCap) {
                        binding.amount.error =
                            "Minimum amount should be 1000 and shouldn't exceed ${accountType.transactionCap}"
                    } else {
                        binding.amount.error = null
                        if (amount >= 500000 && isSelectedSpecialFund) {
                            binding.unitType.visibility = VISIBLE
                            selectedUnitType = unitTypeList?.get(0)?.id ?: 0
                        } else {
                            binding.unitType.visibility = View.GONE
                            binding.unitType.visibility = View.GONE
                            binding.growthBtn.callOnClick()
                            selectedUnitType = 0
                            selectedIncomePlan = 0
                            selectedSpecifyAmount = 0
                            selectedPaymentFrequency = 0
                        }
                    }
                }
                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.debitAccountLayout.setOnClickListener {
            showDebitAccountLayout()
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.paymentServiceLayout.setOnClickListener {
            showPaymentServiceLayout()
        }
        binding.onlineBankingLayout.setOnClickListener {
            showOnlineBankingLayout()
        }

        binding.incomePlanRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.income_plan_flexible) {
                selectedIncomePlan = incomePlanList?.get(0)?.id ?: 0
            } else if (checkedId == R.id.income_plan_fixed) {
                selectedIncomePlan = incomePlanList?.get(1)?.id ?: 0
            }
            toggleContinueBtn(validateInput())
        }

        binding.debitAccountLayout.background = null

        binding.investBtn.setOnClickListener {

            when (paymentMethod) {
                INSTANT_TRANSFER -> {
                    if (binding.amount.visibility == VISIBLE && binding.amountEt.text.toString()
                            .isNotEmpty()
                    ) {
                        disclaimerList?.get(0)?.let { it1 ->
                            DialogUtils.showDisclaimerDialog(
                                requireContext(),
                                it1.description,
                                onAcceptChecked = {
                                    val intent =
                                        Intent(requireContext(), NIFTInstantTransferActivity::class.java)
                                    intent.putExtra(AMOUNT, binding.amountEt.text.toString())
                                    niftActivityResult.launch(intent)
                                })
                        }
                    } else {
                        if (selectedFund.isEmpty()) {
                            DialogUtils.showAlertDialog(
                                requireContext(),
                                layoutInflater,
                                getString(R.string.oops),
                                "Please select fund type first.",
                                R.drawable.ic_alert,
                                getString(R.string.ok)
                            ) {}
                        }

                        if (binding.amountEt.text.isNullOrEmpty()) {
                            binding.amount.error == "Please enter amount to invest."
                        } else {
                            binding.amount.error == null
                        }
                    }
                }
                BILL_GENERATION -> {
                    selfServiceViewModel.getGenerateBillNumber(
                        HBLPreferenceManager.getToken(
                            requireContext()
                        )
                    )
                }
                ONLINE_TRANSFER -> {
                    var ibft: MultipartBody.Part? = null

                    //                val customerID = MultipartBody.Part.createFormData(
                    //                    "customerId",
                    //                    HBLPreferenceManager.getCustomerID(requireContext())
                    //                )
                    val customerID = MultipartBody.Part.createFormData(
                        "customerId",
                        HBLPreferenceManager.getCustomerID(requireContext())
//                        "1DF93F23-087E-4FA1-E166-08D9B2F21152"
                    )
                    val transactionTypeId = MultipartBody.Part.createFormData(
                        "transactionTypeId",
                        "E3BBD22E-BE96-4AB5-A0DD-50D2ECCD0D92"
                    )
                    val amount = MultipartBody.Part.createFormData("amount", amount.toString())
                    val paymentTypeId =
                        MultipartBody.Part.createFormData("paymentTypeId", selectedPaymentType)
                    val fundId = MultipartBody.Part.createFormData("fundId", selectedFund)
                    val unitTypeId =
                        MultipartBody.Part.createFormData("unitTypeId", selectedUnitType.toString())
                    val incomeTypeId =
                        MultipartBody.Part.createFormData(
                            "incomeTypeId",
                            selectedIncomePlan.toString()
                        )
                    val incomeSpecifyId = MultipartBody.Part.createFormData(
                        "incomeSpecifyId",
                        selectedSpecifyAmount.toString()
                    )
                    val incomePaymentFrequency = MultipartBody.Part.createFormData(
                        "incomePaymentFrequency",
                        selectedPaymentFrequency.toString()
                    )
                    val paymentReference = MultipartBody.Part.createFormData(
                        "paymentReference",
                        ""
//                        binding.ibftReferenceNumberEt.text.toString()
                    )
                    val IBFTReferenceNumber = MultipartBody.Part.createFormData(
                        "IBFTReferenceNumber",
                        binding.ibftReferenceNumberEt.text.toString()
                    )
                    val isAccepted =
                        MultipartBody.Part.createFormData("isAccepted", true.toString())
                    val billNumber = MultipartBody.Part.createFormData("billNumber", "")
                    val fundBankAccountNumber =
                        MultipartBody.Part.createFormData(
                            "fundBankAccountNumber",
                            binding.accountNumberTv.text.toString()
                        )
                    ibftReciept?.let {
                        ibft = MultipartBody.Part.createFormData(
                            "IBFTRecepit", it.name,
                            it.asRequestBody("image/*".toMediaTypeOrNull())
                        )
                    }
                    val paymentDate = MultipartBody.Part.createFormData("paymentDate", "")
                    val niftStatus = MultipartBody.Part.createFormData("niftStatus", "")

                    disclaimerList?.get(0)?.let { it1 ->
                        DialogUtils.showDisclaimerDialog(
                            requireContext(),
                            it1.description,
                            onAcceptChecked = {
                                selfServiceViewModel.saveInvestmentInfo(
                                    HBLPreferenceManager.getToken(requireContext()),
                                    customerID,
                                    transactionTypeId,
                                    amount,
                                    paymentTypeId,
                                    fundId,
                                    unitTypeId,
                                    incomeTypeId,
                                    incomeSpecifyId,
                                    incomePaymentFrequency,
                                    paymentReference,
                                    isAccepted,
                                    billNumber,
                                    fundBankAccountNumber,
                                    ibft,
                                    paymentDate,
                                    niftStatus,
                                    IBFTReferenceNumber
                                )
                            })
                    }


                }
            }
        }

        binding.ibftReferenceNumberEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.ibftReferenceNumberEt.text.isNullOrEmpty()) {
                    binding.ibftReferenceNumber.error = "Minimum length should be 3"
                } else {
                    binding.ibftReferenceNumber.error = null
                }
                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.incomeBtn.setOnClickListener {
            selectedUnitType = unitTypeList?.get(1)?.id ?: 0
            binding.incomePlanLayout.visibility = VISIBLE
            binding.incomeBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text
                )
            )
            binding.growthBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text_2
                )
            )
            binding.incomeBtn.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.selected_viewpager_bg)
            binding.growthBtn.background = null
            toggleContinueBtn(validateInput())
        }
        binding.growthBtn.setOnClickListener {
            selectedUnitType = unitTypeList?.get(0)?.id ?: 0
            binding.incomePlanLayout.visibility = View.GONE
            binding.incomeBtn.background = null
            binding.growthBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text
                )
            )
            binding.incomeBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text_2
                )
            )
            binding.growthBtn.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.selected_viewpager_bg)
            toggleContinueBtn(validateInput())
        }

        binding.addIbftBtn.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun showDebitAccountLayout() {
        paymentMethod = INSTANT_TRANSFER
        selectedPaymentType = paymentTypeList?.get(1)?.id ?: ""
        binding.secondLayout.visibility = VISIBLE
        binding.debitAccountLayout.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.investment_method_selected)
        binding.paymentServiceLayout.background = null
        binding.onlineBankingLayout.background = null
        binding.onlineBankingExpandableLayout.visibility = View.GONE
        binding.investBtn.text = getString(R.string.invest_btn_label)
        binding.debitAccountTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hbl_main_green
            )
        )
        binding.billPaymentServiceTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray_text
            )
        )
        binding.onlineBankingTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray_text
            )
        )
        binding.investmentTypeTitleTv.text = getString(R.string.instant_transfer)
        if (instructionsList != null) {
            binding.instructionTv.text = instructionsList!![1].title
            var desc = instructionsList!![1].description
            binding.expandTextView.text = "$desc \n"
        }
        toggleContinueBtn(validateInput())
    }

    private fun showPaymentServiceLayout() {
        paymentMethod = BILL_GENERATION
        selectedPaymentType = paymentTypeList?.get(0)?.id ?: ""
        binding.secondLayout.visibility = VISIBLE
        binding.debitAccountLayout.background = null
        binding.onlineBankingLayout.background = null
        binding.paymentServiceLayout.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.investment_method_selected)
        binding.onlineBankingExpandableLayout.visibility = View.GONE
        binding.investBtn.text = getString(R.string.generate_bill_number_label)
        binding.debitAccountTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray_text
            )
        )
        binding.billPaymentServiceTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hbl_main_green
            )
        )
        binding.onlineBankingTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray_text
            )
        )
        if (instructionsList != null) {
            binding.investmentTypeTitleTv.text = getString(R.string.invoice_payments)
            binding.instructionTv.text = instructionsList!![0].title
            var desc = instructionsList!![0].description
            binding.expandTextView.text = desc + " \n"
        }
        toggleContinueBtn(validateInput())
    }

    private fun showOnlineBankingLayout() {
        paymentMethod = ONLINE_TRANSFER
        selectedPaymentType = paymentTypeList?.get(2)?.id ?: ""
        binding.secondLayout.visibility = VISIBLE
        binding.debitAccountLayout.background = null
        binding.paymentServiceLayout.background = null
        binding.onlineBankingLayout.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.investment_method_selected)
        binding.onlineBankingExpandableLayout.visibility = VISIBLE
        binding.investBtn.text = getString(R.string.invest_btn_label)
        binding.debitAccountTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray_text
            )
        )
        binding.billPaymentServiceTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray_text
            )
        )
        binding.onlineBankingTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hbl_main_green
            )
        )
        if (instructionsList != null) {
            binding.investmentTypeTitleTv.text = getString(R.string.online_banking_inline_label)
            binding.instructionTv.text = instructionsList!![2].title
            var desc = instructionsList!![2].description
            binding.expandTextView.text = desc + " \n"
        }

        toggleContinueBtn(validateInput())
    }

    private fun validateInput(): Boolean {
        return !(selectedFund == "" ||
                (binding.amount.isShown && binding.amountEt.text.isNullOrEmpty()) ||
                (binding.unitType.isShown && selectedUnitType == 0) ||
                (binding.incomePlanLayout.isShown && selectedIncomePlan == 0) ||
                (binding.specifyAmountLayout.isShown && selectedSpecifyAmount == 0) ||
                (binding.paymentFrequencyLayout.isShown && selectedPaymentFrequency == 0) ||
                (binding.onlineBankingExpandableLayout.isShown && (binding.ibftReferenceNumberEt.text.isNullOrEmpty() || !isFileUploaded)))

    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.investBtn.isEnabled = b
    }

    fun SaveBillGenerationInvestmentInfo() {
        //                val customerID = MultipartBody.Part.createFormData(
//                    "customerId",
//                    HBLPreferenceManager.getCustomerID(requireContext())
//                )
        val customerID = MultipartBody.Part.createFormData(
            "customerId",
            HBLPreferenceManager.getCustomerID(requireContext())
//            "1DF93F23-087E-4FA1-E166-08D9B2F21152"
        )
        val transactionTypeId = MultipartBody.Part.createFormData(
            "transactionTypeId",
            "E3BBD22E-BE96-4AB5-A0DD-50D2ECCD0D92"
        )
        val amount = MultipartBody.Part.createFormData("amount", amount.toString())
        val paymentTypeId =
            MultipartBody.Part.createFormData("paymentTypeId", selectedPaymentType)
        val fundId = MultipartBody.Part.createFormData("fundId", selectedFund)
        val unitTypeId =
            MultipartBody.Part.createFormData("unitTypeId", selectedUnitType.toString())
        val incomeTypeId =
            MultipartBody.Part.createFormData("incomeTypeId", selectedIncomePlan.toString())
        val incomeSpecifyId = MultipartBody.Part.createFormData(
            "incomeSpecifyId",
            selectedSpecifyAmount.toString()
        )
        val incomePaymentFrequency = MultipartBody.Part.createFormData(
            "incomePaymentFrequency",
            selectedPaymentFrequency.toString()
        )
        val paymentReference = MultipartBody.Part.createFormData("paymentReference", "")
        val isAccepted = MultipartBody.Part.createFormData("isAccepted", true.toString())
        val billNumber = MultipartBody.Part.createFormData("billNumber", billNumber.toString())
        val fundBankAccountNumber =
            MultipartBody.Part.createFormData("fundBankAccountNumber", "")
        val IBFTRecepit = MultipartBody.Part.createFormData("IBFTRecepit", "")
        val paymentDate = MultipartBody.Part.createFormData("paymentDate", "")
        val niftStatus = MultipartBody.Part.createFormData("niftStatus", "")
        val IBFTReferenceNumber = MultipartBody.Part.createFormData(
            "IBFTReferenceNumber",
            ""
        )
        selfServiceViewModel.saveInvestmentInfo(
            HBLPreferenceManager.getToken(requireContext()),
            customerID,
            transactionTypeId,
            amount,
            paymentTypeId,
            fundId,
            unitTypeId,
            incomeTypeId,
            incomeSpecifyId,
            incomePaymentFrequency,
            paymentReference,
            isAccepted,
            billNumber,
            fundBankAccountNumber,
            IBFTRecepit,
            paymentDate,
            niftStatus,
            IBFTReferenceNumber
        )
    }

    fun showFundSelectionDisclaimer(fundName : String) {
        DialogUtils.showConfirmationDialog(
            requireActivity(),
            layoutInflater,
            getString(R.string.caution),
            "According to your RPQ score you have been suggested $fundName.\n\nIf you choose another fund you will choose it at your own risk.",
            R.drawable.ic_alert,
            onOkPressed = {

            },
            onCancelPressed = {
                if (!fundsList.isNullOrEmpty() && !otherFundslist.isNullOrEmpty()) {
                    binding.fundTypeTv.text = "Please select another fund"
                    allFundslist = mergeLists(fundsList!!, otherFundslist!!)
                    fundsArrayList.clear()

                    fundsArrayList.add(getString(R.string.select))

                    allFundslist?.forEach { otherFund ->
                        fundsArrayList.add(otherFund.name)
                    }

                    binding.fundSpinner.adapter = null
                    binding.fundSpinner.onItemSelectedListener = null
                    otherFundsCheck = true
                    setUpFundsSpinner(fundsArrayList)
                }
            }
        )
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!

                val file = FileUtil.getTempFile(requireContext(), uri)
                Log.d("file path", file?.name + "-----------" + file?.path)
                binding.addIbftBtn.setImageResource(R.drawable.ic_edit)
                isFileUploaded = true
                toggleContinueBtn(validateInput())
                file?.let { actualFile ->
                    ibftReciept = actualFile
                }
            }
        }
}
