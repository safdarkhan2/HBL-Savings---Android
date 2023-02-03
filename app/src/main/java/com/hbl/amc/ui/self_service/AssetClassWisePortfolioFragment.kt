package com.hbl.amc.ui.self_service

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.CustomPercentageFormatter
import com.hbl.amc.databinding.FragmentAssetClasswisePortfolioBinding
import com.hbl.amc.domain.model.AssestWisePortFolio

class AssetClassWisePortfolioFragment : Fragment() {

    private var _binding: FragmentAssetClasswisePortfolioBinding? = null

    private val binding get() = _binding!!

    val fundsNamesList = listOf(
        "HBL Cash Fund",
        "HBL Financial Planning Fund-Active Allocation Plan",
        "HBL Income Fund",
        "HBL Islamic Asset Allocation Fund",
        "HBL Islamic Stock Fund"
    )

    private  lateinit var assetClassWisePortfolioAdapter : AssetClassWisePortfolioFundAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAssetClasswisePortfolioBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()

        return view
    }

    fun initViews()
    {
        assetClassWisePortfolioAdapter = AssetClassWisePortfolioFundAdapter()
        binding.fundsRv.apply {
            adapter = assetClassWisePortfolioAdapter
            layoutManager = GridLayoutManager(this.context,2)
        }
//        binding.fundsRv.isNestedScrollingEnabled = false

        assetClassWisePortfolioAdapter.updateData(fundsNamesList)

        initPieChart()
        setDataToPieChart()

        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
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
        val dataEntries = ArrayList<PieEntry>()
        val assetClasswiseList : List<AssestWisePortFolio> = arguments?.get("AssetClassWiseFundsList") as List<AssestWisePortFolio>
        val colorsHex : List<String> = arguments?.get("PieChartColors") as List<String>
        val colors: ArrayList<Int> = ArrayList()

        for (i in 0 until assetClasswiseList?.size!!) {
            dataEntries.add(PieEntry(assetClasswiseList!![i].investmentPercentage.toFloat(), assetClasswiseList!![i].fundCategory))
            colors.add(Color.parseColor(colorsHex[i]))
        }

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
}