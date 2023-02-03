package com.hbl.amc.domain.usecase

import com.hbl.amc.domain.RequestDTO.ForgotPasswordDTO
import com.hbl.amc.domain.RequestDTO.LoginDTO
import com.hbl.amc.domain.model.ForgotPasswordResponse
import com.hbl.amc.domain.model.LoginResponse
import com.hbl.amc.domain.repository.GenericRepository
import com.hbl.amc.domain.repository.LoginRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginUsecase  : KoinComponent {

    private val loginRepository by inject<LoginRepository>()

    suspend fun loginUser(loginDTO: LoginDTO) : LoginResponse {
        return loginRepository.loginUser(loginDTO)
    }

    suspend fun forgotPassword(forgotPasswordDTO: ForgotPasswordDTO) : ForgotPasswordResponse {
        return loginRepository.forgotPassword(forgotPasswordDTO)
    }
}