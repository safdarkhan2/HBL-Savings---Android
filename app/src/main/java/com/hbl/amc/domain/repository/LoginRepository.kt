package com.hbl.amc.domain.repository

import com.hbl.amc.api_service.ApiService
import com.hbl.amc.domain.RequestDTO.ForgotPasswordDTO
import com.hbl.amc.domain.RequestDTO.LoginDTO
import com.hbl.amc.domain.model.ForgotPasswordResponse
import com.hbl.amc.domain.model.LoginResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginRepository: KoinComponent {

    private val apiService: ApiService by inject()

    suspend fun loginUser(loginDTO: LoginDTO) : LoginResponse {
        return apiService.loginUser(loginDTO)
    }

    suspend fun forgotPassword(forgotPasswordDTO: ForgotPasswordDTO) : ForgotPasswordResponse {
        return apiService.forgotPassword(forgotPasswordDTO)
    }
}