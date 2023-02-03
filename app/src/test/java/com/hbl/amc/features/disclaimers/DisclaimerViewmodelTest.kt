package com.hbl.amc.features.disclaimers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.DocumentChecklistResponse
import com.hbl.amc.domain.model.PersonalInfoLookupsResponse
import com.hbl.amc.domain.usecase.CustomerInfoUseCase
import com.hbl.amc.domain.usecase.DisclaimersUseCase
import com.hbl.amc.ui.API_ID
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.ui.Disclaimers.DisclaimersViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin

@RunWith(JUnit4::class)
class DisclaimerViewmodelTest : BaseUTTest() {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var disclaimersViewModel: DisclaimersViewModel

    val mDispatcher = Dispatchers.Unconfined

    @MockK
    lateinit var disclaimersUseCase: DisclaimersUseCase

    val status = "success"

    @Before
    fun start(){
        super.setUp()
        //Used for initiation of Mockk
        MockKAnnotations.init(this)
        //Start Koin with required dependencies
        startKoin{ modules(configureTestAppComponent(getMockWebServerUrl()))}

        disclaimersViewModel = DisclaimersViewModel(mDispatcher, mDispatcher, disclaimersUseCase)
    }

    @Test
    fun test_personal_info_lookups_data_populates_expected_value(){

        val sampleResponse = getJson("customer_document_checklist.json")
        var jsonObj = Gson().fromJson(sampleResponse, DocumentChecklistResponse::class.java)

        //Make sure personal info lookups use case returns expected response when called
        coEvery { disclaimersUseCase.getCustomerDocumentChecklist(any()) } returns jsonObj

        disclaimersViewModel.documentsChecklistApi.observeForever {  }

        disclaimersViewModel.getCustomerDocumentChecklist(API_ID)

        assert(disclaimersViewModel.documentsChecklistApi.value != null)
        assert(
            disclaimersViewModel.documentsChecklistApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = disclaimersViewModel.documentsChecklistApi.value as LiveDataWrapper<DocumentChecklistResponse>
        Assert.assertEquals(testResult.response!!.status, status)
    }
}