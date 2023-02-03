package com.hbl.amc.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import com.hbl.amc.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


object AppUtils {

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun EditText.validate(message: String, validator: (String) -> Boolean) {
        this.afterTextChanged {
            this.error = if (validator(it)) null else message
        }

    }

    fun String.isValidEmail(): Boolean =
        this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


    fun changeDateFormatToDisplay(date: String): String {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val outputFormatter = SimpleDateFormat("dd/MM/yyyy")
        val dt = dateFormatter.parse(date)
        return outputFormatter.format(dt)
    }

    fun changeUTCDateFormatToDisplay(date: String?): String? {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormatter = SimpleDateFormat("dd/MM/yyyy")
        val dt = dateFormatter.parse(date)
        return outputFormatter.format(dt)
    }

    fun changeDisplayDateFormatToUTC(date: String): String {
        val outputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val dt = dateFormatter.parse(date)
        return outputFormatter.format(dt)
    }

    fun changeDisplayDateTimeToFormattedDateTime(date: String): String {
        val outputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val dt = dateFormatter.parse(date)
        return outputFormatter.format(dt)
    }

    fun checkUTC(date: String): Boolean {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            dateFormatter.parse(date)
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return false
    }

    fun mapProgressIndicatorValues(
        range: Range<Int>,
        percentageRisk: Range<Int>,
        riskValue: Int
    ): Int {
        return when {
            riskValue <= range.lower -> {
                percentageRisk.lower
            }
            riskValue >= range.upper -> {
                percentageRisk.upper
            }
            else -> {
                percentageRisk.lower + (percentageRisk.upper - percentageRisk.lower) * (riskValue - range.lower) / (range.upper - range.lower)
            }
        }
    }

    fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
        val filename = "${System.currentTimeMillis()}.jpg"
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DCIM}/$albumName"
                )
            }

            context.contentResolver.let {
                it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                    it.openOutputStream(uri)?.let(write)
                }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + File.separator + albumName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, filename)
            write(FileOutputStream(image))
        }
        Toast.makeText(context, "Screenshot Saved Successfully!", Toast.LENGTH_LONG).show()
    }

    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    fun getDatesDifference(selectedDate : String) : Int? {
        // parse the date with a suitable formatter
        val from = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        // get today's date
        val today = LocalDate.now()
        // calculate the period between those two
        var period = Period.between(from, today)

        // and print it in a human-readable way
        println("The difference between " + from.format(DateTimeFormatter.ISO_LOCAL_DATE)
                + " and " + today.format(DateTimeFormatter.ISO_LOCAL_DATE) + " is "
                + period.years + " years, " + period.months + " months and "
                + period.days + " days")

        return period.years
    }

    fun getPDFURI(context: Context, base64String: String): Uri? {
        val fileName = "affidavit.pdf"
        val pdfFile = File(context.filesDir, fileName)
        if (pdfFile.exists())
            pdfFile.delete()
        val fos = FileOutputStream(pdfFile)
        fos.write(Base64.decode(base64String, Base64.NO_WRAP))
        fos.close()
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, pdfFile)
    }

    fun viewPDF(context: Context, layoutInflater: LayoutInflater, pdfURI: Uri) {
        val shareIntent = Intent(Intent.ACTION_VIEW)
        shareIntent.setDataAndType(pdfURI, "application/pdf")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(shareIntent)
        } else {
            DialogUtils.showAlertDialog(
                context,
                layoutInflater,
                context.getString(R.string.oops),
                context.getString(R.string.no_app_available_view_pdf),
                R.drawable.ic_alert,
                context.getString(R.string.ok)
            ) {}
        }
    }

    fun getWordDocURI(context: Context, base64String: String): Uri? {
        val fileName = "affidavit.docx"
        val wordFile = File(context.filesDir, fileName)
        if (wordFile.exists())
            wordFile.delete()
        val fos = FileOutputStream(wordFile)
        fos.write(Base64.decode(base64String, Base64.NO_WRAP))
        fos.close()
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, wordFile)
    }

    fun viewWordDoc(context: Context, layoutInflater: LayoutInflater, wordURI: Uri) {
        val shareIntent = Intent(Intent.ACTION_VIEW)
        shareIntent.setDataAndType(wordURI, "application/msword")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(shareIntent)
        } else {
            DialogUtils.showAlertDialog(
                context,
                layoutInflater,
                context.getString(R.string.oops),
                context.getString(R.string.no_app_available_view_docx),
                R.drawable.ic_alert,
                context.getString(R.string.ok)
            ) {}
        }
    }

    fun closeSoftKeyboard(context: Context, v: View) {
        val iMm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        iMm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
    }

    /*private fun openBrowserForTerms(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }*/
}