package com.hbl.amc.ui.Disclaimers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentDisclaimersBinding
import com.hbl.amc.domain.RequestDTO.SaveDisclaimerInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.GetDisclaimerResult
import com.hbl.amc.domain.model.SaveDisclaimer
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.EDIT_MODE
import com.hbl.amc.ui.Preview.PreviewActivity
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoActivity
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoViewModel
import com.hbl.amc.ui.STEP_ID
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.ms.square.android.expandabletextview.ExpandableTextView
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class DisclaimersFragment : Fragment(), KoinComponent {

    private var _binding: FragmentDisclaimersBinding? = null

    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val regulatoryViewModel by viewModel<RegulatoryInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var disclaimersList: List<GetDisclaimerResult>? = null
    private lateinit var saveDisclaimerList: ArrayList<SaveDisclaimer>

    private var disclaimersChecked: Int = 0
    private var isSubmitBtnClicked: Boolean = false

    lateinit var activ : DisclaimersActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activ = requireActivity() as DisclaimersActivity

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

        disclaimersChecked = 0
        isSubmitBtnClicked = false
        saveDisclaimerList = ArrayList()
        if(_binding == null)
        {
            _binding = FragmentDisclaimersBinding.inflate(inflater, container, false)

            initViews()

            regulatoryViewModel.getDisclaimerInfoLookups(HBLPreferenceManager.getToken(requireContext()))
        }

        return binding.root
    }


    private fun initViewModel() {
        regulatoryViewModel.getDisclaimersApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        disclaimersList = it?.response?.result
                        Log.d("@@@", disclaimersList?.count().toString())


                        var layout: View
                        for (i in 0 until (disclaimersList?.count()!!)) {
                            Log.d("@@@", disclaimersList!![i].title)
                            layout = layoutInflater.inflate(R.layout.disclaimer_item, null)

                            layout.findViewById<AppCompatCheckBox>(R.id.disclaimer_checkbox).id = i
                            layout.findViewById<AppCompatCheckBox>(i).text =
                                disclaimersList!![i].title
                            layout.findViewById<ExpandableTextView>(R.id.expand_text_view).text =
                                disclaimersList!![i].description + " \n "
                            binding.contentLayout.addView(layout)

                            layout.findViewById<AppCompatCheckBox>(i)
                                .setOnCheckedChangeListener { buttonView, isChecked ->
                                    if (isChecked) {
                                        disclaimersChecked += 1
                                        Log.d("@@@", disclaimersChecked.toString())
                                    } else {
                                        disclaimersChecked -= 1
                                    }
                                    toggleContinueBtn(validateInput())
                                }
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
                    it?.error?.message?.let { it1 -> Log.d("Disclaimer info error", it1) }
                }
            }
        })

        regulatoryViewModel.saveDisclaimerApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        //save user in shared prefs
                        if (isSubmitBtnClicked) {
                            val intent = Intent(requireContext(), PreviewActivity::class.java)
                            intent.putExtra("FROM_DISCLAIMER", true)
                            startActivity(intent)
                        } else {
                            val intent = Intent(requireContext(), PreviewActivity::class.java)
                            startActivity(intent)
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
                    it?.error?.message?.let { it1 -> Log.d("Disclaimer save info error", it1) }
                }
            }
        })

        regulatoryViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })
    }

    private fun initViews() {

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        binding.appBar.title.text = getString(R.string.disclaimer_heading)

        setupProgress()

        binding.appBar.backBtn.setOnClickListener {
            if(activ.intent.hasExtra(STEP_ID))
            {
                activ.finish()
            } else {
                findNavController().navigateUp()
            }
        }

        binding.documentChecklistBtn.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_disclaimersFragment_to_documentChecklistFragment, bundleOf(
                        Pair(EDIT_MODE, true)
                    )
                )
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }

        binding.continueBtn.setOnClickListener {
            for (i in 0 until binding.contentLayout.childCount) {
                val disc = SaveDisclaimer(disclaimersList!![i].id, true)
                saveDisclaimerList.add(disc)
            }

            val saveDisclaimerInfoDTO = SaveDisclaimerInfoDTO(
                HBLPreferenceManager.getCustomerID(requireContext()),
                saveDisclaimerList,
                "1DE3AE40-8659-48B1-A426-0AD3C7BD8B41"
            )
            regulatoryViewModel.saveDisclaimerInfo(
                HBLPreferenceManager.getToken(requireContext()),
                saveDisclaimerInfoDTO
            )
            isSubmitBtnClicked = true
        }

//        binding.previewTv.setOnClickListener {
//            for (i in 0 until binding.contentLayout.childCount) {
//                val disc = SaveDisclaimer(disclaimersList!![i].id, true)
//                saveDisclaimerList.add(disc)
//            }
//
//            val saveDisclaimerInfoDTO = SaveDisclaimerInfoDTO(
//                HBLPreferenceManager.getCustomerID(requireContext()),
//                saveDisclaimerList,
//                "1DE3AE40-8659-48B1-A426-0AD3C7BD8B41"
//            )
//            regulatoryViewModel.saveDisclaimerInfo(
//                HBLPreferenceManager.getToken(requireContext()),
//                saveDisclaimerInfoDTO
//            )
//            isSubmitBtnClicked = false
//        }
    }

    private fun validateInput(): Boolean {
        return disclaimersChecked >= disclaimersList!!.count()
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
//        binding.previewTv.isClickable = b
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 100
        binding.appBar.progressbar4.progress = 100
        binding.appBar.progress.text = "100%"
        binding.appBar.stepTv.text = "Step 4/4"
    }
}
