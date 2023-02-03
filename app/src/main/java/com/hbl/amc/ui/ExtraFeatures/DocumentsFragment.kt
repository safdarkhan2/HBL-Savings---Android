package com.hbl.amc.ui.ExtraFeatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.databinding.FragmentDocumentsBinding

class DocumentsFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null

    private val binding get() = _binding!!

    val mainDocumentsList = listOf(
        "HBL Investment Fund",
        "HBL Growth Fund",
        "HBL Islamic Dedicated Equity Fund",
        "HBL Stock Fund"
    )

    private  lateinit var documentMainAdapter : DocumentsMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        val view = binding.root

        initView()

        return view
    }

    fun initView()
    {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }

        documentMainAdapter = DocumentsMainAdapter()
        binding.documentMainRv.apply {
            adapter = documentMainAdapter
            layoutManager = GridLayoutManager(this.context,1)
        }

        documentMainAdapter.updateData(mainDocumentsList)
    }
}