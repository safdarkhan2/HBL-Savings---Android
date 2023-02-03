package com.hbl.amc.domain.repository

import android.util.Log
import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.model.GetMutualFundDashboardResponse

import com.hbl.amc.domain.model.GetProfileInfoResponse

import com.hbl.amc.domain.model.VPSDashboardResponse
import okhttp3.Response

import okhttp3.ResponseBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call

class DashboardRepository : KoinComponent {

    private val apiService: ApiService by inject()

    suspend fun getMutualFundDashboard(
        token: String,
        customerID: Int,
        duration: Int
    ): GetMutualFundDashboardResponse {
        return apiService.getMutualFundDashboard(
            token = token,
            customerId = customerID,
            duration = duration
        )
    }

    /*suspend fun getMutualFundAccountStatement(
        token: String, mutualFundAccountStatementDTO: MutualFundAccountStatementDTO
    ): Response {
        return apiService.getMutualFundAccountStatement(
            token = token,
            mutualFundAccountStatementDTO
        )
    }*/


    suspend fun getProfileInfo(token: String, customerID: String): GetProfileInfoResponse {
        Log.d("Repo Token", token)
        return apiService.getProfileInfo(token = token, customerId = customerID)
    }
    suspend fun getVPSFundDashboard(
        token: String,
        customerID: String,
        duration: String
    ): VPSDashboardResponse {
        return apiService.getVPSFundDashboard(
            token = token,
            customerId = customerID,
            duration = duration
        )
    }

}