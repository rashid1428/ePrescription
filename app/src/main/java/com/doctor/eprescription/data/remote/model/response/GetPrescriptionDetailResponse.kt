package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetPrescriptionDetailResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: PrescriptionDetailData
) {

    data class PrescriptionDetailData(
        @SerializedName("renewalRequestDetailData") val renewalRequestDetailData: RenewalRequestDetailData
    )

    data class RenewalRequestDetailData(
        @SerializedName("newPrescriptionId") val newPrescriptionId: Int,
        @SerializedName("previousPrescriptionId") val previousPrescriptionId: Int,
        @SerializedName("doctor") val doctor: Any?, // Replace 'Any?' with the appropriate type if known
        @SerializedName("pharmacy") val pharmacy: Any?, // Replace 'Any?' with the appropriate type if known
        @SerializedName("patient") val patient: Any?, // Replace 'Any?' with the appropriate type if known
        @SerializedName("dispensedMedication") val dispensedMedication: Any?, // Replace 'Any?' with the appropriate type if known
        @SerializedName("previousDoctor") val previousDoctor: PreviousDoctor,
        @SerializedName("previousPharmacy") val previousPharmacy: PreviousPharmacy,
        @SerializedName("previousPatient") val previousPatient: PreviousPatient,
        @SerializedName("prescribedMedication") val prescribedMedication: PrescribedMedication,
        @SerializedName("isEPCSEnable") val isEPCSEnable: Any?, // Replace 'Any?' with the appropriate type if known
        @SerializedName("controlledSubstance") val controlledSubstance: Boolean
    )

    data class PreviousDoctor(
        @SerializedName("prescriberId") val prescriberId: Int,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String
    ) {
        val fullName get() = "$firstName $lastName"
    }

    data class PreviousPharmacy(
        @SerializedName("pharmacyId") val pharmacyId: Int,
        @SerializedName("name") val name: String
    )

    data class PreviousPatient(
        @SerializedName("patientId") val patientId: Int,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("dateOfBirth") val dateOfBirth: String,
        @SerializedName("address") val address: String,
        @SerializedName("addressLine2") val addressLine2: String?, // Replace `String?` with the actual data type for the "addressLine2" field if available
        @SerializedName("city") val city: String,
        @SerializedName("state") val state: String,
        @SerializedName("zipCode") val zipCode: String,
        @SerializedName("countryCode") val countryCode: String?, // Replace `String?` with the actual data type for the "countryCode" field if available
        @SerializedName("phone") val phone: String,
        @SerializedName("height") val height: String,
        @SerializedName("weight") val weight: String,
        @SerializedName("heightUnitOfMeasure") val heightUnitOfMeasure: String?, // Replace `String?` with the actual data type for the "heightUnitOfMeasure" field if available
        @SerializedName("weightUnitOfMeasure") val weightUnitOfMeasure: String? // Replace `String?` with the actual data type for the "weightUnitOfMeasure" field if available
    ) {
        val fullName get() = "$firstName $lastName"
    }

    data class PrescribedMedication(
        @SerializedName("medicineId") val medicineId: Int,
        @SerializedName("medicineRequestType") val medicineRequestType: Int,
        @SerializedName("medicineName") val medicineName: String,
        @SerializedName("productCode") val productCode: String?, // Replace `String?` with the actual data type for the "productCode" field if available
        @SerializedName("productCodeQualifier") val productCodeQualifier: String?, // Replace `String?` with the actual data type for the "productCodeQualifier" field if available
        @SerializedName("medicineType") val medicineType: String?, // Replace `String?` with the actual data type for the "medicineType" field if available
        @SerializedName("strengthValue") val strengthValue: String?, // Replace `String?` with the actual data type for the "strengthValue" field if available
        @SerializedName("strengthForm") val strengthForm: String?, // Replace `String?` with the actual data type for the "strengthForm" field if available
        @SerializedName("strengthUnitOfMeasure") val strengthUnitOfMeasure: String?, // Replace `String?` with the actual data type for the "strengthUnitOfMeasure" field if available
        @SerializedName("quantityValue") val quantityValue: String,
        @SerializedName("quantityUnitId") val quantityUnitId: Int,
        @SerializedName("quantityUnitOfMeasure") val quantityUnitOfMeasure: String,
        @SerializedName("daysSupply") val daysSupply: Int, // Replace `Int` with the actual data type for the "daysSupply" field if available
        @SerializedName("writtenDate") val writtenDate: String,
        @SerializedName("lastFillDate") val lastFillDate: String?, // Replace `String?` with the actual data type for the "lastFillDate" field if available
        @SerializedName("substitutions") val substitutions: Int,
        @SerializedName("numberOfRefills") val numberOfRefills: Int,
        @SerializedName("pharmacyNumberOfRefills") val pharmacyNumberOfRefills: Int,
        @SerializedName("notes") val notes: String,
        @SerializedName("sig") val sig: String,
        @SerializedName("primaryDiagnosisIcdCode") val primaryDiagnosisIcdCode: String,
        @SerializedName("primaryDiagnosisIcdCodeDescription") val primaryDiagnosisIcdCodeDescription: String,
        @SerializedName("compoundMedicineDataList") val compoundMedicineDataList: Any? // Replace `Any?` with the actual data class for the "compoundMedicineDataList" field if available
    )

}