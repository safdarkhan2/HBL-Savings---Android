package com.hbl.amc.ui.self_service

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.CustomPercentageFormatter
import com.hbl.amc.BuildConfig
import com.hbl.amc.R
import com.hbl.amc.api_service.FileDownloadService
import com.hbl.amc.application.HBLApp
import com.hbl.amc.databinding.FragmentDashboardBinding
import com.hbl.amc.domain.RequestDTO.MutualFundAccountStatementDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.FOLIO_NUMBER
import com.hbl.amc.ui.FROM_DATE
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.ui.TO_DATE
import com.hbl.amc.ui.self_service.ServiceGenerator.createService
import com.hbl.amc.utils.DatePickerFragment
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.PerfectDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files.createFile
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import retrofit2.Call
import retrofit2.Response
import okio.IOException
import java.io.File
import java.io.OutputStream
import java.util.jar.Manifest
import android.provider.Browser
import android.util.Base64
import androidx.core.content.FileProvider
import com.google.android.gms.common.util.IOUtils
import com.google.common.io.Files.write
import com.hbl.amc.utils.AppUtils.viewPDF
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Callback
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.TimeUnit


class DashboardFragment : Fragment(), KoinComponent {

    private val STORAGE_PERMISSION_CODE: Int = 1000
    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val dashboardViewModel by viewModel<DashboardViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    val pieChartColors = listOf(
        "#E0E0E0",
        "#C1C1C1",
        "#9D9D9D",
        "#7D7D7D",
        "#727272",
        "#626262",
        "#535353",
        "#484848",
        "#434343",
        "#393939",
        "#292929",
        "#1F1F1F",
        "#000000"
    )

    val dataEntries = ArrayList<PieEntry>()
    val colors: ArrayList<Int> = ArrayList()

    lateinit var statementMonthBtns: List<AppCompatTextView>

    var fromDate = ""
    var toDate = ""

    var actualFromDay: String? = null
    var mon: Int? = null
    var actualFromMonth: String? = null
    var actualFromYear: String? = null

    var fromDateInMilliSec: Long = 0
    var toDateInMilliSec: Long = 0

    var folio_number: Int? = null

    var customerFundwiseBalanceList: List<CustomerFundwiseBalance>? = null
    var lastSixTransactionsList: List<LastSixTransactions>? = null
    var assetClasswisePortfolioList: List<AssestWisePortFolio>? = null

    var vpsFundWiseBalanceList: List<VPSDashboardFundWiseBalance>? = null
    var vpslastTenTransactionsList: List<VPSDashboardLastTenTransaction>? = null
    var vpsAssetClasswisePortfolioList: List<VPSAssestWisePortFolio>? = null

    var isMutualFundSelected = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root


        initViewModel()
        initViews()

        folio_number = requireActivity().intent.getStringExtra(FOLIO_NUMBER)?.toInt()
        folio_number?.let { folio ->
            dashboardViewModel.getMutualFundsDashboard(
                HBLPreferenceManager.getToken(requireContext()),
                folio, 3
            )
        }

        return view
    }

    fun initViewModel() {
        dashboardViewModel.getMutualFundDashboardApi.observe(
            viewLifecycleOwner,
            Observer {
                when (it?.responseStatus) {
                    LiveDataWrapper.Status.SUCCESS -> {
                        if (it.response?.status == "success") {
                            val t_bal = it.response.result.customerTotalBalance.totalBalance
                            val decFormat = PerfectDecimal(t_bal.toString(), 20, 2)?.toDouble()
                            binding.amountTv.text = "Rs. ${
                                decFormat?.let { it1 ->
                                    doubleToStringNoDecimal(
                                        it1
                                    )
                                }
                            }"

                            binding.fundsDataContentLayout.removeAllViews()
                            customerFundwiseBalanceList =
                                it.response.result.customerFundwiseBalanceList
                            for (i in 0 until customerFundwiseBalanceList?.size!!) {
                                var fundsValueLayout =
                                    layoutInflater.inflate(R.layout.dashboard_funds_values, null)
                                binding.fundsDataContentLayout.addView(fundsValueLayout)
                                fundsValueLayout.findViewById<AppCompatTextView>(R.id.fund_name_title).text =
                                    customerFundwiseBalanceList!![i].fund
                                fundsValueLayout.findViewById<AppCompatTextView>(R.id.units_value_tv).text =
                                    customerFundwiseBalanceList!![i].units
                                fundsValueLayout.findViewById<AppCompatTextView>(R.id.units_value_tv).tooltipText =
                                    customerFundwiseBalanceList!![i].units
                                fundsValueLayout.findViewById<AppCompatTextView>(R.id.nav_value_tv).text =
                                    customerFundwiseBalanceList!![i].navValue
                                fundsValueLayout.findViewById<AppCompatTextView>(R.id.nav_value_tv).tooltipText =
                                    customerFundwiseBalanceList!![i].navValue
                                fundsValueLayout.findViewById<AppCompatTextView>(R.id.value_tv).text =
                                    customerFundwiseBalanceList!![i].amount
                                fundsValueLayout.findViewById<AppCompatTextView>(R.id.value_tv).tooltipText =
                                    customerFundwiseBalanceList!![i].amount
                            }

                            binding.lastTransactionContentLayout.removeAllViews()
                            lastSixTransactionsList = it.response.result.lastSixTransactionsList

                            for (i in 0 until lastSixTransactionsList?.size!!) {
                                var lastTransactionsLayout =
                                    layoutInflater.inflate(
                                        R.layout.dashboard_last_transactions,
                                        null
                                    )
                                binding.lastTransactionContentLayout.addView(lastTransactionsLayout)

                                lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.fund_name_tv).text =
                                    lastSixTransactionsList!![i].fundName
                                lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.date_tv).text =
                                    lastSixTransactionsList!![i].transactionDate
                                lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.units_tv).text =
                                    lastSixTransactionsList!![i].units
                                lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.amount_tv).text =
                                    "Rs." +
                                            lastSixTransactionsList!![i].amount
                                lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.transaction_type_tv).text =
                                    lastSixTransactionsList!![i].transactionType

                            }

                            clearFields()
                            it.response.result.investmentData.let { invData ->
                                if (!invData.investments.isNullOrEmpty()) {
                                    binding.totalPuchasesTv.text =
                                        "Rs.${invData.investments}"
                                }
                                if (!invData.units.isNullOrEmpty()) {
                                    binding.totalPuchasesUnitsTv.text =
                                        "${invData.units} Units"
                                }
                            }

                            it.response.result.redemptionsData.let { redData ->
                                if (!redData.redemptions.isNullOrEmpty()) {
                                    binding.totalWithdrawlsTv.text =
                                        "Rs.${redData.redemptions}"
                                }
                                if (!redData.units.isNullOrEmpty()) {
                                    binding.totalWithdrawlsUnitsTv.text =
                                        "${redData.units} Units"
                                }
                            }

                            it.response.result.dividendData.let { divData ->
                                if (!divData.dividend.isNullOrEmpty()) {
                                    binding.totalDividendTv.text =
                                        "Rs.${divData.dividend}"
                                }
                                if (!divData.units.isNullOrEmpty()) {
                                    binding.totalDividendUnitsTv.text =
                                        "${divData.units} Units"
                                }
                            }

                            it.response.result.frontEndLoadData.let { felData ->
                                if (!felData.frontEndLoad.isNullOrEmpty()) {
                                    binding.frontendLoadOnPurchasesTv.text =
                                        "Rs.${felData.frontEndLoad}"
                                }
                                if (!felData.units.isNullOrEmpty()) {
                                    binding.frontendLoadOnPurchasesUnitsTv.text =
                                        "${felData.units} Units"
                                }
                            }

                            it.response.result.taxOnDividendData.let { todData ->
                                if (!todData.taxOnDividend.isNullOrEmpty()) {
                                    binding.totalTaxWithheldOnDividendTv.text =
                                        "Rs.${todData.taxOnDividend}"
                                }
                            }

                            it.response.result.capitalGaintax.let { cgtData ->
                                if (!cgtData.capitalTax.isNullOrEmpty()) {
                                    binding.totalCapitalGainTv.text =
                                        "Rs.${cgtData.capitalTax}"
                                }
                            }


                            dataEntries.clear()
                            colors.clear()
                            assetClasswisePortfolioList = it.response.result.assestWisePortFolio
                            for (i in 0 until assetClasswisePortfolioList?.size!!) {
                                dataEntries.add(
                                    PieEntry(
                                        assetClasswisePortfolioList!![i].investmentPercentage.toFloat(),
                                        assetClasswisePortfolioList!![i].fundCategory
                                    )
                                )
                                colors.add(Color.parseColor(pieChartColors[i]))
                            }
                            initPieChart()
                            setDataToPieChart()

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
                                    ) {
                                        folio_number?.let {
                                            dashboardViewModel.getMutualFundsDashboard(
                                                HBLPreferenceManager.getToken(requireContext()),
                                                it, 3
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LiveDataWrapper.Status.ERROR -> {
                        it.error?.message?.let { it1 ->
                            DialogUtils.showAlertDialog(
                                requireContext(),
                                layoutInflater,
                                getString(R.string.oops),
                                it1,
                                R.drawable.ic_alert,
                                getString(R.string.ok)
                            ) {
                                folio_number?.let {
                                    dashboardViewModel.getMutualFundsDashboard(
                                        HBLPreferenceManager.getToken(requireContext()),
                                        it, 3
                                    )
                                }
                            }
                        }
                    }
                }
            })

        dashboardViewModel.getVPSFundDashboardApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {

                        binding.fundsDataContentLayout.removeAllViews()
                        vpsFundWiseBalanceList = it.response.result.fundWiseBalance
                        for (i in 0 until vpsFundWiseBalanceList?.size!!) {
                            var fundsValueLayout =
                                layoutInflater.inflate(R.layout.dashboard_funds_values, null)
                            binding.fundsDataContentLayout.addView(fundsValueLayout)

                            fundsValueLayout.findViewById<AppCompatTextView>(R.id.fund_name_title).text =
                                vpsFundWiseBalanceList!![i].fund
                            fundsValueLayout.findViewById<AppCompatTextView>(R.id.units_value_tv).text =
                                vpsFundWiseBalanceList!![i].units
                            fundsValueLayout.findViewById<AppCompatTextView>(R.id.units_value_tv).tooltipText =
                                vpsFundWiseBalanceList!![i].units
                            fundsValueLayout.findViewById<AppCompatTextView>(R.id.nav_value_tv).text =
                                ""
                            fundsValueLayout.findViewById<AppCompatTextView>(R.id.nav_value_tv).tooltipText =
                                ""
                            fundsValueLayout.findViewById<AppCompatTextView>(R.id.value_tv).text =
                                vpsFundWiseBalanceList!![i].amount
                            fundsValueLayout.findViewById<AppCompatTextView>(R.id.value_tv).tooltipText =
                                vpsFundWiseBalanceList!![i].amount
                        }

                        binding.lastTransactionContentLayout.removeAllViews()
                        vpslastTenTransactionsList = it.response.result.lastTenTransactions

                        for (i in 0 until vpslastTenTransactionsList?.size!!) {
                            var lastTransactionsLayout =
                                layoutInflater.inflate(
                                    R.layout.dashboard_last_transactions,
                                    null
                                )
                            binding.lastTransactionContentLayout.addView(lastTransactionsLayout)

                            lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.fund_name_tv).text =
                                vpslastTenTransactionsList!![i].fundName
                            lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.date_tv).text =
                                vpslastTenTransactionsList!![i].transactionDate
                            lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.units_tv).text =
                                vpslastTenTransactionsList!![i].units
                            lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.amount_tv).text =
                                "Rs." +
                                        vpslastTenTransactionsList!![i].amount
                            lastTransactionsLayout.findViewById<AppCompatTextView>(R.id.transaction_type_tv).text =
                                vpslastTenTransactionsList!![i].transactionType

                        }

                        clearFields()
                        it.response.result.vpsFundsAnalytics.investmentAmount?.let { invData ->
                            binding.totalPuchasesTv.text = "Rs.${invData}"
                        }

                        it.response.result.vpsFundsAnalytics.investmentUnits?.let { invUnits ->
                            binding.totalPuchasesUnitsTv.text = "Rs.${invUnits}"
                        }

                        it.response.result.vpsFundsAnalytics.redemptionAmount?.let { redData ->
                            binding.totalWithdrawlsTv.text =
                                "Rs.${redData}"

                        }
                        it.response.result.vpsFundsAnalytics.redemptionUnits?.let { redUnits ->
                            binding.totalWithdrawlsUnitsTv.text =
                                "${redUnits} Units"
                        }

                        it.response.result.vpsFundsAnalytics.frontEndLoadAmount?.let { felData ->
                            binding.frontendLoadOnPurchasesTv.text =
                                "Rs.${felData}"
                        }

                        it.response.result.vpsFundsAnalytics.capitalTax?.let { cgtData ->
                            binding.totalCapitalGainTv.text =
                                "Rs.${cgtData}"
                        }

                        dataEntries.clear()
                        colors.clear()
                        vpsAssetClasswisePortfolioList =
                            it.response.result.assestWisePortFolioList
                        for (i in 0 until vpsAssetClasswisePortfolioList?.size!!) {
                            dataEntries.add(
                                PieEntry(
                                    vpsAssetClasswisePortfolioList!![i].investmentPercentage.toFloat(),
                                    vpsAssetClasswisePortfolioList!![i].fundCategory
                                )
                            )
                            colors.add(Color.parseColor(pieChartColors[i]))
                        }
                        initPieChart()
                        setDataToPieChart()

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
                                ) {
                                    dashboardViewModel.getVPSFundDashboard(
                                        HBLPreferenceManager.getToken(requireContext()),
                                        HBLPreferenceManager.getCustomerCNIC(requireContext()),
                                        "3"
                                    )
                                }
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 ->
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {
                            dashboardViewModel.getVPSFundDashboard(
                                HBLPreferenceManager.getToken(requireContext()),
                                HBLPreferenceManager.getCustomerCNIC(requireContext()),
                                "3"
                            )
                        }
                    }
                }
            }
        })

//        dashboardViewModel.getMutualAccountStatementApi.observe(viewLifecycleOwner, Observer {
//            when (it?.responseStatus) {
//                LiveDataWrapper.Status.SUCCESS -> {
//                    val uri = saveFile(it.response)
//                    uri?.let { it1 -> viewPDF(requireContext(), layoutInflater, it1) }
//                }
//                LiveDataWrapper.Status.ERROR -> {
//                    Log.e("mutual account statement api error", it.error.toString())
//                }
//            }
//        })

        dashboardViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer
        {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    fun initViews() {
        statementMonthBtns = listOf(
            binding.last3monthsBtn,
            binding.last6monthsBtn,
            binding.last9monthsBtn,
            binding.last12monthsBtn
        )

        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.myProfileFragment)
        }

        binding.mutualFundSelector.setOnClickListener {
            if (!isMutualFundSelected) {
                binding.vpsFundSelector.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_text
                    )
                )
                binding.mutualFundSelector.setTextColor(Color.WHITE)
                binding.vpsFundSelector.background = null
                binding.mutualFundSelector.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.full_rounded_green)


                isMutualFundSelected = true
                binding.last3monthsBtn.callOnClick()
            }
        }

        binding.vpsFundSelector.setOnClickListener {
            if (isMutualFundSelected) {
                binding.vpsFundSelector.setTextColor(Color.WHITE)
                binding.mutualFundSelector.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_text
                    )
                )
                binding.mutualFundSelector.background = null
                binding.vpsFundSelector.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.full_rounded_green)

                isMutualFundSelected = false
                binding.last3monthsBtn.callOnClick()
            }
        }

        SetAllStatementBtnsToDefault()
        binding.last3monthsBtn.setTextColor(Color.BLACK)
        binding.last3monthsBtn.setBackgroundResource(R.drawable.rounded_bg_light_gray)

        binding.last3monthsBtn.setOnClickListener {

            SetAllStatementBtnsToDefault()
            binding.last3monthsBtn.setTextColor(Color.BLACK)
            binding.last3monthsBtn.setBackgroundResource(R.drawable.rounded_bg_light_gray)
            if (isMutualFundSelected) {
                folio_number?.let {
                    dashboardViewModel.getMutualFundsDashboard(
                        HBLPreferenceManager.getToken(requireContext()),
                        it, 3
                    )
                }
            } else {
                dashboardViewModel.getVPSFundDashboard(
                    HBLPreferenceManager.getToken(requireContext()),
                    HBLPreferenceManager.getCustomerCNIC(requireContext()),
                    "3"
                )
            }

        }

        binding.last6monthsBtn.setOnClickListener {

            SetAllStatementBtnsToDefault()
            binding.last6monthsBtn.setTextColor(Color.BLACK)
            binding.last6monthsBtn.setBackgroundResource(R.drawable.rounded_bg_light_gray)
            if (isMutualFundSelected) {
                folio_number?.let {
                    dashboardViewModel.getMutualFundsDashboard(
                        HBLPreferenceManager.getToken(requireContext()),
                        it, 6
                    )
                }
            } else {
                dashboardViewModel.getVPSFundDashboard(
                    HBLPreferenceManager.getToken(requireContext()),
                    HBLPreferenceManager.getCustomerCNIC(requireContext()),
                    "6"
                )
            }
        }

        binding.last9monthsBtn.setOnClickListener {

            SetAllStatementBtnsToDefault()
            binding.last9monthsBtn.setTextColor(Color.BLACK)
            binding.last9monthsBtn.setBackgroundResource(R.drawable.rounded_bg_light_gray)
            if (isMutualFundSelected) {
                folio_number?.let {
                    dashboardViewModel.getMutualFundsDashboard(
                        HBLPreferenceManager.getToken(requireContext()),
                        it, 9
                    )
                }
            } else {
                dashboardViewModel.getVPSFundDashboard(
                    HBLPreferenceManager.getToken(requireContext()),
                    HBLPreferenceManager.getCustomerCNIC(requireContext()),
                    "9"
                )
            }
        }

        binding.last12monthsBtn.setOnClickListener {

            SetAllStatementBtnsToDefault()
            binding.last12monthsBtn.setTextColor(Color.BLACK)
            binding.last12monthsBtn.setBackgroundResource(R.drawable.rounded_bg_light_gray)
            if (isMutualFundSelected) {
                folio_number?.let {
                    dashboardViewModel.getMutualFundsDashboard(
                        HBLPreferenceManager.getToken(requireContext()),
                        it, 12
                    )
                }
            } else {
                dashboardViewModel.getVPSFundDashboard(
                    HBLPreferenceManager.getToken(requireContext()),
                    HBLPreferenceManager.getCustomerCNIC(requireContext()),
                    "12"
                )
            }
        }

        binding.fromDateTv.setOnClickListener {
            showFromDateCalender(false)
        }

        binding.toDateTv.setOnClickListener {
            showToDateCalender()
        }

        binding.accountStatementCalender.setOnClickListener {
            showFromDateCalender(true)
        }

        binding.accountStatementViewTv.setOnClickListener {


            if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
                //            startDownloading() both functions are working code this one
                val mutualFundStatementDTO =
                    MutualFundAccountStatementDTO(folio_number.toString(), fromDate, toDate)
//                    MutualFundAccountStatementDTO("11536", fromDate, toDate)

                downloadFile(mutualFundStatementDTO) //and this function also is a working code for downloading file from post api
            } else {
                DialogUtils.showAlertDialog(
                    requireContext(),
                    layoutInflater,
                    getString(R.string.oops),
                    "Please select from date and to date to view the statement.",
                    R.drawable.ic_alert,
                    getString(R.string.ok_btn_label)
                )
            }

//            dashboardViewModel.getMutualFundsAccountStatement(
//                HBLPreferenceManager.getToken(
//                    requireContext()
//                ), mutualFundStatementDTO
//            )
//            downloadFile(mutualFundStatementDTO)

            /*if (requireActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                //permission denied, request it
                requireActivity().requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            } else {
                //start work here
//                startDownloading()
            }*/

            ///////////////
        }

        binding.investCardview.setOnClickListener {
            findNavController().navigate(R.id.investmentFragment)
        }

        binding.redeemCardview.setOnClickListener {
            findNavController().navigate(R.id.redemptionFragment)
        }

        binding.convertCardview.setOnClickListener {
            findNavController().navigate(R.id.conversionFragment)
        }

        binding.pledgeCardview.setOnClickListener {
            findNavController().navigate(R.id.pledgeFragment)
        }

        binding.showAllTv.setOnClickListener {
            findNavController().navigate(
                R.id.assetClassWisePortfolioFragment,
                bundleOf(
                    Pair("PieChartColors", pieChartColors),
                    Pair("AssetClassWiseFundsList", assetClasswisePortfolioList)
                )
            )
        }

        binding.settingsIcon.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
    }

    fun startDownloading() {
        val client = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        val mediaType: MediaType? = "application/json".toMediaTypeOrNull()
        val body: RequestBody = RequestBody.create(
            mediaType,
            "{\r\n    \"FolioNumber\": \"11536\",\r\n     \"FromDate\": \"2021-01-01\",\r\n      \"ToDate\": \"2021-02-01\"\r\n}"
        )
        val request: Request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/Dashboard/GetMutualFundsAccountStatement")
            .method("POST", body)
            .addHeader(
                "Authorization",
                HBLPreferenceManager.getToken(requireContext())
            )
            .addHeader("Content-Type", "application/json")
            .build()
//            val response: okhttp3.Response = client.newCall(request).execute()
        DialogUtils.showLoading()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                DialogUtils.hideLoading()
                val uri = saveFile(response.body)
                uri?.let { it1 -> viewPDF(requireContext(), layoutInflater, it1) }
            }

            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                DialogUtils.hideLoading()
                Log.d("failed call", e.message.toString())
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun downloadFile(mutualFundAccountStatementDTO: MutualFundAccountStatementDTO) {
        val apiInterface: FileDownloadService = createService(
            FileDownloadService::class.java
        )
        val call: Call<ResponseBody> = apiInterface.getMutualFundAccountStatement(
            HBLPreferenceManager.getToken(requireContext()),
            mutualFundAccountStatementDTO
        )

        DialogUtils.showLoading()
        call.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    DialogUtils.hideLoading()
//                    val writtenToDisk: Boolean = writeResponseBodyToDisk(response.body())
//                    Log.d("File download was a success? ", writtenToDisk.toString())
//                    val path = requireContext().filesDir.absolutePath + "statement.pdf"
////                    saveFile(response.body(), path)
//                    response.body()?.let { writeResponseBodyToDisk(it) }

                    val uri = saveFile(response.body())
                    uri?.let { it1 -> viewPDF(requireContext(), layoutInflater, it1) }
                    Log.d("File Download message", response.message())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("File Download Error", t.localizedMessage)
                DialogUtils.hideLoading()
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            // todo change the file location/name according to your needs
            val futureStudioIconFile = File(requireContext().filesDir, "statement.pdf")
                //File(requireActivity().getExternalFilesDir(null) + File.separator + "File.pdf")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("File Download: ", "$fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    /*fun downloadFile() = CoroutineScope(Dispatchers.IO).launch {
        val responseBody = FileDownloadService.getMutualFundAccountStatement(
            "",
            MutualFundAccountStatementDTO("", "", ""),
        ).body()
        saveFile(responseBody, "")
    }*/

    fun SetAllStatementBtnsToDefault() {
        for (i in 0..statementMonthBtns.lastIndex) {
            statementMonthBtns[i].setTextColor(Color.parseColor("#B2B2B3"))
            statementMonthBtns[i].setBackgroundResource(R.drawable.rounded_bg)
        }
    }

    fun showFromDateCalender(isShowNewDatePicker: Boolean) {
        val datePickerFragment =
            DatePickerFragment { view, year, month, day -> // Do something with the date chosen by the user
                actualFromDay = day.toString()
                mon = month + 1
                actualFromMonth = mon.toString()
                actualFromYear = year.toString()

                if (day < 10) {
                    actualFromDay = "0${day}"
                }

                if (mon!! < 10) {
                    actualFromMonth = "0${mon}"
                }

                fromDate = "$actualFromYear-$actualFromMonth-$actualFromDay"//"$actualFromDay-$actualFromMonth-$year"
                binding.fromDateTv.text = "$actualFromDay/$actualFromMonth/$year"
                val c: Calendar = Calendar.getInstance()
                c.set(actualFromDay!!.toInt(), actualFromMonth!!.toInt() - 1, year.toInt())
                fromDateInMilliSec = c.timeInMillis
                if (toDateInMilliSec < fromDateInMilliSec) {
                    binding.toDateTv.text = binding.fromDateTv.text
                }
                if (isShowNewDatePicker) {
                    showToDateCalender()
                }
            }

        datePickerFragment.type = FROM_DATE
        datePickerFragment.show(childFragmentManager, "datePicker")
    }

    fun showToDateCalender() {
        if (actualFromDay != null) {
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

                    toDate = "$year-$actualMonth-$actualDay"//"$actualDay-$actualMonth-$year"
                    binding.toDateTv.text = "$actualDay/$actualMonth/$year"
                    var c: Calendar = Calendar.getInstance()
                    c.set(actualDay.toInt(), actualMonth.toInt() - 1, year.toInt())
                    toDateInMilliSec = c.timeInMillis

                }

            datePickerFragment.type = TO_DATE
            datePickerFragment.customDay = actualFromDay!!.toInt()
            datePickerFragment.customMonth = actualFromMonth!!.toInt()
            datePickerFragment.customYear = actualFromYear!!.toInt()
            datePickerFragment.show(childFragmentManager, "datePicker")
        } else {
//                Toast.makeText(this.context,"Please Select From Date First!",Toast.LENGTH_LONG).show()
            DialogUtils.showAlertDialog(
                requireActivity(),
                layoutInflater,
                getString(R.string.caution),
                getString(R.string.select_from_date_first),
                R.drawable.ic_alert,
                resources.getString(R.string.ok), //change ok button title according to your need
            ) {}
        }
    }

    private fun initPieChart() {

        binding.pieChart.description.text = ""
        //adding padding
        binding.pieChart.setExtraOffsets(20f, 0f, 20f, 20f)
        binding.pieChart.isRotationEnabled = false
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        binding.pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        binding.pieChart.legend.isWordWrapEnabled = true
        binding.pieChart.legend.textSize = 12.0F

    }

    private fun setDataToPieChart() {

//        dataEntries.add(PieEntry(20f, "Income Funds"))
//        dataEntries.add(PieEntry(25f, "Funds of Funds"))
//        dataEntries.add(PieEntry(25f, "Money Market Fund"))
//        dataEntries.add(PieEntry(30f, "Equity Schemes"))
//
//        for (i in 0 until dataEntries.count()) {
//            colors.add(Color.parseColor(pieChartColors[i]))
//        }

        val dataSet = PieDataSet(dataEntries, "")
        val data = PieData(dataSet)

        // In Percentage

        dataSet.sliceSpace = 1f
        dataSet.colors = colors
        binding.pieChart.data = data

        data.setValueFormatter(CustomPercentageFormatter(binding.pieChart))


        binding.pieChart.setUsePercentValues(true)
        data.setValueTextSize(13f)
        data.setValueTextColor(Color.WHITE)
        binding.pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)

        binding.pieChart.isDrawHoleEnabled = false

        binding.pieChart.invalidate()
    }

    fun doubleToStringNoDecimal(d: Double): String? {
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
//        formatter.applyPattern("#,###.##")
        return formatter.format(d)
    }

    fun clearFields() {
        binding.totalPuchasesTv.text = ""
        binding.totalPuchasesUnitsTv.text = ""
        binding.totalWithdrawlsTv.text = ""
        binding.totalWithdrawlsUnitsTv.text = ""
        binding.totalDividendTv.text = ""
        binding.totalDividendUnitsTv.text = ""
        binding.frontendLoadOnPurchasesTv.text = ""
        binding.frontendLoadOnPurchasesUnitsTv.text = ""
        binding.totalTaxWithheldOnDividendTv.text = ""
        binding.totalCapitalGainTv.text = ""
    }

    //code for translating response body to pdf file
    fun saveFile(body: ResponseBody?): Uri? {
        if (body == null)
            return null
        var input: InputStream? = null
        try {
            input = body.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val fileName = "Mutual_Funds_Statement.pdf"
            val pdfFile = File(requireContext().filesDir, fileName)
            if (pdfFile.exists())
                pdfFile.delete()
            val fos = FileOutputStream(pdfFile)
            fos.write(input.readBytes())
            fos.close()

            val authority = "${requireContext().packageName}.fileprovider"
            return FileProvider.getUriForFile(requireContext(), authority, pdfFile)
        } catch (e: Exception) {
            Log.e("saveFile", e.toString())
        } finally {
            input?.close()
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataEntries.clear()
        colors.clear()
    }


}