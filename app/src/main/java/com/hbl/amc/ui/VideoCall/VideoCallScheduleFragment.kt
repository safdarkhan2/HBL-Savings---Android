package com.hbl.amc.ui.VideoCall

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentVideoCallScheduleBinding
import com.hbl.amc.domain.RequestDTO.SaveVideoCallSlotDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.ui.CustomerInformation.ProfessionalInformationFragment
import com.hbl.amc.ui.EDIT_MODE
import com.hbl.amc.ui.Start.MainActivity
import com.hbl.amc.utils.AppUtils
import com.hbl.amc.utils.DialogUtils
import kotlinx.coroutines.Dispatchers
import okhttp3.internal.format
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class VideoCallScheduleFragment : Fragment(), KoinComponent {

    private var _binding: FragmentVideoCallScheduleBinding? = null
    private val binding get() = _binding!!

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val customerViewModel by viewModel<CustomerInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var videoCallUrl: String? = null
    var redirecturl: String? = null
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var min = 0

    var editMode = false

    private val AUDIO_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                // block backward navigation from this section
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoCallScheduleBinding.inflate(inflater, container, false)

        initViewModel()
        initView()

        return binding.root
    }

    fun initViewModel() {
        customerViewModel.getVideoCallUrlApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        videoCallUrl = it.response.result.url
                        redirecturl = it.response.result.redrictURL
                        DialogUtils.hideLoading()
                        findNavController().navigate(
                            R.id.videoCallFragment,
                            bundleOf(
                                Pair("Video_call_url", videoCallUrl),
                                Pair("Redirect_url", redirecturl)
                            )
                        )
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
                                ) {
                                    val intent = Intent(
                                        requireContext(),
                                        ProfessionalInformationFragment::class.java
                                    )
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 ->
                        Log.d("get video call url info error", it1)
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {
                            val intent = Intent(
                                requireContext(),
                                ProfessionalInformationFragment::class.java
                            )
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
            }
        })

        customerViewModel.saveVideoCallSlotApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it.response?.status == "success") {
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.success),
                            it.response.message,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {
                            val intent = Intent(
                                requireContext(),
                                ProfessionalInformationFragment::class.java
                            )
                            startActivity(intent)
                            requireActivity().finish()
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
                                ) {
                                    val intent = Intent(
                                        requireContext(),
                                        ProfessionalInformationFragment::class.java
                                    )
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            }
                        }
                    }
                }
                LiveDataWrapper.Status.ERROR -> {
                    it.error?.message?.let { it1 ->
                        Log.d("save video call slot info error", it1)
                        DialogUtils.showAlertDialog(
                            requireContext(),
                            layoutInflater,
                            getString(R.string.oops),
                            it1,
                            R.drawable.ic_alert,
                            getString(R.string.ok)
                        ) {
                            val intent = Intent(
                                requireContext(),
                                ProfessionalInformationFragment::class.java
                            )
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
            }
        })

        customerViewModel.loadingLiveData.observe(this, Observer {
            if (it) {
                DialogUtils.showLoading()
            } else {
                DialogUtils.hideLoading()
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
            AUDIO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    customerViewModel.getVideoCallUrl(
                        HBLPreferenceManager.getToken(requireContext()),
                        HBLPreferenceManager.getCustomerID(requireContext())
                    )
                } else {
                    Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), ProfessionalInformationFragment::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    fun initView() {
        binding.updateDateTv.visibility = GONE
        binding.continueBtn.visibility = GONE
        binding.timePicker.visibility = GONE
        binding.calenderPicker.visibility = GONE
        binding.appBar.backBtn.visibility = GONE
        binding.appBar.infoBtn.visibility = GONE
        binding.appBar.progressbarContainer.visibility = GONE
        binding.appBar.title.visibility = GONE
        binding.appBar.stepTv.visibility = GONE

        DialogUtils.showVideoCallDialog(
            requireActivity(),
            layoutInflater,
            onInitiatePressed = {
                if (checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied, request it
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.RECORD_AUDIO
                        ), AUDIO_PERMISSION_CODE
                    )
                } else {
                    customerViewModel.getVideoCallUrl(
                        HBLPreferenceManager.getToken(requireContext()),
                        HBLPreferenceManager.getCustomerID(requireContext())
                    )
                }


//                customerViewModel.getVideoCallUrl(
//                    HBLPreferenceManager.getToken(requireContext()),
//                    HBLPreferenceManager.getCustomerID(requireContext())
//                )
            },
            onSchedulePressed = {
                binding.calenderPicker.visibility = VISIBLE
            }
        )

        binding.calenderPicker.minDate = System.currentTimeMillis()

        binding.calenderPicker.setOnDateChangeListener { view, year, month, dayOfmonth ->
            Log.d("Calender", "$year-${month + 1}-$dayOfmonth")
            this.year = year
            this.month = month
            this.day = dayOfmonth
            binding.calenderPicker.visibility = GONE
            binding.timePicker.visibility = VISIBLE
            binding.continueBtn.visibility = VISIBLE
            binding.updateDateTv.visibility = VISIBLE
        }

        binding.timePicker.setOnTimeChangedListener { timePicker, hour, min ->

            Log.d("Time", "$hour:$min:00")
            this.hour = hour
            this.min = min
        }

        binding.continueBtn.setOnClickListener {


            val timeSlot = "$year-${month + 1}-$day" + "T" + "$hour:$min:00"
            val formattedTimeSlot = AppUtils.changeDisplayDateTimeToFormattedDateTime(timeSlot)
            Log.d("Time Slot", formattedTimeSlot)

            val saveVideoCallSlotDTO =
                SaveVideoCallSlotDTO(
                    HBLPreferenceManager.getCustomerID(requireContext()),
                    formattedTimeSlot
                )
            customerViewModel.saveVideoCallSlot(
                HBLPreferenceManager.getToken(requireContext()),
                saveVideoCallSlotDTO
            )
        }

        binding.updateDateTv.setOnClickListener {
            binding.timePicker.visibility = GONE
            binding.continueBtn.visibility = GONE
            binding.updateDateTv.visibility = GONE
            binding.calenderPicker.visibility = VISIBLE
        }
    }
}