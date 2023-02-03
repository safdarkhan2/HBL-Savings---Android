package com.hbl.amc.ui.self_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentPledgeBinding
import com.hbl.amc.domain.RequestDTO.SavePledgeInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.DocumentsTypesResult
import com.hbl.amc.domain.model.PledgeInstructions
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.Disclaimers.DisclaimersViewModel
import com.hbl.amc.ui.PLEDGE_FORM
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class PledgeFragment : Fragment(), KoinComponent {

    private var _binding: FragmentPledgeBinding? = null

    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val selfServiceViewModel by viewModel<SelfServiceViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val disclaimersViewModel by viewModel<DisclaimersViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private var pledgeformID: String? = null
    private var uploadDocType: String? = null
    var instructionsList: List<PledgeInstructions>? = null
    var allDocsTypes: List<DocumentsTypesResult>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPledgeBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()

        HBLPreferenceManager.getDocumentsTypesResponse(requireContext())?.let { docTypeRes ->
            docTypeRes.result.also { allDocsTypes = it }
        }

        initViews()

        /*selfServiceViewModel.getPledgeInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            "1df93f23-087e-4fa1-e166-08d9b2f21152"
        )*/

        selfServiceViewModel.getPledgeInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            HBLPreferenceManager.getCustomerID(
                requireContext()
            )
        )

        return view
    }

    fun initViewModel() {
        selfServiceViewModel.getPledgeInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        instructionsList = it.response?.result?.instructionsList

                        binding.instructionTitle.text = instructionsList!![0].title
                        var desc = instructionsList!![0].description
                        binding.expandTextView.text = desc + " \n"
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
                        Log.d("pledge lookups info error", it1)
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

        selfServiceViewModel.savePledgeInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        it.response.let { it2 -> Log.d("pledge save info success", it2.toString()) }
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.success),
                            "Pledge Information Updated!\nReference No. ${it.response.result.darNumber}",
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
                        Log.d("pledge save info error", it1)
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

        disclaimersViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })

        disclaimersViewModel.downloadDocumentApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        if (it.response?.result?.extension.equals("PDF", true)) {
                            val uri = AppUtils.getPDFURI(
                                requireContext(),
                                it.response?.result.documentBase64
                            )
                            AppUtils.viewPDF(requireContext(), layoutInflater, uri!!)
                        } else if (it.response?.result?.extension.equals("docx", true)) {
                            val uri =
                                AppUtils.getWordDocURI(
                                    requireContext(),
                                    it.response?.result.documentBase64
                                )
                            AppUtils.viewWordDoc(requireContext(), layoutInflater, uri!!)
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("download document error", it1) }
                }
            }
        })

        disclaimersViewModel.uploadDocumentApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.result?.let { res ->
//                        if (res.isUploaded) {
                        if (res.documentName == PLEDGE_FORM) {
                            pledgeformID = res.id

                            DialogUtils.showAlertDialog(
                                requireContext(),
                                layoutInflater,
                                getString(R.string.success),
                                "Pledge Form uploaded successfully!",
                                R.drawable.ic_check_yellow,
                                getString(R.string.ok)
                            ) {}
                        }
                    } ?: run {
                        it?.response?.message?.let { msg ->
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
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("upload $uploadDocType error", it1) }
                }
            }
        })
    }

    fun initViews() {

        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }

//        binding.continueBtn.setOnClickListener {
//            val savePledgeInfoDTO = SavePledgeInfoDTO(
////                "1df93f23-087e-4fa1-e166-08d9b2f21152",
//                HBLPreferenceManager.getCustomerID(requireContext()),
//                pledgeformID,
//                "E139AC89-EAEF-4AC4-A162-FADE2325472F"
//            )
//
//            selfServiceViewModel.savePledgeInfo(
//                HBLPreferenceManager.getToken(requireContext()),
//                savePledgeInfoDTO
//            )
//        }

        binding.downloadBtn.setOnClickListener {
            val docType = allDocsTypes?.find { dc -> dc.name == PLEDGE_FORM }
            docType?.let { it1 ->
                disclaimersViewModel.getSampleDocument(
                    it1.id
                )
            }
        }

//        binding.uploadBtn.setOnClickListener {
//            uploadDocType = PLEDGE_FORM
//            ImagePicker.with(requireActivity())
//                .crop()
//                .createIntentFromDialog { launcher.launch(it) }
//        }
    }

//    private val launcher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                val uri = it.data?.data!!
//
//                val file = FileUtil.getTempFile(requireContext(), uri)
//                Log.d("file path", file?.name + "-----------" + file?.path)
//
//                allDocsTypes?.let { ad ->
//                    val doc = ad.find { dc -> dc.name == uploadDocType }
//                    doc?.let { dc ->
//                        file?.let { actualFile ->
//                            val actualDoc = MultipartBody.Part.createFormData(
//                                "file", actualFile.name,
//                                actualFile.asRequestBody("image/*".toMediaTypeOrNull())
//                            )
//                            val docID = MultipartBody.Part.createFormData(
//                                "documentTypeId",
//                                dc.id.toString()
//                            )
//                            val customerID = MultipartBody.Part.createFormData(
//                                "customerId",
//                                HBLPreferenceManager.getCustomerID(requireContext())
//                            )
//                            disclaimersViewModel.uploadDocumentByIds(
//                                docFile = actualDoc,
//                                docTypeId = docID,
//                                customerID = customerID
//                            )
//                        }
//                    }
//                }
//            }
//        }
}