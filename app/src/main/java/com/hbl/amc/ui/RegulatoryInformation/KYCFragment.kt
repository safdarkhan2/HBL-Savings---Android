package com.hbl.amc.ui.RegulatoryInformation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentKycBinding
import com.hbl.amc.domain.RequestDTO.SaveKYCInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.GenericObject
import com.hbl.amc.domain.model.NextOfKin
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.*
import com.hbl.amc.utils.AppUtils.afterTextChanged
import com.hbl.amc.utils.AppUtils.changeUTCDateFormatToDisplay
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showAlertDialog
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class KYCFragment : Fragment() {

    private var selectedResidentStatus = 0
    private var selectedUBORelation : Int? = null
    private var _binding: FragmentKycBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var nextOfKinList : ArrayList<NextOfKin>
    lateinit var nextOfKinAdapter : NextOfKinAdapter
    var residentialStatusList : List<GenericObject>? = null
    var idTypeList : List<GenericObject>? = null
    var uboRelationshipList : List<GenericObject>? = null
    var financialInstitutionRefused : Boolean? = null
    var declarionBenOwner : Boolean? = null
    var reason = ""
    var deleteNextOfKin : NextOfKin? = null
    var editMode = false
    var benDob : String? = null
    var benCnicExpiry : String? = null
    var benCnicIssueDate : String? = null

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val regulatoryInfoViewModel by viewModel<RegulatoryInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(EDIT_MODE)) {
                editMode = it[EDIT_MODE] as Boolean
            }
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                // block backward navigation from this section
            }
        }

        if(!editMode)
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

//        if (_binding == null) {
            _binding = FragmentKycBinding.inflate(inflater, container, false)

            nextOfKinList = ArrayList()

            initViews()
            setupProgress()

            regulatoryInfoViewModel.getKYCInfoLookups(HBLPreferenceManager.getToken(requireContext()))
//        }

        return binding.root
    }

    private fun initViewModel() {
        val resStatusList = ArrayList<String>()
        val idTpList = ArrayList<String>()
        val relationList = ArrayList<String>()

        regulatoryInfoViewModel.kycInfoLookUpsApi.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        residentialStatusList = it.response?.result?.residentList
                        idTypeList = it.response?.result?.identityTypeList
                        uboRelationshipList = it.response?.result?.uboRelationshipList

                        resStatusList.add(getString(R.string.select))
                        residentialStatusList?.forEach { resident ->
                            resStatusList.add(resident.name)
                        }

                        idTpList.add(getString(R.string.select))
                        idTypeList?.forEach { idType ->
                            idTpList.add(idType.name)
                        }

                        relationList.add(getString(R.string.select))
                        uboRelationshipList?.forEach { relation ->
                            relationList.add(relation.name)
                        }

                        setUpResidentialSpinner(resStatusList)
                        setUpRelationSpinner(relationList)

                        if (selectedResidentStatus > 0) {
                            val resident =
                                residentialStatusList?.find { resi -> resi.id == selectedResidentStatus }
                            binding.residentialStatusSpinner.setSelection(
                                resStatusList.indexOf(resident?.name)
                            )
                        }

                        if (selectedUBORelation != null) {
                            val relatn =
                                uboRelationshipList?.find { resi -> resi.id == selectedUBORelation }
                            binding.benRelationshipSpinner.setSelection(
                                relationList.indexOf(relatn?.name)
                            )
                        }

                        regulatoryInfoViewModel.resumeKYC(
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
                    Log.d("kyc lookup error", it.error!!.localizedMessage)
                }
            }
        })

        regulatoryInfoViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        regulatoryInfoViewModel.saveKycInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        it.response.result.nextOfKinList
                        //data received
                        if (editMode) {
                            findNavController().navigateUp()
                        } else {
                            findNavController().navigate(R.id.action_KYCFragment_to_riskProfileQuestionnaireFragment)
                        }
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
                    //handle error here
                }
            }
        })

        regulatoryInfoViewModel.resumeKycApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        it?.response.result.let { kycRes ->
                            if (kycRes.residentStatusId > 0) {
                                val resident =
                                    residentialStatusList?.find { resi -> resi.id == kycRes.residentStatusId }
                                binding.residentialStatusSpinner.setSelection(
                                    resStatusList.indexOf(resident?.name)
                                )
                            }

//                            if (kycRes.everRefusedFinAccount) {
//                                binding.financialLayout.check(binding.yes.id)
//                                kycRes.accountRefusalReason?.let { reas ->
//                                    binding.financialReasonEt.setText(reas)
//                                }
//                            } else {
//                                binding.financialLayout.check(binding.no.id)
//                            }

                            if (kycRes.hasBenificialOwner) {
                                binding.declarationRadioGroup.check(binding.decYes.id)
                                binding.benNameEt.setText(kycRes.uboName)
                                binding.benFatherNameEt.setText(kycRes.uboFatherName)
                                binding.cnicBenifOwnerEt.setText(kycRes.uboNIC)
                                binding.benAddressEt.setText(kycRes.uboAddress)
                                binding.benDateOfBirthEt.setText(changeUTCDateFormatToDisplay(kycRes.uboDOB))
                                binding.benCnicExpiryDateEt.setText(
                                    changeUTCDateFormatToDisplay(
                                        kycRes.uboNICExpiryDate
                                    )
                                )
                                binding.benCnicIssueDateEt.setText(
                                    changeUTCDateFormatToDisplay(
                                        kycRes.uboNICIssuerDate
                                    )
                                )
                                benDob = kycRes.uboDOB
                                benCnicExpiry = kycRes.uboNICExpiryDate
                                benCnicIssueDate = kycRes.uboNICIssuerDate

                                val relatn =
                                    uboRelationshipList?.find { resi -> resi.id == kycRes.uboRelationshipId }
                                binding.benRelationshipSpinner.setSelection(
                                    relationList.indexOf(relatn?.name)
                                )
                            } else {
                                binding.declarationRadioGroup.check(binding.decNo.id)
                            }

                            /*if (findNavController().currentBackStackEntry?.savedStateHandle?.contains(
                                    NEXT_OF_KIN_RES
                                ) == false
                            )*/
                            if (!kycRes.nextOfKinList.isNullOrEmpty()) {

                                nextOfKinList.clear()
                                nextOfKinList.addAll(kycRes.nextOfKinList)

                                nextOfKinAdapter.setNominees(nextOfKinList)
                                nextOfKinAdapter.notifyDataSetChanged()

                                binding.nomineesTv.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    null,
                                    null
                                )
                            } else {
                                binding.nomineesTv.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_add),
                                    null
                                )
                            }
                        }
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
                    //handle error here
                }
            }
        })

        regulatoryInfoViewModel.deleteNextOfKinApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        deleteNextOfKin?.let { dnk ->
                            it.response?.message.let { msg ->
                                if (msg == "Deleted Successfully.") {
                                    nextOfKinList.remove(deleteNextOfKin)
                                    nextOfKinAdapter.setNominees(nextOfKinList)
                                    showAlertDialog(
                                        requireContext(),
                                        layoutInflater,
                                        getString(R.string.success),
                                        "${deleteNextOfKin?.name} is deleted successfully!",
                                        R.drawable.ic_check_yellow,
                                        getString(R.string.ok)
                                    ) {}

                                    if (nextOfKinList.isNullOrEmpty()) {
                                        binding.nomineesTv.setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_add),
                                            null
                                        )
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
                    //handle error here
                }
            }
        })
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<NextOfKin>>(
            NEXT_OF_KIN_RES
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            // Do something with the result.
//            Toast.makeText(requireContext(), result, LENGTH_LONG).show()
            nextOfKinList.clear()
            nextOfKinList.addAll(result)
//            Toast.makeText(requireContext(), nomineesList.size.toString(), LENGTH_LONG).show()
            nextOfKinAdapter.setNominees(nextOfKinList)
            nextOfKinAdapter.notifyDataSetChanged()
        }
    }*/

    private fun initViews() {
//        binding.addMoreNominees.paintFlags = binding.addMoreNominees.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        binding.cnicBenifOwnerEt.setupEditText(requireContext(), binding.cnicBenifOwner)
        binding.benFatherNameEt.setupEditText(requireContext(), binding.benFatherName)
        binding.benNameEt.setupEditText(requireContext(), binding.benName)
        binding.benDateOfBirthEt.setupEditText(requireContext(), binding.benDateOfBirth)
        binding.benCnicIssueDateEt.setupEditText(requireContext(), binding.benCnicIssueDate)
        binding.benCnicExpiryDateEt.setupEditText(requireContext(), binding.benCnicExpiryDate)
        binding.benAddressEt.setupEditText(requireContext(), binding.benAddress)

        if (editMode) {
            binding.appBar.backBtn.visibility = VISIBLE
            binding.appBar.stepTv.visibility = GONE
            binding.appBar.progressbarContainer.visibility = GONE
            binding.statusLayout.visibility = INVISIBLE

            binding.appBar.backBtn.setOnClickListener {
                findNavController().navigate(R.id.action_KYCFragment_to_riskProfileQuestionnaireFragment)
            }

        } else {
            binding.appBar.backBtn.visibility = GONE
        }

        nextOfKinAdapter = NextOfKinAdapter { nextOfKin, type ->
//            if type 1 then edit action perform otherwise type 2 for delete action
            if (type == 1) {
                var totalShare = 0

                /*nextOfKinList.forEach {
                    totalShare += it.share
                }*/

                findNavController().navigate(R.id.addNomineesFragment, bundleOf(
                    Pair(NEXT_OF_KIN_EDIT, nextOfKin),
                    Pair(SHARE, totalShare)
                ))
            } else if (type == 2) {
                deleteNextOfKin = nextOfKin
                regulatoryInfoViewModel.deleteNextOfKin(
                    HBLPreferenceManager.getToken(requireContext()),
                    deleteNextOfKin?.id!!
                )
            }
        }

        binding.nomineesTv.setOnClickListener {
            var totalShare = 0

            /*nextOfKinList.forEach {
                totalShare += it.share
            }*/


            findNavController().navigate(R.id.addNomineesFragment, bundleOf(
                Pair(SHARE, totalShare)
            ))
        }

        binding.continueBtn.setOnClickListener {
            if (validate()) {
                val kycDTO = SaveKYCInfoDTO(
                    HBLPreferenceManager.getCustomerID(requireContext()),
                    "CEFCE01D-735F-4672-BAE8-53E0E4038390",
                    nextOfKinList,
                    selectedResidentStatus,
                    binding.decYes.isChecked,//change param according to selection
                    binding.benAddressEt.text.toString(),
                    benDob,
                    binding.benFatherNameEt.text.toString(),
                    binding.cnicBenifOwnerEt.text.toString(),
                    benCnicExpiry,
                    benCnicIssueDate,
                    binding.benNameEt.text.toString(),
                    selectedUBORelation
                )

                regulatoryInfoViewModel.saveKYCInfo(HBLPreferenceManager.getToken(requireContext()), kycDTO)
            }
        }

        binding.nomineesRv.apply {
            adapter = nextOfKinAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.financialReasonEt.setupEditText(requireContext(), binding.financialReason)

        binding.financialReasonEt.addTextChangedListener {
            when {
                binding.financialReasonEt?.text!!.isEmpty() -> {
                    binding.financialReason.error = "Reason is required"
                }
                binding.financialReasonEt?.text!!.length < 3 -> {
                    binding.financialReason.error = "Minimum length should be 3"
                }
                else -> {
                    binding.financialReason.error = null
                    reason = binding.financialReasonEt.text.toString()
                }
            }

            binding.continueBtn.isEnabled = validate()
        }

        binding.benNameEt.afterTextChanged {
            when {
                binding.benNameEt?.text!!.isEmpty() -> {
                    binding.benName.error = "Name is required"
                }
                binding.benNameEt?.text!!.length < 3 -> {
                    binding.benName.error = "Minimum length should be 3"
                }
                else -> {
                    binding.benName.error = null
                }
            }

            binding.continueBtn.isEnabled = validate()
        }

        binding.benFatherNameEt.afterTextChanged {
            when {
                it.isEmpty() -> {
                    binding.benFatherName.error = "Father Name is required"
                }
                it.length < 3 -> {
                    binding.benFatherName.error = "Minimum length should be 3"
                }
                else -> {
                    binding.benFatherName.error = null
                }
            }

            binding.continueBtn.isEnabled = validate()
        }

        binding.benAddressEt.afterTextChanged {
            when {
                binding.benAddressEt?.text!!.isEmpty() -> {
                    binding.benAddress.error = "Address is required"
                }
                binding.benAddressEt?.text!!.length < 3 -> {
                    binding.benAddress.error = "Minimum length should be 3"
                }
                else -> {
                    binding.benAddress.error = null
                }
            }

            binding.continueBtn.isEnabled = validate()
        }

        binding.cnicBenifOwnerEt.afterTextChanged {
            if (binding.cnicBenifOwnerEt.length() < 15) {
                binding.cnicBenifOwner.error = "Minimum length should be 13"
            } else {
                binding.cnicBenifOwner.error = null
            }

            binding.continueBtn.isEnabled = validate()
        }

        binding.residentialLayout.setOnClickListener {
            binding.residentialStatusSpinner.performClick()
        }

        binding.financialLayout.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                binding.yes.id -> {
                    financialInstitutionRefused = true
                    binding.financialReason.visibility = VISIBLE
                }
                binding.no.id -> {
                    financialInstitutionRefused = false
                    binding.financialReason.editText?.setText("")
                    binding.financialReason.visibility = GONE
                }
            }

            binding.continueBtn.isEnabled = validate()
        }

        binding.declarationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.decYes.id -> {
                    declarionBenOwner = false
                    binding.benName.editText?.setText("")
                    binding.benFatherName.editText?.setText("")
                    binding.benAddress.editText?.setText("")
                    binding.benDateOfBirth.editText?.setText("")
                    binding.benCnicExpiryDate.editText?.setText("")
                    binding.benCnicIssueDate.editText?.setText("")
                    binding.cnicBenifOwner.editText?.setText("")
                    binding.benRelationshipSpinner.setSelection(0)
                    selectedUBORelation = null
                    benCnicExpiry = null
                    benCnicIssueDate = null
                    benDob = null
                    binding.benLayout.visibility = GONE
                }
                binding.decNo.id -> {
                    declarionBenOwner = true
                    binding.benLayout.visibility = VISIBLE
                }
            }

            binding.continueBtn.isEnabled = validate()
        }

        binding.benCnicIssueDate.setEndIconOnClickListener {
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
                    binding.benCnicIssueDateEt.setText("$actualDay/$actualMonth/$year")
                    benCnicIssueDate =
                        AppUtils.changeDisplayDateFormatToUTC(binding.benCnicIssueDateEt.text.toString())

                    binding.continueBtn.isEnabled = validate()
                }

            datePicker?.type = FUTURE_DATE_DISABLE
            datePicker!!.show(childFragmentManager, "datePicker")

            binding.benCnicIssueDate.setTitleColour(requireContext())
        }

        binding.benCnicExpiryDate.setEndIconOnClickListener {
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
                    binding.benCnicExpiryDateEt.setText("$actualDay/$actualMonth/$year")
                    benCnicExpiry =
                        AppUtils.changeDisplayDateFormatToUTC(binding.benCnicExpiryDateEt.text.toString())

                    binding.continueBtn.isEnabled = validate()
                }

            datePickerFragment?.type = PAST_DATE_DISABLE
            datePickerFragment!!.show(childFragmentManager, "datePicker")

            binding.benCnicExpiryDate.setTitleColour(requireContext())
        }

        binding.benDateOfBirth.setEndIconOnClickListener {
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
                    binding.benDateOfBirthEt.setText("$actualDay/$actualMonth/$year")
                    benDob =
                        AppUtils.changeDisplayDateFormatToUTC(binding.benDateOfBirthEt.text.toString())


                    binding.continueBtn.isEnabled = validate()
                }

            datePickerFragment?.type = FUTURE_DATE_DISABLE


            datePickerFragment!!.show(childFragmentManager, "datePicker")

            binding.benDateOfBirth.setTitleColour(requireContext())
        }
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 25
        binding.appBar.progressbar3.progress = 0
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "30%"
        binding.appBar.stepTv.text = "Step 2/4"
    }

    private fun setUpResidentialSpinner(residentialStatusList: List<String>) {
        binding.residentialStatusSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, residentialStatusList)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    this@KYCFragment.residentialStatusList?.let {
                        selectedResidentStatus = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }
                    }

                    binding.continueBtn.isEnabled = validate()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    private fun setUpRelationSpinner(relations: List<String>) {
        binding.benRelationshipSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, relations)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    this@KYCFragment.uboRelationshipList?.let {
                        selectedUBORelation = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            null
                        }
                    }

                    binding.continueBtn.isEnabled = validate()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    fun validate(): Boolean {
        val uboCheck =
            binding.cnicBenifOwnerEt.text!!.isEmpty() or binding.benNameEt.text!!.isEmpty() or
                    binding.benFatherNameEt.text!!.isEmpty() or binding.benAddressEt.text!!.isEmpty() or
                    (selectedUBORelation == null) or benCnicExpiry.isNullOrEmpty() or benCnicIssueDate.isNullOrEmpty() or
                    benDob.isNullOrEmpty() or (binding.cnicBenifOwnerEt.text!!.length < 15) or
                    (binding.benFatherNameEt.text!!.length < 3) or
                    (binding.benNameEt.text!!.length < 3) or
                    (binding.benAddressEt.text!!.length < 3)

        if (selectedResidentStatus == 0|| declarionBenOwner == null ||
            (binding.decNo.isChecked && uboCheck)
        ) {
            return false
        }

        return true
    }
}