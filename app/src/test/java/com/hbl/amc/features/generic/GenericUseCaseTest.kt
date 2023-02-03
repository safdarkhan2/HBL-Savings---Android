package com.hbl.amc.features.generic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hbl.amc.BaseUTTest
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.repository.GenericRepository
import com.hbl.amc.domain.usecase.GenericUseCase
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
class GenericUseCaseTest : BaseUTTest() {
        //Target
        private lateinit var genericUseCase : GenericUseCase

        val mockWebServer : MockWebServer by inject()

        @get:Rule
        var instantExecutorRule = InstantTaskExecutorRule()

        val statusCode = "success"

        @Before
        fun start(){
            super.setUp()
            //Start Koin with required dependencies
            startKoin{ modules(configureTestAppComponent(getMockWebServerUrl()))}
            genericUseCase = GenericUseCase()
        }

        @Test
        fun test_countries_use_case_returns_expected_value()= runBlocking{

            mockNetworkResponseWithFileContent("country_response.json", HttpURLConnection.HTTP_OK)

            val dataReceived = genericUseCase.getCountries()
//            val dataReceived = productUseCase.processProductUseCase(mParam)

            Assert.assertNotNull(dataReceived)
            Assert.assertEquals(dataReceived.status, statusCode)
//        assertEquals(dataReceived.message, message)
        }

        @Test
        fun test_documents_types_use_case_returns_expected_value()= runBlocking {

            mockNetworkResponseWithFileContent("documents_types.json", HttpURLConnection.HTTP_OK)

            val dataReceived = genericUseCase.getDocumentsTypes()

            Assert.assertNotNull(dataReceived)
            Assert.assertEquals(dataReceived.status, statusCode)
        }
}