package com.hbl.amc.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentMyProfileBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.Documents
import com.hbl.amc.domain.model.MutualFunds
import com.hbl.amc.domain.model.RiskLevelsResult
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.productInformation.ProductInfoViewModel
import com.hbl.amc.ui.self_service.DashboardViewModel
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class MyProfileFragment : Fragment(), KoinComponent {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val dashboardViewModel by viewModel<DashboardViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val productViewModel by viewModel<ProductInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var mutualFundsList: List<MutualFunds>? = null
    var documentsList: List<Documents>? = null

    private lateinit var mutualFundsAdapter: ItemProfileMutualFundsAdapter
    private lateinit var pendingDocumentsAdapter: ItemProfilePendingDocumentsAdapter
    private lateinit var updateDocumentsAdapter: ItemProfileUpdateDocumentsAdapter

    val pendingDocList : ArrayList<Documents> = ArrayList()
    val updateDocList : ArrayList <Documents> = ArrayList()

    var minRiskRange : Int? = null
    var maxRiskRange : Int? = null
    var riskLevels : List<RiskLevelsResult>? = null
    var rpqScore : Int? = null

    var userRiskLevel = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()
        initViews()

//        dashboardViewModel.getProfileInfo(
//            HBLPreferenceManager.getToken(requireContext()),
////            "1df93f23-087e-4fa1-e166-08d9b2f21152"
//            HBLPreferenceManager.getCustomerID(requireContext())
//        )

        productViewModel.getRiskLevels(HBLPreferenceManager.getToken(requireContext()))
        return view
    }

    fun initViewModel() {
        dashboardViewModel.getProfileInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        binding.userNameTv.text = it.response.result.customerDetail.name
                        binding.cnicTv.text = it.response.result.customerDetail.cnic
                        binding.accountNumberTv.text = it.response.result.customerDetail.iban
                        binding.invLimitTv.text =
                            "PKR ${it.response.result.customerDetail.investmentLimit}"
                        binding.jointHolderTv.text = it.response.result.customerDetail.accountTitle
                        binding.filerStatusTv.text = it.response.result.customerDetail.filerStatus
                        if (it.response.result.customerDetail.isZakatExempt) {
                            binding.zakatStatusTv.text = "Exempted"
                        } else {
                            binding.zakatStatusTv.text = "Not Exempted"
                        }

                        binding.taxStatusTv.text = it.response.result.customerDetail.taxApplicable
                        binding.bankEt.setText(it.response.result.customerDetail.bank)
                        binding.ibanEt.setText(it.response.result.customerDetail.iban)

                        for (i in it.response.result.customerProfileLookUp.dividendPayoutList.indices) {
                            if (it.response.result.customerProfileLookUp.dividendPayoutList[i].id == it.response.result.customerDetail.dividendId) {
                                binding.dividendModeEt.setText(it.response.result.customerProfileLookUp.dividendPayoutList[i].name)
                            }
                        }

                        binding.phoneNoEt.setText(it.response.result.customerDetail.mobileNumber)
                        binding.emailEt.setText(it.response.result.customerDetail.email)
                        binding.mailingAddressEt.setText(it.response.result.customerDetail.mailingAddress)
                        rpqScore = it.response.result.customerProducts.rpqScore
                        binding.txtRpqScore.text = rpqScore.toString()

                        showRiskIndicatorDetails()

                        mutualFundsList = it.response.result.customerProducts.mutualFundsList
                        mutualFundsList.let { mutualFunds ->
                            if (mutualFunds != null) {
                                mutualFundsAdapter.setMutualFunds(mutualFunds)
                            }
                        }

                        documentsList = it.response.result.documentsList
                        documentsList.let { doc ->

                            if (doc != null) {
                                for(i in doc.indices)
                                {
                                    if(!doc[i].isUploaded)
                                    {
                                        pendingDocList.add(doc[i])
                                    }
                                    else
                                    {
                                        updateDocList.add(doc[i])
                                    }
                                }
                            }

                            pendingDocumentsAdapter.setPendingDoc(pendingDocList)
                            updateDocumentsAdapter.setUpdateDoc(updateDocList)
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
                                    getString(R.string.ok_btn_label)
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
                                    getString(R.string.ok_btn_label)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 ->
                        Log.d("get profile info error", it1)
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok_btn_label)
                        ) {}
                    }
                }
            }
        })

        productViewModel.getRiskLevelsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        it.response?.result?.let { rsk ->
                            minRiskRange = rsk[0].minimumScore
                            maxRiskRange = rsk[rsk.lastIndex].maximumScore
                            riskLevels = rsk
                            Log.d("Token", HBLPreferenceManager.getToken(requireContext()))
                            dashboardViewModel.getProfileInfo(
                                HBLPreferenceManager.getToken(requireContext()),
                                HBLPreferenceManager.getCustomerID(requireContext())
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
                                    getString(R.string.ok_btn_label)
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
                                    getString(R.string.ok_btn_label)
                                ) {}
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("risk levels error", it1) }
                }
            }
        })

        productViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })

        dashboardViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    fun  showRiskIndicatorDetails()
    {
        binding.rpqScoreMsg.text = "Based on your choices your score is $rpqScore. \nAccording to your RPQ score you have been suggested the following funds."

        if (minRiskRange != null && maxRiskRange != null) {
            val progress = rpqScore?.let {
                AppUtils.mapProgressIndicatorValues(
                    Range(minRiskRange!!, maxRiskRange!!),
                    Range(0, 100),
                    it
                )
            }

//            val riskIndicator = RiskIndicator(requireContext())
            progress?.let { it ->
                binding.riskIndicator.progress = it
            }


            rpqScore?.let { getRiskLevel(it) }

            when (userRiskLevel) {
                riskLevels!![0].riskLevel -> {
                    binding.riskIndicator.filledColor = R.color.progress_light_green
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.light_green_dot)
                    //                binding.rpqScoreProgress.setIndicatorColor(requireContext().resources.getColor(R.color.hbl_main_green))
                }
                riskLevels!![1].riskLevel -> {
                    binding.riskIndicator.filledColor = R.color.yellow_theme
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.yellow_dot)
                    //                binding.rpqScoreProgress.setIndicatorColor(requireContext().resources.getColor(R.color.yellow_theme))
                }
                else -> {
                    binding.riskIndicator.filledColor = R.color.red
                    binding.txtRpqScore.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.red_dot)
                    //                binding.rpqScoreProgress.setIndicatorColor(requireContext().resources.getColor(R.color.red))
                }
            }

            binding.riskIndicator.invalidate() //to redraw the risk indicator with updated progress

//            val layoutParams = ConstraintLayout.LayoutParams(
//                ConstraintLayout.LayoutParams.WRAP_CONTENT,
//                ConstraintLayout.LayoutParams.WRAP_CONTENT
//            )

            //start here
//            layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
//            layoutParams.endToEnd = ConstraintSet.PARENT_ID
//            layoutParams.startToStart = ConstraintSet.PARENT_ID
//            layoutParams.topToTop = ConstraintSet.PARENT_ID
//            layoutParams.setMargins(0, 150, 0, 0)
//            riskIndicator.layoutParams = layoutParams
//            binding.riskIndicatorLayout.addView(riskIndicator)
        }
    }

    fun getRiskLevel(rpqScore : Int) {
        if (!riskLevels.isNullOrEmpty()) {
            for (rsk in riskLevels!!) {
                if (rpqScore >= rsk.minimumScore && rpqScore <= rsk.maximumScore) {
                    userRiskLevel = rsk.riskLevel
                }
            }
        }
    }

    fun initViews() {
        binding.saveBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        mutualFundsAdapter = ItemProfileMutualFundsAdapter()
        binding.profileMutualFundsRv.apply {
            adapter = mutualFundsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        pendingDocumentsAdapter = ItemProfilePendingDocumentsAdapter()
        binding.profilePendingDocumentsRv.apply {
            adapter = pendingDocumentsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        updateDocumentsAdapter = ItemProfileUpdateDocumentsAdapter()
        binding.profileUpdatedDocumentsRv.apply {
            adapter = updateDocumentsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        HBLPreferenceManager.getDocumentsTypesResponse(requireContext())?.let { dct ->
            pendingDocumentsAdapter.setDocumentTypes(dct.result)
            updateDocumentsAdapter.setDocumentTypes(dct.result)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}