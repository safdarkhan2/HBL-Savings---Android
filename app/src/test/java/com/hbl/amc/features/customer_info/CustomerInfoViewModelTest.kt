package com.hbl.amc.features.customer_info

import android.provider.UserDictionary
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.RequestDTO.GetAccountTitleDTO
import com.hbl.amc.domain.RequestDTO.SaveBankInfoDTO
import com.hbl.amc.domain.RequestDTO.SavePersonalInfoDTO
import com.hbl.amc.domain.RequestDTO.SaveProfessionalInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.CustomerInfoUseCase
import com.hbl.amc.domain.usecase.GenericUseCase
import com.hbl.amc.ui.API_ID
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.ui.GenericViewModel
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
class CustomerInfoViewModelTest : BaseUTTest() {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var customerInfoViewModel: CustomerInfoViewModel

    val mDispatcher = Dispatchers.Unconfined

    @MockK
    lateinit var customerInfoUseCase: CustomerInfoUseCase


    @Before
    fun start(){
        super.setUp()
        //Used for initiation of Mockk
        MockKAnnotations.init(this)
        //Start Koin with required dependencies
        startKoin{ modules(configureTestAppComponent(getMockWebServerUrl()))}

        customerInfoViewModel = CustomerInfoViewModel(mDispatcher, mDispatcher, customerInfoUseCase)
    }

    @Test
    fun test_personal_info_lookups_data_populates_expected_value(){

        val sampleResponse = getJson("personal_info_lookups.json")
        var jsonObj = Gson().fromJson(sampleResponse, PersonalInfoLookupsResponse::class.java)

        //Make sure personal info lookups use case returns expected response when called
        coEvery { customerInfoUseCase.getPersonalInfoLookup() } returns jsonObj

        customerInfoViewModel.personalInfoLookUpsApi.observeForever {  }

        customerInfoViewModel.getPersonalInfoLookups()

        assert(customerInfoViewModel.personalInfoLookUpsApi.value != null)
        assert(
            customerInfoViewModel.personalInfoLookUpsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.personalInfoLookUpsApi.value as LiveDataWrapper<PersonalInfoLookupsResponse>
//        Assert.assertEquals(testResult.response!!.message, null)
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_save_personal_info_data_populates_expected_value(){

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

        val sampleResponse = getJson("save_personal_info.json")
        var jsonObj = Gson().fromJson(sampleResponse, SavePersonalInfoResponse::class.java)
        //Make sure save personal info use case returns expected response when called
        coEvery { customerInfoUseCase.savePersonalInfo(any()) } returns jsonObj

        customerInfoViewModel.personalInfoLookUpsApi.observeForever {  }

        customerInfoViewModel.savePersonalInfo(savePersonalInfoDTO = savePersonalInfoDto)

        assert(customerInfoViewModel.savePersonalInfoApi.value != null)
        assert(
            customerInfoViewModel.savePersonalInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.savePersonalInfoApi.value as LiveDataWrapper<SavePersonalInfoResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_professional_info_lookups_data_populates_expected_value(){

        val sampleResponse = getJson("professional_info_lookups.json")
        var jsonObj = Gson().fromJson(sampleResponse, ProfessionalInfoLookupResponse::class.java)

        //Make sure professional info lookups use case returns expected response when called
        coEvery { customerInfoUseCase.getProfessionalInfoLookup() } returns jsonObj

        customerInfoViewModel.professionalInfoLookUpsApi.observeForever {  }

        customerInfoViewModel.getProfessionalInfoLookups()

        assert(customerInfoViewModel.professionalInfoLookUpsApi.value != null)
        assert(
            customerInfoViewModel.professionalInfoLookUpsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.professionalInfoLookUpsApi.value as LiveDataWrapper<ProfessionalInfoLookupResponse>
//        Assert.assertEquals(testResult.response!!.message, null)
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_resume_professional_info_data_populates_expected_value(){

        val sampleResponse = getJson("resume_professional_info.json")
        var jsonObj = Gson().fromJson(sampleResponse, ResumeProfessionalInfoRes::class.java)

        //Make sure resume professional info use case returns expected response when called
        coEvery { customerInfoUseCase.resumeProfessionalInfo(API_ID) } returns jsonObj

        customerInfoViewModel.resumeProfessionalInfoApi.observeForever {  }

        customerInfoViewModel.resumeProfessionalInfo(API_ID)

        assert(customerInfoViewModel.resumeProfessionalInfoApi.value != null)
        assert(
            customerInfoViewModel.resumeProfessionalInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.resumeProfessionalInfoApi.value as LiveDataWrapper<ResumeProfessionalInfoRes>
//        Assert.assertEquals(testResult.response!!.message, null)
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_save_professional_info_data_populates_expected_value(){

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

        val sampleResponse = getJson("save_professional_info.json")
        var jsonObj = Gson().fromJson(sampleResponse, SaveProfessionalInfoResponse::class.java)
        //Make sure save personal info use case returns expected response when called
        coEvery { customerInfoUseCase.saveProfessionalInfo(any()) } returns jsonObj

        customerInfoViewModel.saveProfessionalInfoApi.observeForever {  }

        customerInfoViewModel.saveProfessionalInfo(saveProfessionalInfoDTO = saveProfessionalInfoDto)

        assert(customerInfoViewModel.saveProfessionalInfoApi.value != null)
        assert(
            customerInfoViewModel.saveProfessionalInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.saveProfessionalInfoApi.value as LiveDataWrapper<SaveProfessionalInfoResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_bankInfoLookups_data_populates_expected_value() {

        val sampleResponse = getJson("bank_info_lookups.json")
        var jsonObj = Gson().fromJson(sampleResponse, BankInfoLookupsResponse::class.java)

        coEvery { customerInfoUseCase.getBankInfoLookup() } returns jsonObj

        customerInfoViewModel.bankInfoLookUpsApi.observeForever {  }

        customerInfoViewModel.getBankInfoLookups()

        assert(customerInfoViewModel.bankInfoLookUpsApi.value != null)
        assert(
            customerInfoViewModel.bankInfoLookUpsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.bankInfoLookUpsApi.value as LiveDataWrapper<BankInfoLookupsResponse>
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_resume_bank_info_data_populates_expected_value() {

        val sampleResponse = getJson("resume_bank_info.json")
        var jsonObj = Gson().fromJson(sampleResponse, ResumeBankInfoRes::class.java)

        coEvery { customerInfoUseCase.resumeBankInfo(API_ID) } returns jsonObj

        customerInfoViewModel.resumeBankInfoApi.observeForever {  }

        customerInfoViewModel.resumeBankInfo(API_ID)

        assert(customerInfoViewModel.resumeBankInfoApi.value != null)
        assert(
            customerInfoViewModel.resumeBankInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.resumeBankInfoApi.value as LiveDataWrapper<ResumeBankInfoRes>
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_save_bank_account_use_case_returns_expected_value() {

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

        val sampleResponse = getJson("save_bank_account_info.json")
        var jsonObj = Gson().fromJson(sampleResponse, SaveBankInfoResponse::class.java)

        coEvery { customerInfoUseCase.saveBankInfo(any()) } returns jsonObj

        customerInfoViewModel.saveBankInfoApi.observeForever { }

        customerInfoViewModel.saveBankInfo(saveBankInfoDTO = saveBankInfoDTO)

        assert(customerInfoViewModel.saveBankInfoApi.value != null)
        assert(
            customerInfoViewModel.saveBankInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS
        )
        val testResult =
            customerInfoViewModel.saveBankInfoApi.value as LiveDataWrapper<SaveBankInfoResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_get_bank_by_branch_id_data_populates_expected_value() {

        val sampleResponse = getJson("bank_branches.json")
        var jsonObj = Gson().fromJson(sampleResponse, BranchResponse::class.java)

        coEvery { customerInfoUseCase.getBranches(any()) } returns jsonObj

        customerInfoViewModel.allBranchesResponse.observeForever {  }

        customerInfoViewModel.getBranchesData(16)

        assert(customerInfoViewModel.allBranchesResponse.value != null)
        assert(
            customerInfoViewModel.allBranchesResponse.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.allBranchesResponse.value as LiveDataWrapper<BranchResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_get_account_title_by_iban_use_data_populates_expected_value() {

        val sampleResponse = getJson("account_title_by_IBAN.json")
        var jsonObj = Gson().fromJson(sampleResponse, AccountTitleByIBANResponse::class.java)

        coEvery { customerInfoUseCase.getAccountTitleByIBAN(any()) } returns jsonObj

        customerInfoViewModel.accountTitleByIBANApi.observeForever {  }

        val accountTitleDTO = GetAccountTitleDTO("PKLAH301022155465468")
        customerInfoViewModel.getAccountTitleByIBAN(accountTitleDTO = accountTitleDTO)

        assert(customerInfoViewModel.accountTitleByIBANApi.value != null)
        assert(
            customerInfoViewModel.accountTitleByIBANApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = customerInfoViewModel.accountTitleByIBANApi.value as LiveDataWrapper<AccountTitleByIBANResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }
}