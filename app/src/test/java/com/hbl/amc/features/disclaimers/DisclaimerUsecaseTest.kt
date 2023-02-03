package com.hbl.amc.features.disclaimers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.repository.CustomerInfoRepository
import com.hbl.amc.domain.usecase.CustomerInfoUseCase
import com.hbl.amc.domain.usecase.DisclaimersUseCase
import com.hbl.amc.ui.API_ID
import kotlinx.coroutines.runBlocking
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
class DisclaimerUsecaseTest : BaseUTTest() {
    //Target
    private lateinit var disclaimersUseCase: DisclaimersUseCase

    //Inject Mockwebserver created with koin
    val mockWebServer: MockWebServer by inject()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val statusCode = "success"
    val gson = Gson()

    @Before
    fun start() {
        super.setUp()
        //Start Koin with required dependencies
        startKoin { modules(configureTestAppComponent(getMockWebServerUrl())) }

        disclaimersUseCase = DisclaimersUseCase()
    }

    @Test
    fun test_get_customer_document_checklist_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("customer_document_checklist.json", HttpURLConnection.HTTP_OK)

        val dataReceived = disclaimersUseCase.getCustomerDocumentChecklist(API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, statusCode)
    }
}