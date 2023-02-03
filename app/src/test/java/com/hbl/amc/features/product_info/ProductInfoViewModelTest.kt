package com.hbl.amc.features.product_info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.RequestDTO.SaveOtherInfoDTO
import com.hbl.amc.domain.RequestDTO.SavePersonalInfoDTO
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.*
import com.hbl.amc.domain.usecase.ProductInfoUseCase
import com.hbl.amc.ui.productInformation.ProductInfoViewModel
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
class ProductInfoViewModelTest : BaseUTTest() {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var productInfoViewModel: ProductInfoViewModel

    val mDispatcher = Dispatchers.Unconfined

    @MockK
    lateinit var productInfoUseCase: ProductInfoUseCase

    @Before
    fun start() {
        super.setUp()
        //Used for initiation of Mockk
        MockKAnnotations.init(this)
        //Start Koin with required dependencies
        startKoin{ modules(configureTestAppComponent(getMockWebServerUrl()))}

        productInfoViewModel = ProductInfoViewModel(mDispatcher, mDispatcher, productInfoUseCase)
    }

    @Test
    fun test_products_lookups_data_populates_expected_value() {
        val sampleResponse = getJson("products_lookups.json")
        val jsonObj = Gson().fromJson(sampleResponse, ProductsGetResponse::class.java)

        //Make sure personal info lookups use case returns expected response when called
        coEvery { productInfoUseCase.getProducts("A8364A6C-283C-4DF9-8CFB-0E33203F87CC") } returns jsonObj

        productInfoViewModel.getProductsApi.observeForever {  }

        productInfoViewModel.getProducts("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        assert(productInfoViewModel.getProductsApi.value != null)
        assert(
            productInfoViewModel.getProductsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.getProductsApi.value as LiveDataWrapper<ProductsGetResponse>
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_save_productInfo_populates_expected_value(){

        val productSaveDTO = ProductSaveResult(
            "A8364A6C-283C-4DF9-8CFB-0E33203F87CC",
            "5D7C838B-8847-4ED6-B0AB-35D94C85A106",
            "20ed2240-7626-4078-86e5-80684f3788b1"
        )

        val sampleResponse = getJson("save_product_info.json")
        val jsonObj = Gson().fromJson(sampleResponse, ProductSaveResponse::class.java)
        //Make sure save personal info use case returns expected response when called
        coEvery { productInfoUseCase.saveProduct(any()) } returns jsonObj

        productInfoViewModel.saveProductsApi.observeForever {  }

        productInfoViewModel.saveProduct(productSaveDTO = productSaveDTO)

        assert(productInfoViewModel.saveProductsApi.value != null)
        assert(
            productInfoViewModel.saveProductsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.saveProductsApi.value as LiveDataWrapper<ProductSaveResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_mutual_funds_data_populates_expected_value() {
        val sampleResponse = getJson("mutual_funds_listing.json")
        val jsonObj = Gson().fromJson(sampleResponse, MutualFundsGetResponse::class.java)

        //Make sure personal info lookups use case returns expected response when called
        coEvery { productInfoUseCase.getMutualFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87C") } returns jsonObj

        productInfoViewModel.getMutualFundsApi.observeForever {  }

        productInfoViewModel.getMutualFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87C")

        assert(productInfoViewModel.getMutualFundsApi.value != null)
        assert(
            productInfoViewModel.getMutualFundsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.getMutualFundsApi.value as LiveDataWrapper<MutualFundsGetResponse>
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_mutualFundsByCustomerID_data_populates_expected_value() {
        val sampleResponse = getJson("mutual_funds_by_customer_id_listing.json")
        val jsonObj = Gson().fromJson(sampleResponse, GetSelectedFundsResponse::class.java)

        //Make sure personal info lookups use case returns expected response when called
        coEvery { productInfoUseCase.getSelectedMutualFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87C") } returns jsonObj

        productInfoViewModel.getSelectedMutualFundsApi.observeForever {  }

        productInfoViewModel.getSelectedMutualFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87C")

        assert(productInfoViewModel.getSelectedMutualFundsApi.value != null)
        assert(
            productInfoViewModel.getSelectedMutualFundsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.getSelectedMutualFundsApi.value as LiveDataWrapper<GetSelectedFundsResponse>
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_save_mutualFunds_populates_expected_value(){

        val saveMutualFundDTO = MutualFundSaveResult(
            "A8364A6C-283C-4DF9-8CFB-0E33203F87CC",
            "CDDCBFFE-5A95-4310-B8D4-FDF047130CB4",
            "7f3c02ad-c276-444e-a951-0222746843ff",
            false
        )

        val sampleResponse = getJson("save_mutual_funds.json")
        val jsonObj = Gson().fromJson(sampleResponse, MutualFundSaveResponse::class.java)
        //Make sure save personal info use case returns expected response when called
        coEvery { productInfoUseCase.saveMutualFund(any()) } returns jsonObj

        productInfoViewModel.saveMutualFundApi.observeForever {  }

        productInfoViewModel.saveMutualFund(saveMutualFundDTO = saveMutualFundDTO)

        assert(productInfoViewModel.saveMutualFundApi.value != null)
        assert(
            productInfoViewModel.saveMutualFundApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.saveMutualFundApi.value as LiveDataWrapper<MutualFundSaveResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_vps_funds_data_populates_expected_value() {
        val sampleResponse = getJson("vps_funds_listing.json")
        val jsonObj = Gson().fromJson(sampleResponse, VPSFundsGetResponse::class.java)

        //Make sure personal info lookups use case returns expected response when called
        coEvery { productInfoUseCase.getVPSFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC") } returns jsonObj

        productInfoViewModel.getVPSFundsApi.observeForever {  }

        productInfoViewModel.getVPSFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        assert(productInfoViewModel.getVPSFundsApi.value != null)
        assert(
            productInfoViewModel.getVPSFundsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.getVPSFundsApi.value as LiveDataWrapper<VPSFundsGetResponse>
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_vpsFundsByCustomerID_data_populates_expected_value() {
        val sampleResponse = getJson("vps_funds_by_customer_id_listing.json")
        val jsonObj = Gson().fromJson(sampleResponse, GetSelectedFundsResponse::class.java)

        //Make sure personal info lookups use case returns expected response when called
        coEvery { productInfoUseCase.getSelectedVPSFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC") } returns jsonObj

        productInfoViewModel.getSelectedVPSFundsApi.observeForever {  }

        productInfoViewModel.getSelectedVPSFunds("A8364A6C-283C-4DF9-8CFB-0E33203F87CC")

        assert(productInfoViewModel.getSelectedVPSFundsApi.value != null)
        assert(
            productInfoViewModel.getSelectedVPSFundsApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.getSelectedVPSFundsApi.value as LiveDataWrapper<GetSelectedFundsResponse>
        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_save_vpsFunds_populates_expected_value(){

        val saveVPSFundDTO = VPSFundSaveResult(
            "256b57a3-ec77-4f3e-d1ca-08d97c087923",
            "CDDCBFFE-5A95-4310-B8D4-FDF047130CB4",
            "7f3c02ad-c276-444e-a951-0222746843ff",
            "749F366D-9BB6-44DA-8FA8-EC871F4C7617",
            70,
            20,
            10
        )

        val sampleResponse = getJson("save_vps_funds.json")
        val jsonObj = Gson().fromJson(sampleResponse, VPSFundSaveResponse::class.java)
        //Make sure save personal info use case returns expected response when called
        coEvery { productInfoUseCase.saveVPSFund(any()) } returns jsonObj

        productInfoViewModel.saveVPSFundApi.observeForever {  }

        productInfoViewModel.saveVPSFund(saveVpsFundDTO = saveVPSFundDTO)

        assert(productInfoViewModel.saveVPSFundApi.value != null)
        assert(
            productInfoViewModel.saveVPSFundApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.saveVPSFundApi.value as LiveDataWrapper<VPSFundSaveResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }

    @Test
    fun test_save_other_info_populates_expected_value(){

        val saveOtherInfoDTO = SaveOtherInfoDTO(
            "asfasdfasdfadf",
            "256b57a3-ec77-4f3e-d1ca-08d97c087923",
            60
        )

        val sampleResponse = getJson("save_other_product_info.json")
        val jsonObj = Gson().fromJson(sampleResponse, SaveOtherInfoResponse::class.java)
        //Make sure save personal info use case returns expected response when called
        coEvery { productInfoUseCase.saveOtherInfo(any()) } returns jsonObj

        productInfoViewModel.saveOtherInfoApi.observeForever {  }

        productInfoViewModel.saveOtherInfo(saveOtherInfoDTO = saveOtherInfoDTO)

        assert(productInfoViewModel.saveOtherInfoApi.value != null)
        assert(
            productInfoViewModel.saveOtherInfoApi.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = productInfoViewModel.saveOtherInfoApi.value as LiveDataWrapper<SaveOtherInfoResponse>

        Assert.assertEquals(testResult.response!!.status, "success")
    }
}