package com.hbl.amc.ui.RegulatoryInformation

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
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentCrsBinding
import com.hbl.amc.domain.RequestDTO.SaveCrsInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.City
import com.hbl.amc.domain.model.GenericObject
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.CustomerInformation.ProfessionalInformationFragment
import com.hbl.amc.ui.EDIT_MODE
import com.hbl.amc.ui.GenericViewModel
import com.hbl.amc.ui.STEP_ID
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.productInformation.ProductSelectionActivity
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.hbl.amc.utils.setupEditText
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class CRSFragment : Fragment(), KoinComponent {

    private var _binding: FragmentCrsBinding? = null

    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val regulatoryViewModel by viewModel<RegulatoryInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val genericViewModel by viewModel<GenericViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var crsCountriesList: List<GenericObject>? = null
    var cityList: List<City>? = null
    var crsReasonsList: List<GenericObject>? = null

    var selectedCrsCountry: Int = 0
    var selectedCity: Int = 0
    var selectedCrsReason = 0

    lateinit var activ: RegulatoryInfoActivity

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

        if (activ.intent.hasExtra(STEP_ID)) {
            requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initViewModel()

        if (_binding == null) {
            _binding = FragmentCrsBinding.inflate(inflater, container, false)

            initViews()

            regulatoryViewModel.getCrsInfoLookups(HBLPreferenceManager.getToken(requireContext()))
        }

        return binding.root
    }

    private fun initViewModel() {
        val crsCountries: ArrayList<String> = ArrayList()
        val crsReasons: ArrayList<String> = ArrayList()
        val cities: ArrayList<String> = ArrayList()

        regulatoryViewModel.crsInfoLookUpsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        crsCountriesList = it.response?.result?.crsCountries
                        crsReasonsList = it.response?.result?.crsReasons

                        crsCountries.add(getString(R.string.select))
                        crsReasons.add(getString(R.string.select))


                        crsCountriesList?.forEach { country ->
                            crsCountries.add(country.name)
                        }

                        crsReasonsList?.forEach { reason ->
                            crsReasons.add(reason.name)
                        }

                        setUpCrsCountrySpinner(crsCountries = crsCountries)
                        setUpCrsReasonSpinner(crsReasons = crsReasons)

                        regulatoryViewModel.resumeCRS(
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
                    it.error?.message?.let { it1 ->
                        Log.d("get crs info error", it1)
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

        genericViewModel.allCitiesResponse.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    cityList = it?.response?.result?.cities

                    cities.clear()
                    cities.add(getString(R.string.select))

                    cityList?.forEach { city ->
                        cities.add(city.name)
                    }

                    setUpCitiesSpinner(cities = cities)

                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("customer info error", it1) }
                }
            }
        })

        regulatoryViewModel.saveCrsInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
//                    val user = it.response?.let { res -> res.result }
//                    Log.d("test_ans", it.response.toString())
                    //save user in shared prefs
//                    findNavController().navigate(R.id.productSelectionFragment)
                    if (it?.response?.status == "success") {
                        val intent = Intent(requireContext(), ProductSelectionActivity::class.java)
                        startActivity(intent)
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
                    it.error?.message?.let { it1 -> Log.d("CRS save info error", it1) }
                }
            }
        })

        regulatoryViewModel.resumeCRSApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.let { response ->
                        response.result?.let { rsrs ->
                            val country = crsCountriesList?.find { cn -> cn.id == rsrs.countryId }
                            country?.let { cn ->
                                binding.taxResidenceCountrySpinner.setSelection(
                                    crsCountries.indexOf(cn.name)
                                )
                            }

                            val city = cityList?.find { cn -> cn.id == rsrs.crsCityId }
                            city?.let { cn ->
                                binding.citySpinner.setSelection(
                                    cities.indexOf(cn.name)
                                )
                            }

                            //check is coming from server
                            if (rsrs.reasonId <= 0) {
//                                binding.tinNoInputLayout.visibility = VISIBLE
//                                binding.reasonSpinnerLayout.visibility = GONE
                                binding.tinNoRadiogroup.check(binding.tinNoYes.id)
                                binding.tinNoTextedit.setText(rsrs.tinNo)
                            } else {
//                                binding.tinNoInputLayout.visibility = GONE
//                                binding.reasonSpinnerLayout.visibility = VISIBLE
                                binding.tinNoRadiogroup.check(binding.tinNoNo.id)
                                val reason = crsReasonsList?.find { rs -> rs.id == rsrs.reasonId }
                                reason?.let { re ->
                                    binding.reasonsSpinner.setSelection(
                                        crsReasons.indexOf(re.name)
                                    )
                                }
                            }
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
                    it.error?.message?.let { it1 -> Log.d("resume CRS info error", it1) }
                }
            }
        })

        regulatoryViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })
    }

    private fun setUpCrsCountrySpinner(crsCountries: List<String>) {
        binding.taxResidenceCountrySpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, crsCountries)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    crsCountriesList?.let {
                        selectedCrsCountry = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        if (p2 > 0) {
                            genericViewModel.getCitiesData(it[p2 - 1].id)
                            if (it[p2 - 1].id == 1 || it[p2 - 1].id == 5) {
                                binding.citySpinnerLayout.visibility = GONE
                                binding.tinNoTv.visibility = GONE
                                binding.tinNoRadiogroup.clearCheck()
                                binding.tinNoRadiogroup.visibility = GONE
                                binding.tinNoInputLayout.visibility = GONE
                                binding.reasonSpinnerLayout.visibility = GONE
                                selectedCrsReason = 0
                                binding.tinNoTextedit.text = null
                            } else {
                                binding.citySpinnerLayout.visibility = VISIBLE
                                binding.tinNoTv.visibility = VISIBLE
                                binding.tinNoRadiogroup.visibility = VISIBLE
                            }
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

    private fun setUpCrsReasonSpinner(crsReasons: List<String>) {
        binding.reasonsSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, crsReasons)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    crsReasonsList?.let {
                        selectedCrsReason = if (p2 > 0) {
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

        setupProgress()

        binding.kycButton.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_crsFragment_to_KYCFragment, bundleOf(
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
                    R.id.action_crsFragment_to_riskProfileQuestionnaireFragment, bundleOf(
                        Pair(EDIT_MODE, true)
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        binding.fatcaButton.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_crsFragment_to_fatcaChecklistFragment, bundleOf(
                        Pair(EDIT_MODE, true)
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        binding.continueBtn.setOnClickListener {
            val saveCrsInfoDTO = SaveCrsInfoDTO(
                selectedCrsCountry,
                selectedCrsReason,
                binding.tinNoTextedit.text.toString(),
                HBLPreferenceManager.getCustomerID(requireContext()),
                "70F43914-04CE-485C-AE9E-578CF3A36700", selectedCity
            )
            regulatoryViewModel.saveCrsInfo(
                HBLPreferenceManager.getToken(requireContext()),
                saveCrsInfoDTO
            )
        }

        binding.appBar.backBtn.setOnClickListener {
            if (activ.intent.hasExtra(STEP_ID)) {
                activ.finish()
            } else {
                findNavController().navigateUp()
            }
        }

        binding.tinNoTextedit.setupEditText(requireContext(), binding.tinNoInputLayout)

        binding.tinNoTextedit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.tinNoTextedit.length() < 3) {
                    binding.tinNoInputLayout.error = "Minimum length should be 3"
                } else {
                    binding.tinNoInputLayout.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.tinNoRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.tin_no_no) {
                binding.reasonSpinnerLayout.visibility = VISIBLE
                binding.tinNoInputLayout.visibility = GONE
            } else if (checkedId == R.id.tin_no_yes) {
                binding.tinNoInputLayout.visibility = VISIBLE
                binding.reasonSpinnerLayout.visibility = GONE
            }
        }

    }

    private fun validateInput(): Boolean {
        if (selectedCrsCountry == 0 ||
            (binding.citySpinnerLayout.isVisible && selectedCity == 0) ||
            (binding.tinNoRadiogroup.isVisible &&binding.tinNoRadiogroup.checkedRadioButtonId < 0) ||
            (binding.tinNoInputLayout.isVisible && binding.tinNoTextedit.text!!.length < 3) ||
            (binding.reasonSpinnerLayout.isVisible && selectedCrsReason <= 0)
        ) {
            return false
        } else {
            return true
        }
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 0
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "50%"
        binding.appBar.stepTv.text = "Step 2/4"
    }
}