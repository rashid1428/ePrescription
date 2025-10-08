package com.doctor.eprescription.domain.repo

import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.*
import com.doctor.eprescription.data.remote.model.response.*
import com.doctor.eprescription.data.remote.model.response.common.CommonResponse

interface PatientRepo {

    suspend fun getPatients(email: String, request: GetPatientRequest): Resource<PatientsResponse>
    suspend fun getTemplateList(
        email: String,
        request: GetTemplatesRequest
    ): Resource<TemplatesResponse>

    suspend fun getMenu(request: GetMenuRequest): Resource<GetMenuResponse>
    suspend fun getMedicine(
        request: GetMedicineRequest
    ): Resource<GetMedicineResponse>

    suspend fun getDoseList(): Resource<GetDoseListResponse>
    suspend fun getRouteList(request: GetRouteListRequest): Resource<GetRouteListResponse>
    suspend fun getDirections(request: GetRouteListRequest): Resource<GetDirectionResponse>
    suspend fun getQuantityList(request: GetRouteListRequest): Resource<GetQuantitiesResponse>
    suspend fun getIcdCodeList(): Resource<GetIcdCodeListResponse>
    suspend fun addPendingMedicine(request: AddPendingMedicineRequest): Resource<AddPendingMedicineResponse>
    suspend fun getPendingMedicine(request: GetRouteListRequest): Resource<GetPendingMedicineResponse>
    suspend fun deletePendingMedicine(request: DeletePendingMedicineRequest): Resource<GetPendingMedicineResponse>
    suspend fun deleteMyTemplateMedicine(request: DeleteMyTemplateMedicineRequest): Resource<GetPendingMedicineResponse>
    suspend fun addMyTemplateMedicine(request: AddMyTemplateMedicineRequest): Resource<CommonResponse>
    suspend fun getStateList(): Resource<GetStateListResponse>
    suspend fun getPharmacy(request: GetPharmacyRequest): Resource<GetPharmacyResponse>
    suspend fun addPatient(email: String, request: AddPatientRequest): Resource<AddPatientResponse>
    suspend fun updatePatient(
        email: String,
        request: AddPatientRequest
    ): Resource<AddPatientResponse>

    suspend fun sendMedicine(request: SendMedicineRequest): Resource<Any>
    suspend fun getSessionPrescription(request: GetSessionPrescriptionRequest): Resource<GetSessionPrescriptionResponse>
    suspend fun getPrescriptionSession(request: GetSessionPrescriptionRequest): Resource<GetSessionPrescriptionResponse>
    suspend fun getPrescriptionDetails(request: GetPrescriptionDetailRequest): Resource<GetPrescriptionDetailResponse>
    suspend fun cancelErx(request: CancelErxRequest): Resource<CancelErxResponse>
    suspend fun deletePatient(request: DeletePatientRequest): Resource<DeletePatientResponse>
    suspend fun getSig(request: GetSigRequest): Resource<GetSigResponse>
    suspend fun updatePendingMedicine(request: UpdatePendingMedicineRequest): Resource<CommonResponse>
    suspend fun updatePharmacy(request: UpdatePharmacyRequest): Resource<CommonResponse>
    suspend fun ecpsVerification(request: EcpsVerificationRequest): Resource<Any>
    suspend fun getFoodAndAllergiesList(): Resource<FoodAndAllergiesListResponse>
    suspend fun updatePatientFoodAndDrugAllergies(request: UpdateFoodAndDrugAllergiesRequest): Resource<CommonResponse>
}