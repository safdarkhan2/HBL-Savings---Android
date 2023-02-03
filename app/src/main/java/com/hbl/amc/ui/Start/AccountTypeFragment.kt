package com.hbl.amc.ui.Start

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.AccountTypeFragmentBinding
import com.hbl.amc.databinding.FragmentAccountTypeBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.AccountTypesResult
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.hbl.amc.utils.DialogUtils.Companion.showProfileBasedDialog
import com.hbl.amc.utils.formatAmount
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.NumberFormat
import java.util.*


class AccountTypeFragment : Fragment() {

    private var _binding: AccountTypeFragmentBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val customerViewModel by viewModel<CustomerInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var accountTypesResult : AccountTypesResult? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding == null) {
            _binding = AccountTypeFragmentBinding.inflate(inflater, container, false)

            initViewModel()
            initViews()

            customerViewModel.getAccountTypes()
        }

        return binding.root
    }

    fun initViewModel() {
        customerViewModel.accountTypesApi.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        accountTypesResult = it.response.result

                        HBLPreferenceManager.saveAccountTypeResult(requireContext(), accountTypesResult!!)

                        val acType1 = accountTypesResult?.accountTypesList!!.find { at1 -> at1.code == "01-BB" }
                        acType1?.let { ac ->
                            binding.acType1.text = ac.name
                            if (ac.accountLimit == 0) {
                                binding.investmentLimitAnnual1.text = "No Limit"
                            } else {
                                binding.investmentLimitAnnual1.text = formatAmount(ac.accountLimit)
                            }

                            if (ac.anyTimeInvestment == 0) {
                                binding.investmentLimitAny1.text = "No Limit"
                            } else {
                                binding.investmentLimitAny1.text = formatAmount(ac.anyTimeInvestment)
                            }

                            if (ac.transactionCap == 0) {
                                binding.transactionCap1.text = "No Limit"
                            } else {
                                binding.transactionCap1.text = formatAmount(ac.transactionCap)
                            }

                            binding.products1.text = ac.products
                            binding.products1.paintFlags = binding.products1.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                        }

                        val acType2 = accountTypesResult?.accountTypesList!!.find { at2 -> at2.code == "01-KS" }
                        acType2?.let { ac ->
                            binding.acType2.text = ac.name
                            if (ac.accountLimit == 0) {
                                binding.investmentLimitAnnual2.text = "No Limit"
                            } else {
                                binding.investmentLimitAnnual2.text = formatAmount(ac.accountLimit)
                            }

                            if (ac.anyTimeInvestment == 0) {
                                binding.investmentLimitAny2.text = "No Limit"
                            } else {
                                binding.investmentLimitAny2.text = formatAmount(ac.anyTimeInvestment)
                            }

                            if (ac.transactionCap == 0) {
                                binding.transactionCap2.text = "No Limit"
                            } else {
                                binding.transactionCap2.text = formatAmount(ac.transactionCap)
                            }

                            binding.products2.text = ac.products
                            binding.products2.paintFlags = binding.products2.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                        }
                    } else {
                        //handle error here if any
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    //handle error here if any
                }
            }
        })

        customerViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })
    }

    private fun initViews() {

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.acBtn1.setOnClickListener {
            accountTypesResult?.let { atr ->
                val acType = atr.accountTypesList.find { it.code == "01-BB" }
                acType?.let { ac ->
                    HBLPreferenceManager.saveAccountType(
                        requireContext(),
                        ac
                    )
                    findNavController().navigate(R.id.action_accountTypeFragment_to_welcomeFragment)
                }
            } ?: run {
                customerViewModel.getAccountTypes()
            }
        }

        binding.acBtn2.setOnClickListener {
            accountTypesResult?.let { atr ->
                val acType = atr.accountTypesList.find { it.code == "01-KS" }
                acType?.let { ac ->
                    HBLPreferenceManager.saveAccountType(
                        requireContext(),
                        ac
                    )
                    findNavController().navigate(R.id.action_accountTypeFragment_to_welcomeFragment)
                }
            } ?: run {
                customerViewModel.getAccountTypes()
            }
        }

        binding.products1.setOnClickListener {
            accountTypesResult?.let { act ->
                val instruction = act.instructionList.find { it.code == "01-BB" }
                instruction?.let { inst ->

                    showProfileBasedDialog(
                        requireContext(), layoutInflater, inst.title,
                        inst.description, getString(R.string.proceed)
                    ) {
                        accountTypesResult?.let { atr ->
                            val acType = atr.accountTypesList.find { it.code == "01-BB" }
                            acType?.let { ac ->
                                HBLPreferenceManager.saveAccountType(
                                    requireContext(),
                                    ac
                                )
                                findNavController().navigate(R.id.action_accountTypeFragment_to_welcomeFragment)
                            }
                        } ?: run {
                            customerViewModel.getAccountTypes()
                        }
                    }
                }
            }
        }

        binding.products2.setOnClickListener {
            accountTypesResult?.let { act ->
                val instruction = act.instructionList.find { it.code == "01-KS" }
                instruction?.let { inst ->

                    showProfileBasedDialog(
                        requireContext(), layoutInflater, inst.title,
                        inst.description, getString(R.string.proceed)
                    ) {
                        accountTypesResult?.let { atr ->
                            val acType = atr.accountTypesList.find { it.code == "01-KS" }
                            acType?.let { ac ->
                                HBLPreferenceManager.saveAccountType(
                                    requireContext(),
                                    ac
                                )
                                findNavController().navigate(R.id.action_accountTypeFragment_to_welcomeFragment)
                            }
                        } ?: run {
                            customerViewModel.getAccountTypes()
                        }
                    }
                }
            }
        }
    }
}