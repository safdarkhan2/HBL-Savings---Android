package com.hbl.amc.ui.CustomerInformation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.View.*
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.chaos.view.PinView
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentCustomerInformationDetailBinding
import com.hbl.amc.domain.RequestDTO.*
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Disclaimers.DisclaimersActivity
import com.hbl.amc.ui.Disclaimers.DisclaimersViewModel
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoActivity
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.VideoCall.VideoCallActivity
import com.hbl.amc.ui.productInformation.ProductSelectionActivity
import com.hbl.amc.utils.*
import com.hbl.amc.utils.AppUtils.afterTextChanged
import com.hbl.amc.utils.AppUtils.changeDateFormatToDisplay
import com.hbl.amc.utils.AppUtils.changeDisplayDateFormatToUTC
import com.hbl.amc.utils.AppUtils.changeUTCDateFormatToDisplay
import com.hbl.amc.utils.AppUtils.closeSoftKeyboard
import com.hbl.amc.utils.AppUtils.getDatesDifference
import com.hbl.amc.utils.AppUtils.getPDFURI
import com.hbl.amc.utils.AppUtils.getWordDocURI
import com.hbl.amc.utils.AppUtils.viewPDF
import com.hbl.amc.utils.AppUtils.viewWordDoc
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showAccountTypeChangeDialog
import com.hbl.amc.utils.DialogUtils.Companion.showAlertDialog
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class CustomerInformationDetailFragment : AppCompatActivity() {

//    private var _binding: FragmentCustomerInformationDetailBinding? = null

    lateinit var binding: FragmentCustomerInformationDetailBinding

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val customerViewModel by viewModel<CustomerInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val genericViewModel by viewModel<GenericViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val disclaimersViewModel by viewModel<DisclaimersViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var frontImage: Bitmap? = null
    var backImage: Bitmap? = null
    var bFormImage: Bitmap? = null
    var cnicUploadResult: CnicUploadResult? = null

    var genderList: List<GenericObject>? = null
    var religionList: List<GenericObject>? = null
    var mobileOwnerList: List<GenericObject>? = null
    var relationshipList: List<GenericObject>? = null
    var countryList: List<Country>? = null
    var cityList: List<City>? = null
    var selectedGender = 0
    var selectedReligion = 0
    var selectedMobileOwner = 0
    var selectedCountry = 0
    var selectedCity = 0
    var selectedMailingCountry = 0
    var selectedGuardianCountry = 0
    var selectedGuardianCity = 0
    var selectedMailingCity = 0
    var countrySpinner = 1
    var cnicIssueDate = ""
    var cnicExpiry = ""
    var guardianCnicExpiry: String? = null
    var dob = ""
    var guardianDob: String? = null
    var mailingAddress = ""
    var mobileNo = ""
    var frontCheck = true
    var docType = ""
    var allDocsTypes: List<DocumentsTypesResult>? = null

    var verifyBtn: AppCompatButton? = null
    var resendBtn: AppCompatButton? = null
    var pinView: PinView? = null
    var otpDetail: TextView? = null
    var cdt: CountDownTimer? = null

    var selectedReligionName = ""
    private val sharedPrefFile = "hblsharedpreference"
    var cnicBackPath: String? = null
    var cnicAddress: String? = null
    var mobileUndertakingCloseFamily: String? = null
    var mobileNumberLetterByEmployer: String? = null
    var serviceProviderBill: String? = null
    var editMode = false

    var resCityID = 0
    var resMailingCityID = 0
    var fieldType = 0

    //    var khasSarmaya = false
    var isMinor = false
    var guardianRelationShip = 0
    var idType: Int? = null
    var accountTypes: AccountTypes? = null
    var currentStepID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCustomerInformationDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        DialogUtils.createPogressDialog(context = this)

        accountTypes = HBLPreferenceManager.getAccountType(this)

        intent?.also {
            if (it.hasExtra(CNIC_FRONT)) {
                frontImage = it
                    .getByteArrayExtra(CNIC_FRONT)?.let { it1 ->
                        BitmapFactory.decodeByteArray(
                            it.getByteArrayExtra(CNIC_FRONT), 0, it1.size
                        )
                    }
//                frontImage = it.getParcelableExtra(CNIC_FRONT)!!

                frontImage?.let { bs ->
                    HBLPreferenceManager.saveFrontImageURI(
                        this,
                        FileUtils.bitmapToBase64String(bs)
                    )
                }
            }

            if (it.hasExtra(ID_TYPE)) {
                idType = it.getIntExtra(ID_TYPE, 0)
            }

            if (it.hasExtra(CNIC_BACK)) {
//                backImage = it.getParcelableExtra(CNIC_BACK)!!
                backImage = it
                    .getByteArrayExtra(CNIC_BACK)?.let { it1 ->
                        BitmapFactory.decodeByteArray(
                            it.getByteArrayExtra(CNIC_BACK), 0, it1.size
                        )
                    }

                backImage?.let {
                    HBLPreferenceManager.saveBackImageURI(
                        this,
                        FileUtils.bitmapToBase64String(it)
                    )
                }
            }

            if (it.hasExtra(CNIC_RESULT)) {
                it.getSerializableExtra(CNIC_RESULT)?.let { cnicRes ->
                    cnicUploadResult = cnicRes as CnicUploadResult
                    cnicUploadResult?.let { cnc ->
                        HBLPreferenceManager.saveCNICFrontPath(this, cnc.fileName)
                    }
                }
            }

            if (it.hasExtra(CNIC_ADDRESS)) {
                cnicAddress = it.getStringExtra(CNIC_ADDRESS)
            }

            if (it.hasExtra(CNIC_BACK_PATH)) {
                cnicBackPath = it.getStringExtra(CNIC_BACK_PATH)

                cnicBackPath?.let {
                    HBLPreferenceManager.saveCNICBackPath(this, it)
                }
            }

            if (it.hasExtra(EDIT_MODE)) {
                editMode = it.getBooleanExtra(EDIT_MODE, false)
            }

            if (it.hasExtra(B_FORM)) {
                bFormImage = it
                    .getByteArrayExtra(B_FORM)?.let { it1 ->
                        BitmapFactory.decodeByteArray(
                            it.getByteArrayExtra(B_FORM), 0, it1.size
                        )
                    }
//                bFormImage = it.getParcelableExtra(B_FORM)!!

                bFormImage?.let {
                    HBLPreferenceManager.saveBFormImageBitmap(
                        this,
                        FileUtils.bitmapToBase64String(it)
                    )
                }
            }
        }

        HBLPreferenceManager.getDocumentsTypesResponse(this)?.let { docTypeRes ->
            docTypeRes.result.also { allDocsTypes = it }
        }

        initViewModel()

        initView()

        customerViewModel.getPersonalInfoLookups()
    }

    private fun initViewModel() {
        val genders: ArrayList<String> = ArrayList()
        val religions: ArrayList<String> = ArrayList()
        val mobileOwnershipList: ArrayList<String> = ArrayList()
        val countries: ArrayList<String> = ArrayList()
        val cities: ArrayList<String> = ArrayList()
        val relationships: ArrayList<String> = ArrayList()

        customerViewModel.personalInfoLookUpsApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    genderList = it?.response?.result?.genderList
                    religionList = it?.response?.result?.religionList
                    countryList = it?.response?.result?.countryList
                    mobileOwnerList = it?.response?.result?.mobileOwnershipList
                    relationshipList = it?.response?.result?.relationshipsList

                    genders.add(getString(R.string.select))
                    religions.add(getString(R.string.select))
                    mobileOwnershipList.add(getString(R.string.select))
                    countries.add(getString(R.string.select))
                    relationships.add(getString(R.string.select))

                    genderList?.forEach { sex ->
                        genders.add(sex.name)
                    }

                    religionList?.forEach { sex ->
                        religions.add(sex.name)
                    }

                    mobileOwnerList?.forEach { m_owner ->
                        mobileOwnershipList.add(m_owner.name)
                    }

                    countryList?.forEach { m_owner ->
                        countries.add(m_owner.name)
                    }

                    relationshipList?.forEach { rs ->
                        relationships.add(rs.name)
                    }

                    setUpGenderSpinner(genders = genders)
                    setUpReligionSpinner(religions = religions)
                    setUpMobileOwnerSpinner(mobileOwners = mobileOwnershipList)
                    setUpCountriesSpinner(countries = countries)
                    setUpMailingCountriesSpinner(countries = countries)
                    setUpGuardianCountriesSpinner(countries = countries)
                    setUpRelationshipSpinner(relations = relationships)

                    cnicUploadResult?.let { cnicRes ->
                        binding.cnicNameEt.setText(cnicRes.name)
                        binding.cnicNicopNoEt.setText(cnicRes.nicNo)
                        binding.cnicNicopIssueDateEt.setText(changeDateFormatToDisplay(cnicRes.issueDate))
                        cnicIssueDate = cnicRes.issueDate
                        cnicExpiry = cnicRes.expiryDate
                        dob = cnicRes.dateOfBirth
                        binding.fatherSpouseNameEt.setText(cnicRes.fatherName)
                        binding.dateOfBirthEt.setText(changeDateFormatToDisplay(cnicRes.dateOfBirth))
//                        binding.motherMaidenNameEt.setText(cnicRes.mothersMaidenName)
//                        binding.emailAddressEt.setText(cnicRes.customerEmail)
                        binding.addressEt.setText(cnicAddress)
//                        binding.mobileNoEt.setText(cnicRes.mobileNumber)
//                        binding.ccPicker.fullNumber = cnicRes.mobileNumber
                        val sGender = genderList?.find { gn -> gn.id == cnicRes.genderId }
                        sGender?.let { gn ->
                            binding.genderSpinner.setSelection(genders.indexOf(gn.name))
                        }

                        val country = countryList?.find { cn -> cn.id == cnicRes.countryId }
                        country?.let { cn ->
                            binding.countrySpinner.setSelection(countries.indexOf(cn.name))
                        }
//                        binding.religionSpinner.setSelection(cnicRes.religionId)
//                        binding.mobileOwnershipSpinner.setSelection(cnicRes.mobileOwnershipId)
//                        binding.cnicExpiryCheckbox.isChecked = cnicRes.isLifeTimeExpiry
//                        binding.residentialCheckbox.isChecked = cnicRes.isParmanentAddress

//                        if (!cnicRes.isLifeTimeExpiry) {
                        binding.cnicExpiryDateEt.setText(changeDateFormatToDisplay(cnicRes.expiryDate))
//                        }

                        /*if (!cnicRes.isParmanentAddress) {
                            binding.mailingAddressEt.setText(cnicRes.customerMailingAddress)
                            binding.mailingCitySpinner.setSelection(cnicRes.customerMailingCountryId)
                        }*/

                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        genericViewModel.allCitiesResponse.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    cityList = it?.response?.result?.cities

                    cities.clear()
                    cities.add(getString(R.string.select))

                    cityList?.forEach { city ->
                        cities.add(city.name)
                    }

                    when (countrySpinner) {
                        1 -> {
                            setUpCitiesSpinner(cities = cities)

                            if (editMode) {
                                val city = cityList?.find { cn -> cn.id == resCityID }
                                city?.let { cn -> binding.citySpinner.setSelection(cities.indexOf(cn.name)) }
                            }

                            /*cnicUploadResult?.let { res ->
                                            binding.citySpinner.setSelection(res.customerCityId)
                                        }*/

                        }
                        2 -> {
                            setUpMailingCitiesSpinner(cities = cities)

                            if (editMode) {
                                val mailingCity = cityList?.find { cn -> cn.id == resMailingCityID }
                                mailingCity?.let { cn ->
                                    binding.mailingCitySpinner.setSelection(
                                        cities.indexOf(
                                            cn.name
                                        )
                                    )
                                }
                            }
                            /*cnicUploadResult?.let { res ->
                                            binding.mailingCitySpinner.setSelection(res.customerMailingCityId)
                                        }*/
                        }
                        3 -> {
                            setUpGuardianCitiesSpinner(cities = cities)

                            if (editMode) {
                                val gCity =
                                    cityList?.find { cn -> cn.id == resMailingCityID } //guardian city id to be added after getting from get by customer id api
                                gCity?.let { cn ->
                                    binding.guardianCitySpinner.setSelection(
                                        cities.indexOf(
                                            cn.name
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.changeAccountTypeApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        val st = it?.response?.result
                        if (st != null) {
                            HBLPreferenceManager.saveCustomerID(this, st.transactionID)
                            HBLPreferenceManager.saveToken(this, "Bearer ${st.token}")
                            currentStepID = st.stepId
                            showOTPDialog(st.maskMobile, st.transactionID)
                        }
                    } else {
                        it.response?.message?.let { msg ->
                            showAlertDialog(
                                this, layoutInflater, getString(R.string.oops),
                                msg, R.drawable.ic_alert, getString(R.string.ok)
                            ) {}
                        }
                    }

                    it?.response?.let { res ->
                        res.result?.let { st ->

                        } ?: run {
                            res.message?.let { msg ->
                                showAlertDialog(
                                    this, layoutInflater, getString(R.string.oops),
                                    msg, R.drawable.ic_alert, getString(R.string.ok)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.savePersonalInfoApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        val st = it?.response?.result
                        if (st != null) {
                            HBLPreferenceManager.saveCustomerID(this, st.transactionID)
                            HBLPreferenceManager.saveToken(this, "Bearer ${st.token}")
                            currentStepID = st.stepId

                            if (st.isExistingAccount) {
                                showOTPDialog(st.maskMobile, st.transactionID)
                            } else {
                                val accountTypesResult =
                                    HBLPreferenceManager.getAccountTypeResult(this)
                                accountTypesResult?.let { atr ->
                                    var aType = ""
                                    var bType = ""
                                    if (accountTypes?.code == "01-KS") {
                                        bType = atr.accountTypesList[0].name
                                        aType = atr.accountTypesList[1].name
                                    } else {
                                        aType = atr.accountTypesList[0].name
                                        bType = atr.accountTypesList[1].name
                                    }

                                    val MessageForAccountChange =
                                        "You have selected $aType Account previously. If you want to proceed with $bType Account then you need to complete all the Onboarding steps again."

                                    showAccountTypeChangeDialog(this,
                                        layoutInflater,
                                        getString(R.string.disclaimer_label),
                                        message = MessageForAccountChange,
                                        R.drawable.ic_alert,
                                        aType,
                                        bType,
                                        {
                                            showOTPDialog(st.maskMobile, st.transactionID)
                                        },
                                        {
                                            val step = HBLPreferenceManager.getOnboardingSteps(this)
                                            val newStep =
                                                step.find { stt -> stt.sequenceNo == 1 || stt.stepCode == 1 }
                                            val changeAccountTypeDTO = ChangeAccountTypeDTO(
                                                accountTypes?.id!!, //get from account selection
                                                st.transactionID,
                                                binding.cnicNicopNoEt.text.toString(),
                                                newStep?.let { ns -> ns.id }
                                                    ?: run { "6357d461-314c-4cf9-8857-d1563012d285" }
                                            )
                                            customerViewModel.changeAccountType(changeAccountTypeDTO = changeAccountTypeDTO)
                                        })
                                }
                            }
                        }
                    } else {
                        it.response?.message?.let { msg ->
                            showAlertDialog(
                                this, layoutInflater, getString(R.string.oops),
                                msg, R.drawable.ic_alert, getString(R.string.ok)
                            ) {}
                        }
                    }

                    it?.response?.let { res ->
                        res.result?.let { st ->

                        } ?: run {
                            res.message?.let { msg ->
                                showAlertDialog(
                                    this, layoutInflater, getString(R.string.oops),
                                    msg, R.drawable.ic_alert, getString(R.string.ok)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.resendOTPApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.let { it1 ->
                        Log.d("otp response", it1.result.transactionID)

                        cdt?.let { cd -> cd.start() }
                        pinView?.setText("")
                        verifyBtn!!.isEnabled = false
                        resendBtn!!.isEnabled = false
                        verifyBtn!!.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.hbl_main_green
                            )
                        )
                        resendBtn!!.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.hbl_main_green
                            )
                        )
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.verifyOTPApi.observe(this, Observer {

            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.message?.let { msg ->
                        if (!msg.equals("success", true)) {
                            showAlertDialog(this,
                                layoutInflater,
                                "Oops!",
                                msg,
                                R.drawable.ic_alert,
                                "OK",
                                onOkPressed = {

                                })
                        }
                    }

                    if (it?.response?.result != null) {
                        it?.response?.result?.transactionID?.let { it2 ->
                            Log.d(
                                "verify otp response",
                                it2
                            )
                        }

                        val sharedPreferences: SharedPreferences =
                            getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString(SELECTED_RELIGION, selectedReligionName)
                        editor.apply()
                        editor.commit()


//                        //navigate
                        if (editMode) {
//                            findNavController().navigateUp()
                            onBackPressed()
                        } else {
                            stepNavigator()
//                            findNavController().navigate(R.id.action_customerInformationDetailFragment_to_professionalInformationFragment)
                        }

                    }
                }
                LiveDataWrapper.Status.ERROR -> {

                    it?.response?.let { vpt ->
                        vpt?.message?.let { msg ->
                            showAlertDialog(this,
                                layoutInflater,
                                "Oops!",
                                msg,
                                R.drawable.ic_alert,
                                "OK",
                                onOkPressed = {

                                })
                        }
                    }
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.loadingLiveData.observe(this, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.loadingLiveData.observe(this, Observer {
            if (it) showLoading() else hideLoading()
        })

        genericViewModel.loadingLiveData.observe(this, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.uploadDocumentApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.result?.let { res ->
//                        if (res.isUploaded) {
                        when (res.documentName) {
                            SERVICE_PROVIDER_BILL -> {
                                serviceProviderBill = res.id

                                showAlertDialog(
                                    this,
                                    layoutInflater,
                                    getString(R.string.success),
                                    "Service Provider Bill uploaded successfully!",
                                    R.drawable.ic_check_yellow,
                                    getString(R.string.ok)
                                ) {}
                            }
                            MOBILE_OWNERSHIP_AFFIDAVIT -> {
                                mobileUndertakingCloseFamily = res.id

                                showAlertDialog(
                                    this,
                                    layoutInflater,
                                    getString(R.string.success),
                                    "Affidavit uploaded successfully!",
                                    R.drawable.ic_check_yellow,
                                    getString(R.string.ok)
                                ) {}
                            }
                            MOBILE_NUMBER_LETTER_BY_EMPLOYER -> {
                                mobileNumberLetterByEmployer = res.id

                                showAlertDialog(
                                    this,
                                    layoutInflater,
                                    getString(R.string.success),
                                    "Mobile number letter from employer uploaded successfully!",
                                    R.drawable.ic_check_yellow,
                                    getString(R.string.ok)
                                ) {}
                            }
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
                            showAlertDialog(
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

        customerViewModel.resumePersonalInfoApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        val res = it?.response?.result
                        resCityID = res.customerCityId
                        resMailingCityID = res.customerMailingCityId
                        idType = res.identityTypeId

                        res.customerNIC?.let { cnic ->
                            binding.cnicNicopNoEt.setText(cnic)
                        }

                        binding.cnicNicopIssueDateEt.setText(changeUTCDateFormatToDisplay(res.issueDate))
                        cnicIssueDate = res.issueDate

                        if (!res.isLifeTimeExpiry) {
                            cnicExpiry = res.expiryDate
                            binding.cnicExpiryDateEt.setText(changeUTCDateFormatToDisplay(res.expiryDate))
                        } else {
                            binding.cnicExpiryCheckbox.isChecked = res.isLifeTimeExpiry
                        }

                        res.customerName?.let { cn ->
                            binding.cnicNameEt.setText(cn)
                        }

                        res.fatherName?.let { fn ->
                            binding.fatherSpouseNameEt.setText(fn)
                        }

                        res.mothersMaidenName?.let { mn ->
                            binding.motherMaidenNameEt.setText(mn)
                        }


                        val sex = genderList?.find { gen -> gen.id == res.genderId }
                        sex?.let { sx -> binding.genderSpinner.setSelection(genders.indexOf(sx.name)) }

                        dob = res.customerDOB
                        binding.dateOfBirthEt.setText(changeUTCDateFormatToDisplay(res.customerDOB))
                        binding.emailAddressEt.setText(res.customerEmail)
                        binding.addressEt.setText(res.customerAddress)

                        val country = countryList?.find { cn -> cn.id == res.customerCountryId }
                        country?.let { cn ->
                            val index = countries.indexOf(cn.name)
                            binding.countrySpinner.setSelection(index)
                        }

                        val religion = religionList?.find { rn -> rn.id == res.religionId }
                        religion?.let { rn ->
                            val index = religions.indexOf(rn.name)
                            binding.religionSpinner.setSelection(index)
                        }

                        binding.ccPicker.fullNumber = res.mobileNumber

                        val mbo = mobileOwnerList?.find { rn -> rn.id == res.mobileOwnershipId }
                        mbo?.let { rn ->
                            val index = mobileOwnershipList.indexOf(rn.name)
                            binding.mobileOwnershipSpinner.setSelection(index)
                        }

                        if (!res.isParmanentAddress) {
                            binding.mailingAddressEt.setText(res.customerMailingAddress)

                            val mailingCountry =
                                countryList?.find { cn -> cn.id == res.customerMailingCountryId }
                            mailingCountry?.let { cn ->
                                binding.mailingCountrySpinner.setSelection(
                                    countries.indexOf(cn.name)
                                )
                            }
                        } else {
                            binding.residentialCheckbox.isChecked = res.isParmanentAddress
                        }

                        binding.nameOfGuardianEt.setText(res.guardianName)
                        binding.guardianCnicNoEt.setText(res.guardianNIC)
                        binding.fatherNameOfGuardianEt.setText(res.guardianFatherName)
                        binding.guardianDateOfBirthEt.setText(changeUTCDateFormatToDisplay(res.guardianDOB))
                        binding.guardianAddressEt.setText(res.guardianAddress)
                        binding.educationEt.setText(res.education)
                        binding.guardianCnicExpiryDateEt.setText(changeUTCDateFormatToDisplay(res.guardianNICExpiry))
                        res.guardianDOB?.let { gdob ->
                            guardianDob = gdob
                        }

                        res.minorRelationshipId?.let { mri ->
                            val mnr = relationshipList?.find { rn -> rn.id == mri }
                            mnr?.let { rn ->
                                binding.guardianRelationshipSpinner.setSelection(
                                    relationships.indexOf(rn.name)
                                )
                            }
                        }

                        val gCountry = countryList?.find { cn -> cn.id == res.guardianCountryId }
                        gCountry?.let { cn ->
                            binding.guardianRelationshipSpinner.setSelection(
                                countries.indexOf(cn.name)
                            )
                        }

                        getDatesDifference(binding.dateOfBirthEt.text.toString())?.let { diff ->
                            isMinor = diff < 18

                            if (isMinor) {
                                binding.guardianLayout.visibility = VISIBLE
                            } else {
                                binding.guardianLayout.visibility = GONE
                            }
                        }

                    } else {
                        it?.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                showAlertDialog(
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
                                showAlertDialog(
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
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        disclaimersViewModel.downloadDocumentApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        if (it.response?.result?.extension.equals("PDF", true)) {
                            val uri = getPDFURI(this, it.response?.result.documentBase64)
                            viewPDF(this, layoutInflater, uri!!)
                        } else if (it.response?.result?.extension.equals("docx", true)) {
                            val uri = getWordDocURI(this, it.response?.result.documentBase64)
                            viewWordDoc(this, layoutInflater, uri!!)
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        customerViewModel.checkCnicApi.observe(this, Observer { res ->
            when (res.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (res.response?.status == "success") {
                        binding.cnicNicopNo.error = null
                        res.response?.result.id?.let { cid ->
                            HBLPreferenceManager.saveCustomerID(this,
                                cid
                            )
                        }
                    } else {
                        binding.cnicNicopNo.error = res.response?.message
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
//                    binding.cnicNicopNo.error = it.error?.message
                }
            }
        })

        customerViewModel.checkEmailOrMobileApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        if (fieldType == 1) {
                            binding.emailAddress.error = null
                        } else if (fieldType == 2) {
                            binding.mobileNumberErrorTv.visibility = GONE
                            binding.mobileNumberTv.setTextColor(
                                ContextCompat.getColor(
                                    this@CustomerInformationDetailFragment,
                                    R.color.hbl_main_green
                                )
                            )
                        }

                    } else {
                        if (fieldType ==1) {
                            binding.emailAddress.error = it.response?.message
                        } else if (fieldType == 2) {
                            binding.mobileNumberErrorTv.text = it.response?.message
                            binding.mobileNumberErrorTv.visibility = VISIBLE
                            binding.mobileNumberTv.setTextColor(
                                ContextCompat.getColor(
                                    this@CustomerInformationDetailFragment,
                                    R.color.design_default_color_error
                                )
                            )
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    /*if (fieldType ==1) {
                        binding.emailAddress.error = it.error?.message
                    } else if (fieldType == 2) {
                        binding.mobileNumberErrorTv.text = it.error?.message
                        binding.mobileNumberErrorTv.visibility = VISIBLE
                        binding.mobileNumberTv.setTextColor(
                            ContextCompat.getColor(
                                this@CustomerInformationDetailFragment,
                                R.color.design_default_color_error
                            )
                        )
                    }*/
                }
            }
        })
    }

    fun stepNavigator() {
        val allSteps = HBLPreferenceManager.getOnboardingSteps(this)

        when (currentStepID) {
            allSteps[1].id -> {
                if (selectedMobileOwner == 202) {
                    val intent = Intent(this, ProfessionalInformationFragment::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(
                        this, VideoCallActivity::class.java
                    )
                    startActivity(intent)
                }
            }
            allSteps[2].id -> {
                val intent = Intent(this, BankAccountInformationFragment::class.java)
                startActivity(intent)
            }
            allSteps[3].id -> {
                val intent = Intent(this, RegulatoryInfoActivity::class.java)
                startActivity(intent)
            }
            allSteps[4].id -> {
                val intent = Intent(this, RegulatoryInfoActivity::class.java)
                intent.putExtra(STEP_ID, allSteps[4].stepCode)
                startActivity(intent)
            }
            allSteps[5].id -> {
                val intent = Intent(this, RegulatoryInfoActivity::class.java)
                intent.putExtra(STEP_ID, allSteps[5].stepCode)
                startActivity(intent)
            }
            allSteps[6].id -> {
                val intent = Intent(this, RegulatoryInfoActivity::class.java)
                intent.putExtra(STEP_ID, allSteps[6].stepCode)
                startActivity(intent)
            }
            allSteps[7].id -> {
                val intent = Intent(this, ProductSelectionActivity::class.java)
                startActivity(intent)
            }
            allSteps[8].id -> {
                val intent = Intent(this, ProductSelectionActivity::class.java)
                intent.putExtra(STEP_ID, allSteps[8].stepCode)
                startActivity(intent)
            }
            allSteps[9].id -> {
                val intent = Intent(this, ProductSelectionActivity::class.java)
                intent.putExtra(STEP_ID, allSteps[9].stepCode)
                startActivity(intent)
            }
            allSteps[10].id -> {
                val intent = Intent(this, ProductSelectionActivity::class.java)
                intent.putExtra(STEP_ID, allSteps[10].stepCode)
                startActivity(intent)
            }
            allSteps[11].id -> {
                val intent = Intent(this, DisclaimersActivity::class.java)
                startActivity(intent)
            }
            allSteps[12].id -> {
                val intent = Intent(this, DisclaimersActivity::class.java)
                intent.putExtra(STEP_ID, allSteps[12].stepCode)
                startActivity(intent)
            }
        }
    }

    private fun setUpGenderSpinner(genders: List<String>) {
        binding.genderSpinner.apply {
            adapter = ArrayAdapter(
                this@CustomerInformationDetailFragment,
                R.layout.dropdown_item,
                genders
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    genderList?.let {
                        selectedGender = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                    }

                    toggleContinueBtn(validateInput())
//                    selectedGender = genderList!![p2].id.toInt()

//                    enableOrDisableNextBtn(isValid(data))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpReligionSpinner(religions: List<String>) {
        binding.religionSpinner.apply {
            adapter = ArrayAdapter(
                this@CustomerInformationDetailFragment,
                R.layout.dropdown_item,
                religions
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    religionList?.let {
                        selectedReligion = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        if (p2 > 0) {
                            selectedReligionName = it[p2 - 1].name
                            Log.d("SelectedReligion", selectedReligionName)
                        }
                    }

                    toggleContinueBtn(validateInput())
//                    selectedGender = genderList!![p2].id.toInt()

//                    enableOrDisableNextBtn(isValid(data))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpMobileOwnerSpinner(mobileOwners: List<String>) {
        binding.mobileOwnershipSpinner.apply {
            adapter = ArrayAdapter(
                this@CustomerInformationDetailFragment,
                R.layout.dropdown_item,
                mobileOwners
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    mobileOwnerList?.let {
                        selectedMobileOwner = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                    }

                    when (selectedMobileOwner) {
                        201 -> {
                            binding.affidavitLayout.visibility = VISIBLE
                            binding.ownershipInfoTv.visibility = VISIBLE
                            binding.serviceProviderBillLayout.visibility = GONE
                            binding.servicePv.visibility = GONE
                            docType = "MobileNumberUndertakingCloseFamilyMember"
                        }
                        202 -> {
                            binding.ownershipInfoTv.visibility = GONE
                            binding.affidavitLayout.visibility = GONE
                            binding.serviceProviderBillLayout.visibility = GONE
                            binding.servicePv.visibility = GONE
                        }
                        203 -> {
                            binding.affidavitLayout.visibility = VISIBLE
                            binding.serviceProviderBillLayout.visibility = VISIBLE
                            binding.ownershipInfoTv.visibility = VISIBLE
                            binding.servicePv.visibility = VISIBLE
                        }
                        204 -> {
                            binding.serviceProviderBillLayout.visibility = VISIBLE
                            binding.ownershipInfoTv.visibility = VISIBLE
                            binding.affidavitLayout.visibility = GONE
                            binding.servicePv.visibility = GONE
                            docType = "ServiceBillProvider"
                        }
                        else -> {
                            binding.serviceProviderBillLayout.visibility = GONE
                            binding.ownershipInfoTv.visibility = GONE
                            binding.affidavitLayout.visibility = GONE
                            binding.servicePv.visibility = GONE
                        }
                    }

                    if (p2 > 0) {
                        binding.mainScroll.post {
                            binding.mainScroll.fullScroll(FOCUS_DOWN)
                        }
                    }

                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpCountriesSpinner(countries: List<String>) {
        binding.countrySpinner.apply {
            adapter = ArrayAdapter(
                this@CustomerInformationDetailFragment,
                R.layout.dropdown_item,
                countries
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    countryList?.let {
                        selectedCountry = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        if (p2 > 0) {
                            countrySpinner = 1
                            genericViewModel.getCitiesData(it[p2 - 1].id)
                        }
                    }
//                    selectedGender = genderList!![p2].id.toInt()

//                    enableOrDisableNextBtn(isValid(data))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpMailingCountriesSpinner(countries: List<String>) {
        binding.mailingCountrySpinner.apply {
            adapter = ArrayAdapter(
                this@CustomerInformationDetailFragment,
                R.layout.dropdown_item,
                countries
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    countryList?.let {
                        selectedMailingCountry = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        if (p2 > 0) {
                            countrySpinner = 2
                            genericViewModel.getCitiesData(it[p2 - 1].id)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpGuardianCountriesSpinner(countries: List<String>) {
        binding.guardianCountrySpinner.apply {
            adapter = ArrayAdapter(
                this@CustomerInformationDetailFragment,
                R.layout.dropdown_item,
                countries
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    countryList?.let {
                        selectedGuardianCountry = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        if (p2 > 0) {
                            countrySpinner = 3
                            genericViewModel.getCitiesData(it[p2 - 1].id)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpRelationshipSpinner(relations: List<String>) {
        binding.guardianRelationshipSpinner.apply {
            adapter = ArrayAdapter(
                this@CustomerInformationDetailFragment,
                R.layout.dropdown_item,
                relations
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    relationshipList?.let {
                        guardianRelationShip = if (p2 > 0) {
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

    private fun setUpCitiesSpinner(cities: List<String>) {
        binding.citySpinner.apply {
            adapter =
                ArrayAdapter(this@CustomerInformationDetailFragment, R.layout.dropdown_item, cities)

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
//                    selectedGender = genderList!![p2].id.toInt()

//                    enableOrDisableNextBtn(isValid(data))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpMailingCitiesSpinner(cities: List<String>) {
        binding.mailingCitySpinner.apply {
            adapter =
                ArrayAdapter(this@CustomerInformationDetailFragment, R.layout.dropdown_item, cities)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    cityList?.let {
                        selectedMailingCity = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                    }

                    toggleContinueBtn(validateInput())
//                    selectedGender = genderList!![p2].id.toInt()

//                    enableOrDisableNextBtn(isValid(data))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpGuardianCitiesSpinner(cities: List<String>) {
        binding.guardianCitySpinner.apply {
            adapter =
                ArrayAdapter(this@CustomerInformationDetailFragment, R.layout.dropdown_item, cities)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    cityList?.let {
                        selectedGuardianCity = if (p2 > 0) {
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

    private fun showOTPDialog(maskMobile: String, transactionID: String) {
        /*val dialog = AppCompatDialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.window!!.setBackgroundDrawableResource( android.R.color.background_light);
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.otp_dialog)
        dialog.window!!.setBackgroundDrawableResource( R.color.transparent_colour)
*/
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val dialog = layoutInflater.inflate(R.layout.otp_dialog, null)
        dlg.setView(dialog)
        dlg.setCancelable(false)
        dlg.create()

        val timerView = dialog.findViewById<TextView>(R.id.timer_tv)
        verifyBtn = dialog.findViewById(R.id.otp_verify_btn)
        resendBtn = dialog.findViewById(R.id.resend_otp_btn)
        pinView = dialog.findViewById(R.id.pin_view)
        otpDetail = dialog.findViewById(R.id.otp_detail_msg_tv)

        otpDetail?.text = resources.getString(R.string.otp_detail_label) + maskMobile

        verifyBtn!!.isEnabled = false
        resendBtn!!.isEnabled = false
//        verifyBtn!!.setTextColor(ContextCompat.getColor(this, R.color.hbl_main_green))
//        resendBtn!!.setTextColor(ContextCompat.getColor(this, R.color.hbl_main_green))

        pinView!!.addTextChangedListener {
            verifyBtn!!.isEnabled = it?.length == 6

            /*if (verifyBtn!!.isEnabled) {
                verifyBtn!!.setTextColor(Color.WHITE)
            } else {
                verifyBtn!!.setTextColor(ContextCompat.getColor(this, R.color.hbl_main_green))
            }*/
        }

        cdt = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainedSecs = millisUntilFinished / 1000

                val min = if ((remainedSecs / 60) < 10) {
                    "0" + (remainedSecs / 60)
                } else {
                    (remainedSecs / 60)
                }

                val sec = if ((remainedSecs % 60) < 10) {
                    "0" + (remainedSecs % 60)
                } else {
                    (remainedSecs % 60)
                }

                timerView!!.text = "$min:$sec"

            }

            override fun onFinish() {
                // timerView!!.setText("try again")
                resendBtn!!.isEnabled = true
//                resendBtn!!.setTextColor(Color.WHITE)
//                Toast.makeText(this, "Resend button enabled!", LENGTH_LONG).show()
            }
        }

        cdt?.start()

        val dg = dlg.show()

        verifyBtn?.setOnClickListener {
            dg.dismiss()
            val verifyOtpDTO = VerifyOtpDTO(
                otp = pinView!!.text.toString(),
                transactionID = transactionID,
                transactionType = 1001
            )

            customerViewModel.verifyOTP(
                HBLPreferenceManager.getToken(this),
                verifyOtpDTO = verifyOtpDTO
            )
        }

        resendBtn!!.setOnClickListener {

            val sendOtpDTO = SendOtpDTO(
                "",
                cnicExpiry,
                false,
                false,
                issueDate = cnicIssueDate,
                transactionID = transactionID
            )
            customerViewModel.resendOTP(
                HBLPreferenceManager.getToken(this),
                sendOtpDTO = sendOtpDTO
            )
        }

        val closeButton = dialog.findViewById<ImageView>(R.id.close_btn) as ImageView

        closeButton.setOnClickListener {
            dg.dismiss()
        }

//        dialog.show()
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
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
                            disclaimersViewModel.uploadDocumentByIds(
                                docFile = actualDoc,
                                docTypeId = docID,
                                null
                            )
                        }
                    }
                }

            }
        }

    private fun initView() {

        binding.topHeaderLayout.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(this)?.let {
                DialogUtils.showInfoPopup(this, layoutInflater, it.code)
            }
        }

        if (editMode) {
//            binding.topHeaderLayout.backBtn.visibility = GONE
            binding.topHeaderLayout.progressbarContainer.visibility = GONE
            binding.topHeaderLayout.stepTv.visibility = GONE
            binding.personalInfoLabelTv.visibility = INVISIBLE
            binding.professionalInfoLabelTv.visibility = INVISIBLE
            binding.bankAccountLabelTv.visibility = INVISIBLE
            binding.uploadFrontBackTv.visibility = GONE

            customerViewModel.resumePersonalInfo(
                HBLPreferenceManager.getToken(this),
                HBLPreferenceManager.getCustomerID(this)
            )
        }

        binding.topHeaderLayout.title.text = getString(R.string.customer_information)

        if (frontImage != null) {
            binding.frontCnicIv.setImageBitmap(frontImage)
        } else {
            frontImage =
                FileUtils.base64ToBitmap(HBLPreferenceManager.getFrontImageURI(this)!!)//Uri.parse(HBLPreferenceManager.getFrontImageURI(this))
            frontImage?.let { img -> binding.frontCnicIv.setImageBitmap(img) }
        }

        if (bFormImage != null) {
            binding.frontCnicIv.setImageBitmap(bFormImage)
            binding.switchSide.visibility = GONE
        } else {
            bFormImage =
                FileUtils.base64ToBitmap(HBLPreferenceManager.getBFormImageBitmap(this)!!)//Uri.parse(HBLPreferenceManager.getFrontImageURI(this))
            bFormImage?.let { img ->
                binding.frontCnicIv.setImageBitmap(img)
                binding.switchSide.visibility = GONE
            }
        }

        binding.ccPicker.registerCarrierNumberEditText(binding.mobileNoEt)
        binding.ccPicker.setCountryForNameCode("PK")
        binding.ccPicker.setDialogCornerRaius(20f)
        binding.ccPicker.setNumberAutoFormattingEnabled(true)

        binding.uploadFrontBackTv.setOnClickListener {
//            findNavController().popBackStack()
            onBackPressed()
        }

        binding.switchSide.setOnClickListener {
            if (frontCheck) {
                frontCheck = false

                if (backImage != null) {
                    binding.frontCnicIv.setImageBitmap(backImage)
                } else {
                    backImage =
                        FileUtils.base64ToBitmap(HBLPreferenceManager.getBackImageURI(this)!!)//Uri.parse(HBLPreferenceManager.getBackImageURI(this))
                    backImage?.let {
                        binding.frontCnicIv.setImageBitmap(it)
                    }
                }

                binding.switchSide.setImageResource(R.drawable.ic_flip)
            } else {
                frontCheck = true

                frontImage?.let {
                    binding.frontCnicIv.setImageBitmap(it)
                }

                binding.switchSide.setImageResource(R.drawable.ic_idcard_side_change)
            }
        }

        binding.uploadAffidavitBtn.setOnClickListener {
//            docType = MOBILE_OWNERSHIP_AFFIDAVIT
            if (selectedMobileOwner == 203) {
                docType = "MobileNumberLetterByEmployer"
            }

            ImagePicker.with(this)
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.downloadAffidavitBtn.setOnClickListener {
            val docType = when (selectedMobileOwner) {
                201 -> {
                    allDocsTypes?.find { dc -> dc.name == "MobileNumberUndertakingCloseFamilyMember" }//handle according to proper doc type
                }
                203 -> {
                    allDocsTypes?.find { dc -> dc.name == "MobileNumberLetterByEmployer" }
                }
                else -> {
                    null
                }
            }

            docType?.let { it1 ->
                disclaimersViewModel.getSampleDocument(
                    it1.id
                )
            }
        }

        binding.addServiceProviderBillBtn.setOnClickListener {
            docType = SERVICE_PROVIDER_BILL
            ImagePicker.with(this)
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.cnicExpiryCheckbox.setOnClickListener {
            if (binding.cnicExpiryCheckbox.isChecked) {
                binding.cnicExpiryDate.visibility = GONE
//                binding.cnicExpiryDateEt.setText("")
//                cnicExpiry = ""
            } else {
                binding.cnicExpiryDate.visibility = VISIBLE
            }

            toggleContinueBtn(validateInput())
        }

        binding.residentialCheckbox.setOnClickListener {
            showHideMailingAddress(!binding.residentialCheckbox.isChecked)
            toggleContinueBtn(validateInput())
        }

        showHideMailingAddress(!binding.residentialCheckbox.isChecked)

        setupProgress()

        binding.cnicNicopNoEt.setupEditText(this, binding.cnicNicopNo)
        binding.cnicNicopIssueDateEt.setupEditText(this, binding.cnicNicopIssueDate)
        binding.cnicExpiryDateEt.setupEditText(this, binding.cnicExpiryDate)
        binding.dateOfBirthEt.setupEditText(this, binding.dateOfBirth)
        binding.cnicNameEt.setupEditText(this, binding.cnicName)
        binding.fatherSpouseNameEt.setupEditText(this, binding.fatherSpouseName)
        binding.motherMaidenNameEt.setupEditText(this, binding.motherMaidenName)
        binding.emailAddressEt.setupEditText(this, binding.emailAddress)
        binding.addressEt.setupEditText(this, binding.address)
        binding.nameOfGuardianEt.setupEditText(this, binding.nameOfGuardian)
        binding.guardianCnicNoEt.setupEditText(this, binding.guardianCnicNo)
        binding.fatherNameOfGuardianEt.setupEditText(this, binding.fatherNameOfGuardian)
        binding.guardianDateOfBirthEt.setupEditText(this, binding.dateOfBirthOfGuardian)
        binding.guardianAddressEt.setupEditText(this, binding.guardianAddress)
        binding.guardianCnicExpiryDateEt.setupEditText(this, binding.guardianCnicExpiryDate)
        binding.educationEt.setupEditText(this, binding.education)

        binding.cnicNicopIssueDate.setEndIconOnClickListener {
            val datePicker =
                DatePickerFragment { view, year, month, day -> // Do something with the date chosen by the user
                    var actualDay = day.toString()
                    val mon = month + 1
                    var actualMonth = mon.toString()

                    if (day < 10) {
                        actualDay = "0${day}"
                    }

                    if (mon < 10) {
                        actualMonth = "0${mon}"
                    }

//                    cnicIssueDate = "$actualDay-$actualMonth-$year"
                    binding.cnicNicopIssueDateEt.setText("$actualDay/$actualMonth/$year")
                    cnicIssueDate =
                        changeDisplayDateFormatToUTC(binding.cnicNicopIssueDateEt.text.toString())

                    toggleContinueBtn(validateInput())
                }

            datePicker?.type = FUTURE_DATE_DISABLE
            datePicker!!.show(supportFragmentManager, "datePicker")

            binding.cnicNicopIssueDate.setTitleColour(this)
        }

        binding.cnicExpiryDate.setEndIconOnClickListener {
            val datePickerFragment =
                DatePickerFragment { view, year, month, day -> // Do something with the date chosen by the user
                    var actualDay = day.toString()
                    val mon = month + 1
                    var actualMonth = mon.toString()

                    if (day < 10) {
                        actualDay = "0${day}"
                    }

                    if (mon < 10) {
                        actualMonth = "0${mon}"
                    }

//                    cnicExpiry = changeDisplayDateFormatToUTC("$actualDay/$actualMonth/$year")
                    binding.cnicExpiryDateEt.setText("$actualDay/$actualMonth/$year")
                    cnicExpiry =
                        changeDisplayDateFormatToUTC(binding.cnicExpiryDateEt.text.toString())

                    toggleContinueBtn(validateInput())
                }

            datePickerFragment?.type = PAST_DATE_DISABLE
            datePickerFragment!!.show(supportFragmentManager, "datePicker")

            binding.cnicExpiryDate.setTitleColour(this)
        }

        binding.guardianCnicExpiryDate.setEndIconOnClickListener {
            val datePickerFragment =
                DatePickerFragment { view, year, month, day -> // Do something with the date chosen by the user
                    var actualDay = day.toString()
                    val mon = month + 1
                    var actualMonth = mon.toString()

                    if (day < 10) {
                        actualDay = "0${day}"
                    }

                    if (mon < 10) {
                        actualMonth = "0${mon}"
                    }

//                    cnicExpiry = changeDisplayDateFormatToUTC("$actualDay/$actualMonth/$year")
                    binding.guardianCnicExpiryDateEt.setText("$actualDay/$actualMonth/$year")
                    guardianCnicExpiry =
                        changeDisplayDateFormatToUTC(binding.guardianCnicExpiryDateEt.text.toString())

                    toggleContinueBtn(validateInput())
                }

            datePickerFragment?.type = PAST_DATE_DISABLE
            datePickerFragment!!.show(supportFragmentManager, "datePicker")

            binding.guardianCnicExpiryDate.setTitleColour(this)
        }

        binding.dateOfBirth.setEndIconOnClickListener {
            val datePickerFragment =
                DatePickerFragment { view, year, month, day -> // Do something with the date chosen by the user
                    var actualDay = day.toString()
                    val mon = month + 1
                    var actualMonth = mon.toString()

                    if (day < 10) {
                        actualDay = "0${day}"
                    }

                    if (mon < 10) {
                        actualMonth = "0${mon}"
                    }

//                    dob = changeDisplayDateFormatToUTC("$actualDay/$actualMonth/$year")
                    binding.dateOfBirthEt.setText("$actualDay/$actualMonth/$year")
                    dob = changeDisplayDateFormatToUTC(binding.dateOfBirthEt.text.toString())

                    getDatesDifference(binding.dateOfBirthEt.text.toString())?.let { diff ->
                        isMinor = diff < 18

                        if (isMinor) {
                            binding.guardianLayout.visibility = VISIBLE
                        } else {
                            binding.guardianLayout.visibility = GONE
                        }
                    }

                    toggleContinueBtn(validateInput())
                }

            datePickerFragment?.type = FUTURE_DATE_DISABLE

            if (accountTypes?.code == "01-KS") {
                datePickerFragment?.isMinor = false
            }

            datePickerFragment!!.show(supportFragmentManager, "datePicker")

            binding.dateOfBirth.setTitleColour(this)
        }

        binding.dateOfBirthOfGuardian.setEndIconOnClickListener {
            val datePickerFragment =
                DatePickerFragment { view, year, month, day -> // Do something with the date chosen by the user
                    var actualDay = day.toString()
                    val mon = month + 1
                    var actualMonth = mon.toString()

                    if (day < 10) {
                        actualDay = "0${day}"
                    }

                    if (mon < 10) {
                        actualMonth = "0${mon}"
                    }

//                    dob = changeDisplayDateFormatToUTC("$actualDay/$actualMonth/$year")
                    binding.guardianDateOfBirthEt.setText("$actualDay/$actualMonth/$year")
                    guardianDob =
                        changeDisplayDateFormatToUTC(binding.guardianDateOfBirthEt.text.toString())

                    toggleContinueBtn(validateInput())
                }

            datePickerFragment?.type = FUTURE_DATE_DISABLE
            datePickerFragment?.isMinor = false
            datePickerFragment!!.show(supportFragmentManager, "datePicker")

            binding.dateOfBirthOfGuardian.setTitleColour(this)
        }

        //binding.sendOtpBtn.isEnabled = false
        binding.sendOtpBtn.setOnClickListener {

            //save personal info
            if (binding.residentialCheckbox.isChecked) {
                mailingAddress = binding.addressEt.text.toString()
                selectedMailingCountry = selectedCountry
                selectedMailingCity = selectedCity
            } else {
                mailingAddress = binding.mailingAddressEt.text.toString()
            }

            val savePersonalInfoDTO = if (editMode) {
                SavePersonalInfoDTO(
                    binding.addressEt.text.toString(),
                    selectedCity,
                    selectedCountry,
                    dob,
                    binding.emailAddressEt.text.toString(),
                    mailingAddress,
                    selectedMailingCity,
                    selectedMailingCountry,
                    binding.cnicNicopNoEt.text.toString(),
                    binding.cnicNameEt.text.toString(),
                    cnicExpiry,
                    selectedGender,
                    binding.fatherSpouseNameEt.text.toString(),
                    binding.cnicExpiryCheckbox.isChecked,
                    binding.residentialCheckbox.isChecked,
                    cnicIssueDate,
                    mobileNo,
                    selectedMobileOwner,
                    binding.motherMaidenNameEt.text.toString(),
                    HBLPreferenceManager.getCNICBackPath(this),
                    HBLPreferenceManager.getCNICFrontPath(this),
                    selectedReligion,
                    serviceProviderBill,
                    "6357d461-314c-4cf9-8857-d1563012d285",
                    HBLPreferenceManager.getCustomerID(this),
                    accountTypes?.id!!, //implement account type lookup api on new account selection screen
                    binding.educationEt.text.toString(),
                    binding.guardianAddressEt.text.toString(),
                    selectedGuardianCity,
                    selectedGuardianCountry,
                    guardianDob,
                    binding.fatherNameOfGuardianEt.text.toString(),
                    binding.guardianCnicNoEt.text.toString(),
                    guardianCnicExpiry,
                    null,
                    binding.nameOfGuardianEt.text.toString(),
                    idType,
                    guardianRelationShip,
                    mobileNumberLetterByEmployer,
                    mobileUndertakingCloseFamily
                )
            } else {
                SavePersonalInfoDTO(
                    binding.addressEt.text.toString(),
                    selectedCity,
                    selectedCountry,
                    dob,
                    binding.emailAddressEt.text.toString(),
                    mailingAddress,
                    selectedMailingCity,
                    selectedMailingCountry,
                    binding.cnicNicopNoEt.text.toString(),
                    binding.cnicNameEt.text.toString(),
                    cnicExpiry,
                    selectedGender,
                    binding.fatherSpouseNameEt.text.toString(),
                    binding.cnicExpiryCheckbox.isChecked,
                    binding.residentialCheckbox.isChecked,
                    cnicIssueDate,
                    mobileNo,
                    selectedMobileOwner,
                    binding.motherMaidenNameEt.text.toString(),
                    HBLPreferenceManager.getCNICBackPath(this),
                    HBLPreferenceManager.getCNICFrontPath(this),
                    selectedReligion,
                    serviceProviderBill,
                    "6357d461-314c-4cf9-8857-d1563012d285",
                    null,
                    accountTypes?.id!!, //implement account type lookup api on new account selection screen
                    binding.educationEt.text.toString(),
                    binding.guardianAddressEt.text.toString(),
                    selectedGuardianCity,
                    selectedGuardianCountry,
                    guardianDob,
                    binding.fatherNameOfGuardianEt.text.toString(),
                    binding.guardianCnicNoEt.text.toString(),
                    guardianCnicExpiry,
                    null,
                    binding.nameOfGuardianEt.text.toString(),
                    idType,
                    guardianRelationShip,
                    mobileNumberLetterByEmployer,
                    mobileUndertakingCloseFamily
                )
            }

            customerViewModel.savePersonalInfo(savePersonalInfoDTO)
        }

        binding.cnicNicopNoEt.afterTextChanged {
            if (binding.cnicNicopNoEt.length() < 15) {
                binding.cnicNicopNo.error = "Minimum length should be 13"
            } else {
                binding.cnicNicopNo.error = null
                customerViewModel.checkCustomerCnic(
                    CNICCheckDTO(
                        binding.cnicNicopNoEt.text.toString(),
                        3
                    )
                )
            }

            toggleContinueBtn(validateInput())
        }

        binding.guardianCnicNoEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.guardianCnicNoEt.length() < 15) {
                    binding.guardianCnicNo.error = "Minimum length should be 13"
                } else {
                    binding.guardianCnicNo.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.cnicNameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.cnicNameEt.length() < 3) {
                    binding.cnicName.error = "Minimum length should be 3"
                } else {
                    binding.cnicName.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.fatherNameOfGuardianEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.fatherNameOfGuardianEt.length() < 3) {
                    binding.fatherNameOfGuardian.error = "Minimum length should be 3"
                } else {
                    binding.fatherNameOfGuardian.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.nameOfGuardianEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.nameOfGuardianEt.length() < 3) {
                    binding.nameOfGuardian.error = "Minimum length should be 3"
                } else {
                    binding.nameOfGuardian.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.educationEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.educationEt.length() < 3) {
                    binding.education.error = "Minimum length should be 3"
                } else {
                    binding.education.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.fatherSpouseNameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.fatherSpouseNameEt.length() < 3) {
                    binding.fatherSpouseName.error = "Minimum length should be 3"
                } else {
                    binding.fatherSpouseName.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.motherMaidenNameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.motherMaidenNameEt.length() < 3) {
                    binding.motherMaidenName.error = "Minimum length should be 3"
                } else {
                    binding.motherMaidenName.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.emailAddressEt.afterTextChanged {
            if (binding.emailAddressEt.text.toString().isEmpty()) {
                binding.emailAddress.error = "Email is not empty"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressEt.text.toString())
                    .matches()
            ) {
                binding.emailAddress.error = "Email is not valid"
            } else {
                binding.emailAddress.error = null
            }

            toggleContinueBtn(validateInput())
        }

        binding.emailAddressEt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                fieldType = 1
                val customer_id = if (HBLPreferenceManager.getCustomerID(this).isNullOrEmpty()) {
                    null
                } else {
                    HBLPreferenceManager.getCustomerID(this)
                }

                customerViewModel.checkCustomerMobileOrEmail(
                    MobileCheckDTO(
                        customer_id,
                        binding.emailAddressEt.text.toString(),
                        fieldType
                    )
                )
                closeSoftKeyboard(this, binding.emailAddressEt)
                return@OnEditorActionListener true
            }
            false
        })

        binding.emailAddressEt.listener =
            object : CustomKeyBoardControlEditText.KeyboardListener {
                override fun onStateChanged(
                    customKeyBoardControlEditText: CustomKeyBoardControlEditText?,
                    showing: Boolean
                ) {
                    fieldType = 1
                    val customer_id = if (HBLPreferenceManager.getCustomerID(this@CustomerInformationDetailFragment).isNullOrEmpty()) {
                        null
                    } else {
                        HBLPreferenceManager.getCustomerID(this@CustomerInformationDetailFragment)
                    }

                    customerViewModel.checkCustomerMobileOrEmail(
                        MobileCheckDTO(
                            customer_id,
                            binding.emailAddressEt.text.toString(),
                            fieldType
                        )
                    )
                }
            }

        binding.addressEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.addressEt.length() < 3) {
                    binding.address.error = "Minimum length should be 3"
                } else {
                    binding.address.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.guardianAddressEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.guardianAddressEt.length() < 3) {
                    binding.guardianAddress.error = "Minimum length should be 3"
                } else {
                    binding.guardianAddress.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.mailingAddressEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.mailingAddressEt.length() < 3) {
                    binding.mailingAddress.error = "Minimum length should be 3"
                } else {
                    binding.mailingAddress.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.mobileNoEt.afterTextChanged {
                if (!binding.ccPicker.isValidFullNumber) {
                    binding.mobileNumberErrorTv.visibility = VISIBLE
                    binding.mobileNumberTv.setTextColor(
                        ContextCompat.getColor(
                            this@CustomerInformationDetailFragment,
                            R.color.design_default_color_error
                        )
                    )
                } else {
                    binding.mobileNumberErrorTv.text = getString(R.string.mobile_error)
                    binding.mobileNumberErrorTv.visibility = GONE
                    binding.mobileNumberTv.setTextColor(
                        ContextCompat.getColor(
                            this@CustomerInformationDetailFragment,
                            R.color.hbl_main_green
                        )
                    )
                }

                mobileNo = binding.ccPicker.fullNumberWithPlus
                //binding.ccPicker.selectedCountryCodeWithPlus + binding.mobileNoEt.text.toString()
                Log.d("mobile", mobileNo)
                toggleContinueBtn(validateInput())
            }

        binding.mobileNoEt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                fieldType = 2
                val customer_id = if (HBLPreferenceManager.getCustomerID(this).isNullOrEmpty()) {
                    null
                } else {
                    HBLPreferenceManager.getCustomerID(this)
                }

                if (binding.ccPicker.isValidFullNumber) {
                    customerViewModel.checkCustomerMobileOrEmail(
                        MobileCheckDTO(
                            customer_id,
                            mobileNo,
                            fieldType
                        )
                    )
                }

                closeSoftKeyboard(this, binding.mobileNoEt)
                return@OnEditorActionListener true
            }
            false
        })

        binding.mobileNoEt.listener =
            object : CustomKeyBoardControlEditText.KeyboardListener {
                override fun onStateChanged(
                    customKeyBoardControlEditText: CustomKeyBoardControlEditText?,
                    showing: Boolean
                ) {
                    fieldType = 2
                    val customer_id = if (HBLPreferenceManager.getCustomerID(this@CustomerInformationDetailFragment).isNullOrEmpty()) {
                        null
                    } else {
                        HBLPreferenceManager.getCustomerID(this@CustomerInformationDetailFragment)
                    }

                    if (binding.ccPicker.isValidFullNumber) {
                        customerViewModel.checkCustomerMobileOrEmail(
                            MobileCheckDTO(
                                customer_id,
                                mobileNo,
                                fieldType
                            )
                        )
                    }
                }
            }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.mainScroll.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (binding.cnicNameEt.error != null) {
                    binding.cnicNameEt.error = null
                }

                if (binding.cnicNicopNoEt.error != null) {
                    binding.cnicNicopNoEt.error = null
                }

                if (binding.emailAddressEt.error != null) {
                    binding.emailAddressEt.error = null
                }

                if (binding.mobileNoEt.error != null) {
                    binding.mobileNoEt.error = null
                }
            }
        }*/

        /*binding.cnicNicopIssueDateEt.addTextChangedListener {
            if (binding.cnicNicopIssueDateEt.text!!.isEmpty()) {
                binding.cnicNicopIssueDate.error = "CNIC Issue Date should not be empty"
            } else {
                binding.cnicNicopIssueDate.error = null
            }
        }

        binding.cnicExpiryDateEt.addTextChangedListener {
            if (binding.cnicExpiryDateEt.text!!.isEmpty()) {
                binding.cnicExpiryDate.error = "CNIC Expiry Date should not be empty"
            } else {
                binding.cnicExpiryDate.error = null
            }
        }

        binding.dateOfBirthEt.addTextChangedListener {
            if (binding.dateOfBirthEt.text!!.isEmpty()) {
                binding.dateOfBirth.error = "Date of Birth should not be empty"
            } else {
                binding.dateOfBirth.error = null
            }
        }*/


        /*binding.mobileNoEt.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.ccPicker.isValidFullNumber) {
                    binding.mobileNumberErrorTv.visibility = VISIBLE
                    binding.mobileNumberTv.setTextColor(ContextCompat.getColor(this, R.color.design_default_color_error))
                } else {
                    binding.mobileNumberErrorTv.visibility = GONE
                    binding.mobileNumberTv.setTextColor(ContextCompat.getColor(this, R.color.hbl_main_green))
                }

                true
            }

            false
        }*/

        binding.ccPicker.setOnCountryChangeListener {
            binding.mobileNoEt.setText("")
        }

        binding.topHeaderLayout.backBtn.setOnClickListener {
//            findNavController().navigateUp()
            onBackPressed()
        }
    }


    private fun setupProgress() {
        binding.topHeaderLayout.progressbar1.progress = 32
        binding.topHeaderLayout.progressbar2.progress = 0
        binding.topHeaderLayout.progressbar3.progress = 0
        binding.topHeaderLayout.progressbar4.progress = 0
        binding.topHeaderLayout.progress.text = "10%"
        binding.topHeaderLayout.stepTv.text = "Step 1/4"
    }

    private fun validateInput(): Boolean {

        var cnicExpiryLifetimeCheck = if (binding.cnicExpiryCheckbox.isChecked) {
            false
        } else {
            cnicExpiry.isEmpty()
        }

        val emailIsValid = !Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressEt.text.toString())
            .matches()

        val mobileNoCheck = !binding.ccPicker.isValidFullNumber

        var minorCheck = false
        if (isMinor) {
            minorCheck =
                (binding.nameOfGuardianEt.text!!.length < 3 ||
                        guardianRelationShip == 0 || guardianCnicExpiry.isNullOrEmpty()
                        || binding.fatherNameOfGuardianEt.text!!.isEmpty() ||
                        guardianDob.isNullOrEmpty() || selectedGuardianCountry == 0 || selectedGuardianCity == 0
                        || binding.guardianAddressEt.text!!.isEmpty() || binding.guardianCnicNoEt.text!!.isEmpty())
        }

        if (!binding.residentialCheckbox.isChecked) {
            return !(cnicIssueDate.isEmpty() ||
                    cnicExpiryLifetimeCheck ||
                    dob.isEmpty() ||
                    binding.cnicNicopNoEt.text!!.length < 15 ||
                    binding.cnicNameEt.text!!.length < 3 ||
                    binding.fatherSpouseNameEt.text!!.length < 3 ||
                    binding.motherMaidenNameEt.text!!.length < 3 ||
                    mobileNoCheck ||
                    binding.addressEt.text!!.length < 3 ||
                    selectedMobileOwner == 0 ||
                    selectedGender == 0 ||
                    selectedReligion == 0 ||
                    selectedCountry == 0 ||
                    selectedCity == 0 ||
                    binding.emailAddressEt.text!!.isEmpty() ||
                    emailIsValid ||
                    selectedMailingCountry == 0 ||
                    selectedMailingCity == 0 ||
                    binding.mailingAddressEt.text!!.length < 3 || binding.educationEt.text!!.length < 3
                    || minorCheck || binding.emailAddress.error != null ||
                    binding.cnicNicopNo.error != null || binding.mobileNumberErrorTv.visibility == VISIBLE)
        }

        return !(cnicIssueDate.isEmpty() ||
                cnicExpiryLifetimeCheck ||
                dob.isEmpty() ||
                binding.cnicNicopNoEt.text!!.length < 15 ||
                binding.cnicNameEt.text!!.length < 3 ||
                binding.fatherSpouseNameEt.text!!.length < 3 ||
                binding.motherMaidenNameEt.text!!.length < 3 ||
                mobileNoCheck ||
                binding.addressEt.text!!.length < 3 ||
                selectedMobileOwner == 0 ||
                selectedGender == 0 ||
                selectedReligion == 0 ||
                selectedCountry == 0 ||
                selectedCity == 0 ||
                binding.emailAddressEt.text!!.isEmpty() ||
                emailIsValid || binding.educationEt.text!!.length < 3 || minorCheck ||
                binding.emailAddress.error != null ||
                binding.cnicNicopNo.error != null
                || binding.mobileNumberErrorTv.visibility == VISIBLE)
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.sendOtpBtn.isEnabled = b
    }

    private fun showHideMailingAddress(show: Boolean) {
        binding.mailingCountrySpinner.setSelection(0)
        binding.mailingCitySpinner.setSelection(0)
        mailingAddress = ""
        binding.mailingAddressEt.text!!.clear()

        if (show) {
            binding.mailingCityLayout.visibility = VISIBLE
            binding.mailingCountryLayout.visibility = VISIBLE
            binding.mailingCityTv.visibility = VISIBLE
            binding.mailingCountryTv.visibility = VISIBLE
            binding.mailingAddress.visibility = VISIBLE
            binding.mailingCityView.visibility = VISIBLE
            binding.mailingCountryView.visibility = VISIBLE
        } else {
            binding.mailingCityLayout.visibility = GONE
            binding.mailingCountryLayout.visibility = GONE
            binding.mailingCityTv.visibility = GONE
            binding.mailingCountryTv.visibility = GONE
            binding.mailingAddress.visibility = GONE
            binding.mailingCityView.visibility = GONE
            binding.mailingCountryView.visibility = GONE
        }
    }
}