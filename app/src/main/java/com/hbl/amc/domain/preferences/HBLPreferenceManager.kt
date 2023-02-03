package com.hbl.amc.domain.preferences

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.hbl.amc.domain.model.AccountTypes
import com.hbl.amc.domain.model.AccountTypesResult
import com.hbl.amc.domain.model.AllStepsResult
import com.hbl.amc.domain.model.DocumentsTypesResponse
import com.hbl.amc.ui.*

class HBLPreferenceManager {
    companion object {
        fun saveDocumentsTypesResponse(context: Context, documentsTypesResponse: DocumentsTypesResponse) {
            val docTypes = Gson().toJson(documentsTypesResponse)
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(DOCUMENTS_TYPES, docTypes).apply()
        }

        fun getDocumentsTypesResponse(context: Context): DocumentsTypesResponse? {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            val docTypes = sharedPreferences.getString(DOCUMENTS_TYPES, "")
            return Gson().fromJson(docTypes, DocumentsTypesResponse::class.java)

        }

        fun saveCustomerID(context: Context, customerID : String) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(CUSTOMER_ID, customerID).apply()
        }

        fun getCustomerID(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(CUSTOMER_ID, "")?.let {
                return it
            } ?: return ""
        }

        fun saveCustomerCNIC(context: Context, customerCNIC : String) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(CUSTOMER_CNIC, customerCNIC).apply()
        }

        fun getCustomerCNIC(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(CUSTOMER_CNIC, "")?.let {
                return it
            } ?: return ""
        }

        fun saveToken(context: Context, token : String) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(TOKEN, token).apply()
        }

        fun getToken(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(TOKEN, "")?.let {
                return it
            } ?: return ""
        }

        fun saveOnboardingSteps(context: Context, onboardingSteps : List<AllStepsResult>) {
            val steps = Gson().toJson(onboardingSteps)
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(ONBOARDING_STEPS, steps).apply()
        }

        fun getOnboardingSteps(context: Context): List<AllStepsResult> {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            val stepsResult = sharedPreferences.getString(ONBOARDING_STEPS, "")
            return Gson().fromJson(stepsResult, Array<AllStepsResult>::class.java).asList()
        }

        fun saveFrontImageURI(context: Context, frontImg : String?) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(FRONT_IMAGE_URI, frontImg).apply()
        }

        fun getFrontImageURI(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(FRONT_IMAGE_URI, "")?.let {
                return it
            } ?: return null
        }

        fun saveBackImageURI(context: Context, backImg : String?) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(BACK_IMAGE_URI, backImg).apply()
        }

        fun getBackImageURI(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(BACK_IMAGE_URI, "")?.let {
                return it
            } ?: return null
        }

        fun saveBFormImageBitmap(context: Context, bform : String?) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(BForm_IMAGE_BITMAP, bform).apply()
        }

        fun getBFormImageBitmap(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(BForm_IMAGE_BITMAP, "")?.let {
                return it
            } ?: return null
        }

        fun saveCNICFrontPath(context: Context, path : String) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(FRONT_PATH, path).apply()
        }

        fun getCNICFrontPath(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(FRONT_PATH, "")?.let {
                return it
            } ?: return ""
        }

        fun saveCNICBackPath(context: Context, path : String) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(BACK_PATH, path).apply()
        }

        fun getCNICBackPath(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(BACK_PATH, "")?.let {
                return it
            } ?: return ""
        }

        fun clearPreferences(context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
        }

        fun clearIDCardOrBform(context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

            if (sharedPreferences.contains(FRONT_IMAGE_URI)) {
                sharedPreferences.edit().remove(FRONT_IMAGE_URI).apply()
            }

            if (sharedPreferences.contains(BACK_IMAGE_URI)) {
                sharedPreferences.edit().remove(BACK_IMAGE_URI).apply()
            }

            if (sharedPreferences.contains(BForm_IMAGE_BITMAP)) {
                sharedPreferences.edit().remove(BForm_IMAGE_BITMAP).apply()
            }
        }

        fun getAccountType(context: Context): AccountTypes? {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(SELECTED_ACCOUNT, "")?.let {
                return Gson().fromJson(it, AccountTypes::class.java)
            } ?: return null
        }

        fun saveAccountType(context: Context, accountTypes: AccountTypes) {
            val acType = Gson().toJson(accountTypes)
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(SELECTED_ACCOUNT, acType).apply()
        }

        fun getAccountTypeResult(context: Context): AccountTypesResult? {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(ACCOUNT_TYPE_RESULT, "")?.let {
                return Gson().fromJson(it, AccountTypesResult::class.java)
            } ?: return null
        }

        fun saveAccountTypeResult(context: Context, accountTypesResult : AccountTypesResult) {
            val acType = Gson().toJson(accountTypesResult)
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(ACCOUNT_TYPE_RESULT, acType).apply()
        }

        fun savePaymentSuccess(context: Context, payment : String) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(PAYMENT, payment).apply()
        }

        fun getPayment(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            sharedPreferences.getString(PAYMENT, "")?.let {
                return it
            } ?: return ""
        }
    }
}