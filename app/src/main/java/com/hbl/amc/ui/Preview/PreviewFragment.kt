package com.hbl.amc.ui.Preview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentPreviewBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null

    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val previewViewModel by viewModel<PreviewViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    val extraFieldsList = listOf(
        "stepId",
        "id",
        "createdDate",
        "createdBy",
        "updatedDate",
        "updatedBy",
        "isActive",
        "isDeleted"
    )

    var personalInfoList: MutableMap<String, Any>? = null
    var professionalInfoList: MutableMap<String, Any>? = null
    var bankInfoList: MutableMap<String, Any>? = null
    var kycInfoList: MutableMap<String, Any>? = null
    var rpqInfoList: MutableMap<String, Any>? = null
    var fatcaInfoList: MutableMap<String, Any>? = null
    var crsInfoList: MutableMap<String, Any>? = null
    var otherInfoList: MutableMap<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                // block backward navigation from this section
//                Toast.makeText(requireContext(), "abc", LENGTH_LONG).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()
        initViews()

//        previewViewModel.getPreviewInfo(API_ID)

        return view
    }

    fun initViewModel() {
        previewViewModel.getPreviewInfoApi.observe(viewLifecycleOwner, Observer{
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {

                }
                LiveDataWrapper.Status.ERROR -> {
                    it?.error?.message?.let { it1 -> Log.d("get preview info error", it1) }
                }
                LiveDataWrapper.Status.LOADING -> {
                    it?.responseStatus
                }
            }
        })
    }


    private fun initViews() {

        binding.appBar.title.visibility = GONE
        binding.appBar.progressbarContainer.visibility = GONE
        binding.appBar.progress.visibility = GONE
        binding.appBar.stepTv.visibility = GONE
        binding.appBar.backBtn.visibility = INVISIBLE

        for (i in 1..5) {
            var previewSectionLayout = layoutInflater.inflate(R.layout.preview_section, null)
            binding.contentLayout.addView(previewSectionLayout)

            for (j in 1..5) {
                var previewSubSectionLayout =
                    layoutInflater.inflate(R.layout.preview_sub_section, null)
                previewSectionLayout.findViewById<LinearLayoutCompat>(R.id.sub_section_content_layout)
                    .addView(previewSubSectionLayout)
                previewSectionLayout.findViewById<LinearLayoutCompat>(R.id.sub_section_content_layout).visibility =
                    GONE
            }

            previewSectionLayout.findViewById<ImageView>(R.id.expand_btn).setOnClickListener()
            {
                previewSectionLayout.findViewById<LinearLayoutCompat>(R.id.sub_section_content_layout).visibility =
                    VISIBLE
                previewSectionLayout.findViewById<ImageView>(R.id.expand_btn).visibility = GONE
                previewSectionLayout.findViewById<ImageView>(R.id.collapse_btn).visibility = VISIBLE
            }

            previewSectionLayout.findViewById<ImageView>(R.id.collapse_btn).setOnClickListener()
            {
                previewSectionLayout.findViewById<LinearLayoutCompat>(R.id.sub_section_content_layout).visibility =
                    GONE
                previewSectionLayout.findViewById<ImageView>(R.id.expand_btn).visibility = VISIBLE
                previewSectionLayout.findViewById<ImageView>(R.id.collapse_btn).visibility = GONE
            }

            previewSectionLayout.findViewById<TextView>(R.id.edit_btn).setOnClickListener(){

            }
        }

        binding.submitBtn.setOnClickListener {
            findNavController().navigate(R.id.action_previewFragment_to_thankYouFragment)
        }
    }

    fun filterExtraFields(collection : MutableMap<String,Any>?,fields: List<String>)
    {
        for (i in 0 until fields.count()) {
            collection?.remove(fields[i])
        }
    }
}