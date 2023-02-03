package com.hbl.amc.features.regulatory_info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.RequestDTO.SaveCrsInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveFatcaInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveKYCInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveRpqInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.CustomerInfoUseCase
import com.hbl.amc.domain.usecase.RegulatoryInfoUseCase
import com.hbl.amc.ui.API_ID
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import java.net.HttpURLConnection

@RunWith(JUnit4::class)
class RegulatoryInfoViewModelTest : BaseUTTest() {
    private val status = "success"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var regulatoryInfoViewModel: RegulatoryInfoViewModel

    val mDispatcher = Dispatchers.Unconfined

    @MockK
    lateinit var regulatoryInfoUseCase: RegulatoryInfoUseCase


    @Before
    fun start(){
        super.setUp()
        //Used for initiation of Mockk
        MockKAnnotations.init(this)
        //Start Koin with required dependencies
        startKoin{ modules(configureTestAppComponent(getMockWebServerUrl()))}

        regulatoryInfoViewModel = RegulatoryInfoViewModel(mDispatcher, mDispatcher, regulatoryInfoUseCase)
    }

    @Test
    fun test_kyc_info_lookups_data_populates_expected_value(){

        val sampleResponse = getJson("kyc_info_lookups.json")
        var jsonObj = Gson().fromJson(sampleResponse, KYCLookupsResponse::class.java)

        //Make sure kyc info lookups use case returns expected response when called
        coEvery { regulatoryInfoUseCase.getKYCInfoLookup() } returns jsonObj

        regulatoryInfoViewModel.kycInfoLookUpsApi.observeForever {  }

        regulatoryInfoViewModel.getKYCInfoLookups()

        assert(regulatoryInfoViewModel.kycInfoLookUpsApi.value != null)
        assert(
            regulatoryInfoViewModel.kycInfoLookUpsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.kycInfoLookUpsApi.value as LiveDataWrapper<KYCLookupsResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_resume_kyc_info_data_populates_expected_value(){

        val sampleResponse = getJson("resume_kyc_info.json")
        var jsonObj = Gson().fromJson(sampleResponse, ResumeKYCResponse::class.java)

        //Make sure kyc info lookups use case returns expected response when called
        coEvery { regulatoryInfoUseCase.resumeKYC(any()) } returns jsonObj

        regulatoryInfoViewModel.resumeKycApi.observeForever {  }

        regulatoryInfoViewModel.resumeKYC(API_ID)

        assert(regulatoryInfoViewModel.resumeKycApi.value != null)
        assert(
            regulatoryInfoViewModel.resumeKycApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.resumeKycApi.value as LiveDataWrapper<ResumeKYCResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_save_kyc_info_data_populates_expected_value(){

        val sampleResponse = getJson("save_kyc_info.json")
        var jsonObj = Gson().fromJson(sampleResponse, SaveKYCInfoResponse::class.java)

        //Make sure kyc info lookups use case returns expected response when called
        coEvery { regulatoryInfoUseCase.saveKYCInfo(any()) } returns jsonObj

        regulatoryInfoViewModel.saveKycInfoApi.observeForever {  }

        val nextOFKins = ArrayList<NextOfKin>()
        nextOFKins.add(
            NextOfKin(
                "A8364A6C-283C-4DF9-8CFB-0E33203F87CC",
                "ABC",
                "31301-8238929-2",
                "2021-12-01",
                "2021-01-01",
                "XYZ",
                2,
                "",
                ""
            )
        )

        val saveKYCInfoDTO = SaveKYCInfoDTO(
            "A8364A6C-283C-4DF9-8CFB-0E33203F87CC",
            "CEFCE01D-735F-4672-BAE8-53E0E4038390",
            true,
            nextOFKins,
            25
        )

        regulatoryInfoViewModel.saveKYCInfo(saveKYCInfoDTO = saveKYCInfoDTO)

        assert(regulatoryInfoViewModel.saveKycInfoApi.value != null)
        assert(
            regulatoryInfoViewModel.saveKycInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.saveKycInfoApi.value as LiveDataWrapper<SaveKYCInfoResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_rpq_lookups_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("rpq_lookups.json")
        var jsonObj = Gson().fromJson(sampleResponse, RpqInfoLookupsResponse::class.java)

        coEvery { regulatoryInfoUseCase.getRpqInfoLookup() } returns jsonObj

        regulatoryInfoViewModel.rpqInfoLookUpsApi.observeForever {  }

        regulatoryInfoViewModel.getRpqInfoLookups()

        assert(regulatoryInfoViewModel.rpqInfoLookUpsApi.value != null)
        assert(
            regulatoryInfoViewModel.rpqInfoLookUpsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.rpqInfoLookUpsApi.value as LiveDataWrapper<RpqInfoLookupsResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_resume_rpq_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("resume_rpq.json")
        var jsonObj = Gson().fromJson(sampleResponse, ResumeRPQResponse::class.java)

        coEvery { regulatoryInfoUseCase.resumeRPQ(any()) } returns jsonObj

        regulatoryInfoViewModel.resumeRPQApi.observeForever {  }

        regulatoryInfoViewModel.resumeRPQ(API_ID)

        assert(regulatoryInfoViewModel.resumeRPQApi.value != null)
        assert(
            regulatoryInfoViewModel.resumeRPQApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.resumeRPQApi.value as LiveDataWrapper<ResumeRPQResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_save_rpq_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("save_rpq.json")
        var jsonObj = Gson().fromJson(sampleResponse, SaveRpqInfoResponse::class.java)

        coEvery { regulatoryInfoUseCase.saveRpqInfo(any()) } returns jsonObj

        regulatoryInfoViewModel.saveRpqInfoApi.observeForever {  }

        val saveRpqInfoDTO = SaveRpqInfoDTO(
            25,
            59,
            45,
            50,
            35,
            41,
            31,
            "00769A6D-1403-44D9-BE2D-C75A4FB6A177",
            27,
            55
        )

        regulatoryInfoViewModel.saveRpqInfo(saveRpqInfoDTO = saveRpqInfoDTO)

        assert(regulatoryInfoViewModel.saveRpqInfoApi.value != null)
        assert(
            regulatoryInfoViewModel.saveRpqInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.saveRpqInfoApi.value as LiveDataWrapper<SaveRpqInfoResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_fatca_lookups_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("fatca_lookups.json")
        var jsonObj = Gson().fromJson(sampleResponse, FatcaInfoLookupsResponse::class.java)

        coEvery { regulatoryInfoUseCase.getFatcaInfoLookup() } returns jsonObj

        regulatoryInfoViewModel.fatcaInfoLookUpsApi.observeForever {  }

        regulatoryInfoViewModel.getFatcaInfoLookups()

        assert(regulatoryInfoViewModel.fatcaInfoLookUpsApi.value != null)
        assert(
            regulatoryInfoViewModel.fatcaInfoLookUpsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.fatcaInfoLookUpsApi.value as LiveDataWrapper<FatcaInfoLookupsResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_resume_fatca_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("resume_fatca.json")
        var jsonObj = Gson().fromJson(sampleResponse, ResumeFatcaResponse::class.java)

        coEvery { regulatoryInfoUseCase.resumeFatca(any()) } returns jsonObj

        regulatoryInfoViewModel.resumeFatcaApi.observeForever {  }

        regulatoryInfoViewModel.resumeFatca(API_ID)

        assert(regulatoryInfoViewModel.resumeFatcaApi.value != null)
        assert(
            regulatoryInfoViewModel.resumeFatcaApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.resumeFatcaApi.value as LiveDataWrapper<ResumeFatcaResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_save_fatca_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("save_fatca.json")
        var jsonObj = Gson().fromJson(sampleResponse, SaveFatcaInfoResponse::class.java)

        coEvery { regulatoryInfoUseCase.saveFatcaInfo(any()) } returns jsonObj

        regulatoryInfoViewModel.saveFatcaInfoApi.observeForever {  }

        val saveFatcaInfoDTO = SaveFatcaInfoDTO(
            "2021-09-27",
            16,
            1,
            "15",
            "abc",
            "12315465",
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            null,
            null,
            null,
            "0423456789",
            "86 A virtual force",
            null,
            API_ID,
            "7B3D5EC2-4765-4286-88F8-6CCBF08ED4AD"
        )

        regulatoryInfoViewModel.saveFatcaInfo(saveFatcaInfoDTO = saveFatcaInfoDTO)

        assert(regulatoryInfoViewModel.saveFatcaInfoApi.value != null)
        assert(
            regulatoryInfoViewModel.saveFatcaInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.saveFatcaInfoApi.value as LiveDataWrapper<SaveFatcaInfoResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_crs_lookups_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("crs_lookups.json")
        var jsonObj = Gson().fromJson(sampleResponse, CRSInfoLookupsResponse::class.java)

        coEvery { regulatoryInfoUseCase.getCrsInfoLookup() } returns jsonObj

        regulatoryInfoViewModel.crsInfoLookUpsApi.observeForever {  }

        regulatoryInfoViewModel.getCrsInfoLookups()

        assert(regulatoryInfoViewModel.crsInfoLookUpsApi.value != null)
        assert(
            regulatoryInfoViewModel.crsInfoLookUpsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.crsInfoLookUpsApi.value as LiveDataWrapper<CRSInfoLookupsResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_resume_crs_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("resume_crs.json")
        var jsonObj = Gson().fromJson(sampleResponse, ResumeCRSResponse::class.java)

        coEvery { regulatoryInfoUseCase.resumeCRS(any()) } returns jsonObj

        regulatoryInfoViewModel.resumeCRSApi.observeForever {  }

        regulatoryInfoViewModel.resumeCRS(API_ID)

        assert(regulatoryInfoViewModel.resumeCRSApi.value != null)
        assert(
            regulatoryInfoViewModel.resumeCRSApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.resumeCRSApi.value as LiveDataWrapper<ResumeCRSResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }

    @Test
    fun test_save_crs_data_populates_expected_value() =  runBlocking {

        val sampleResponse = getJson("save_crs.json")
        var jsonObj = Gson().fromJson(sampleResponse, SaveCRSInfoResponse::class.java)

        coEvery { regulatoryInfoUseCase.saveCrsInfo(any()) } returns jsonObj

        regulatoryInfoViewModel.saveCrsInfoApi.observeForever {  }

        val saveCrsInfoDTO = SaveCrsInfoDTO(
            15,
            30,
            "BCasda",
            API_ID,
            "70F43914-04CE-485C-AE9E-578CF3A36700"
        )

        regulatoryInfoViewModel.saveCrsInfo(saveCrsInfoDTO = saveCrsInfoDTO)

        assert(regulatoryInfoViewModel.saveCrsInfoApi.value != null)
        assert(
            regulatoryInfoViewModel.saveCrsInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = regulatoryInfoViewModel.saveCrsInfoApi.value as LiveDataWrapper<SaveCRSInfoResponse>

        Assert.assertEquals(testResult.response!!.status, status)
    }
}