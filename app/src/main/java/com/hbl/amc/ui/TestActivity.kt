package com.hbl.amc.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import com.hbl.amc.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        findViewById<Button>(R.id.test_btn).setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!

                val file = FileUtil.getTempFile(this, uri)
                Log.d("file path", file?.name + "-----------" + file?.path)

                findViewById<ImageView>(R.id.imageView2).setImageURI(uri)
//                allDocsTypes?.let { ad ->
//                    val doc = ad.find { dc -> dc.name == docType }
//                    doc?.let { dc ->
//                        file?.let { actualFile ->
//                            val actualDoc = MultipartBody.Part.createFormData("file", actualFile.name,
//                                actualFile.asRequestBody("image/*".toMediaTypeOrNull())
//                            )
//                            val docID = MultipartBody.Part.createFormData("documentTypeId",
//                                dc.id.toString()
//                            )
//                            disclaimersViewModel.uploadDocumentByIds(
//                                docFile = actualDoc,
//                                docTypeId = docID,
//                                null
//                            )
//                        }
//                    }
//                }

            }
        }
}