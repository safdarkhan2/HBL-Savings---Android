package com.hbl.amc.ui.Disclaimers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentDocumentChecklistBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.DocumentChecklistData
import com.hbl.amc.domain.model.DocumentChecklistResult
import com.hbl.amc.domain.model.DocumentsTypesResponse
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.EDIT_MODE
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class DocumentChecklistFragment : Fragment() {

    private var _binding: FragmentDocumentChecklistBinding? = null

    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val disclaimersViewModel by viewModel<DisclaimersViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    lateinit var docAdapter: DocsChecklistAdapter

    var documentUploadId = 0
    var editMode = false
    var documentsTypesResponse : DocumentsTypesResponse? = null
    lateinit var docChecklist : ArrayList<DocumentChecklistData>

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
//                Toast.makeText(requireContext(), "abc", LENGTH_LONG).show()
            }
        }

        if (!editMode) {
            requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()

        HBLPreferenceManager.getDocumentsTypesResponse(requireContext())?.let {
            documentsTypesResponse = it
        }

        if(_binding == null){
            _binding = FragmentDocumentChecklistBinding.inflate(inflater, container, false)

            docChecklist = ArrayList()

            initViews()
            setupProgress()

            disclaimersViewModel.getCustomerDocumentChecklist(HBLPreferenceManager.getCustomerID(requireContext()))
        }

        return binding.root
    }

    fun initViewModel() {
        disclaimersViewModel.documentsChecklistApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.let { res ->
                        res.result?.let { docList ->
                            if (docList.isNotEmpty()) {
                                if (documentsTypesResponse != null) {
                                    docChecklist.clear()
                                    val docTypeList = documentsTypesResponse!!.result
                                    for (doc in docList) {
                                        val documentType =
                                            docTypeList.find { dc -> dc.id == doc.documentTypeId }
                                        documentType?.let { dct ->
                                            val docCheckData = DocumentChecklistData(
                                                doc.documentName,
                                                doc.documentTypeId,
                                                doc.id,
                                                doc.isUploaded,
                                                doc.isRequired,
                                                doc.isEditable,
                                                dct.description
                                            )

                                            docChecklist.add(docCheckData)
                                        }

                                        Log.d("check list", docList.toString())
                                    }
                                    Log.d("check list after`", docList.toString())
                                    docAdapter.setDocumentsChecklist(docChecklist)
                                }/* else {
                                    docAdapter.setDocumentsChecklist(docList)
                                }*/
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

                }
            }
        })

        disclaimersViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.uploadDocumentApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.let { res ->
                        res.result?.let { result ->
                            if (result.isUploaded) {
                                disclaimersViewModel.getCustomerDocumentChecklist(HBLPreferenceManager.getCustomerID(requireContext()))
                            } else {
                                //show alert
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {

                }
            }
        })
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!

                val file = FileUtil.getTempFile(requireContext(), uri)
                Log.d("file path", file?.name + "-----------" + file?.path)

                file?.let { actualFile ->
                    val actualDoc = MultipartBody.Part.createFormData("file", actualFile.name,
                        actualFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    val docID = MultipartBody.Part.createFormData("documentTypeId",
                        documentUploadId.toString()
                    )
                    val customerID = MultipartBody.Part.createFormData("customerId", HBLPreferenceManager.getCustomerID(requireContext()))

                    disclaimersViewModel.uploadDocumentByIds(
                        docFile = actualDoc,
                        docTypeId = docID,
                        customerID = customerID
                    )
                }
            }
        }

    private fun initViews() {

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        if (editMode) {
            binding.appBar.backBtn.visibility = VISIBLE
            binding.appBar.stepTv.visibility = GONE
            binding.appBar.progressbarContainer.visibility = GONE
            binding.statusLayout.visibility = INVISIBLE
        } else {
            binding.appBar.backBtn.visibility = GONE
        }

        binding.appBar.title.text = getString(R.string.disclaimer_heading)

        binding.continueBtn.setOnClickListener {
            if(editMode)
            {
                findNavController().navigateUp()
            }
            else
            {
                findNavController().navigate(R.id.action_documentChecklistFragment_to_disclaimersFragment)
            }

        }

        binding.appBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        docAdapter = DocsChecklistAdapter{ docUpload ->
            documentUploadId = docUpload.documentTypeId

            ImagePicker.with(requireActivity())
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        binding.docChecklistRV.apply {
            adapter = docAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 100
        binding.appBar.progressbar4.progress = 50
        binding.appBar.progress.text = "90%"
        binding.appBar.stepTv.text = "Step 4/4"
    }
}