package com.hbl.amc.features.customer_info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.RequestDTO.GetAccountTitleDTO
import com.hbl.amc.domain.RequestDTO.SaveBankInfoDTO
import com.hbl.amc.domain.RequestDTO.SavePersonalInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveProfessionalInfoDTO
import com.hbl.amc.domain.repository.CustomerInfoRepository
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
class CustomerInfoRepositoryTest : BaseUTTest() {

    //Target
    private lateinit var customerInfoRepository: CustomerInfoRepository

    //Inject api service created with koin
    val apiService by inject<ApiService>()

    //Inject Mockwebserver created with koin
    val mockWebServer: MockWebServer by inject()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val status = "success"
    val gson = Gson()

    @Before
    fun start() {
        super.setUp()
        startKoin { modules(configureTestAppComponent(getMockWebServerUrl())) }
        customerInfoRepository = CustomerInfoRepository()
    }

    @Test
    fun test_personal_info_lookups_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("personal_info_lookups.json", HttpURLConnection.HTTP_OK)

        val dataReceived = customerInfoRepository.PersonalInfoLookups()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_personalInfo_retrieves_expected_data() = runBlocking {

        val savePersonalInfoDto = SavePersonalInfoDTO(
            "86-A Phase 6 DHA Lahore",
            3,
            1,
            "1992-04-30",
            "abc@xyz.com",
            "Mailing Address",
            3,
            1,
            false,
            "34201-0465891231-9",
            "/abc/xyz",
            "Azeem Khalid",
            "2030-01-22",
            1,
            "abc",
            true,
            "2020-01-25",
            "04235488897",
            1,
            "xyz",
            1,
            "A8364A6C-283C-4DF9-8CFB-0E33203F87CC"
        )

        mockNetworkResponseWithFileContent("save_personal_info.json", HttpURLConnection.HTTP_OK)

        val dataReceived = customerInfoRepository.savePersonalInfo(
            gson.toJson(savePersonalInfoDto).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_professionalInfoLookups_retrieves_expected_data() = runBlocking {

        mockNetworkResponseWithFileContent(
            "professional_info_lookups.json",
            HttpURLConnection.HTTP_OK
        )

        val dataReceived = customerInfoRepository.professionalInfoLookups()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_resume_professional_info_retrieves_expected_data() = runBlocking {
        mockNetworkResponseWithFileContent(
            "resume_professional_info.json",
            HttpURLConnection.HTTP_OK
        )

        val dataReceived = customerInfoRepository.resumeProfessionalInfo(API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_professionalInfo_retrieves_expected_data() = runBlocking {

        val saveProfessionalInfoDto = SaveProfessionalInfoDTO(
            "A8364A6C-283C-4DF9-8CFB-0E33203F87CC",
            "00769A6D-1403-44D9-BE2D-C75A4FB6A177",
            "asfasdfasdfadf",
            "Virtual Force",
            3,
            false,
            101,
            "0140405E-779F-4090-9089-A6237B4ED75C",
            "185AB59D-4173-4FD4-9886-96D461B89876",
            false,
            17,
            false,
            15,
            1,
            null,
            null,
            "30612306-DBFA-4AE3-8E6B-A937E839B1D0"
        )

        mockNetworkResponseWithFileContent("save_professional_info.json", HttpURLConnection.HTTP_OK)

        val dataReceived = customerInfoRepository.saveProfessionalInfo(
            gson.toJson(saveProfessionalInfoDto)
                .toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_bankInfoLookups_retrieves_expected_data() = runBlocking {

        mockNetworkResponseWithFileContent("bank_info_lookups.json", HttpURLConnection.HTTP_OK)

        val dataReceived = customerInfoRepository.BankInfoLookups()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_resume_bank_info_retrieves_expected_data() = runBlocking {
        mockNetworkResponseWithFileContent("resume_bank_info.json", HttpURLConnection.HTTP_OK)

        val dataReceived = customerInfoRepository.resumeBankInfo(API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_bank_account_retrieves_expected_data() = runBlocking {

        val saveBankInfoDTO = SaveBankInfoDTO(
            "PKLAH301022155465468",
            "AZEEM KHALID",
            16,
            623,
            1,
            1,
            API_ID,
            "764F2E15-C953-4E15-8D44-54DE84EE5CA4"
        )

        mockNetworkResponseWithFileContent("save_bank_account_info.json", HttpURLConnection.HTTP_OK)

        val dataReceived = customerInfoRepository.saveBankInfo(
            gson.toJson(saveBankInfoDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_get_bank_by_branch_id_retrieves_expected_data() = runBlocking {
        mockNetworkResponseWithFileContent("bank_branches.json", HttpURLConnection.HTTP_OK)

        val dataReceived = customerInfoRepository.getBranches(16)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_get_account_title_by_iban_retrieves_expected_data() = runBlocking {
        mockNetworkResponseWithFileContent("account_title_by_IBAN.json", HttpURLConnection.HTTP_OK)

        val accountTitleDTO = GetAccountTitleDTO("PKLAH301022155465468")

        val dataReceived = customerInfoRepository.getAccountTitleByIBAN(
            gson.toJson(accountTitleDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

}