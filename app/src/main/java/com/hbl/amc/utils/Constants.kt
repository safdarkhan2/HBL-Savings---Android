package com.hbl.amc.ui

import android.Manifest

const val TIME_OUT : Long = 120
const val API_ID = "69D54E87-EEA7-4A2C-BA9D-56468987"
const val SECTION_ID = "1de3ae40-8659-48b1-a426-0ad3c7bd8b41" //disclaimer section id
const val CUSTOMER_ID = "customer_id"
const val CUSTOMER_CNIC = "customer_cnic"
const val TOKEN = "token"
const val STEP_ID = "step_id"
const val PAST_DATE_DISABLE = 1
const val FUTURE_DATE_DISABLE = 2
const val SHARE = "share"
const val TO_DATE = 3
const val FROM_DATE = 4
const val NEXT_OF_KIN_EDIT = "next_of_kin_edit"
const val CNIC_RESULT = "cnic_result"
const val CNIC_FRONT = "cnic_front"
const val CNIC_ADDRESS = "cnic_address"
const val CNIC_BACK = "cnic_back"
const val CNIC_BACK_PATH = "cnic_back_path"
const val SELECTED_RELIGION = "Religion"
const val LIFECYCLE_ALLOCATION = "Lifecycle"
const val CUSTOMIZE_ALLOCATION = "Customize"
const val NEXT_OF_KIN_RES = "next_of_kin_res"
const val PREFERENCES_NAME =  "hbl_amc_prefs"
const val DOCUMENTS_TYPES = "documents_types"
const val MOBILE_OWNERSHIP_AFFIDAVIT = "MobileNumberUndertakingCloseFamilyMember"
const val MOBILE_NUMBER_LETTER_BY_EMPLOYER = "MobileNumberLetterByEmployer"
const val SERVICE_PROVIDER_BILL = "ServiceBillProvider"
const val FUNDS_RESULT = "funds_result"
const val ONBOARDING_STEPS = "onboarding_steps"
const val EDIT_MODE = "edit_mode"
const val FRONT_IMAGE_URI = "front_image_uri"
const val BACK_IMAGE_URI = "back_image_uri"
const val BForm_IMAGE_BITMAP = "back_image_bitmap"
const val FRONT_PATH = "front_path"
const val BACK_PATH = "back_path"
const val ZAKAT_FUND = "ZakatDeclarationForm"
const val SOURCE_OF_INCOME = "SourceOfIncome"
const val SCANNED_IMAGE = "Scanned_Image"
const val B_FORM = "b_form"
const val IS_BUNYADI_BACHAT = true
const val ID_TYPE = "id_type"
const val SELECTED_ACCOUNT = "selected_account"
const val ACCOUNT_TYPE_RESULT = "account_type_result"
const val W9BENFORM = "W9BenForm"
const val W8BENFORM = "W8BenForm"
const val PLEDGE_FORM = "PledgeForm"
const val TO_RPQ = "to_rpq"
const val SESSION_EXPIRE = "session_expire"
const val FOLIO_NUMBER = "FOLIO_NUMBER"
const val AMOUNT = "amount"
const val PAYMENT = "payment"
const val INSTANT_TRANSFER = "InstantTrasfer"
const val BILL_GENERATION = "BillGeneration"
const val ONLINE_TRANSFER = "OnlineTransfer"



val appPerms = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA
)