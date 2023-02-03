package com.hbl.amc.ui.productInformation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentProductOtherInfoBinding
import com.hbl.amc.domain.RequestDTO.SaveOtherInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.OtherInfoResult
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.API_ID
import com.hbl.amc.ui.Disclaimers.DisclaimersActivity
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.setupEditText
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class ProductOtherInfoFragment : Fragment(), KoinComponent {

    var _binding: FragmentProductOtherInfoBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val productViewModel by viewModel<ProductInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductOtherInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()
        initView()
        productViewModel.getOtherInfo(
            HBLPreferenceManager.getToken(requireContext()),
            HBLPreferenceManager.getCustomerID(requireContext())
        )

        return view
    }

    fun initViewModel() {
        productViewModel.getOtherInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { result ->
                        showDetails(result)
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
                    it?.error?.message?.let { it1 -> Log.d("funds error", it1) }
                }
            }
        })

        productViewModel.saveOtherInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    //save user in shared prefs
//                    findNavController().navigate(R.id.documentChecklistFragment)
                    if (it.response?.status == "success") {
                        val intent = Intent(requireContext(), DisclaimersActivity::class.java)
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
                    it?.error?.message?.let { it1 -> Log.d("CRS save info error", it1) }
                }
            }
        })

        productViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    private fun showDetails(result: OtherInfoResult) {
        binding.ntnNoEt.setText(result.ntn)
        binding.retirementAgeEt.setText(result.retirementAge.toString())
    }

    private fun initView() {
        binding.appBar.title.text = getString(R.string.product_selection)

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        setupProgress()

        binding.ntnNoEt.setupEditText(requireContext(), binding.ntnNo)
        binding.retirementAgeEt.setupEditText(requireContext(), binding.retirementAge)

        binding.continueBtn.setOnClickListener {

            val saveOtherInfoDTO = SaveOtherInfoDTO(
                binding.ntnNoEt.text.toString(),
                HBLPreferenceManager.getCustomerID(requireContext()),
                binding.retirementAgeEt.text.toString().toInt(),
                "BE57F567-8C5B-4300-902B-89E51FFE9DA3"
            )
            productViewModel.saveOtherInfo(
                HBLPreferenceManager.getToken(requireContext()),
                saveOtherInfoDTO
            )
        }

        binding.appBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fundsBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.productsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_productOtherInfoFragment_to_productSelectionFragment)
        }

        binding.ntnNoEt.setupEditText(requireContext(), binding.ntnNo)
        binding.retirementAgeEt.setupEditText(requireContext(), binding.retirementAge)

        binding.ntnNoEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.ntnNoEt.length() < 3) {
                    binding.ntnNo.error = "Minimum length should be 3"
                } else {
                    binding.ntnNo.error = null
                }

                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.retirementAgeEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!binding.retirementAgeEt.text.isNullOrEmpty()) {
                    if (binding.retirementAgeEt.text.toString().toInt() < 60 || binding.retirementAgeEt.text.toString().toInt() > 70) {
                        binding.retirementAge.error = "Retirement age should be between 60 and 70"
                    } else {
                        binding.retirementAge.error = null
                    }

                    toggleContinueBtn(validateInput())
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateInput(): Boolean {
        return !(binding.ntnNoEt.length() < 3 || (!binding.retirementAgeEt.text.isNullOrEmpty() && binding.retirementAge.error != null))
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 100
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "80%"
        binding.appBar.stepTv.text = "Step 3/4"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}