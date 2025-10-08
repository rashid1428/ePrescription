package com.doctor.eprescription.data.remote.repo

import com.doctor.eprescription.data.remote.ApiNames
import com.doctor.eprescription.data.remote.ApiService
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.*
import com.doctor.eprescription.data.remote.model.response.*
import com.doctor.eprescription.data.remote.model.response.common.CommonResponse
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.PatientRepo
import javax.inject.Inject

class PatientRepoImpl @Inject constructor(apiService: ApiService, val cacheRepo: CacheRepo) :
    PatientRepo,
    BaseRepo(apiService) {

    override suspend fun getPatients(
        email: String,
        request: GetPatientRequest
    ): Resource<PatientsResponse> {
        return safeApiCall(email, request, ApiNames.PATIENT_LIST)

        /*return safeApiCall {
            apiService.getPatients(email, request)
        }*/
    }

    override suspend fun getTemplateList(
        email: String,
        request: GetTemplatesRequest
    ): Resource<TemplatesResponse> {
        return safeApiCall(email, request, ApiNames.TEMPLATE_LIST)
    }

    override suspend fun getMenu(
        request: GetMenuRequest
    ): Resource<GetMenuResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_MENU)
        /*return safeApiCall {
            apiService.getMenu(email, request)
        }*/
    }

    override suspend fun getMedicine(
        request: GetMedicineRequest
    ): Resource<GetMedicineResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_MEDICINES)
    }

    override suspend fun getDoseList(): Resource<GetDoseListResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), "", ApiNames.GET_DOSE_LIST)
    }

    override suspend fun getRouteList(request: GetRouteListRequest): Resource<GetRouteListResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_ROUTE_LIST)
    }

    override suspend fun getDirections(request: GetRouteListRequest): Resource<GetDirectionResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_DIRECTION_LIST)
    }

    override suspend fun getQuantityList(request: GetRouteListRequest): Resource<GetQuantitiesResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_QUANTITIES)
    }

    override suspend fun getIcdCodeList(): Resource<GetIcdCodeListResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), "", ApiNames.GET_ICD_CODE_LIST)
    }

    override suspend fun addPendingMedicine(request: AddPendingMedicineRequest): Resource<AddPendingMedicineResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.ADD_PENDING_MEDICINE)
    }

    override suspend fun getPendingMedicine(request: GetRouteListRequest): Resource<GetPendingMedicineResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_PENDING_MEDICINE)
    }

    override suspend fun deletePendingMedicine(request: DeletePendingMedicineRequest): Resource<GetPendingMedicineResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.DELETE_PENDING_MEDICINE)
    }

    override suspend fun deleteMyTemplateMedicine(request: DeleteMyTemplateMedicineRequest): Resource<GetPendingMedicineResponse> {
        return safeApiCall(
            email = cacheRepo.getDoctorEmail(),
            request = request,
            apiUrl = ApiNames.DELETE_MY_TEMPLATE_MEDICINE
        )
    }

    override suspend fun addMyTemplateMedicine(request: AddMyTemplateMedicineRequest): Resource<CommonResponse> {
        return safeApiCall(
            email = cacheRepo.getDoctorEmail(),
            request = request,
            apiUrl = ApiNames.ADD_MY_TEMPLATE_MEDICINE
        )
    }

    override suspend fun getStateList(): Resource<GetStateListResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), "", ApiNames.GET_STATE_LIST)
    }

    override suspend fun getPharmacy(request: GetPharmacyRequest): Resource<GetPharmacyResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_PHARMACY)
    }

    override suspend fun addPatient(
        email: String,
        request: AddPatientRequest
    ): Resource<AddPatientResponse> {
        return safeApiCall(email, request, ApiNames.ADD_PATIENT)
    }

    override suspend fun updatePatient(
        email: String,
        request: AddPatientRequest
    ): Resource<AddPatientResponse> {
        return safeApiCall(email, request, ApiNames.UPDATE_PATIENT)
    }

    override suspend fun sendMedicine(request: SendMedicineRequest): Resource<Any> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.NEW_ERX)
    }

    override suspend fun getSessionPrescription(request: GetSessionPrescriptionRequest): Resource<GetSessionPrescriptionResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_SESSION_PRESCRIPTION)
    }

    override suspend fun getPrescriptionSession(request: GetSessionPrescriptionRequest): Resource<GetSessionPrescriptionResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_PRESCRIPTION_SESSION)
    }

    override suspend fun getPrescriptionDetails(request: GetPrescriptionDetailRequest): Resource<GetPrescriptionDetailResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_PRESCRIPTION_DETAILS)
    }

    override suspend fun cancelErx(request: CancelErxRequest): Resource<CancelErxResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.CANCEL_ERX)
    }

    override suspend fun deletePatient(request: DeletePatientRequest): Resource<DeletePatientResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.DELETE_PATIENT)
    }

    override suspend fun getSig(request: GetSigRequest): Resource<GetSigResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.GET_SIG)
    }

    override suspend fun updatePendingMedicine(request: UpdatePendingMedicineRequest): Resource<CommonResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.UPDATE_PENDING_MEDICINE)
    }

    override suspend fun updatePharmacy(request: UpdatePharmacyRequest): Resource<CommonResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.UPDATE_PHARMACY)
    }

    override suspend fun ecpsVerification(request: EcpsVerificationRequest): Resource<Any> {
        return safeApiCall(cacheRepo.getDoctorEmail(), request, ApiNames.ECPS_VERIFICATION_REQUEST)
    }

    override suspend fun getFoodAndAllergiesList(): Resource<FoodAndAllergiesListResponse> {
        return safeApiCall(cacheRepo.getDoctorEmail(), "", ApiNames.GET_FOOD_AND_ALLERGIES_LIST)
    }

    override suspend fun updatePatientFoodAndDrugAllergies(request: UpdateFoodAndDrugAllergiesRequest): Resource<CommonResponse> {
        return safeApiCall(
            email = cacheRepo.getDoctorEmail(),
            request = request,
            apiUrl = ApiNames.UPDATE_PATIENT_FOOD_AND_DRUG_ALLERGIES
        )
    }
}