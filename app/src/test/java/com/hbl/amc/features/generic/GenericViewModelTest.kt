package com.hbl.amc.features.generic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.hbl.amc.BaseUTTest
import com.hbl.amc.configureTestAppComponent
import com.hbl.amc.domain.datasource.LiveDataWrapper
import com.hbl.amc.domain.model.CountryResponse
import com.hbl.amc.domain.model.DocumentsTypesResponse
import com.hbl.amc.domain.usecase.GenericUseCase
import com.hbl.amc.ui.GenericViewModel
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
class GenericViewModelTest : BaseUTTest() {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var genericViewModel: GenericViewModel

    val mDispatcher = Dispatchers.Unconfined

    @MockK
    lateinit var genericUseCase: GenericUseCase

    val status = "success"

    @Before
    fun start(){
        super.setUp()
        //Used for initiation of Mockk
        MockKAnnotations.init(this)
        //Start Koin with required dependencies
        startKoin{ modules(configureTestAppComponent(getMockWebServerUrl()))}

        genericViewModel = GenericViewModel(mDispatcher, mDispatcher, genericUseCase)
    }

    @Test
    fun test_generic_view_model_country_data_populates_expected_value(){

//        mLoginActivityViewModel = LoginActivityViewModel(mDispatcher,mDispatcher,mLoginUseCase)
        val sampleResponse = getJson("country_response.json")
        var jsonObj = Gson().fromJson(sampleResponse, CountryResponse::class.java)
        //Make sure login use case returns expected response when called
        coEvery { genericUseCase.getCountries() } returns jsonObj
//        coEvery { genericUseCase.getCountries(any()) } returns jsonObj
        genericViewModel.allCountriesResponse.observeForever {  }

        genericViewModel.getCountriesData()
//        genericViewModel.requestProductData(mParam)

        assert(genericViewModel.allCountriesResponse.value != null)
        assert(
            genericViewModel.allCountriesResponse.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = genericViewModel.allCountriesResponse.value as LiveDataWrapper<CountryResponse>
        Assert.assertEquals(testResult.response!!.message, null)
        Assert.assertEquals(testResult.response!!.status, "success")

//        val string = "awesome"
//        assertThat(string).startsWith("awe")
//        assertWithMessage("Without me, it's just aweso").that(string).contains("me")

//        assertThat(testResult.response?.model)
//            .containsExactly(sampleResponse)
//            .inOrder()
    }

    @Test
    fun test_get_document_types_data_populates_expected_value(){

        val sampleResponse = getJson("documents_types.json")
        var jsonObj = Gson().fromJson(sampleResponse, DocumentsTypesResponse::class.java)

        coEvery { genericUseCase.getDocumentsTypes() } returns jsonObj

        genericViewModel.documentTypesResponse.observeForever {  }

        genericViewModel.getAllDocumentsTypes()

        assert(genericViewModel.documentTypesResponse.value != null)
        assert(
            genericViewModel.documentTypesResponse.value!!.responseStatus
                    == LiveDataWrapper.Status.SUCCESS)
        val testResult = genericViewModel.documentTypesResponse.value as LiveDataWrapper<DocumentsTypesResponse>
//        Assert.assertEquals(testResult.response!!.message, null)
        Assert.assertEquals(testResult.response!!.status, status)
    }
}