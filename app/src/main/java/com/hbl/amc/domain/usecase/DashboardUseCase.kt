package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.model.GetMutualFundDashboardResponse

import com.hbl.amc.domain.model.GetProfileInfoResponse

import com.hbl.amc.domain.model.VPSDashboardResponse
import com.hbl.amc.domain.repository.DashboardRepository
import okhttp3.Response
import okhttp3.ResponseBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call

class DashboardUseCase : KoinComponent {

    private val dashboardRepository by inject<DashboardRepository>()

    suspend fun getMutualFundDashboard(
        token: String,
        customerID: Int,
        duration: Int
    ): GetMutualFundDashboardResponse {
        return dashboardRepository.getMutualFundDashboard(
            token = token,
            customerID = customerID,
            duration = duration
        )
    }

    /*suspend fun getMutualFundAccountStatement(
        token: String, mutualFundAccountStatementDTO: MutualFundAccountStatementDTO
    ): Response {
        return dashboardRepository.getMutualFundAccountStatement(
            token = token,
            mutualFundAccountStatementDTO
        )
    }*/


    suspend fun getProfileInfo(token: String, customerID: String): GetProfileInfoResponse {
        return dashboardRepository.getProfileInfo(token = token, customerID = customerID)
    }

    suspend fun getVPSFundDashboard(
        token: String,
        customerID: String,
        duration: String
    ): VPSDashboardResponse {
        return dashboardRepository.getVPSFundDashboard(
            token = token,
            customerID = customerID,
            duration = duration
        )
    }
}