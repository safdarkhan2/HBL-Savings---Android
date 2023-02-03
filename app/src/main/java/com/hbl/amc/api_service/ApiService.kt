package com.hbl.amc.api_service

import com.hbl.amc.domain.RequestDTO.*
import com.hbl.amc.domain.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("generic/getcountries")
    suspend fun getCountries(): CountryResponse

    @GET("generic/getcitiesbycountry/{countryid}")
    suspend fun getCities(@Path("countryid") countryId: Int): CityResponse

    @GET("personalinfo/lookups")
    suspend fun getPersonalInfoLookups(): PersonalInfoLookupsResponse

    @POST("PersonalInfo")
    suspend fun savePersonalInfo(
        @Body savePersonalInfoDTO: SavePersonalInfoDTO
    ): SavePersonalInfoResponse

    @GET("professionalinfo/lookups")
    suspend fun getProfessionalInfoLookups(@Header("Authorization") token: String): ProfessionalInfoLookupResponse

    @POST("professionalinfo")
    suspend fun saveProfessionalInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveProfessionalInfoResponse

    @GET("bankinfo/lookups")
    suspend fun getBankInfoLookups(@Header("Authorization") token: String): BankInfoLookupsResponse

    @POST("bankinfo/account/titlebyiban")
    suspend fun getAccountTitleByIBAN(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): AccountTitleByIBANResponse
//    @GET("bankinfo/getaccountbyIban/{iban}")
//    suspend fun getAccountTitleByIBAN(@Path("iban") iban : String): AccountTitleByIBANResponse

    @GET("bankinfo/getbankbranchbyid/{branchID}")
    suspend fun getBranches(
        @Header("Authorization") token: String,
        @Path("branchID") branchID: Int
    ): BranchResponse

    @POST("bankinfo")
    suspend fun saveBankInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveBankInfoResponse

    @GET("crsinfo/lookups")
    suspend fun getCRSInfoLookups(@Header("Authorization") token: String): CRSInfoLookupsResponse

    @POST("crsinfo")
    suspend fun saveCRSInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveCRSInfoResponse

    @GET("rpqinfo/lookups")
    suspend fun getRPQInfoLookups(@Header("Authorization") token: String): RpqInfoLookupsResponse

    @POST("rpqinfo")
    suspend fun saveRPQInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveRPQInfoResponse

    @GET("kycinfo/lookups")
    suspend fun getKYCInfoLookups(@Header("Authorization") token: String): KYCLookupsResponse

    @POST("kycinfo")
    suspend fun saveKYCInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveKYCInfoResponse

    @GET("fatcainfo/lookups")
    suspend fun getFatcaInfoLookups(@Header("Authorization") token: String): FatcaInfoLookupsResponse

    @POST("fatcainfo")
    suspend fun saveFatcaInfo(
        @Header("Authorization") token: String,
        @Body requestBody: SaveFatcaInfoDTO
    ): SaveFatcaInfoResponse

    @GET("disclaimer/GetBySectionId/{sectionId}")
    suspend fun getDisclaimersInfoLookups(
        @Header("Authorization") token: String,
        @Path("sectionId") sectionId: String
    ): GetDisclaimerResponse

    @POST("disclaimer")
    suspend fun saveDisclaimerInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveDisclaimerResponse

    @GET("products/getbyid/{customerId}")
    suspend fun getProducts(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ProductsGetResponse

    @POST("products/funds/SaveProducts")
    suspend fun saveProduct(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): ProductSaveResponse

    @GET("products/funds/mutual/{customerId}")
    suspend fun getMutualFunds(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): MutualFundsGetResponse

    @POST("products/funds/SaveMutual")
    suspend fun saveMutualFund(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): MutualFundSaveResponse

    @GET("products/funds/VPSFundData/{customerId}")
    suspend fun getVPSFundsData(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): VPSFundsDataGetResponse

    @GET("products/funds/vps/{categoryId}")
    suspend fun getVPSFunds(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: String
    ): VPSFundsGetResponse

    @POST("products/funds/SaveVPSFund")
    suspend fun saveVPSFund(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): VPSFundSaveResponse

    @GET("products/funds/mutualfunds/getbyId/{customerId}")
    suspend fun getSelectedMutualFunds(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetSelectedFundsResponse

    @GET("products/funds/vpsfund/getbyid/{customerId}")
    suspend fun getSelectedVPSFunds(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetSelectedFundsResponse

    @Multipart
    @POST("documents/scan/nicfront")
    suspend fun uploadFrontCnic(@Part cnicFront: MultipartBody.Part): CnicUploadResponse

    @Multipart
    @POST("documents/scan/nicback")
    suspend fun uploadBackCnic(@Part cnicBack: MultipartBody.Part): CnicUploadResponse

    @POST("otherinfo")
    suspend fun saveOtherInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveOtherInfoResponse

    @GET("otherinfo/getbyid/{customerId}")
    suspend fun getOtherInfo(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): SaveOtherInfoResponse

    @GET("professionalinfo/getbyid/{customerId}")
    suspend fun resumeProfessionalInfo(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ResumeProfessionalInfoRes

    @GET("bankinfo/getbyid/{customerId}")
    suspend fun resumeBankInfo(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ResumeBankInfoRes

    @POST("OTP/Send")
    suspend fun resendOTP(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SendOTPResponse

    @POST("OTP/Verify")
    suspend fun verifyOTP(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): VerifyOTPResponse

    @GET("kycinfo/getbyid/{customerId}")
    suspend fun resumeKYC(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ResumeKYCResponse

    @GET("rpqinfo/getbyid/{customerId}")
    suspend fun resumeRPQ(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ResumeRPQResponse

    @GET("fatcainfo/getbyid/{customerId}")
    suspend fun resumeFatca(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ResumeFatcaResponse

    @GET("crsinfo/getbyid/{customerId}")
    suspend fun resumeCRS(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ResumeCRSResponse

    @GET("documents/types")
    suspend fun getDocumentTypes(): DocumentsTypesResponse

    @GET("documents/list/{customerId}")
    suspend fun getCustomerDocumentsChecklist(@Path("customerId") customerId: String): DocumentChecklistResponse

    @Multipart
    @POST("Documents/Upload")
    suspend fun uploadDocument(
        @Part docFile: MultipartBody.Part,
        @Part documentTypeId: MultipartBody.Part,
        @Part customerId: MultipartBody.Part?
    ): UploadDocumentResponse

    @Multipart
    @POST("nextofkin")
    suspend fun addNextOfKin(
        @Header("Authorization") token: String,
        @Part customerId: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part relationship: MultipartBody.Part,
        @Part share: MultipartBody.Part,
        @Part nic: MultipartBody.Part,
        @Part nicFront: MultipartBody.Part?,
        @Part nicBack: MultipartBody.Part?,
        @Part bForm: MultipartBody.Part?,
        @Part nicExpiryDate: MultipartBody.Part,
        @Part nicIssueDate: MultipartBody.Part,
        @Part identityTypeId: MultipartBody.Part?
    ): SaveNextOfKinResponse

    @Multipart
    @POST("nextofkin")
    suspend fun updateNextOfKin(
        @Header("Authorization") token: String,
        @Part customerId: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part relationship: MultipartBody.Part,
        @Part share: MultipartBody.Part,
        @Part nic: MultipartBody.Part,
        @Part nicFront: MultipartBody.Part?,
        @Part nicBack: MultipartBody.Part?,
        @Part bForm: MultipartBody.Part?,
        @Part nicExpiryDate: MultipartBody.Part,
        @Part nicIssueDate: MultipartBody.Part,
        @Part id: MultipartBody.Part,
        @Part identityTypeId: MultipartBody.Part?
    ): SaveNextOfKinResponse

    @DELETE("nextofkin/{Id}")
    suspend fun deleteNExtOfKin(
        @Header("Authorization") token: String,
        @Path("Id") id: String
    ): DeleteNextOfKin

    @GET("Steps/GetAllSteps")
    suspend fun getAllOnboardingSteps(): AllStepsResponse

    @GET("preview/{customerId}")
    suspend fun getPreviewInfo(@Path("customerId") customerId: String): GetPreviewInfoResponse

    @GET("personalinfo/getbyid/{customerId}")
    suspend fun resumePersonalInfo(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): ResumePersonalInfo

    @GET("products/funds/risklevels")
    suspend fun getRiskLevels(@Header("Authorization") token: String): RiskLevelsResponse

    //Self Service api's
    @GET("investment/investmentLookup/{customerId}")
    suspend fun getInvestmentInfoLookup(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetInvestmentInfoLookupResponse

    @Multipart
    @POST("Investment")
    suspend fun saveInvestmentInfo(
        @Header("Authorization") token: String,
        @Part customerId: MultipartBody.Part,
        @Part transactionTypeId: MultipartBody.Part,
        @Part amount: MultipartBody.Part,
        @Part paymentTypeId: MultipartBody.Part,
        @Part fundId: MultipartBody.Part,
        @Part unitTypeId: MultipartBody.Part,
        @Part incomeTypeId: MultipartBody.Part,
        @Part incomeSpecifyId: MultipartBody.Part,
        @Part incomePaymentFrequency: MultipartBody.Part,
        @Part paymentReference: MultipartBody.Part,
        @Part isAccepted: MultipartBody.Part,
        @Part billNumber: MultipartBody.Part?,
        @Part fundBankAccountNumber: MultipartBody.Part?,
        @Part IBFTRecepit: MultipartBody.Part?,
        @Part paymentDate: MultipartBody.Part?,
        @Part niftStatus: MultipartBody.Part?,
        @Part IBFTReferenceNumber: MultipartBody.Part?
    ): SaveInvestmentInfoResponse

    @GET("investment/generatebillnumber")
    suspend fun generateBillNumber(@Header("Authorization") token: String): GenerateBillNumberResponse

    @GET("redeem/redeemlookup/{customerId}")
    suspend fun getRedemptionInfoLookup(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetRedemptionInfoResponse

    @POST("redeem")
    suspend fun saveRedemptionInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveRedemptionInfoResponse

    @GET("conversion/conversionlookup/{customerId}")
    suspend fun getConversionInfoLookup(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetConversionInfoResponse

    @POST("conversion")
    suspend fun saveConversionInfo(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): SaveConversionInfoResponse

    @GET("pledge/pledgelookup/{customerId}")
    suspend fun getPledgeInfoLookup(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetPledgeInfoResponse

    @POST("pledge")
    suspend fun savePledgeInfo(
        @Header("Authorization") token: String,
        @Body requestBody: SavePledgeInfoDTO
    ): SavePledgeInfoResponse

    /***********************************************************************************************/
    @POST("Documents/SampleDownloadBase64")
    suspend fun getSampleDocument(
        @Body sampleDownloadDTO: SampleDownloadDTO
    ): SampleDownloadResponse

    @POST("PersonalInfo/saveaccountdetails")
    suspend fun changeAccountType(@Body changeAccountTypeDTO: ChangeAccountTypeDTO): SavePersonalInfoResponse

    @GET("AccountTypes/GetAccountTypes")
    suspend fun getAccountTypes(): AccountTypesResponse

    //Dashboard Apis
    @GET("Dashboard/GetMutualFundsDashboard/{folioNumber}/{duration}")
    suspend fun getMutualFundDashboard(
        @Header("Authorization") token: String,
        @Path("folioNumber") customerId: Int,
        @Path("duration") duration: Int
    ): GetMutualFundDashboardResponse

    /*@Streaming
    @POST("Dashboard/GetMutualFundsAccountStatement")
    fun getMutualFundAccountStatement(
        @Header("Authorization") token: String,
        @Body mutualFundAccountStatementDTO: MutualFundAccountStatementDTO
    ): Response*/

    @GET("Dashboard/GetVPSFundsDashboard/{cnic}/{duration}")
    suspend fun getVPSFundDashboard(
        @Header("Authorization") token: String,
        @Path("cnic") customerId: String,
        @Path("duration") duration: String
    ): VPSDashboardResponse

    //Login Apis
    @POST("login/CustomerLogin")
    suspend fun loginUser(@Body loginDTO: LoginDTO) : LoginResponse

    @POST("login/ForgotPassword")
    suspend fun forgotPassword(forgotPasswordDTO: ForgotPasswordDTO) : ForgotPasswordResponse

    //Video Call Apis
    @GET("Video/VideoURL/{customerId}")
    suspend fun getVideocallURL(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetVideoCallUrlResponse

    @POST("Video/SaveSlots")
    suspend fun saveVideoCallSlot(
        @Header("Authorization") token: String,
        @Body requestBody: SaveVideoCallSlotDTO
    ): SaveVideoCallSlotResponse

    //Profile Apis
    @GET("Profile/GetCustomerProfile/{customerId}")
    suspend fun getProfileInfo(
        @Header("Authorization") token: String,
        @Path("customerId") customerId: String
    ): GetProfileInfoResponse

    @POST("PersonalInfo/verifycustomernicexists")
    suspend fun customerCnicCheck(
        @Body requestBody: CNICCheckDTO
    ) : DuplicationCheckResponse

    @POST("PersonalInfo/verifyemailandmobilenoexists")
    suspend fun customerMobileOrEmailCheck(
        @Body requestBody: MobileCheckDTO
    ) : DuplicationCheckResponse
}