package com.hbl.amc.ui.RegulatoryInformation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.google.gson.Gson
import com.hbl.amc.R
import com.hbl.amc.databinding.AddNomineesFragmentBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.GenericObject
import com.hbl.amc.domain.model.NextOfKin
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.AppUtils.afterTextChanged
import com.hbl.amc.utils.AppUtils.changeDisplayDateFormatToUTC
import com.hbl.amc.utils.AppUtils.changeUTCDateFormatToDisplay
import com.hbl.amc.utils.AppUtils.checkUTC
import com.hbl.amc.utils.DatePickerFragment
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showAlertDialog
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.hbl.amc.utils.setTitleColour
import com.hbl.amc.utils.setupEditText
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Body


class AddNomineesFragment : Fragment() {

    private var cnicExpiryDate = ""
    private var cnicIssueDate = ""
    private var relationshipId : Int? = null
    var remainingShare = 0
    var totalShare = 0
    var idTypeList : List<GenericObject>? = null
    var relationshipList : List<GenericObject>? = null
    private var selectedIdType : Int? = null
    var emergencyContact = ""


    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val regulatoryInfoViewModel by viewModel<RegulatoryInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private var _binding: AddNomineesFragmentBinding? = null

    private val binding get() = _binding!!

//    lateinit var nextOfKinList : ArrayList<NextOfKin>
    var nextOfKinEdit : NextOfKin? = null

    var frontCNIC : File? = null
    var backCNIC : File? = null
    var bForm : File? = null
    var docType = "frontCNIC"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddNomineesFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalShare = arguments?.getInt(SHARE) ?: 0

        if (arguments?.containsKey(NEXT_OF_KIN_EDIT)!!) {
            nextOfKinEdit = arguments?.getSerializable(NEXT_OF_KIN_EDIT) as NextOfKin
        }

        initViewModel()
        initViews()

        regulatoryInfoViewModel.getKYCInfoLookups(HBLPreferenceManager.getToken(requireContext()))
    }

    fun initViewModel() {
        val idTpList = ArrayList<String>()
        val relationsLt = ArrayList<String>()

        regulatoryInfoViewModel.saveNextOfKinApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { nx ->
                        Log.d("next of kin new", nx.customerId)

                        findNavController().navigateUp()

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
                    it.error?.message?.let { it1 ->
                        Log.d("next of kin error", it1)
                    }
                }
            }
        })

        regulatoryInfoViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })

        regulatoryInfoViewModel.kycInfoLookUpsApi.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        idTypeList = it.response.result.identityTypeList
                        relationshipList = it.response.result.uboRelationshipList

                        idTpList.add(getString(R.string.select))
                        idTypeList?.forEach { idType ->
                            idTpList.add(idType.name)
                        }

                        relationsLt.add(getString(R.string.select))
                        relationshipList?.forEach { rl ->
                            relationsLt.add(rl.name)
                        }

                        setupRelationshipSpinner(relationsLt)
                        setUpIdentityTypeSpinner(idTpList)


                        if (nextOfKinEdit != null) {
                            relationshipId = nextOfKinEdit?.relationshipId
                            val rel = relationshipList?.find { re -> re.id == relationshipId }
                            rel?.let { re ->
                                var index = relationsLt.indexOf(re.name)
                                binding.relationshipSpinner.setSelection(index, true)
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
                    it.error?.message?.let {
                            it1 -> Log.d("next of kin error", it1)
                    }
                }
            }
        })
    }

    private fun setUpIdentityTypeSpinner(idTypes: List<String>) {
        binding.docSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, idTypes)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    idTypeList?.let {
                        selectedIdType = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            null
                        }

//                        isBform = false
                        frontCNIC = null
                        backCNIC = null
                        bForm = null
                        binding.uploadCnicFront.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_add),
                            null
                        )

                        binding.uploadCnicBack.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_add),
                            null
                        )

                        binding.uploadBform.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_add),
                            null
                        )

                        when (selectedIdType) {
                            6001 -> {
                                binding.uploadCnicFront.visibility = View.VISIBLE
                                binding.uploadCnicBack.visibility = View.VISIBLE
                                binding.uploadBform.visibility = View.GONE
                            }
                            6002 -> {
                                binding.uploadCnicFront.visibility = View.VISIBLE
                                binding.uploadCnicBack.visibility = View.VISIBLE
                                binding.uploadBform.visibility = View.GONE
                            }
                            6003 -> {
                                binding.uploadCnicFront.visibility = View.GONE
                                binding.uploadCnicBack.visibility = View.GONE
                                binding.uploadBform.visibility = View.VISIBLE
                            }
                            else -> {
                                binding.uploadCnicFront.visibility = View.GONE
                                binding.uploadCnicBack.visibility = View.GONE
                                binding.uploadBform.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!

                val file = FileUtil.getTempFile(requireContext(), uri)
                Log.d("file path", file?.name + "-----------" + file?.path)

                when (docType) {
                    "frontCNIC" -> {
                        file?.let { actualFile ->
                            frontCNIC = actualFile
                            binding.uploadCnicFront.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit),
                                null
                            )
                        }
                    }
                    "backCNIC" -> {
                        file?.let { f ->
                            backCNIC = f
                            binding.uploadCnicBack.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit),
                                null
                            )
                        }
                    }
                    "bForm" -> {
                        file?.let { f ->
                            bForm = f
                            binding.uploadBform.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit),
                                null
                            )
                        }
                    }
                }
            }
        }

    fun initViews() {

        binding.ccPicker.registerCarrierNumberEditText(binding.mobileNoEt)
        binding.ccPicker.setCountryForNameCode("PK")
        binding.ccPicker.setDialogCornerRaius(20f)
        binding.ccPicker.setNumberAutoFormattingEnabled(true)

        binding.ccPicker.setOnCountryChangeListener {
            binding.mobileNoEt.setText("")
        }

        binding.fullNameCnicEt.addTextChangedListener {
            if (binding.fullNameCnicEt.text?.length!! < 3) {
                binding.fullNameCnic.error = "Minimum length should be 3"
            } else {
                binding.fullNameCnic.error = null
            }

            binding.addNomineeBtn.isEnabled = validate()
        }

        binding.cnicNicopBformEt.addTextChangedListener {
            if (binding.cnicNicopBformEt.text?.length!! < 15) {
                binding.cnicNicopBform.error = "Minimum length should be 13"
            } else {
                binding.cnicNicopBform.error = null
            }

            binding.addNomineeBtn.isEnabled = validate()
        }
//        resetBackStackEntryLiveData()

        binding.uploadCnicFront.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.uploadCnicBack.setOnClickListener {
            docType = "backCNIC"
            ImagePicker.with(requireActivity())
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.uploadBform.setOnClickListener {
            docType = "bForm"

            ImagePicker.with(requireActivity())
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.mobileNoEt.afterTextChanged {
            if (!binding.ccPicker.isValidFullNumber) {
                binding.mobileNumberErrorTv.visibility = View.VISIBLE
                binding.mobileNumberTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.design_default_color_error
                    )
                )
            } else {
                binding.mobileNumberErrorTv.visibility = View.GONE
                binding.mobileNumberTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hbl_main_green
                    )
                )
            }

            emergencyContact = binding.ccPicker.fullNumberWithPlus
            //binding.ccPicker.selectedCountryCodeWithPlus + binding.mobileNoEt.text.toString()
            Log.d("mobile", emergencyContact)
//            toggleContinueBtn(validateInput())
        }

        binding.addNomineeBtn.setOnClickListener {

            if (validate()) {
                var nicFront : MultipartBody.Part? = null
                var nicBack : MultipartBody.Part? = null
                var form_b : MultipartBody.Part? = null

                frontCNIC?.let {
                    nicFront = MultipartBody.Part.createFormData("nicFront", it.name,
                        it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                backCNIC?.let {
                    nicBack = MultipartBody.Part.createFormData("nicBack", it.name,
                        it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                bForm?.let {
                    form_b = MultipartBody.Part.createFormData("bForm", it.name,
                        it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                val customerID = MultipartBody.Part.createFormData("customerId", HBLPreferenceManager.getCustomerID(requireContext()))
                val name = MultipartBody.Part.createFormData("name", binding.fullNameCnicEt.text.toString())

                val rlshp = MultipartBody.Part.createFormData("relationshipId", relationshipId.toString())
                val idType = selectedIdType?.let {
                    MultipartBody.Part.createFormData("identityTypeId", it.toString())
                } ?: kotlin.run {
                    null
                }

                val emergencyNo = MultipartBody.Part.createFormData("emergencyContactNo", emergencyContact)
                val nic = MultipartBody.Part.createFormData("nic", binding.cnicNicopBformEt.text.toString())
                val nicIssue = MultipartBody.Part.createFormData("nicIssueDate", cnicIssueDate)
                val nicExpiry = MultipartBody.Part.createFormData("nicExpiryDate", cnicExpiryDate)

                if (nextOfKinEdit != null) {
                    val id = MultipartBody.Part.createFormData("id", nextOfKinEdit?.id!!)

                    regulatoryInfoViewModel.updateNextOfKin(
                        HBLPreferenceManager.getToken(requireContext()),
                        customerID,
                        name,
                        rlshp,
                        share = emergencyNo,
                        nic,
                        nicFront,
                        nicBack,
                        form_b,
                        nicExpiry,
                        nicIssue,
                        id,
                        idType
                    )
                } else {
                    regulatoryInfoViewModel.saveNextOfKin(
                        HBLPreferenceManager.getToken(requireContext()),
                        customerID,
                        name,
                        rlshp,
                        share = emergencyNo,
                        nic,
                        nicFront,
                        nicBack,
                        form_b,
                        nicExpiry,
                        nicIssue,
                        idType
                    )
                }

                /*if (nextOfKinEdit != null) {
                    *//*val updatedNK = nextOfKinList.find { it.nic == nextOfKinEdit?.nic }
                    updatedNK?.name = binding.fullNameCnicEt.text.toString()
                    updatedNK?.nic = binding.cnicNicopBformEt.text.toString()
                    updatedNK?.nicIssueDate = cnicIssueDate
                    updatedNK?.nicExpiryDate = cnicExpiryDate
                    updatedNK?.relationship = relationShip
                    updatedNK?.share = binding.shareEt.text.toString().toInt()*//*

                    *//*nextOfKinList[nextOfKinList.indexOf(nextOfKinEdit)] = updatedNK!!

                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        NEXT_OF_KIN_RES,
                        nextOfKinList
                    )*//*

                    findNavController().navigateUp()

                } else {
                    if (binding.fullNameCnicEt.text!!.isNotEmpty()) {

                        *//*val nextOfKin = NextOfKin(
                            "3487223",
                            binding.fullNameCnicEt.text.toString(),
                            binding.cnicNicopBformEt.text.toString(),
                            cnicExpiryDate,
                            cnicIssueDate,
                            relationShip,
                            binding.shareEt.text.toString().toInt(),
                            "",
                            ""
                        )

                        nextOfKinList.add(nextOfKin)
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            NEXT_OF_KIN_RES,
                            nextOfKinList
                        )*//*
                    }
                }*/
            }
        }

        binding.appBar.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.relationshipLayout.setOnClickListener {
            binding.relationshipSpinner.performClick()
        }

        binding.cnicIssueDate.setEndIconOnClickListener {
            val newFragment = DatePickerFragment { view, year, month, day -> // Do something with the date chosen by the user
                var actualDay = day.toString()
                val mon = month + 1
                var actualMonth = mon.toString()

                if (day < 10) {
                    actualDay = "0${day}"
                }

                if (mon < 10) {
                    actualMonth = "0${mon}"
                }

//                cnicIssueDate = "$actualDay-$actualMonth-$year"
                binding.cnicIssueDate.editText?.setText("$actualDay/$actualMonth/$year")
                cnicIssueDate = changeDisplayDateFormatToUTC(binding.cnicIssueDate.editText?.text.toString())
                binding.addNomineeBtn.isEnabled = validate()
            }

            newFragment?.type = FUTURE_DATE_DISABLE
            newFragment!!.show(childFragmentManager, "datePicker")

            binding.cnicIssueDate.setTitleColour(requireContext())
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

//                    cnicExpiryDate = "$actualDay-$actualMonth-$year"
                    binding.cnicExpiryDate.editText?.setText("$actualDay/$actualMonth/$year")
                    cnicExpiryDate = changeDisplayDateFormatToUTC(binding.cnicExpiryDate.editText?.text.toString())
                    binding.addNomineeBtn.isEnabled = validate()
                }

            datePickerFragment?.type = PAST_DATE_DISABLE
            datePickerFragment!!.show(childFragmentManager, "datePicker")

            binding.cnicExpiryDate.setTitleColour(requireContext())
        }

        binding.fullNameCnicEt.setupEditText(requireContext(), binding.fullNameCnic)
        binding.cnicIssueDate.editText?.setupEditText(requireContext(), binding.cnicIssueDate)
        binding.cnicExpiryDate.editText?.setupEditText(requireContext(), binding.cnicExpiryDate)
        binding.cnicNicopBformEt.setupEditText(requireContext(), binding.cnicNicopBform)

        if (nextOfKinEdit != null) {
            binding.addNomineeBtn.text = "Update"
//            binding.shareEt.setText(nextOfKinEdit?.share.toString())
            binding.cnicNicopBformEt.setText(nextOfKinEdit?.nic)
            binding.fullNameCnicEt.setText(nextOfKinEdit?.name)
            binding.ccPicker.fullNumber = nextOfKinEdit?.emergencyContactNo
            cnicIssueDate = nextOfKinEdit?.nicIssueDate!!
            cnicExpiryDate = nextOfKinEdit?.nicExpiryDate!!

            if (checkUTC(cnicIssueDate)) {
                binding.cnicIssueDate.editText?.setText(changeUTCDateFormatToDisplay(cnicIssueDate))
                binding.cnicExpiryDate.editText?.setText(changeUTCDateFormatToDisplay(cnicExpiryDate))
            } else {
                binding.cnicIssueDate.editText?.setText(nextOfKinEdit?.nicIssueDate!!.replace("-", "/"))
                binding.cnicExpiryDate.editText?.setText(nextOfKinEdit?.nicExpiryDate!!.replace("-", "/"))
            }

            //cnic expiry and issue missing
        }

        /*binding.shareEt.addTextChangedListener {

            //decimal check
            val str: String = binding.shareEt.text.toString()

            if (str.isEmpty()) {
                binding.share.error = "Share is required"
                binding.addNomineeBtn.isEnabled = validate()
                return@addTextChangedListener
            }

            //code if we want to make this field upto 2 decimal places
            *//*val str2 = PerfectDecimal(str, 3, 2)

            if (str2 != str) {
                binding.shareEt.setText(str2)
                binding.shareEt.setSelection(str2!!.length)
            }*//*

            //error validation
            when {
                binding.shareEt.text!!.toString().toDouble() > 100 -> {
                    binding.share.error = "Please enter shares value upto 100"
                }
               checkTotalShare() -> {
                    binding.share.error = "Remaining value of shares should be less than or equal to $remainingShare"
                }
                else -> {
                    binding.share.error = null
                }
            }

            binding.addNomineeBtn.isEnabled = validate()
        }*/

    }

    private fun setupRelationshipSpinner(relations : ArrayList<String>) {
        binding.relationshipSpinner.adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, relations)
        binding.relationshipSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                relationshipList?.let {
                    relationshipId = if (position > 0) {
                        it[position - 1].id
                    } else {
                        null
                    }
                }

                binding.addNomineeBtn.isEnabled = validate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun validate() : Boolean {
        val mobileNoCheck = !binding.ccPicker.isValidFullNumber

        if (cnicIssueDate.isEmpty() || cnicExpiryDate.isEmpty() || binding.fullNameCnicEt.text!!.length < 3
            || binding.cnicNicopBformEt.text!!.length < 15 || relationshipId == null
            || mobileNoCheck) {//binding.shareEt.text!!.isEmpty() || binding.shareEt.text!!.toString().toDouble() > 100 ||
//            checkTotalShare()) {
            return false
        }

        return true
    }

    /*private fun checkTotalShare() : Boolean {
        var total = 0

        if (nextOfKinEdit != null) {
            total += totalShare
            remainingShare = 100 - (total - nextOfKinEdit?.share!!)
            total -= nextOfKinEdit?.share!!
//            total += binding.shareEt.text!!.toString().toInt()

        } else {
            total += totalShare
            remainingShare = 100 - total
//            total += binding.shareEt.text!!.toString().toInt()
        }

        return total > 100
    }*/

    fun resetBackStackEntryLiveData() {
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<String>(NEXT_OF_KIN_RES)
    }
}