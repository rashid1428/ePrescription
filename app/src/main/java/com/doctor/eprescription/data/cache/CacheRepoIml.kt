package com.doctor.eprescription.data.cache

import com.doctor.eprescription.data.remote.model.response.LoginResponse
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.extension.fromJson
import com.doctor.eprescription.extension.toJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CacheRepoIml @Inject constructor(private val dataStoreHelper: DataStoreHelper) : CacheRepo {
    override suspend fun isLoggedIn(): Flow<Boolean> {
        return dataStoreHelper.getValue(PrefKeys.IS_LOGGED_IN, false)
    }

    override suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStoreHelper.setValue(PrefKeys.IS_LOGGED_IN, isLoggedIn)
    }

    override suspend fun saveLoginResponse(response: LoginResponse) {
        saveDoctorEmail(response.data.email)
        dataStoreHelper.setValue(PrefKeys.LOGIN_RESPONSE, response.toJson())
    }

    override suspend fun getLoginResponse(): Flow<LoginResponse?> {
        return dataStoreHelper.getValueOrNull(PrefKeys.LOGIN_RESPONSE)
            .map {
                it?.fromJson(LoginResponse::class.java)
            }
    }

    override suspend fun saveDoctorEmail(email: String) {
        dataStoreHelper.setValue(PrefKeys.DOCTOR_EMAIL, email)
    }

    override suspend fun getDoctorEmail(): String {
        return dataStoreHelper.getValue(PrefKeys.DOCTOR_EMAIL, "").firstOrNull().orEmpty()
    }
}