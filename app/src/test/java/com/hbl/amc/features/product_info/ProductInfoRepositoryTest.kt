package com.hbl.amc.features.product_info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.model.MutualFundSaveResult
import com.hbl.amc.domain.model.ProductSaveResult
import com.hbl.amc.domain.model.VPSFundSaveResult
import com.hbl.amc.domain.repository.ProductInfoRepository
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
class ProductInfoRepositoryTest : BaseUTTest() {

    //Target
    private lateinit var productInfoRepository: ProductInfoRepository

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
        productInfoRepository = ProductInfoRepository()
    }

    @Test
    fun test_product_info_lookups_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("products_lookups.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.getProducts("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_productInfo_retrieves_expected_data() = runBlocking {

        val productSaveDTO = ProductSaveResult(
            "A8364A6C-283C-4DF9-8CFB-0E33203F87CC",
            "5D7C838B-8847-4ED6-B0AB-35D94C85A106",
            "20ed2240-7626-4078-86e5-80684f3788b1"
        )

        mockNetworkResponseWithFileContent("save_product_info.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.saveProduct(
            gson.toJson(productSaveDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_mutualFunds_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("mutual_funds_listing.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.getMutualFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_mutualFundsByCustomerID_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("mutual_funds_by_customer_id_listing.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.getSelectedMutualFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_mutualFunds_retrieves_expected_data() = runBlocking {

        val saveMutualFundDTO = MutualFundSaveResult(
            "A8364A6C-283C-4DF9-8CFB-0E33203F87CC",
            "CDDCBFFE-5A95-4310-B8D4-FDF047130CB4",
            "7f3c02ad-c276-444e-a951-0222746843ff",
            false
        )

        mockNetworkResponseWithFileContent("save_mutual_funds.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.saveMutualFund(
            gson.toJson(saveMutualFundDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_vpsFunds_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("vps_funds_listing.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.getVPSFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_vpsFundsByCustomerID_retrieves_expected_data() = runBlocking<Unit> {

        mockNetworkResponseWithFileContent("vps_funds_by_customer_id_listing.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.getSelectedVPSFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

    @Test
    fun test_save_vpsFunds_retrieves_expected_data() = runBlocking {

        val saveVPSFundDTO = VPSFundSaveResult(
            "256b57a3-ec77-4f3e-d1ca-08d97c087923",
            "CDDCBFFE-5A95-4310-B8D4-FDF047130CB4",
            "7f3c02ad-c276-444e-a951-0222746843ff",
            "749F366D-9BB6-44DA-8FA8-EC871F4C7617",
            70,
            20,
            10
        )

        mockNetworkResponseWithFileContent("save_vps_funds.json", HttpURLConnection.HTTP_OK)

        val dataReceived = productInfoRepository.saveVPSFund(
            gson.toJson(saveVPSFundDTO).toRequestBody("application/json".toMediaTypeOrNull())
        )

        Assert.assertNotNull(dataReceived)
        Assert.assertEquals(dataReceived.status, status)
    }

}