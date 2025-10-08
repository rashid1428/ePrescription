package com.doctor.eprescription.data.remote.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @SerializedName("callStatus") val callStatus: Boolean,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,

    @SerializedName("data") val data: Data
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("organizationId") val organizationId: Int,
        @SerializedName("organizationName") val _organizationName: String?,

        @SerializedName("id") val id: Int,
        @SerializedName("email") val email: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("profilePicture") val profilePicture: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("dateOfBirth") val dateOfBirth: String,
        @SerializedName("homeStreet") val homeStreet: String,
        @SerializedName("homeCity") val homeCity: String,
        @SerializedName("homeState") val homeState: HomeState,
        @SerializedName("homeZipCode") val homeZipCode: String,
        @SerializedName("specialityId") val specialityId: Int,
        @SerializedName("speciality") val speciality: String,
        @SerializedName("deaNumber") val deaNumber: String,
        @SerializedName("nationalProviderIdentifierNumber") val nationalProviderIdentifierNumber: String,
        @SerializedName("hospitalAffiliation") val hospitalAffiliation: String,
        @SerializedName("stateLicenseNumber") val stateLicenseNumber: String,
        @SerializedName("socialSecurityNumber") val socialSecurityNumber: String,
        @SerializedName("phone") val phone: String,
        @SerializedName("chargeViaCopayFlag") val chargeViaCopayFlag: String,
        @SerializedName("urgentAppointmentFlag") val urgentAppointmentFlag: String,
        @SerializedName("deaPicture") val deaPicture: List<String>,
        @SerializedName("stateLicensePicture") val stateLicensePicture: List<String>,
        @SerializedName("costPerSession") val costPerSession: Int,
        @SerializedName("isOtherSpecialtyFlag") val isOtherSpecialtyFlag: String,
        @SerializedName("otherSpecialty") val otherSpecialty: String,
        @SerializedName("medicalSchool") val medicalSchool: String,
        @SerializedName("graduationYear") val graduationYear: String,
        @SerializedName("degreeTypeId") val degreeTypeId: Int,
        @SerializedName("degreeTypeName") val degreeTypeName: String,
        @SerializedName("isOtherDegreeType") val isOtherDegreeType: String,
        @SerializedName("otherDegreeType") val otherDegreeType: String,
        @SerializedName("businessAssociatePdfURL") val businessAssociatePdfURL: String,
        @SerializedName("providerContractPdfURL") val providerContractPdfURL: String,
        @SerializedName("einImages") val einImages: List<String>,
        @SerializedName("medicalSchoolAddress") val medicalSchoolAddress: String,
        @SerializedName("stateDrivingLicenseIdImages") val stateDrivingLicenseIdImages: List<String>,
        @SerializedName("stateDrivingLicenseId") val stateDrivingLicenseId: String,
        @SerializedName("doctorStatus") val doctorStatus: String,
        @SerializedName("medicalLicenseNumberStateId") val medicalLicenseNumberStateId: Int,
        @SerializedName("medicalLicenseNumberStateName") val medicalLicenseNumberStateName: String,
        @SerializedName("businessLegalName") val businessLegalName: String,
        @SerializedName("doingBusinessAsName") val doingBusinessAsName: String,
        @SerializedName("businessAddress") val businessAddress: String,

        @SerializedName("businessCity") val businessCity: String,
        @SerializedName("businessState") val businessState: HomeState,
        @SerializedName("businessZipCode") val businessZipCode: String,
        @SerializedName("websiteURL") val websiteURL: String,
        @SerializedName("employerIdentificationNumber") val employerIdentificationNumber: String,
        @SerializedName("bankAccountNumber") val bankAccountNumber: String,
        @SerializedName("routingNumber") val routingNumber: String,
        @SerializedName("accountOwnershipType") val accountOwnershipType: String,
        @SerializedName("bankName") val bankName: String,
        @SerializedName("accountType") val accountType: String,
        @SerializedName("credentialId") val credentialId: String,
        @SerializedName("is2faPasswordSet") val is2faPasswordSet: Boolean
    ) : Parcelable {
        val fullName get() = "$firstName $lastName"
        val organizationName get() = _organizationName.orEmpty()
    }

    @Parcelize
    data class HomeState(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("abbreviation") val abbreviation: String?,
    ) : Parcelable
}
