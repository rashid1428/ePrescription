package com.doctor.eprescription.data.remote.repo

import android.util.Log
import com.doctor.eprescription.data.remote.ApiNames
import com.doctor.eprescription.data.remote.ApiService
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.LoginRequest
import com.doctor.eprescription.data.remote.model.response.LoginResponse
import com.doctor.eprescription.domain.repo.LoginRepo
import com.doctor.eprescription.extension.toJson
import com.google.gson.JsonParser
import javax.inject.Inject

class LoginRepoImpl @Inject constructor(apiService: ApiService) : LoginRepo,
    BaseRepo(apiService) {
    override suspend fun login(email: String, loginRequest: LoginRequest): Resource<LoginResponse> {
        return safeApiCall(email, loginRequest, ApiNames.LOGIN)

        /*val jsonRequest = JsonParser.parseString(loginRequest.toJson()).asJsonObject

        val call = apiService.postRequest(ApiNames.LOGIN, email, jsonRequest)

        try {
            if (call.isSuccessful && call.body() != null) {
                Log.e("LoginRepoImpl", call.body().toString())
            } else {

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return Resource.Error("Just testing")*/
        /*return safeApiCall(
            call = { apiService.login(email, loginRequest) }
        )*/
    }

}