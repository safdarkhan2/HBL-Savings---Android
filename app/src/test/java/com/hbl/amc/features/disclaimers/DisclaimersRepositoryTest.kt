package com.hbl.amc.features.disclaimers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.repository.CustomerInfoRepository
import com.hbl.amc.domain.repository.DisclaimersRepository
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
class disclaimers : BaseUTTest() {

    //Target
    private lateinit var disclaimersRepository: DisclaimersRepository

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
        disclaimersRepository = DisclaimersRepository()
    }

    @Test
    fun test_get_customer_document_checklist_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("customer_document_checklist.json", HttpURLConnection.HTTP_OK)

        val dataReceived = disclaimersRepository.getCustomerDocumentsChecklist(API_ID)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }
}