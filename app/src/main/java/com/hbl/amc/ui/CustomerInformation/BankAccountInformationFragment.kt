package com.hbl.amc.ui.CustomerInformation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentBankAccountInformationBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.utils.setupEditText
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.hbl.amc.databinding.FragmentProfessionalInformationBinding
import com.hbl.amc.domain.RequestDTO.GetAccountTitleDTO
import com.hbl.amc.domain.RequestDTO.SaveBankInfoDTO
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoActivity
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.AppUtils.closeSoftKeyboard
import com.hbl.amc.utils.CustomKeyBoardControlEditText
import com.hbl.amc.utils.DialogUtils


class BankAccountInformationFragment : AppCompatActivity() {

    private lateinit var binding: FragmentBankAccountInformationBinding
//    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val genericViewModel by viewModel<GenericViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val customerViewModel by viewModel<CustomerInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var customerIBAN: String = ""
    var customerAccountTitle: String = ""
    var cityList: List<City>? = null
    var bankList: List<Bank>? = null
    var branchList: List<Branch>? = null
    var selectedCity = 0
    var selectedBank = 0
    var selectedBranch = 0
    var accountTypes: AccountTypes? = null

    var isRda = false
    var isTitleMatched = false
    var isHabibBankSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBankAccountInformationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        DialogUtils.createPogressDialog(context = this)

        accountTypes = HBLPreferenceManager.getAccountType(this)

        initViewModel()
        initViews()
        customerViewModel.getBankInfoLookups(HBLPreferenceManager.getToken(this))
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 0
        binding.appBar.progressbar3.progress = 0
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "25%"
        binding.appBar.stepTv.text = "Step 1/4"
    }

    private fun initViews() {

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(this)?.let {
                DialogUtils.showInfoPopup(this, layoutInflater, it.code)
            }
        }

        setupProgress()


        binding.appBar.title.text = getString(R.string.customer_information)

        binding.continueBtn.isEnabled = false

        binding.bankAccountNumberEdittext.setupEditText(this, binding.bankAccountNumberInputLayout)
        binding.accountTitleInputEdittext.setupEditText(this, binding.accountTitleInputLayout)

        binding.mainLayout.setOnTouchListener { v, event ->
            if (binding.accountTitleInputEdittext.text.isNullOrEmpty()) {
                checkIban()
            }

            true
        }


        binding.bankAccountNumberEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.bankAccountNumberEdittext.length() < 2) {
                    customerIBAN = ""
                    customerAccountTitle = ""
                    binding.bankAccountNumberEdittext.error = "Minimum length should be 2"

                    toggleContinueBtn(validateInput())
                } else {
                    binding.bankAccountNumberEdittext.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.accountTitleInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.accountTitleInputEdittext.length() < 1) {

                    binding.accountTitleInputEdittext.error = "Minimum length should be 1"

                } else {
                    binding.accountTitleInputEdittext.error = null
                }
                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.bankAccountNumberEdittext.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                checkIban()
                closeSoftKeyboard(this, binding.bankAccountNumberEdittext)
                return@OnEditorActionListener true
            }
            false
        })

        binding.bankAccountNumberEdittext.listener =
            object : CustomKeyBoardControlEditText.KeyboardListener {
                override fun onStateChanged(
                    customKeyBoardControlEditText: CustomKeyBoardControlEditText?,
                    showing: Boolean
                ) {
//                    checkIban()
                }
            }

        binding.isRdaRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.is_rda_yes) {
                isRda = true
            } else if (checkedId == R.id.is_rda_no) {
                isRda = false
            }
            toggleContinueBtn(validateInput())
        }

        binding.continueBtn.setOnClickListener {
            if (validateInput()) {
                val saveBankInfoDTO = SaveBankInfoDTO(
                    customerIBAN,
                    customerAccountTitle,
                    selectedBank,
                    selectedBranch,
                    selectedCity, 178,
                    HBLPreferenceManager.getCustomerID(this),
                    "764F2E15-C953-4E15-8D44-54DE84EE5CA4",
                    isRda,
                    isTitleMatched
                )

                customerViewModel.saveBankInfo(
                    HBLPreferenceManager.getToken(this),
                    saveBankInfoDTO
                )
            }
        }

        binding.appBar.backBtn.setOnClickListener {
//            findNavController().navigateUp()
            onBackPressed()
        }

        binding.personalInfoTab.setOnClickListener {
            /*findNavController().navigate(
                R.id.action_bankAccountInformationFragment_to_customerInformationDetailFragment,
                bundleOf(Pair(EDIT_MODE, true))
            )*/
            val intent = Intent(this, CustomerInformationDetailFragment::class.java)
            intent.putExtra(EDIT_MODE, true)
            startActivity(intent)
        }

        binding.professionalInfoTab.setOnClickListener {
            /*findNavController().navigate(
                R.id.action_bankAccountInformationFragment_to_professionalInformationFragment,
                bundleOf(Pair(EDIT_MODE, true))
            )*/
            val intent = Intent(this, ProfessionalInformationFragment::class.java)
            intent.putExtra(EDIT_MODE, true)
            startActivity(intent)
        }
    }

    fun checkIban() {
//        if (binding.bankAccountNumberEdittext.length() >= 24) {
        if (binding.bankAccountNumberEdittext.length() >= 2) {
            if (isHabibBankSelected) {
                val accountTitleDTO = GetAccountTitleDTO(
                    binding.bankAccountNumberEdittext.text.toString(),
                    selectedBank, HBLPreferenceManager.getCustomerID(this)
                )
                customerViewModel.getAccountTitleByIBAN(
                    HBLPreferenceManager.getToken(
                        this
                    ), accountTitleDTO
                )
            }
        }
    }

    private fun initViewModel() {

        val banks: ArrayList<String> = ArrayList()
        val countries: ArrayList<String> = ArrayList()
        val branches: ArrayList<String> = ArrayList()
        val cities: ArrayList<String> = ArrayList()
        var resumeResult: ResumeBankInfoResult? = null

        customerViewModel.bankInfoLookUpsApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        bankList = it.response.result.banksList

                        banks.add(getString(R.string.select))
                        countries.add(getString(R.string.select))

                        bankList?.forEach { bank ->
                            banks.add(bank.bankName)
                        }

                        setUpBanksSpinner(banks = banks)

                        val pakistan = it.response.result.countryList.find { cn ->
                            cn.name.contains(
                                "pakistan",
                                true
                            )
                        }
                        pakistan?.let { pk -> genericViewModel.getCitiesData(pk.id) }

                        customerViewModel.resumeBankInfo(
                            HBLPreferenceManager.getToken(this),
                            HBLPreferenceManager.getCustomerID(this)
                        )
                    } else {
                        it.response?.message?.let { msg ->
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
                    it.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.accountTitleByIBANApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { result ->
                        customerIBAN = result.customerIBAN
                        customerAccountTitle = result.customerAccountTitle
                        isTitleMatched = result.isTitleMatched

//                        if (isHabibBankSelected) {
//                        binding.accountTitleInputLayout.isEnabled = false
//                        binding.titleInfoTv.visibility = GONE
//                        }

//                        binding.accountTitleInputLayout.isEnabled = !isTitleMatched
                        binding.accountTitleInputEdittext.setText(customerAccountTitle)
                        binding.accountTitleInputLayout.visibility = VISIBLE
                        binding.branchNameLayout.visibility = VISIBLE
                        binding.cityNameLayout.visibility = VISIBLE
                        binding.isRdaLayout.visibility = VISIBLE
                        /*if (!result.isTitleMatched) {
                            if (!IS_BUNYADI_BACHAT) {
                                binding.titleInfoTv.visibility = View.VISIBLE
                            }
                        } else {
                            binding.titleInfoTv.visibility = View.GONE
                        }*/

                        toggleContinueBtn(validateInput())
                    } ?: run {
                        it.response?.message?.let { msg ->
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
                    it.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.allBranchesResponse.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        branchList = it.response.result.branchlist

                        branches.clear()
                        branches.add(getString(R.string.select))

                        branchList?.forEach { branch ->
                            branches.add(branch.branchName)
                        }

                        setUpBranchesSpinner(branches = branches)

                        resumeResult?.let { res ->
                            val branch = branchList?.find { br -> br.id == res.customerBranchId }
                            binding.branchSpinner.setSelection(branches.indexOf(branch?.branchName))
                        }
                    } else {
                        it.response?.message?.let { msg ->
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
                    it.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        genericViewModel.allCitiesResponse.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    cityList = it.response?.result?.cities
                    cities.add(getString(R.string.select))

                    cityList?.forEach { city ->
                        cities.add(city.name)
                    }

                    setUpCitiesSpinner(cities = cities)

                    resumeResult?.let { res ->
                        val city = cityList?.find { ci -> ci.id == res.branchCityId }
                        binding.citySpinner.setSelection(cities.indexOf(city?.name))
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.saveBankInfoApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    //val user = it?.response?.let { res -> res.result }
                    //save user in shared prefs
                    //move to new activity
                    if (it.response?.status == "success") {
                        val intent = Intent(this, RegulatoryInfoActivity::class.java)
                        startActivity(intent)
                    } else {
                        it.response?.message?.let { msg ->
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
                    it.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.resumeBankInfoApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.let { res ->

                        if (res.result.customerIBAN != null) {
                            resumeResult = res.result
                            binding.bankAccountNumberEdittext.setText(resumeResult?.customerIBAN)
                            binding.accountTitleInputEdittext.setText(resumeResult?.customerAccountTitle)

                            customerIBAN = resumeResult?.customerIBAN!!
                            customerAccountTitle = resumeResult?.customerAccountTitle!!

                            val bankName =
                                bankList?.find { bn -> bn.id == resumeResult?.customerBankId }
                            binding.bankSpinner.setSelection(banks.indexOf(bankName?.bankName))

                            if (res.result.isRDAAccountType) {
                                isRda = true
                                binding.isRdaRadiogroup.check(binding.isRdaYes.id)
                            } else {
                                isRda = false
                                binding.isRdaRadiogroup.check(binding.isRdaNo.id)
                            }

                            toggleContinueBtn(validateInput())
                        }
                    } ?: run {
                        it.response?.message?.let { msg ->
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
                    it.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.loadingLiveData.observe(this, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })

        genericViewModel.loadingLiveData.observe(this, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    private fun setUpBanksSpinner(banks: List<String>) {
        binding.bankSpinner.apply {
            adapter =
                ArrayAdapter(this@BankAccountInformationFragment, R.layout.dropdown_item, banks)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    bankList?.let {
                        selectedBank = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        if (p2 > 0) {
                            customerViewModel.getBranchesData(
                                HBLPreferenceManager.getToken(
                                    this@BankAccountInformationFragment
                                ), it[p2 - 1].id
                            )

//                            isHabibBankSelected = it[p2 - 1].bankName == "HABIB BANK LIMITED"
                            isHabibBankSelected = it[p2 - 1].bankName == ""
                            if (isHabibBankSelected) {
                                binding.bankAccountNumberInputLayout.visibility = VISIBLE
                                binding.bankAccountNumberEdittext.text = null
                                binding.accountTitleInputLayout.visibility = GONE
                                binding.accountTitleInputLayout.isEnabled = false
                                binding.titleInfoTv.visibility = GONE
                                binding.branchNameLayout.visibility = GONE
                                binding.cityNameLayout.visibility = GONE
                                binding.isRdaLayout.visibility = GONE
                                toggleContinueBtn(validateInput())
                            } else {
                                binding.bankAccountNumberInputLayout.visibility = VISIBLE
                                binding.accountTitleInputLayout.visibility = VISIBLE
                                binding.accountTitleInputLayout.isEnabled = true
                                binding.titleInfoTv.visibility = VISIBLE
                                binding.branchNameLayout.visibility = VISIBLE
                                binding.cityNameLayout.visibility = VISIBLE
                                binding.isRdaLayout.visibility = VISIBLE
                                toggleContinueBtn(validateInput())
                            }
                        }
                    }

                    if (binding.accountTitleInputEdittext.text.isNullOrEmpty()) {
                        checkIban()
                    }

                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }
    }

    private fun setUpBranchesSpinner(branches: List<String>) {
        binding.branchSpinner.apply {
            adapter =
                ArrayAdapter(this@BankAccountInformationFragment, R.layout.dropdown_item, branches)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (!branchList.isNullOrEmpty()) {
                        selectedBranch = if (p2 > 0) {
                            branchList!![p2 - 1].id
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

    private fun setUpCitiesSpinner(cities: List<String>) {
        binding.citySpinner.apply {
            adapter =
                ArrayAdapter(this@BankAccountInformationFragment, R.layout.dropdown_item, cities)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    cityList?.let {
                        selectedCity = if (p2 > 0) {
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

    private fun validateInput(): Boolean {
        var isValid = true
        var isAccountTitleValidated = true
        if(!isHabibBankSelected)
        {
            isAccountTitleValidated = binding.accountTitleInputEdittext.text?.length!! >= 1
        }
        if (binding.bankAccountNumberEdittext.error != null
            || !isAccountTitleValidated
            || (selectedBank == 0)
            || (selectedBranch == 0)
            || (selectedCity == 0)
            || binding.isRdaRadiogroup.checkedRadioButtonId < 0
        ) {
            isValid = false
        }

        return isValid
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }
}