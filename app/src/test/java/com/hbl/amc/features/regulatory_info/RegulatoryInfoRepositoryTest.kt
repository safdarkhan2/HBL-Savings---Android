package com.hbl.amc.features.regulatory_info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.RequestDTO.SaveCrsInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveFatcaInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveKYCInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveRpqInfoDTO
import com.hbl.amc.domain.model.NextOfKin
import com.hbl.amc.domain.repository.RegulatoryInfoRepository
import com.hbl.amc.ui.API_ID
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import java.net.HttpURLConnection

@RunWith(JUnit4::class)
class RegulatoryInfoRepositoryTest : BaseUTTest() {
    //Target
    private lateinit var regulatoryInfoRepository: RegulatoryInfoRepository
    //Inject api service created with koin
    val apiService by inject<ApiService>()
    //Inject Mockwebserver created with koin
    val mockWebServer : MockWebServer by inject()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val status = "success"
    val gson = Gson()

    @Before
    fun start(){
        super.setUp()
        startKoin{ modules(configureTestAppComponent(getMockWebServerUrl()))}
        regulatoryInfoRepository = RegulatoryInfoRepository()
    }

    @Test
    fun test_kyc_info_lookups_retrieves_expected_data() =  runBlocking<Unit>{

        mockNetworkResponseWithFileContent("kyc_info_lookups.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.kycInfoLookups()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_resume_kyc_info_retrieves_expected_data() =  runBlocking<Unit>{

        mockNetworkResponseWithFileContent("resume_kyc_info.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.resumeKYC(customerID = API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_kyc_info_retrieves_expected_data() =  runBlocking<Unit>{

        mockNetworkResponseWithFileContent("save_kyc_info.json", HttpURLConnection.HTTP_OK)

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

        val dataReceived = regulatoryInfoRepository.saveKYCInfo(gson.toJson(saveKYCInfoDTO).toRequestBody("application/json".toMediaTypeOrNull()))

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_rpq_lookups_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("rpq_lookups.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.rpqInfoLookups()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_resume_rpq_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("resume_rpq.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.resumeKYC(customerID = API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_rpq_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("save_rpq.json", HttpURLConnection.HTTP_OK)

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

        val dataReceived = regulatoryInfoRepository.saveRpqInfo(
            gson.toJson(saveRpqInfoDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_fatca_lookups_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("fatca_lookups.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.fatcaInfoLookups()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_resume_fatca_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("resume_fatca.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.resumeFatca(customerId = API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_fatca_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("save_fatca.json", HttpURLConnection.HTTP_OK)

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

        val dataReceived = regulatoryInfoRepository.saveFatcaInfo(
            gson.toJson(saveFatcaInfoDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_crs_lookups_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("crs_lookups.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.crsInfoLookups()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_resume_crs_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("resume_crs.json", HttpURLConnection.HTTP_OK)

        val dataReceived = regulatoryInfoRepository.resumeCRS(customerId = API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_crs_retrieves_expected_data() =  runBlocking {
        mockNetworkResponseWithFileContent("save_crs.json", HttpURLConnection.HTTP_OK)

        val saveCrsInfoDTO = SaveCrsInfoDTO(
            15,
            30,
            "BCasda",
            API_ID,
            "70F43914-04CE-485C-AE9E-578CF3A36700"
        )

        val dataReceived = regulatoryInfoRepository.saveCrsInfo(
            gson.toJson(saveCrsInfoDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }
}