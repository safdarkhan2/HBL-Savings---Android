package com.hbl.amc.ui.Start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.AccountTypeFragmentBinding
import com.hbl.amc.databinding.FragmentForgotPasswordBinding
import com.hbl.amc.domain.RequestDTO.ForgotPasswordDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.utils.DialogUtils
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val loginViewModel by viewModel<LoginViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        initViewModel()
        initViews()
        return binding.root
    }

    fun initViewModel() {
        loginViewModel.forgotPasswordApi.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        DialogUtils.showAlertDialog(
                            requireContext(), layoutInflater, getString(R.string.oops),
                            it.response.message, R.drawable.ic_alert, getString(R.string.ok)
                        ) {
                            findNavController().navigateUp()
                        }
                    } else {
                        it.response?.let { msg ->
                            DialogUtils.showAlertDialog(
                                requireContext(), layoutInflater, getString(R.string.oops),
                                msg?.message, R.drawable.ic_alert, getString(R.string.ok)
                            ) {}
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {

                }
            }
        })

        loginViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })
    }

    fun initViews() {
        binding.resetPassword.setOnClickListener {
            if (!binding.usernameEt.text.isNullOrEmpty()) {
                loginViewModel.forgotPassword(ForgotPasswordDTO(
                    binding.usernameEt.text.toString()
                ))
            }
        }
    }
}