package com.hbl.amc.ui.productInformation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentProductSelectionBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.Product
import com.hbl.amc.domain.model.ProductSaveResult
import com.hbl.amc.domain.model.ProductsGetResult
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ProductSelectionFragment : Fragment() {

    private var _binding: FragmentProductSelectionBinding? = null
    private val binding get() = _binding!!
    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val productViewModel by viewModel<ProductInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    lateinit var productsAdapter: ProductsAdapter
    private lateinit var productList: List<Product>
    private var selectedProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductSelectionBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()
        initViews()
        setupProgress()

        productViewModel.getProducts(
            HBLPreferenceManager.getToken(requireContext()),
            HBLPreferenceManager.getCustomerID(requireContext())
        )
        return view
    }

    private fun initViews() {
        binding.appBar.title.text = getString(R.string.product_selection)

        binding.appBar.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(requireContext())?.let {
                DialogUtils.showInfoPopup(requireContext(), layoutInflater, it.code)
            }
        }

        binding.continueBtn.setOnClickListener {
            val productSaveDTO = ProductSaveResult(
                HBLPreferenceManager.getCustomerID(requireContext()),
                "5d7c838b-8847-4ed6-b0ab-35d94c85a106", //need to update once server is ready
                selectedProduct!!.id
            )
            productViewModel.saveProduct(
                HBLPreferenceManager.getToken(requireContext()),
                productSaveDTO
            )
        }

        /*binding.appBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }*/

        binding.appBar.backBtn.visibility = GONE
    }

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

    private fun initViewModel() {
        productViewModel.getProductsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let { pgr ->
                        if (!pgr.productList.isNullOrEmpty()) {
                            productList = pgr.productList
                            showDetails(pgr)
                        }
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

        productViewModel.saveProductsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let {
                        if (selectedProduct!!.productCode == "MutualFund") {
                            productViewModel.getSelectedMutualFunds(
                                HBLPreferenceManager.getToken(
                                    requireContext()
                                ), HBLPreferenceManager.getCustomerID(requireContext())
                            )
                        } else {
                            productViewModel.getSelectedVPSFunds(
                                HBLPreferenceManager.getToken(
                                    requireContext()
                                ), HBLPreferenceManager.getCustomerID(requireContext())
                            )
                        }
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

        productViewModel.getSelectedMutualFundsApi.observe(viewLifecycleOwner, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let {
                        findNavController().navigate(
                            R.id.action_productSelectionFragment_to_mutualFundsSelectionFragment,
                            bundleOf(Pair(FUNDS_RESULT, it))
                        )
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

        productViewModel.getSelectedVPSFundsApi.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    it.response?.result?.let {
                        findNavController().navigate(
                            R.id.action_productSelectionFragment_to_vpsFundsSelectionFragment,
                            bundleOf(Pair(FUNDS_RESULT, it))
                        )
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

        productViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) showLoading() else hideLoading()
        })
    }

    private fun showDetails(productsGetResult: ProductsGetResult) {

        if (productsGetResult.productId != null) {
            for (i in productList.indices) {
                if (productList[i].id == productsGetResult.productId) {
                    productList[i].isSelected = true
                    selectedProduct = productList[i]
                    toggleContinueBtn(validateInput())
                    break
                }
            }
        }

        val onSelectOrUnSelectProduct: (position: Int, isSelected: Boolean) -> Unit =
            { position: Int, isSelected: Boolean ->
                for (i in productList.indices) {
                    if (i == position) {
                        selectedProduct = if (isSelected) {
                            productList[i]
                        } else {
                            null
                        }
                        productList[i].isSelected = isSelected
                    } else {
                        productList[i].isSelected = false
                    }

                    toggleContinueBtn(validateInput())
                }
                productsAdapter.updateData(productList!!)
            }

        productsAdapter = ProductsAdapter(onSelectOrUnSelectProduct)
        binding.productsRv.apply {
            adapter = productsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        productsAdapter.updateData(productList)
    }

    private fun toggleContinueBtn(b: Boolean) {
        binding.continueBtn.isEnabled = b
    }

    private fun validateInput(): Boolean {
        var isValid = true
        if (selectedProduct == null) {
            isValid = false
        }

        return isValid
    }

    private fun showAlertDialog() {
        DialogUtils.showAlertDialog(
            requireActivity(),
            layoutInflater,
            getString(R.string.vps_funds),
            getString(R.string.dummy_text) + " " + getString(R.string.dummy_text),
            R.drawable.ic_vps_funds,
            resources.getString(R.string.agree), //change ok button title according to your need
        ) {}
    }

    private fun setupProgress() {
        binding.appBar.progressbar1.progress = 100
        binding.appBar.progressbar2.progress = 100
        binding.appBar.progressbar3.progress = 33
        binding.appBar.progressbar4.progress = 0
        binding.appBar.progress.text = "65%"
        binding.appBar.stepTv.text = "Step 3/4"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}