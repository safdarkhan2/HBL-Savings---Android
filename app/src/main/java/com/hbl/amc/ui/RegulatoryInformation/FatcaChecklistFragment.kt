package com.hbl.amc.ui.RegulatoryInformation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentFatcaChecklistBinding
import com.hbl.amc.domain.RequestDTO.SaveFatcaInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.City
import com.hbl.amc.domain.model.DocumentsTypesResult
import com.hbl.amc.domain.model.FatcaCountryList
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Disclaimers.DisclaimersViewModel
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.*
import com.hbl.amc.utils.AppUtils.changeDisplayDateFormatToUTC
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showAlertDialog
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FatcaChecklistFragment : Fragment() {

    private var w9BenformID: String? = null
    private var w8BenformID: String? = null
    private var uploadDocType: String? = null
    private var phoneNo = ""
    private var _binding: FragmentFatcaChecklistBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val regulatoryViewModel by viewModel<RegulatoryInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val genericViewModel by viewModel<GenericViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var fatcaCountriesList: List<FatcaCountryList>? = null
    var citiesList: List<City>? = null

    var selectedFatcaCountry1: Int? = null
    var selectedFatcaCountry2: Int? = null
    var selectedFatcaCountry3: Int? = null
    var selectedCountry: Int = 0
    var selectedCountryOfBirth: Int = 0
    var selectedCity: Int = 0

    var submissionDate = ""
    var resumeFatcaCity = 0
    var allDocsTypes: List<DocumentsTypesResult>? = null

    private val disclaimersViewModel by viewModel<DisclaimersViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private var isTransferFundsChecked: Boolean = false
    private var isForeignCitizenChecked: Boolean = false
    private var isMultinationalChecked: Boolean = false
    private var isOverseasCareOfChecked: Boolean = false
    private var isPowerOfAttorneyChecked: Boolean = false
    private var isUsTaxResidenceChecked: Boolean = false
    private var isW8W9FormChecked: Boolean = false
    var editMode = false
    var isUSSelected = false

    lateinit var activ : RegulatoryInfoActivity

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

        if(activ.intent.hasExtra(STEP_ID))
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

        HBLPreferenceManager.getDocumentsTypesResponse(requireContext())?.let { docTypeRes ->
            docTypeRes.result.also { allDocsTypes = it }
        }

        if (_binding == null) {
            _binding = FragmentFatcaChecklistBinding.inflate(inflater, container, false)

            initViews()

            regulatoryViewModel.getFatcaInfoLookups(HBLPreferenceManager.getToken(requireContext()))
        }

        return binding.root
    }

    fun initViewModel() {
        val fatcaCountries: ArrayList<String> = ArrayList()

        regulatoryViewModel.fatcaInfoLookUpsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        fatcaCountriesList = it?.response?.result?.countryList

                        fatcaCountries.add(getString(R.string.select))


                        fatcaCountriesList?.forEach { country ->
                            fatcaCountries.add(country.name)
                        }

                        setUpFatcaCountrySpinner(fatcaCountries = fatcaCountries)

                        regulatoryViewModel.resumeFatca(
                            HBLPreferenceManager.getToken(requireContext()),
                            HBLPreferenceManager.getCustomerID(requireContext())
                        )
                    } else {
                        it?.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                showAlertDialog(
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
                                showAlertDialog(
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
                    it?.error?.message?.let { it1 -> Log.d("fatca lookups info error", it1) }
                }
            }
        })

        regulatoryViewModel.saveFatcaInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    val user = it?.response?.let { res -> res.result }
                    Log.d("test_ans", it?.response.toString())
                    //save user in shared prefs
                    if (it?.response?.status == "success") {
                        if (editMode) {
                            findNavController().navigateUp()
                        } else {
                            findNavController().navigate(R.id.action_fatcaChecklistFragment_to_crsFragment)
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
//                    Toast.makeText(
//                        this.context,
//                        "FATCA Info sent to server successfully",
//                        Toast.LENGTH_LONG
//                    )
//                        .show()

                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("FATCA save info error", it1) }
                }
            }
        })

        genericViewModel.allCitiesResponse.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    citiesList = it?.response?.result?.cities
                    val cities: ArrayList<String> = ArrayList()
                    cities.add(getString(R.string.select))

                    citiesList?.forEach { city ->
                        cities.add(city.name)
                    }
                    setUpCitiesSpinner(cities = cities)

                    if (resumeFatcaCity > 0) {
                        val city = citiesList?.find { ci -> ci.id == resumeFatcaCity }
                        city?.let { cn ->
                            binding.citySpinner.setSelection(
                                cities.indexOf(cn.name)
                            )
                        }
                    }

                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("city fetch error", it1) }
                }
            }
        })

        regulatoryViewModel.resumeFatcaApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.result?.let { fatcaRes ->
                        binding.fullNameInputEdittext.setText(fatcaRes.customerName)

                        if (AppUtils.checkUTC(fatcaRes.w8BenOrW9SubmittedDate)) {
                            binding.submissionDateEt.setText(
                                AppUtils.changeUTCDateFormatToDisplay(
                                    fatcaRes.w8BenOrW9SubmittedDate
                                )
                            )
                        } else {
                            binding.submissionDateEt.setText(
                                AppUtils.changeDateFormatToDisplay(
                                    fatcaRes.w8BenOrW9SubmittedDate
                                )
                            )
                        }

                        fatcaRes.w8BenOrW9SubmittedDate?.let { sdt ->
                            submissionDate = sdt
                        }

                        if (fatcaRes.isMultiNational) {
                            binding.multipleNationalitiesRadiogroup.check(binding.multipleNationalitiesYes.id)

                            binding.nationalitiesLayout.visibility = VISIBLE

                            val n1 = fatcaCountriesList?.find { nat1 -> nat1.id == fatcaRes.nat1 }
                            n1?.let { nat1 ->
                                binding.nationality1Spinner.setSelection(
                                    fatcaCountries.indexOf(nat1.name)
                                )
                            }

                            val n2 = fatcaCountriesList?.find { nat2 -> nat2.id == fatcaRes.nat2 }
                            n2?.let { nat2 ->
                                binding.nationality2Spinner.setSelection(
                                    fatcaCountries.indexOf(nat2.name)
                                )
                            }

                            val n3 = fatcaCountriesList?.find { nat3 -> nat3.id == fatcaRes.nat3 }
                            n3?.let { nat3 ->
                                binding.nationality1Spinner.setSelection(
                                    fatcaCountries.indexOf(nat3.name)
                                )
                            }

                        } else {
                            binding.multipleNationalitiesRadiogroup.check(binding.multipleNationalitiesNo.id)
                            binding.nationalitiesLayout.visibility = GONE
                        }

                        if (fatcaRes.IsUSResident) {
                            binding.greenCardRadiogroup.check(binding.greenCardYes.id)
                            binding.greenCardInputLayout.visibility = VISIBLE
                            fatcaRes.greenCardNo?.let { gnc ->
                                binding.greenCardInputEdittext.setText(gnc)
                            }
                        } else {
                            binding.greenCardRadiogroup.check(binding.greenCardNo.id)
                            binding.greenCardInputLayout.visibility = GONE
                        }

                        if (fatcaRes.isForiegnCitizen) {
                            binding.foreignCitizenshipRadiogroup.check(binding.foreignCitizenshipYes.id)
                        } else {
                            binding.foreignCitizenshipRadiogroup.check(binding.foreignCitizenshipNo.id)
                        }

                        if (fatcaRes.isOverseasCareOf) {
                            binding.overseasContactInfoRadiogroup.check(binding.overseasContactInfoYes.id)
                            binding.careOfContactInfoLayout.visibility = VISIBLE

                            fatcaRes.residentialAddess?.let { rsa ->
                                binding.addressInputEdittext.setText(rsa)
                            }

                            val country =
                                fatcaCountriesList?.find { nat1 -> nat1.id == fatcaRes.countryId }
                            country?.let { cn ->
                                resumeFatcaCity = fatcaRes.cityId
                                binding.countrySpinner.setSelection(
                                    fatcaCountries.indexOf(cn.name)
                                )
                            }

                            fatcaRes.phoneNo?.let { phn ->
                                binding.ccPicker.fullNumber = phn
                            }

                        } else {
                            binding.overseasContactInfoRadiogroup.check(binding.overseasContactInfoNo.id)
                            binding.careOfContactInfoLayout.visibility = GONE
                        }

                        if (fatcaRes.isUSTaxResidence) {
                            binding.usTaxResidentRadiogroup.check(binding.usTaxResidentYes.id)
                        } else {
                            binding.usTaxResidentRadiogroup.check(binding.usTaxResidentNo.id)
                        }

                        if (fatcaRes.isPowerOfAttorney) {
                            binding.powerOfAttorneyRadiogroup.check(binding.powerOfAttorneyYes.id)
                        } else {
                            binding.powerOfAttorneyRadiogroup.check(binding.powerOfAttorneyNo.id)
                        }

                        if (fatcaRes.isAccountTransfer) {
                            binding.transferFundsRadiogroup.check(binding.transferFundsYes.id)
                        } else {
                            binding.transferFundsRadiogroup.check(binding.transferFundsNo.id)
                        }

                        if (fatcaRes.isW8W9Form) {
                            binding.w8benFormsRadiogroup.check(binding.w8benFormsYes.id)
                        } else {
                            binding.w8benFormsRadiogroup.check(binding.w8benFormsNo.id)
                        }

                    } ?: run {
                        it?.response?.message?.let { msg ->
                            val res = it.response
                            if (res.statusTitle == "Not Authorized" || res.messageCode == "404" || res.messageCode == "403") {
                                showAlertDialog(
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
                                showAlertDialog(
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
                    it?.error?.message?.let { it1 -> Log.d("resume fatca error", it1) }
                }
            }
        })

        regulatoryViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.downloadDocumentApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        if (it.response?.result?.extension.equals("PDF", true)) {
                            val uri = AppUtils.getPDFURI(requireContext(), it.response?.result.documentBase64)
                            AppUtils.viewPDF(requireContext(), layoutInflater, uri!!)
                        } else if (it.response?.result?.extension.equals("docx", true)) {
                            val uri =
                                AppUtils.getWordDocURI(requireContext(), it.response?.result.documentBase64)
                            AppUtils.viewWordDoc(requireContext(), layoutInflater, uri!!)
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        disclaimersViewModel.uploadDocumentApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.result?.let { res ->
//                        if (res.isUploaded) {
                        if (res.documentName == W8BENFORM) {
                            w8BenformID = res.id

                            showAlertDialog(
                                requireContext(),
                                layoutInflater,
                                getString(R.string.success),
                                "$W8BENFORM uploaded successfully!",
                                R.drawable.ic_check_yellow,
                                getString(R.string.ok)
                            ) {}
                        } else if (res.documentName == W9BENFORM) {
                            w9BenformID = res.id

                            showAlertDialog(
                                requireContext(),
                                layoutInflater,
                                getString(R.string.success),
                                "$W9BENFORM uploaded successfully!",
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
                            showAlertDialog(
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
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("upload $uploadDocType error", it1) }
                }
            }
        })
    }

    private fun setUpFatcaCountrySpinner(fatcaCountries: List<String>) {
        binding.nationality1Spinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fatcaCountries)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fatcaCountriesList?.let {
                        selectedFatcaCountry1 = if (p2 > 0) {
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

        binding.nationality2Spinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fatcaCountries)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fatcaCountriesList?.let {
                        selectedFatcaCountry2 = if (p2 > 0) {
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

        binding.nationality3Spinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fatcaCountries)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fatcaCountriesList?.let {
                        selectedFatcaCountry3 = if (p2 > 0) {
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

        binding.countryBirthSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fatcaCountries)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fatcaCountriesList?.let {
                        selectedCountryOfBirth = if (p2 > 0) {
                            it[p2 - 1].id

                        } else {
                            0
                        }

                        if (p2 > 0) {
                            isUSSelected = fatcaCountriesList!![p2 - 1].name == "United States Of America"
                            toggleForms()
                        }
                        else
                        {
                            isUSSelected = false
                            toggleForms()
                        }


                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }

        binding.countrySpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fatcaCountries)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fatcaCountriesList?.let {
                        selectedCountry = if (p2 > 0) {
                            it[p2 - 1].id

                        } else {
                            0
                        }

                        if (p2 > 0) {
                            genericViewModel.getCitiesData(it[p2 - 1].id)
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
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, cities)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    citiesList?.let {
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

    private fun initViews() {

        binding.appBar.title.text = getString(R.string.regulatory_information)

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        if (editMode) {
            binding.appBar.stepTv.visibility = GONE
            binding.appBar.progressbarContainer.visibility = GONE
            binding.statusLayout.visibility = INVISIBLE
        }

        setupProgress()

        binding.kycButton.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_fatcaChecklistFragment_to_KYCFragment, bundleOf(
                        Pair(EDIT_MODE, true)
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        binding.rpqButton.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_fatcaChecklistFragment_to_riskProfileQuestionnaireFragment,
                    bundleOf(
                        Pair(EDIT_MODE, true)
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        binding.ccPicker.registerCarrierNumberEditText(binding.phoneNumberInputEdittext)
        binding.ccPicker.setCountryForNameCode("PK")
        binding.ccPicker.setDialogCornerRaius(20f)
        binding.ccPicker.setNumberAutoFormattingEnabled(true)

        binding.submissionDateLayout.setEndIconOnClickListener {
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

//                    submissionDate = "$actualDay-$actualMonth-$year"
                    binding.submissionDateEt.setText("$actualDay/$actualMonth/$year")
                    submissionDate =
                        changeDisplayDateFormatToUTC(binding.submissionDateEt.text.toString())
                    toggleContinueBtn(validateInput())
                }

            datePickerFragment?.type = FUTURE_DATE_DISABLE
            datePickerFragment!!.show(childFragmentManager, "datePicker")

            binding.submissionDateLayout.setTitleColour(requireContext())
        }

        binding.continueBtn.setOnClickListener {
            isTransferFundsChecked = binding.transferFundsYes.isChecked
            isForeignCitizenChecked = binding.foreignCitizenshipYes.isChecked
            isMultinationalChecked = binding.multipleNationalitiesYes.isChecked
            isOverseasCareOfChecked = binding.overseasContactInfoYes.isChecked
            isPowerOfAttorneyChecked = binding.powerOfAttorneyYes.isChecked
            isUsTaxResidenceChecked = binding.usTaxResidentYes.isChecked
            isW8W9FormChecked = binding.w8benFormsYes.isChecked


            val saveFatcaInfoDTO = SaveFatcaInfoDTO(
                submissionDate,
                selectedCity,
                selectedCountry,
                selectedCountryOfBirth, //add as per clients feedback
                binding.fullNameInputEdittext.text.toString(),
                binding.greenCardInputEdittext.text.toString(),
                isTransferFundsChecked,
                isForeignCitizenChecked,
                isMultinationalChecked,
                isOverseasCareOfChecked,
                isPowerOfAttorneyChecked,
                isUsTaxResidenceChecked,
                isW8W9FormChecked,
                selectedFatcaCountry1,
                selectedFatcaCountry2,
                selectedFatcaCountry3,
                phoneNo,
                binding.addressInputEdittext.text.toString(),
                if (isW8W9FormChecked && isUSSelected) {
                    w9BenformID
                } else if (isW8W9FormChecked) {
                    w8BenformID
                } else {
                    null
                },
                HBLPreferenceManager.getCustomerID(requireContext()),
                "7B3D5EC2-4765-4286-88F8-6CCBF08ED4AD"
            )
            regulatoryViewModel.saveFatcaInfo(
                HBLPreferenceManager.getToken(requireContext()),
                saveFatcaInfoDTO
            )
        }

        binding.appBar.backBtn.setOnClickListener {
            if (activ.intent.hasExtra(STEP_ID)) {
                activ.finish()
            } else {
                findNavController().navigateUp()
            }
        }


        binding.greenCardInputEdittext.setupEditText(requireContext(), binding.greenCardInputLayout)
        binding.fullNameInputEdittext.setupEditText(requireContext(), binding.fullNameInputLayout)
        binding.addressInputEdittext.setupEditText(requireContext(), binding.addressInputLayout)
        /*binding.phoneNumberInputEdittext.setupEditText(
            requireContext(),
            binding.phoneNumberInputLayout
        )*/

        binding.phoneNumberInputEdittext.addTextChangedListener {
            if (!binding.ccPicker.isValidFullNumber) {
                binding.mobileNumberErrorTv.visibility = VISIBLE
                binding.phoneNoTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.design_default_color_error
                    )
                )
            } else {
                binding.mobileNumberErrorTv.visibility = GONE
                binding.phoneNoTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hbl_main_green
                    )
                )
            }

            phoneNo = binding.ccPicker.fullNumberWithPlus
            //binding.ccPicker.selectedCountryCodeWithPlus + binding.mobileNoEt.text.toString()
            Log.d("mobile", phoneNo)
            toggleContinueBtn(validateInput())
        }

        binding.fullNameInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.fullNameInputEdittext.length() < 3) {
                    binding.fullNameInputEdittext.error = "Minimum length should be 3"
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.greenCardInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.greenCardInputEdittext.length() < 3) {
                    binding.greenCardInputEdittext.error = "Minimum length should be 3"
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.addressInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.addressInputEdittext.length() < 3) {
                    binding.addressInputEdittext.error = "Minimum length should be 3"
                }
                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.phoneNumberInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.phoneNumberInputEdittext.length() < 11) {
                    binding.phoneNumberInputEdittext.error = "Minimum length should be 11"
                }
                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.multipleNationalitiesRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.multiple_nationalities_yes) {
                binding.nationalitiesLayout.visibility = VISIBLE
            } else if (checkedId == R.id.multiple_nationalities_no) {
                binding.nationalitiesLayout.visibility = GONE
                binding.nationality1Spinner.setSelection(0)
                binding.nationality2Spinner.setSelection(0)
                binding.nationality3Spinner.setSelection(0)
            }
            toggleContinueBtn(validateInput())
        }

        binding.greenCardRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.green_card_yes) {
                binding.greenCardInputLayout.visibility = VISIBLE
            } else if (checkedId == R.id.green_card_no) {
                binding.greenCardInputLayout.visibility = GONE
            }
            toggleContinueBtn(validateInput())
        }

        binding.overseasContactInfoRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.overseas_contact_info_yes) {
                binding.careOfContactInfoLayout.visibility = VISIBLE
            } else if (checkedId == R.id.overseas_contact_info_no) {
                binding.careOfContactInfoLayout.visibility = GONE
            }
            toggleContinueBtn(validateInput())
        }

        binding.usTaxResidentRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            toggleContinueBtn(validateInput())
        }


        binding.foreignCitizenshipRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            toggleContinueBtn(validateInput())
        }

        binding.powerOfAttorneyRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            toggleContinueBtn(validateInput())
        }

        binding.transferFundsRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            toggleContinueBtn(validateInput())
        }

        binding.w8benFormsRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.w8ben_forms_yes) {
                toggleForms()
            } else if (checkedId == R.id.w8ben_forms_no) {
                binding.w9Upload.visibility = GONE
                binding.w8benUpload.visibility = GONE
                binding.submissionDateLayout.visibility = GONE
            }
            toggleContinueBtn(validateInput())
        }

        binding.w8benDownloadBtn.setOnClickListener {
            val docType = allDocsTypes?.find { dc -> dc.name == W8BENFORM }
            docType?.let { it1 ->
                disclaimersViewModel.getSampleDocument(
                    it1.id
                )
            }
        }

        binding.w9DownloadBtn.setOnClickListener {
            val docType = allDocsTypes?.find { dc -> dc.name == W9BENFORM }
            docType?.let { it1 ->
                disclaimersViewModel.getSampleDocument(
                    it1.id
                )
            }
        }

        binding.w8benUploadBtn.setOnClickListener {
            uploadDocType = W8BENFORM
            ImagePicker.with(requireActivity())
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.w9UploadBtn.setOnClickListener {
            uploadDocType = W9BENFORM
            ImagePicker.with(requireActivity())
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!

                val file = FileUtil.getTempFile(requireContext(), uri)
                Log.d("file path", file?.name + "-----------" + file?.path)

                allDocsTypes?.let { ad ->
                    val doc = ad.find { dc -> dc.name == uploadDocType }
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
                                HBLPreferenceManager.getCustomerID(requireContext())
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

    fun toggleForms()
    {
        if(isUSSelected)
        {
            binding.w9Upload.visibility = VISIBLE
            binding.w8benUpload.visibility = GONE
            binding.submissionDateLayout.visibility = VISIBLE
        }
        else
        {
            binding.w8benUpload.visibility = VISIBLE
            binding.w9Upload.visibility = GONE
            binding.submissionDateLayout.visibility = VISIBLE
        }
    }

    private fun validateInput(): Boolean {
        val mobileNoCheck = !binding.ccPicker.isValidFullNumber

        return !(binding.fullNameInputEdittext.length() < 3 ||
                binding.multipleNationalitiesRadiogroup.checkedRadioButtonId < 0 ||
                binding.greenCardRadiogroup.checkedRadioButtonId < 0 ||
                binding.usTaxResidentRadiogroup.checkedRadioButtonId < 0 ||
                binding.overseasContactInfoRadiogroup.checkedRadioButtonId < 0 ||
                binding.foreignCitizenshipRadiogroup.checkedRadioButtonId < 0 ||
                binding.powerOfAttorneyRadiogroup.checkedRadioButtonId < 0 ||
                binding.transferFundsRadiogroup.checkedRadioButtonId < 0 ||
                binding.w8benFormsRadiogroup.checkedRadioButtonId < 0 ||
                (binding.greenCardInputLayout.isVisible && binding.greenCardInputEdittext.length() < 3) ||
                (binding.careOfContactInfoLayout.isVisible && (binding.addressInputEdittext.length() < 3 || mobileNoCheck || selectedCountry < 1 || selectedCity < 1)) ||
                (binding.nationalitiesLayout.isVisible && (selectedFatcaCountry1 == null && selectedFatcaCountry2 == null && selectedFatcaCountry3 == null)) ||
                selectedCountryOfBirth == 0)
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 75
        binding.appBar.progressbar3.progress = 0
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "40%"
        binding.appBar.stepTv.text = "Step 2/4"
    }
}