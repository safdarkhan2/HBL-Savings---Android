package com.hbl.amc.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtils {

    companion object{

        public fun askCameraPermissions(context : Context, requestCode : Int){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(context as Activity,   arrayOf<String>(Manifest.permission.CAMERA), requestCode);
        }
    }
}