package com.hbl.amc.features.generic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.repository.CustomerInfoRepository
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
class GenericRepositoryTest : BaseUTTest() {
    //Target
    private lateinit var genericRepository: GenericRepository

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
        genericRepository = GenericRepository()
    }

    @Test
    fun test_countries_retrieves_expected_data() = runBlocking{

        mockNetworkResponseWithFileContent("country_response.json", HttpURLConnection.HTTP_OK)

        val dataReceived = genericRepository.getCountries()
//            val dataReceived = productUseCase.processProductUseCase(mParam)

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
//        assertEquals(dataReceived.message, message)
    }

    @Test
    fun test_documents_types_retrieves_expected_data() = runBlocking {

        mockNetworkResponseWithFileContent("documents_types.json", HttpURLConnection.HTTP_OK)

        val dataReceived = genericRepository.getDocumentsTypes()

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }
}