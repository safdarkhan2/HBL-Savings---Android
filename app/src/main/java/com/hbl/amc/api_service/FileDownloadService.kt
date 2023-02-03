package com.hbl.amc.api_service

import com.hbl.amc.domain.RequestDTO.MutualFundAccountStatementDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

interface FileDownloadService {
    @Streaming
    @POST("Dashboard/GetMutualFundsAccountStatement")
    fun getMutualFundAccountStatement(
        @Header("Authorization") token: String,
        @Body mutualFundAccountStatementDTO: MutualFundAccountStatementDTO
    ): Call<ResponseBody>
}