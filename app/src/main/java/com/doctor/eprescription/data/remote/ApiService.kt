package com.doctor.eprescription.data.remote

import com.doctor.eprescription.data.remote.model.request.GetMenuRequest
import com.doctor.eprescription.data.remote.model.request.GetPatientRequest
import com.doctor.eprescription.data.remote.model.request.LoginRequest
import com.doctor.eprescription.data.remote.model.response.GetMenuResponse
import com.doctor.eprescription.data.remote.model.response.LoginResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST
    suspend fun postRequest(
        @Url url: String,
        @Header(HeadersNames.EMAIL) email: String,
        @Body body: JsonObject
    ): Response<JsonObject>


    @POST(ApiNames.LOGIN)
    suspend fun login(
        @Header(HeadersNames.EMAIL) email: String,
        @Body body: LoginRequest
    ): Response<LoginResponse>


    @POST(ApiNames.PATIENT_LIST)
    suspend fun getPatients(
        @Header(HeadersNames.EMAIL) email: String,
        @Body body: GetPatientRequest
    ): Response<PatientsResponse>

    @POST(ApiNames.GET_MENU)
    suspend fun getMenu(
        @Header(HeadersNames.EMAIL) email: String,
        @Body body: GetMenuRequest
    ): Response<GetMenuResponse>
}