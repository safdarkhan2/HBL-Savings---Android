package com.hbl.amc.ui.CustomerInformation

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import com.hbl.amc.databinding.FragmentCustomerInformationMainBinding
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.CnicUploadResult
import com.hbl.amc.domain.model.DocumentsTypesResult
import com.hbl.amc.domain.model.GenericObject
import com.hbl.amc.domain.preferences.HBLPreferenceManager
import com.hbl.amc.ui.*
import com.hbl.amc.ui.Disclaimers.DisclaimersViewModel
import com.hbl.amc.ui.ML_Kit.LiveObjectDetectionActivity
import com.hbl.amc.utils.AppUtils.byteArrayToBitmap
import com.hbl.amc.utils.DialogUtils
import com.hbl.amc.utils.DialogUtils.Companion.hideLoading
import com.hbl.amc.utils.DialogUtils.Companion.showAlertDialog
import com.hbl.amc.utils.DialogUtils.Companion.showLoading
import com.hbl.amc.utils.FileUtils
import com.hbl.amc.utils.FileUtils.sizeInMb
import com.hbl.amc.utils.FileUtils.sizeStrInMb
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.ByteArrayOutputStream
import java.io.File


class CustomerInformationMainFragment : AppCompatActivity() {

//    private var _binding: FragmentCustomerInformationMainBinding? = null

    private var selectedIdType = 0

    //    private val binding get() = _binding!!
    private lateinit var binding: FragmentCustomerInformationMainBinding

    private var imageUri: Uri? = null
    private val REQUEST_FRONT_IMAGE_CAPTURE = 1
    private val REQUEST_BACK_IMAGE_CAPTURE = 2

    var activityResultLauncher: ActivityResultLauncher<Array<String>>
    var activityResultLauncher2: ActivityResultLauncher<Array<String>>

    private val mainDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO

    private val customerViewModel by viewModel<CustomerInfoViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    private val disclaimersViewModel by viewModel<DisclaimersViewModel> {
        parametersOf(mainDispatcher, ioDispatcher)
    }

    var idCardSide = "Front"
    var frontImage: Uri? = null
    var backImage: Uri? = null

    var frontImageBitmap: Bitmap? = null
    var backImageBitmap: Bitmap? = null
    var bFormImage: Bitmap? = null
    var cnicUploadResult: CnicUploadResult? = null
    var frontCardImageFile: File? = null
    var backCardImageFile: File? = null
    var bFormImageFile: File? = null
    var permissionsGranted = false
    var isBform = false
    var allDocTypes : List<DocumentsTypesResult>? = null
    var idTypesList : List<GenericObject>? = null

    init {
        this.activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if (allAreGranted) {
                takeFrontPhoto()
            }
        }

        this.activityResultLauncher2 = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if (allAreGranted) {
                takeBackPhoto()
            }
        }
    }

    fun initViewModel() {
        customerViewModel.scanFrontIdCardApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    backCardImageFile?.let { backCardFile ->
                        val actualDoc = MultipartBody.Part.createFormData(
                            "file", backCardFile.name,
                            backCardFile.asRequestBody("image/*".toMediaTypeOrNull())
                        )

                        cnicUploadResult = it.response?.result

                        customerViewModel.scanBackIdCard(
                            actualDoc
                        )
                    }

                    /*if (it.response?.status == "success") {
                        cnicUploadResult = it.response.result

                        backCardImageFile?.let { backCardFile ->
                            val actualDoc = MultipartBody.Part.createFormData(
                                "file", backCardFile.name,
                                backCardFile.asRequestBody("image/*".toMediaTypeOrNull())
                            )

                            customerViewModel.scanBackIdCard(
                                actualDoc
                            )
                        }

                        *//*findNavController().navigate(
                            R.id.action_customerInformationMainFragment_to_customerInformationDetailFragment,
                            bundleOf(
                                Pair(CNIC_RESULT, cnicUploadResult),
                                Pair(CNIC_FRONT, frontImage),
                                Pair(CNIC_BACK, backImage)
                            )
                        )*//*

                        *//*Toast.makeText(
                            this,
                            "Front Image of ID card is uploaded successfully!",
                            Toast.LENGTH_SHORT
                        )
                            .show()*//*
                    } else {
                        backCardImageFile?.let { requestBody ->
                            customerViewModel.scanBackIdCard(
                                requestBody
                            )
                        }
                    }*/*/
                }
                LiveDataWrapper.Status.ERROR -> {
                    Toast.makeText(
                        this,
                        it?.error?.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })

        customerViewModel.scanBackIdCardApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    val status = it?.response?.status

                    val intent = Intent(this, CustomerInformationDetailFragment::class.java)
                    intent.putExtra(ID_TYPE, selectedIdType)
                    /*val bs = ByteArrayOutputStream()
                    frontImageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, bs)
                    intent.putExtra(CNIC_FRONT, bs.toByteArray())

                    val bas = ByteArrayOutputStream()
                    backImageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, bas)
                    intent.putExtra(CNIC_BACK, bas.toByteArray())*/
                    frontImageBitmap?.let { bs ->
                        HBLPreferenceManager.saveFrontImageURI(
                            this,
                            FileUtils.bitmapToBase64String(bs)
                        )
                    }

                    backImageBitmap?.let {
                        HBLPreferenceManager.saveBackImageURI(
                            this,
                            FileUtils.bitmapToBase64String(it)
                        )
                    }

                    if (status == "success") {
                        intent.putExtra(CNIC_RESULT, cnicUploadResult)
//                        intent.putExtra(CNIC_FRONT, frontImageBitmap)
//                        intent.putExtra(CNIC_BACK, backImageBitmap)
                        intent.putExtra(CNIC_BACK_PATH, it?.response.result.fileName)
                        intent.putExtra(CNIC_ADDRESS, it?.response.result.address)
                    }

                    startActivity(intent)
                }
                LiveDataWrapper.Status.ERROR -> {
                    Toast.makeText(
                        this,
                        it?.error?.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })

        customerViewModel.loadingLiveData.observe(this, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.loadingLiveData.observe(this, Observer {
            if (it) showLoading() else hideLoading()
        })

        disclaimersViewModel.uploadDocumentApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    val status = it?.response?.status

//                    if (status == "success") {
                    val intent = Intent(this, CustomerInformationDetailFragment::class.java)
                    val bs = ByteArrayOutputStream()
                    bFormImage?.compress(Bitmap.CompressFormat.PNG, 50, bs)
                    bFormImage?.let {
                        HBLPreferenceManager.saveBFormImageBitmap(
                            this,
                            FileUtils.bitmapToBase64String(it)
                        )
                    }
//                        intent.putExtra(B_FORM, bs.toByteArray())
                    startActivity(intent)
                    /*} else {
                        findNavController().navigate(
                            R.id.action_customerInformationMainFragment_to_customerInformationDetailFragment,
                            bundleOf(
                                Pair(B_FORM, bFormImage)
                            )
                        )
                    }*/
                }
                LiveDataWrapper.Status.ERROR -> {
                    Toast.makeText(
                        this,
                        it?.error?.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })

        customerViewModel.personalInfoLookUpsApi.observe(this, Observer {
            when (it?.responseStatus) {
                LiveDataWrapper.Status.SUCCESS -> {
                    if (it?.response?.status == "success") {
                        idTypesList = it.response?.result?.identityTypeList

                        val idTypes = ArrayList<String>()
                        idTypes.add(getString(R.string.select))

                        idTypesList?.forEach { idt ->
                            idTypes.add(idt.name)
                        }

                        setUpIdentityTypeSpinner(idTypes = idTypes)

                        binding.docSpinner.setSelection(1)
                    }
                }
                LiveDataWrapper.Status.ERROR -> {}
            }
        })
    }

    private fun setUpIdentityTypeSpinner(idTypes: List<String>) {
        binding.docSpinner.apply {
            adapter = ArrayAdapter(this@CustomerInformationMainFragment, R.layout.dropdown_item, idTypes)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    idTypesList?.let {
                        selectedIdType = if (p2 > 0) {
                            it[p2 - 1].id
                        } else {
                            0
                        }

                        isBform = false

                        when (selectedIdType) {
                            6001 -> {
                                binding.frontIdCardView.visibility = VISIBLE
                                binding.uploadFrontTv.visibility = VISIBLE
                                binding.backIdCardView.visibility = VISIBLE
                                binding.uploadBackCnicTv.visibility = VISIBLE
                                binding.uploadBformTv.visibility = GONE
                                binding.bformView.visibility = GONE
                            }
                            6002 -> {
                                binding.frontIdCardView.visibility = VISIBLE
                                binding.uploadFrontTv.visibility = VISIBLE
                                binding.backIdCardView.visibility = VISIBLE
                                binding.uploadBackCnicTv.visibility = VISIBLE
                                binding.uploadBformTv.visibility = GONE
                                binding.bformView.visibility = GONE
                            }
                            6003 -> {
                                binding.frontIdCardView.visibility = GONE
                                binding.uploadFrontTv.visibility = GONE
                                binding.backIdCardView.visibility = GONE
                                binding.uploadBackCnicTv.visibility = GONE
                                binding.uploadBformTv.visibility = VISIBLE
                                binding.uploadBformTv.text = "Upload B-Form"
                                binding.bformView.visibility = VISIBLE
                                isBform = true
                            }
                            6004 -> {
                                binding.frontIdCardView.visibility = VISIBLE
                                binding.uploadFrontTv.visibility = VISIBLE
                                binding.backIdCardView.visibility = VISIBLE
                                binding.uploadBackCnicTv.visibility = VISIBLE
                                binding.uploadBformTv.visibility = GONE
                                binding.bformView.visibility = GONE
                            }
                            6005 -> {
                                binding.frontIdCardView.visibility = VISIBLE
                                binding.uploadFrontTv.visibility = VISIBLE
                                binding.backIdCardView.visibility = VISIBLE
                                binding.uploadBackCnicTv.visibility = VISIBLE
                                binding.uploadBformTv.visibility = GONE
                                binding.bformView.visibility = GONE
                            }
                            else -> {
                                binding.frontIdCardView.visibility = GONE
                                binding.uploadFrontTv.visibility = GONE
                                binding.backIdCardView.visibility = GONE
                                binding.uploadBackCnicTv.visibility = GONE
                                binding.uploadBformTv.visibility = GONE
                                binding.bformView.visibility = GONE
                            }
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCustomerInformationMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        DialogUtils.createPogressDialog(context = this)

        HBLPreferenceManager.getDocumentsTypesResponse(this)?.let { docTypeRes ->
            docTypeRes.result.also { allDocTypes = it }
        }

        HBLPreferenceManager.clearIDCardOrBform(this)

        initViewModel()
        initView()

        customerViewModel.getPersonalInfoLookups()
    }

    private fun initView() {

        binding.topHeaderLayout.title.text = getString(R.string.customer_information)

        setupProgress()

        binding.continueBtn.setOnClickListener {
            if (isBform) {

                if (!allDocTypes.isNullOrEmpty()) {
                    val dc = allDocTypes!!.find { doc -> doc.name == "BForm" }
                    bFormImageFile?.let { bformFile ->
                        val actualDoc = MultipartBody.Part.createFormData(
                            "file", bformFile.name,
                            bformFile.asRequestBody("image/*".toMediaTypeOrNull())
                        )
                        val docID = MultipartBody.Part.createFormData(
                            "documentTypeId",
                            dc?.id.toString()
                        )

                        disclaimersViewModel.uploadDocumentByIds(actualDoc, docID, null)
                    }
                }
            } else {
                frontCardImageFile?.let { frontCardFile ->
                    val actualDoc = MultipartBody.Part.createFormData(
                        "file", frontCardFile.name,
                        frontCardFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    customerViewModel.scanFrontIdCard(actualDoc)
                }
            }
        }

        binding.uploadFrontTv.setOnClickListener {

            idCardSide = "Front"
//            activityResultLauncher.launch(appPerms)
            // if(activityResultLauncher.contract.)

            /*if (permissionGranted()) {

            } else {
                requestPermission()
            }*/

            /*ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
//                .cropOval()				//Allow dimmed layer to have a circle inside
                .maxResultSize(1080, 540)
                .createIntentFromDialog { launcher.launch(it) }*/

//            startActivity(Intent(this, LiveObjectDetectionActivity::class.java))
//            frontCardDocResult.launch(Intent(this, LiveObjectDetectionActivity::class.java))
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.uploadBackCnicTv.setOnClickListener {
            idCardSide = "Back"
//            activityResultLauncher2.launch(appPerms)
//
//            backCardDocResult.launch(Intent(this, LiveObjectDetectionActivity::class.java))
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            /*ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .maxResultSize(1080, 540)
                .createIntentFromDialog { launcher.launch(it) }*/
        }

        binding.uploadBformTv.setOnClickListener {
            idCardSide = "b-form"
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.topHeaderLayout.backBtn.setOnClickListener {
            this.onBackPressed()
        }

        binding.topHeaderLayout.infoBtn.setOnClickListener {
            HBLPreferenceManager.getAccountType(this)?.let {
                DialogUtils.showInfoPopup(this, layoutInflater, it.code)
            }
        }
    }

    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                when (idCardSide) {
                    "Front" -> {
                        frontCardDocResult.launch(Intent(this, LiveObjectDetectionActivity::class.java))
                    }
                    "Back" -> {
                        backCardDocResult.launch(Intent(this, LiveObjectDetectionActivity::class.java))
                    }
                    else -> {
                        bFormDocResult.launch(Intent(this, LiveObjectDetectionActivity::class.java))
                    }
                }
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
//                requestPermissions()
                showAlertDialog(
                    this,
                    layoutInflater,
                    getString(R.string.alert),
                    getString(R.string.camera_permission_alert),
                    R.drawable.ic_alert,
                    getString(R.string.ok)
                )
            }
        }

    fun requestPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                if (idCardSide == "Front") {
                    frontCardDocResult.launch(Intent(this, LiveObjectDetectionActivity::class.java))
                } else {
                    backCardDocResult.launch(Intent(this, LiveObjectDetectionActivity::class.java))
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
//            showInContextUI(...)
                showAlertDialog(
                    this,
                    layoutInflater,
                    getString(R.string.alert),
                    getString(R.string.camera_permission_alert),
                    R.drawable.ic_alert,
                    getString(R.string.ok)
                )
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA)
            }
        }
    }

    private val frontCardDocResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            // Handle the Intent

            try {
                intent?.let {
                    val img = it.getByteArrayExtra(SCANNED_IMAGE)
                    img?.let { btArr ->
                        frontImageBitmap = byteArrayToBitmap(btArr)
                        binding.frontCnicIv.setImageBitmap(frontImageBitmap)
                        frontCardImageFile = FileUtils.bitmapToFile(frontImageBitmap!!, "front_card", this)
                        binding.continueBtn.isEnabled = validate()
                    }
                }
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

    private val backCardDocResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            // Handle the Intent

            try {
                intent?.let {
                    val img = it.getByteArrayExtra(SCANNED_IMAGE)
                    img?.let { btArr ->
                        backImageBitmap = byteArrayToBitmap(btArr)
                        binding.backCnicIv.setImageBitmap(backImageBitmap)
                        backCardImageFile = FileUtils.bitmapToFile(backImageBitmap!!, "back_card", this)
                        binding.continueBtn.isEnabled = validate()

                        binding.scrollView.post {
                            binding.scrollView.fullScroll(View.FOCUS_DOWN)
                        }
                    }
                }
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

    private val bFormDocResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            // Handle the Intent

            try {
                intent?.let {
                    val img = it.getByteArrayExtra(SCANNED_IMAGE)
                    img?.let { btArr ->
                        bFormImage = byteArrayToBitmap(btArr)
                        binding.bformIv.setImageBitmap(bFormImage)
                        bFormImageFile = FileUtils.bitmapToFile(bFormImage!!, "b_form", this)
                        binding.continueBtn.isEnabled = validate()

//                        bFormImageFile?.let { it1 -> Toast.makeText(this, "${it1.sizeStrInMb(2)} MB", Toast.LENGTH_LONG).show() }

                        binding.scrollView.post {
                            binding.scrollView.fullScroll(View.FOCUS_DOWN)
                        }
                    }
                }
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val uri = it.data?.data!!

                val file = FileUtil.getTempFile(this, uri)
                Log.d("file path", file?.name + "-----------" + file?.path)

                if (idCardSide == "Front") {
                    frontCardImageFile = file?.let { actualFile ->
                        actualFile
                    } ?: run { null }

                    frontImage = uri
                    binding.frontCnicIv.setImageURI(frontImage)

                } else if (idCardSide == "Back") {
                    backCardImageFile = file?.let { actualFile ->
                        actualFile
                    } ?: run { null }

                    backImage = uri
                    binding.backCnicIv.setImageURI(backImage)
                    binding.scrollView.post {
                        binding.scrollView.fullScroll(View.FOCUS_DOWN)
                    }
                }

                binding.continueBtn.isEnabled = validate()

            }
        }

    private fun validate(): Boolean {
        return if (isBform) {
            bFormImage != null
        } else {
            (frontImageBitmap != null && backImageBitmap != null)
        }
    }


    private fun setupProgress() {
        binding.topHeaderLayout.progressbar1.progress = 5
        binding.topHeaderLayout.progressbar2.progress = 0
        binding.topHeaderLayout.progressbar3.progress = 0
        binding.topHeaderLayout.progressbar4.progress = 0
        binding.topHeaderLayout.progress.text = "0%"
        binding.topHeaderLayout.stepTv.text = "Step 1/4"
    }

    private fun takeFrontPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_FRONT_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun takeBackPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_BACK_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_FRONT_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            binding.frontCnicIv.setImageBitmap(imageBitmap)
        } else if (requestCode == REQUEST_BACK_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            binding.backCnicIv.setImageBitmap(imageBitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            takeFrontPhoto()
            // main logic
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }

    }*/

}

