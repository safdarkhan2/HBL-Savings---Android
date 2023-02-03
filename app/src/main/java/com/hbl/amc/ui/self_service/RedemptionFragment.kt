package com.hbl.amc.ui.self_service

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaos.view.PinView
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentRedemptionBinding
import com.hbl.amc.domain.RequestDTO.SaveRedemptionInfoDTO
import com.hbl.amc.domain.RequestDTO.SendOtpDTO
import com.hbl.amc.domain.RequestDTO.VerifyOtpDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.ui.Generic.ItemInstructionsAdapter
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


class RedemptionFragment : Fragment(), KoinComponent {

    private var _binding: FragmentRedemptionBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val selfServiceViewModel by viewModel<SelfServiceViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val customerViewModel by viewModel<CustomerInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var fundsList: List<RedemptionFunds>? = null
    var redemptionTypesList: List<RedemptionType>? = null
    var instructionsList: List<RedemptionInstructions>? = null
    var disclaimerList: List<RedemptionDisclaimer>? = null
    var bankInfo: RedemptionBankInfo? = null

    var selectedFund: String = ""
    var selectedFundPos = 0
    var selectedRedemptionType = 0
    var isAllUnitsSelected = false
    var amount: Double = 0.0

    var verifyBtn: AppCompatButton? = null
    var resendBtn: AppCompatButton? = null
    var pinView: PinView? = null
    var otpDetail: TextView? = null
    var cdt: CountDownTimer? = null
    var darNumber : String? = null

    var dialog: AppCompatDialog? = null
    private lateinit var instructionsAdapter: ItemInstructionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRedemptionBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModels()
        initViews()

        /*selfServiceViewModel.getRedemptionInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            "1df93f23-087e-4fa1-e166-08d9b2f21152"
        )*/

        selfServiceViewModel.getRedemptionInfoLookup(
            HBLPreferenceManager.getToken(requireContext()),
            HBLPreferenceManager.getCustomerID(
                requireContext()
            )
        )

        return view
    }

    fun initViewModels() {
        val fundsArrayList: ArrayList<String> = ArrayList()
        val redemptionTypesArrayList: ArrayList<String> = ArrayList()

        selfServiceViewModel.getRedemptionInfoApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        bankInfo = it.response?.result?.bankInfo
                        fundsList = it.response?.result?.fundsList
                        redemptionTypesList = it.response?.result?.redumptionTypeList
                        instructionsList = it.response?.result?.instructionsList
                        disclaimerList = it.response?.result?.disclaimerList

                        binding.nameTv.text = bankInfo?.name
                        binding.accountNumberTv.text = bankInfo?.ibanNumber
                        binding.bankNameTv.text = bankInfo?.bankName
//                    binding.cnicTv.text = bankInfo?.customerNIC

                        binding.totalBalanceTv.text = "PKR " + bankInfo?.totalBalance?.let { tb ->
                            formatAmount(
                                tb
                            )
                        }

                        binding.transcationsAllowedTv.text =
                            bankInfo?.transactionAllowed.toString() + "(Per Day)"


                        fundsArrayList.add(getString(R.string.select))
                        redemptionTypesArrayList.add(getString(R.string.select))

                        fundsList?.forEach { f ->
                            fundsArrayList.add(f.name)
                        }

                        redemptionTypesList?.forEach { rt ->
                            redemptionTypesArrayList.add(rt.name)
                        }

                        setUpFundsSpinner(funds = fundsArrayList)
                        setUpRedemptionTypesSpinner(types = redemptionTypesArrayList)

                        instructionsList?.let { insList ->
                            instructionsAdapter.setInstructions(insList)
                        }


                        binding.ibftLimitTv.text =
                            "PKR ${formatAmount(fundsList!![0].ibftLimitPerDay)}"
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
                        Log.d("redemption lookups info error", it1)
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

        selfServiceViewModel.saveRedemptionInfoApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        val st = it?.response?.result
                        println(st)
                        if (st != null) {
                            darNumber = st.darNumber
                            showOTPDialog(
                                st.maskMobile,
                                st.transactionID,
                                st.expiryDate,
                                st.issueDate
                            )
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
                    it?.error?.message?.let { it1 ->
                        Log.d("redemption info error", it1)
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

        customerViewModel.resendOTPApi.observe(this, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it?.response?.let { it1 ->
                        Log.d("otp response", it1.result.transactionID)

                        cdt?.let { cd -> cd.start() }
                        pinView?.setText("")
                        verifyBtn!!.isEnabled = false
                        resendBtn!!.isEnabled = false
                        verifyBtn!!.setTextColor(
                            ContextCompat.getColor(
                                this.requireContext(),
                                R.color.hbl_main_green
                            )
                        )
                        resendBtn!!.setTextColor(
                            ContextCompat.getColor(
                                this.requireContext(),
                                R.color.hbl_main_green
                            )
                        )
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 ->
                        Log.d("customer info error", it1)
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

        customerViewModel.verifyOTPApi.observe(this, Observer {

            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {

                        dialog?.dismiss()

                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.thank_you),
                            "Redemption Request Submitted Successfully \n Reference No: $darNumber",
                            R.drawable.ic_check,
                            getString(R.string.ok)
                        ) {
                            findNavController().popBackStack()
                        }
                    }
                    else{
                        it.response?.message?.let { it1 ->
                            DialogUtils.showAlertDialog(
                                requireContext(),
                                layoutInflater,
                                getString(R.string.oops),
                                it1,
                                R.drawable.ic_check,
                                getString(R.string.ok)
                            ) {}
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {

                    it?.response?.let { vpt ->
                        vpt?.message?.let { msg ->
                            DialogUtils.showAlertDialog(requireContext(),
                                layoutInflater,
                                "Oops!",
                                msg,
                                R.drawable.ic_alert,
                                "OK",
                                onOkPressed = {

                                })
                        }
                    }
                    it?.error?.message?.let { it1 -> Log.d("verify otp error", it1) }
                }
            }
        })

        selfServiceViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })

        customerViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    private fun setUpFundsSpinner(funds: List<String>) {
        binding.selectFundSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, funds)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fundsList?.let {
                        selectedFund = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            ""
                        }
                        selectedFundPos = p2 - 1

                        if (p2 > 0) {
                            if (fundsList!![p2 - 1].isInstantTransferAllowed) {
                                binding.fundsBalanceLayout.visibility = VISIBLE
                                binding.selectedFundNameTv.text =
                                    "Balance in " + fundsList!![p2 - 1].name
                                binding.selectedFundBalanceTv.text =
                                    "PKR ${doubleToStringNoDecimal(fundsList!![p2 - 1].fundBalance)}"

                                selectedRedemptionType = 3001
                                binding.ibftLimitLayout.visibility = VISIBLE
                                binding.amountLayout.visibility = VISIBLE
                                binding.amountInputEdittext.text = null
                                binding.redemptionRequestRadiogroup.visibility = GONE
                                binding.howToRedeemSpinner.setSelection(1)
                                binding.howToRedeemSpinner.isEnabled = true
                            } else {
                                selectedRedemptionType = 3002
                                binding.fundsBalanceLayout.visibility = VISIBLE
                                binding.selectedFundNameTv.text =
                                    "Redeemable Balance"
                                binding.selectedFundBalanceTv.text =
                                    "PKR ${doubleToStringNoDecimal(fundsList!![p2 - 1].fundBalance)}"

                                binding.ibftLimitLayout.visibility = GONE
                                binding.redemptionRequestRadiogroup.visibility = VISIBLE
                                binding.redemptionRequestRadiogroup.clearCheck()
                                binding.amountLayout.visibility = GONE
                                binding.howToRedeemSpinner.setSelection(2)
                                binding.howToRedeemSpinner.isEnabled = false
                            }

                        } else {
                            binding.fundsBalanceLayout.visibility = GONE
                        }
                    }
                    toggleContinueBtn(validateInput())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setUpRedemptionTypesSpinner(types: List<String>) {
        binding.howToRedeemSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    redemptionTypesList?.let {
                        selectedRedemptionType = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        if (selectedRedemptionType == 3001) {
                            binding.ibftLimitLayout.visibility = VISIBLE
                            binding.amountLayout.visibility = VISIBLE
                            binding.amountInputEdittext.text = null
                            binding.redemptionRequestRadiogroup.visibility = GONE
                            isAllUnitsSelected = false
                        } else if (selectedRedemptionType == 3002) {
                            binding.ibftLimitLayout.visibility = GONE
                            binding.redemptionRequestRadiogroup.visibility = VISIBLE
                            binding.redemptionRequestRadiogroup.clearCheck()
                            binding.amountLayout.visibility = GONE
                        } else {
                            binding.ibftLimitLayout.visibility = GONE
                            binding.amountLayout.visibility = GONE
                            binding.redemptionRequestRadiogroup.visibility = GONE
                        }

                        toggleContinueBtn(validateInput())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    fun initViews() {

        instructionsAdapter = ItemInstructionsAdapter()
        binding.instructionsRv.apply {
            adapter = instructionsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.amountInputEdittext.setupEditText(requireContext(), binding.amountLayout)

        binding.continueBtn.setOnClickListener {
            if (binding.amountLayout.error == null) {
                val saveRedemptionInfoDTO = SaveRedemptionInfoDTO(
                    amount,
                    HBLPreferenceManager.getCustomerID(requireContext()),
//                    "1df93f23-087e-4fa1-e166-08d9b2f21152",
                    selectedFund,
                    fundsList!![0].ibftLimitPerDay,
                    !isAllUnitsSelected,
                    selectedRedemptionType,
                    "87754FB2-2704-46A4-8F5F-61EBF3ECC2DE",//TODO Change this after getting from backend team
                    null
                )


                disclaimerList?.get(0)?.let { it1 ->
                    DialogUtils.showDisclaimerDialog(requireContext(), it1.description, onAcceptChecked = {
                        //doSomethingHere()
                        selfServiceViewModel.saveRedemptionInfo(
                            HBLPreferenceManager.getToken(
                                requireContext()
                            ), saveRedemptionInfoDTO
                        )
                    })
                }
            }
        }

        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.redemptionRequestRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.redemption_request_allunits) {
                binding.amountLayout.visibility = GONE
                isAllUnitsSelected = true
            } else if (checkedId == R.id.redemption_request_amount) {
                binding.amountLayout.visibility = VISIBLE
                binding.amountInputEdittext.text = null
                isAllUnitsSelected = false
            }
            toggleContinueBtn(validateInput())
        }

        binding.amountInputEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.amountInputEdittext.text.toString().isNotEmpty()) {
                    amount = binding.amountInputEdittext.text.toString().toDouble()
                    if (amount < 1000) {
                        binding.amountLayout.error =
                            "Amount should be more than PKR 1000"
                        toggleContinueBtn(validateInput())
                        return
                    } else {
                        binding.amountLayout.error = null
                    }
                    if (selectedRedemptionType == 3001) {
                        if (amount > fundsList!![0].ibftLimitPerDay.toDouble()) {
                            binding.amountLayout.error =
                                "Amount should be less than or equal to ibft limit per day"
                        } else {
                            binding.amountLayout.error = null
                        }
                    } else if (selectedRedemptionType == 3002) {
                        amount = binding.amountInputEdittext.text.toString().toDouble()
                        if (amount > fundsList!![selectedFundPos].fundBalance.toDouble()) {
                            binding.amountLayout.error =
                                "Amount should be less than or equal to fund balance"
                        } else {
                            binding.amountLayout.error = null
                        }
                    }
                }
                toggleContinueBtn(validateInput())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun doubleToStringNoDecimal(d: Int): String? {
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###")
        return formatter.format(d)
    }

    private fun validateInput(): Boolean {
        return selectedFund != "" &&
                selectedRedemptionType > 0 &&
                (isAllUnitsSelected || (binding.amountLayout.error == null &&
                        binding.amountInputEdittext.text.toString().isNotEmpty()))

    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun showOTPDialog(
        maskMobile: String,
        transactionID: String,
        expiryDate: String,
        issueDate: String
    ) {
        //val dialog = AppCompatDialog(this, R.style.Theme_Dialog)
        val dlg: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)

        val dialog = layoutInflater.inflate(R.layout.otp_dialog, null)
        dlg.setView(dialog)
        dlg.setCancelable(false)
        dlg.create()

        val timerView = dialog.findViewById<TextView>(R.id.timer_tv)
        verifyBtn = dialog.findViewById(R.id.otp_verify_btn)
        resendBtn = dialog.findViewById(R.id.resend_otp_btn)
        pinView = dialog.findViewById(R.id.pin_view)
        otpDetail = dialog.findViewById(R.id.otp_detail_msg_tv)

        otpDetail?.text = resources.getString(R.string.otp_detail_label) + maskMobile

        verifyBtn!!.isEnabled = false
        resendBtn!!.isEnabled = false
//        verifyBtn!!.setTextColor(ContextCompat.getColor(this, R.color.hbl_main_green))
//        resendBtn!!.setTextColor(ContextCompat.getColor(this, R.color.hbl_main_green))

        pinView!!.addTextChangedListener {
            verifyBtn!!.isEnabled = it?.length == 6

            /*if (verifyBtn!!.isEnabled) {
                verifyBtn!!.setTextColor(Color.WHITE)
            } else {
                verifyBtn!!.setTextColor(ContextCompat.getColor(this, R.color.hbl_main_green))
            }*/
        }

        cdt = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainedSecs = millisUntilFinished / 1000

                val min = if ((remainedSecs / 60) < 10) {
                    "0" + (remainedSecs / 60)
                } else {
                    (remainedSecs / 60)
                }

                val sec = if ((remainedSecs % 60) < 10) {
                    "0" + (remainedSecs % 60)
                } else {
                    (remainedSecs % 60)
                }

                timerView!!.text = "$min:$sec"

            }

            override fun onFinish() {
                // timerView!!.setText("try again")
                resendBtn!!.isEnabled = true
//                resendBtn!!.setTextColor(Color.WHITE)
//                Toast.makeText(this, "Resend button enabled!", LENGTH_LONG).show()
            }
        }

        cdt?.start()

        val dg = dlg.show()

        verifyBtn?.setOnClickListener {
            dg.dismiss()
            val verifyOtpDTO = VerifyOtpDTO(
                pinView!!.text.toString(),
                transactionID = transactionID,
                transactionType = 1002
            )

            customerViewModel.verifyOTP(
                HBLPreferenceManager.getToken(requireContext()),
                verifyOtpDTO = verifyOtpDTO
            )
        }

        resendBtn!!.setOnClickListener {

            val sendOtpDTO = SendOtpDTO(
                "",
                expiryDate,
                false,
                false,
                issueDate,
                transactionID = transactionID
            )
            customerViewModel.resendOTP(
                HBLPreferenceManager.getToken(requireContext()),
                sendOtpDTO = sendOtpDTO
            )
        }

        val closeButton = dialog?.findViewById<ImageView>(R.id.close_btn) as ImageView

        closeButton.setOnClickListener {
            dg.dismiss()
        }

//        dg.show()
    }

}
