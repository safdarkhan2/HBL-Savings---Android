package com.hbl.amc.ui.self_service

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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentConversionBinding
import com.hbl.amc.domain.RequestDTO.SaveConversionInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.formatAmount
import com.hbl.amc.utils.setupEditText
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ConversionFragment : Fragment(), KoinComponent {
    private var _binding: FragmentConversionBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val selfServiceViewModel by viewModel<SelfServiceViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var fromFundsList: List<FromFund>? = null
    var toFundsList: List<ToFund>? = null
    var unitTypesList: List<ConversionUnitTypes>? = null
    var incomePlansList: List<ConversionIncomePlan>? = null
    var paymentFrequencyList: List<ConversionPaymentFrequency>? = null
    var specifyAmountList: List<ConversionSpecifyAmount>? = null
    var instructionsList: List<ConversionInstructions>? = null
    var disclaimerList: List<ConversionDisclaimer>? = null

    var selectedFromFund: String = ""
    var selectedToFund: String = ""
    var selectedUnitTypes: Int = 0
    var selectedIncomePlan: Int = 0
    var selectedPaymentFrequency: Int = 0
    var selectedSpecifyAmount: Int = 0

    var isAllUnitsSelected = false
    var amount = 0.0
    var isSelectedSpecialFund = false
    var isUnitTypeGrowth = true
    var isFundPriceGreaterThan500k = false
    var fromFundBalance = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConversionBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()
        initView()

        /*selfServiceViewModel.getConversionInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            "1df93f23-087e-4fa1-e166-08d9b2f21152"
        )*/

        selfServiceViewModel.getConversionInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            HBLPreferenceManager.getCustomerID(
                requireContext()
            )
        )
        return view
    }

    fun initViewModel() {
        val toFundsArrayList: ArrayList<String> = ArrayList()
        val fromFundsArrayList: ArrayList<String> = ArrayList()
        val specifyAmountsArrayList: ArrayList<String> = ArrayList()
        val paymentFrequencyArrayList: ArrayList<String> = ArrayList()

        selfServiceViewModel.getConversionInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        toFundsList = it.response?.result?.toFundList
                        fromFundsList = it.response?.result?.fromFundList
                        specifyAmountList = it.response?.result?.specifyAmountList
                        paymentFrequencyList = it.response?.result?.paymentFrequencyList
                        unitTypesList = it.response?.result?.unitTypesList
                        incomePlansList = it.response?.result?.incomePlanList
                        instructionsList = it.response?.result?.instructionsList
                        disclaimerList = it.response?.result?.disclaimerList

                        toFundsArrayList.add(getString(R.string.select))
                        fromFundsArrayList.add(getString(R.string.select))
                        specifyAmountsArrayList.add(getString(R.string.select))
                        paymentFrequencyArrayList.add(getString(R.string.select))

                        toFundsList?.forEach { fFund ->
                            toFundsArrayList.add(fFund.name)
                        }

                        fromFundsList?.forEach { tFund ->
                            fromFundsArrayList.add(tFund.name)
                        }

                        specifyAmountList?.forEach { sa ->
                            specifyAmountsArrayList.add(sa.name)
                        }

                        paymentFrequencyList?.forEach { pf ->
                            paymentFrequencyArrayList.add(pf.name)
                        }

                        setUpToFundsSpinner(toFundsArrayList)
                        setUpSpecifyAmountSpinner(specifyAmountsArrayList)
                        setUpPaymentFrequencySpinner(paymentFrequencyArrayList)
                        setUpFromFundsSpinner(fromFundsArrayList)

                        binding.instructionTv.text = instructionsList!![0].title
                        var desc = instructionsList!![0].description
                        binding.expandTextView.text = "$desc \n"
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
                        Log.d("Conversion lookups info error", it1)
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

        selfServiceViewModel.saveConversionInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        Log.d("conversion_response", it.response.toString())
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.success),
                            "Conversion Completed!\nReference No. ${it.response.result.darNumber}",
                            R.drawable.ic_check,
                            getString(R.string.ok)
                        ) {
                            findNavController().popBackStack()
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
                    it.error?.message?.let { it1 ->
                        Log.d("Conversion save info error", it1)
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
    }

    private fun setUpToFundsSpinner(toFunds: List<String>) {
        binding.toFundSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, toFunds)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    toFundsList?.let {
                        selectedToFund = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            ""
                        }

                        binding.conversionRequestRadiogroup.visibility = GONE
                        binding.amountLayout.visibility = GONE
                        binding.unitTypeLayout.visibility = GONE
                        binding.incomePlanRadiogroup.visibility = GONE
                        binding.paymentFrequencyLayout.visibility = GONE
                        binding.specifyAmountLayout.visibility = GONE

                        if (p2 > 0) {
                            binding.conversionRequestRadiogroup.visibility = VISIBLE
                            isSelectedSpecialFund = toFundsList!![p2 - 1].isOfferingIncomeUnit
                            if (!isSelectedSpecialFund) {
                                binding.conversionRequestRadiogroup.clearCheck()
                                binding.unitTypeLayout.visibility = GONE
                                binding.amountLayout.visibility = GONE
                            }
                        } else {
                            binding.amountLayout.visibility = GONE
                            binding.amountInputEdittext.text = null
                            binding.conversionRequestRadiogroup.visibility = GONE

                        }
                    }
                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpFromFundsSpinner(fromFunds: List<String>) {
        binding.fromFundSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fromFunds)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fromFundsList?.let {
                        selectedFromFund = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            ""
                        }

                        if (p2 > 0) {
                            binding.fundBalanceLayout.visibility = VISIBLE
                            binding.fundBalanceTitleTv.text =
                                "Balance in " + fromFundsList!![p2 - 1].name
                            binding.fundBalanceAmountTv.text =
                                "Rs. ${formatAmount(fromFundsList!![p2 - 1].fundBalance)}"
                            binding.toFundLayout.visibility = VISIBLE
                            isFundPriceGreaterThan500k =
                                fromFundsList!![p2 - 1].fundBalance >= 500000
                            fromFundBalance = fromFundsList!![p2 - 1].fundBalance
                        } else {
                            binding.fundBalanceLayout.visibility = GONE
                            binding.toFundLayout.visibility = GONE
                            binding.conversionRequestRadiogroup.visibility = GONE
                            binding.amountLayout.visibility = GONE
                            binding.unitTypeLayout.visibility = GONE
                            binding.incomePlanRadiogroup.visibility = GONE
                            binding.paymentFrequencyLayout.visibility = GONE
                            binding.specifyAmountLayout.visibility = GONE

                        }
                    }
                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpSpecifyAmountSpinner(amounts: List<String>) {
        binding.specifyAmountSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, amounts)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    specifyAmountList?.let {
                        selectedSpecifyAmount = if (p2 > 0) {
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

    private fun setUpPaymentFrequencySpinner(paymentFrequency: List<String>) {
        binding.paymentFrequencySpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, paymentFrequency)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    paymentFrequencyList?.let {
                        selectedPaymentFrequency = if (p2 > 0) {
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

    private fun initView() {

        binding.amountInputEdittext.setupEditText(requireContext(), binding.amountLayout)
        binding.incomeTv.background.alpha = 0

        binding.growthTv.setOnClickListener {
            isUnitTypeGrowth = true
            binding.growthTv.background.alpha = 255
            binding.incomeTv.background.alpha = 0
            binding.growthTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text
                )
            )
            binding.incomeTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text_2
                )
            )
            selectedUnitTypes = unitTypesList!![0].id

            binding.incomePlanRadiogroup.visibility = GONE
            binding.specifyAmountLayout.visibility = GONE
            binding.paymentFrequencyLayout.visibility = GONE

            toggleContinueBtn(validateInput())
        }

        binding.incomeTv.setOnClickListener {
            isUnitTypeGrowth = false
            binding.incomeTv.background.alpha = 255
            binding.growthTv.background.alpha = 0
            binding.incomeTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text
                )
            )
            binding.growthTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_text_2
                )
            )
            selectedUnitTypes = unitTypesList!![1].id
            binding.incomePlanRadiogroup.visibility = VISIBLE

            toggleContinueBtn(validateInput())
        }

        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.conversionRequestRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.conversion_request_allunits) {
                binding.amountLayout.visibility = GONE
                binding.amountInputEdittext.text = null
                isAllUnitsSelected = true
            } else if (checkedId == R.id.conversion_request_amount) {
                binding.amountLayout.visibility = VISIBLE
                binding.unitTypeLayout.visibility = GONE
                binding.incomePlanRadiogroup.visibility = GONE
                binding.specifyAmountLayout.visibility = GONE
                binding.paymentFrequencyLayout.visibility = GONE
                isAllUnitsSelected = false
            }

            if (isAllUnitsSelected && isFundPriceGreaterThan500k && isSelectedSpecialFund) {
                binding.unitTypeLayout.visibility = VISIBLE
                selectedUnitTypes = unitTypesList!![0].id
                binding.incomeTv.background.alpha = 0
                binding.growthTv.background.alpha = 255
            }
            toggleContinueBtn(validateInput())
        }

        binding.amountInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.amountInputEdittext.text.toString().isNotEmpty()) {
                    amount = binding.amountInputEdittext.text.toString().toDouble()

                    if (amount > fromFundBalance) {
                        binding.amountInputEdittext.setText("" + fromFundBalance)
                        binding.amountLayout.error =
                            "Amount cannot be greater than from fund balance"
                    } else {
                        binding.amountLayout.error = null
                    }

                    if (amount >= 500000 && isSelectedSpecialFund) {
                        binding.unitTypeLayout.visibility = VISIBLE
                        selectedUnitTypes = unitTypesList!![0].id
                        binding.incomeTv.background.alpha = 0
                        binding.growthTv.background.alpha = 255
                    } else {
                        binding.unitTypeLayout.visibility = GONE
                    }
                }
                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.incomePlanRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.income_plan_fixed) {
                binding.specifyAmountLayout.visibility = VISIBLE
                binding.paymentFrequencyLayout.visibility = VISIBLE
                selectedIncomePlan = incomePlansList!![1].id

            } else if (checkedId == R.id.income_plan_flexible) {
                binding.specifyAmountLayout.visibility = VISIBLE
                binding.paymentFrequencyLayout.visibility = VISIBLE
                selectedIncomePlan = incomePlansList!![0].id
            }
            toggleContinueBtn(validateInput())
        }

        binding.continueBtn.setOnClickListener {
            val saveConversionInfoDTO = SaveConversionInfoDTO(
                amount,
                HBLPreferenceManager.getCustomerID(requireContext()),
//                "1df93f23-087e-4fa1-e166-08d9b2f21152",
                selectedFromFund,
                selectedPaymentFrequency,
                selectedSpecifyAmount,
                selectedIncomePlan,
                selectedToFund,
                "53573644-2B7A-439C-8467-30C8BA5101CB",
                selectedUnitTypes,
                null
            )

            disclaimerList?.get(0)?.let { it1 ->
                DialogUtils.showDisclaimerDialog(requireContext(), it1.description, onAcceptChecked = {
                    //doSomethingHere()
                    selfServiceViewModel.saveConversionInfo(
                        HBLPreferenceManager.getToken(requireContext()),
                        saveConversionInfoDTO
                    )
                })
            }

        }
    }

    fun doubleToStringNoDecimal(d: Double): String? {
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###")
        return formatter.format(d)
    }

    private fun validateInput(): Boolean {


        return selectedToFund != "" &&
                selectedFromFund != "" &&
                ((binding.conversionRequestRadiogroup.isVisible && isAllUnitsSelected && !binding.unitTypeLayout.isVisible) ||
                        (binding.conversionRequestRadiogroup.isVisible && !isAllUnitsSelected && binding.amountLayout.error == null && binding.amountInputEdittext.text.toString()
                            .isNotEmpty()) ||
                        (binding.unitTypeLayout.isVisible && selectedUnitTypes != 0)) &&
                (!binding.incomePlanRadiogroup.isVisible || selectedIncomePlan != 0) &&
                (!binding.specifyAmountLayout.isVisible || selectedSpecifyAmount != 0) &&
                (!binding.paymentFrequencyLayout.isVisible || selectedPaymentFrequency != 0)

    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }
}
