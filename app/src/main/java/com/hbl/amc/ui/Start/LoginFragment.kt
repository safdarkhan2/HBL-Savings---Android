package com.hbl.amc.ui.Start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentLoginBinding
import java.util.concurrent.Executor
import android.view.MotionEvent

import android.view.View.OnTouchListener
import android.text.InputType
import android.util.Log
import androidx.lifecycle.Observer
import com.hbl.amc.domain.RequestDTO.LoginDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.ACCOUNT_TYPE_RESULT
import com.hbl.amc.ui.ExtraFeatures.ExtraFeaturesActivity
import com.hbl.amc.ui.FOLIO_NUMBER
import com.hbl.amc.ui.GenericViewModel
import com.hbl.amc.ui.self_service.SelfServiceActivity
import com.hbl.amc.utils.DialogUtils
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class LoginFragment : Fragment() {

    private var _binding : FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var isPasswordMasked = true

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val loginViewModel by viewModel<LoginViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val genericViewModel by viewModel<GenericViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        executor = ContextCompat.getMainExecutor(this.requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(requireContext(),
//                        "Authentication error: $errString", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
//                    Toast.makeText(requireContext(),
//                        "Authentication succeeded!", Toast.LENGTH_SHORT)
//                        .show()
                    findNavController().navigate(R.id.action_loginFragment_to_accountTypeFragment)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
//                    Toast.makeText(requireContext(), "Authentication failed",
//                        Toast.LENGTH_SHORT)
//                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("HBL-AMC")
            .setSubtitle("Biometric Login")
            .setNegativeButtonText("Login via password")
            .build()

        initViewModel()
        initViews()

        genericViewModel.getAllDocumentsTypes()
        genericViewModel.getOnboardingSteps()

        return view
    }

    fun initViewModel() {
        loginViewModel.loginApi.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        val res = it.response?.result
                        HBLPreferenceManager.saveToken(requireContext(), "Bearer ${res.token}")
                        HBLPreferenceManager.saveCustomerID(requireContext(), res.customerId)
                        HBLPreferenceManager.saveCustomerCNIC(requireContext(), res.cnic)
                        val intent = Intent(requireContext(), SelfServiceActivity::class.java)
                        intent.putExtra(FOLIO_NUMBER, res.folioNumber)
                        intent.putExtra(ACCOUNT_TYPE_RESULT, res.accountType)
                        startActivity(intent)
                    } else {
                        it.response?.message?.let { msg ->
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
                LiveDataWrapper.Status.ERROR -> {
                    Log.d("Test", it?.response?.message.toString())
                }
            }
        })

        loginViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
        })

            genericViewModel.documentTypesResponse.observe(viewLifecycleOwner, Observer {
                when (it?.responseStatus) {
                    LiveDataWrapper.Status.SUCCESS -> {
                        it.response?.let { res ->
                            HBLPreferenceManager.saveDocumentsTypesResponse(requireContext(), res)
                        }
                    }
                    LiveDataWrapper.Status.ERROR -> {
                        it.error?.message?.let { it1 -> Log.d("documents types error", it1) }
                    }
                }
            })

            genericViewModel.onboardingStepsResponse.observe(viewLifecycleOwner, Observer {
                when (it?.responseStatus) {
                    LiveDataWrapper.Status.SUCCESS -> {
                        it.response?.let { res ->
                            HBLPreferenceManager.saveOnboardingSteps(requireContext(), res.result)

                            res.result.let { it1 -> Log.d("1st step", it1[0].name) }
                        }
                    }
                    LiveDataWrapper.Status.ERROR -> {
                        it.error?.message?.let { it1 -> Log.d("onboarding steps error", it1) }
                    }
                }
            })

            genericViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
                if (it) DialogUtils.showLoading() else DialogUtils.hideLoading()
            })
    }

    private fun initViews() {
//        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPersistent.bottomSheet)

//        binding.emailEdittext.setText("ali@customerlogin.com")
//        binding.emailEdittext.setText("52250-0365534-3")
//        binding.passwordEdittext.setText("test123")

        binding.continueBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_accountTypeFragment)
        }

        binding.fingetprintBtn.setOnClickListener{
            biometricPrompt.authenticate(promptInfo)
        }

        binding.passwordEdittext.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.passwordEdittext.getRight() - binding.passwordEdittext.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    if (isPasswordMasked) {
                        isPasswordMasked = false
                        binding.passwordEdittext.inputType = InputType.TYPE_CLASS_TEXT
                        binding.passwordEdittext.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_show_password), null)
                    } else {
                        isPasswordMasked = true
                        binding.passwordEdittext.inputType = 129
                        binding.passwordEdittext.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_hide_password), null)
                    }
                    return@OnTouchListener true
                }
            }
            false
        })

        binding.login.setOnClickListener {
//            findNavController().navigate(R.id.dashboardFragment)

//            val intent = Intent(requireContext(), SelfServiceActivity::class.java)
//            intent.putExtra("FOLIO_NUMBER",binding.emailEdittext.text.toString())
//            startActivity(intent)

            if (validate()) {
                val loginDTO = LoginDTO(
                    UserName = binding.emailEdittext.text.toString(),
                    password = binding.passwordEdittext.text.toString()
                )

                loginViewModel.loginUser(loginDTO)
            }
//            val intent = Intent(requireContext(), SelfServiceActivity::class.java)
//            HBLPreferenceManager.saveToken(requireContext(), "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IkFsaSBaaWEiLCJuYmYiOjE2MzkxMzA3NTYsImV4cCI6MTYzOTE0MTU1NiwiaWF0IjoxNjM5MTMwNzU2LCJpc3MiOiJIQkxBTUMiLCJhdWQiOiJIQkxBTUMifQ.SbhAmV4wtBY2YxX7ut-7APEIwa1w97xwGTkCjaqKKYw")
//            intent.putExtra(FOLIO_NUMBER, "11536")
//            startActivity(intent)
        }

        binding.guides.setOnClickListener {
            val intent = Intent(requireContext(), ExtraFeaturesActivity::class.java)
            intent.putExtra("guides", true)
            startActivity(intent)
        }

        binding.products.setOnClickListener {
//            val intent = Intent(requireContext(), SelfServiceActivity::class.java)
//            intent.putExtra("products", true)
//            startActivity(intent)
        }

        binding.contactUs.setOnClickListener {
            val intent = Intent(requireContext(), ExtraFeaturesActivity::class.java)
            intent.putExtra("contact", true)
            startActivity(intent)
        }

        binding.products.setOnClickListener {
            val intent = Intent(requireContext(), ExtraFeaturesActivity::class.java)
            intent.putExtra("contactUs", true)
            startActivity(intent)
        }

        binding.markets.setOnClickListener {
            val intent = Intent(requireContext(), ExtraFeaturesActivity::class.java)
            intent.putExtra("webview", true)
            startActivity(intent)
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    fun validate() : Boolean {
        when {
            binding.emailEdittext.text!!.isEmpty() -> {
                binding.emailLayout.error = "Email/Folio/Mobile/CNIC is required!"
            }
            binding.emailEdittext.text!!.length < 3 -> {
                binding.emailLayout.error = "" //handle validations accordingly
            }
            else -> {
                binding.emailLayout.error = null
            }
        }

        when {
            binding.passwordEdittext.text!!.isEmpty() -> {
                binding.passwordLayout.error = "Password is required!"
            }
            binding.passwordEdittext.text!!.length < 3 -> {
                binding.passwordLayout.error = ""
            }
            else -> {
                binding.passwordLayout.error = null
            }
        }

        return (binding.emailEdittext.text!!.isNotEmpty() && binding.passwordEdittext.text!!.isNotEmpty())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}