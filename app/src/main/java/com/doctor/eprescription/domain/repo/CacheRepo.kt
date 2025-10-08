package com.doctor.eprescription.domain.repo

import com.doctor.eprescription.data.remote.model.response.LoginResponse
import kotlinx.coroutines.flow.Flow

interface CacheRepo {

    suspend fun isLoggedIn(): Flow<Boolean>

    suspend fun setLoggedIn(isLoggedIn: Boolean)

    suspend fun saveDoctorEmail(email: String)

    suspend fun getDoctorEmail(): String

    suspend fun saveLoginResponse(response: LoginResponse)

    suspend fun getLoginResponse(): Flow<LoginResponse?>
}