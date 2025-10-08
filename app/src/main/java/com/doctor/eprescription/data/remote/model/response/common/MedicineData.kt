package com.doctor.eprescription.data.remote.model.response.common

import android.os.Parcelable
import com.doctor.eprescription.data.remote.model.request.AddPendingMedicineRequest
import com.doctor.eprescription.data.remote.model.response.AddPendingMedicineResponse
import com.doctor.eprescription.data.remote.model.response.TemplatesResponse
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicineData(
    @SerializedName("title") val _title: String?,
    @SerializedName("requestType") val requestType: Int,
    @SerializedName("isControlledSubstance") val isControlledSubstance: Int,
    @SerializedName("myTemplateMedicineId") val myTemplateMedicineId: Int,
    @SerializedName("pendingMedicineId") val pendingMedicineId: Int,
    @SerializedName("medicineId") val medicineId: Int,
    @SerializedName("medicineName") val _medicineName: String?,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("doseUnit") val doseUnit: String,
    @SerializedName("doseUnitName") val _doseUnitName: String?,
    @SerializedName("route") val route: String,
    @SerializedName("routeName") val _routeName: String?,
    @SerializedName("direction") val direction: String,
    @SerializedName("directionName") val _directionName: String?,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("quantityUnitId") val quantityUnitId: String,
    @SerializedName("quantityUnit") val quantityUnit: String,
    @SerializedName("refill") val refill: String,
    @SerializedName("rxcui") val rxcui: String?,
    @SerializedName("maySubstitute") val maySubstitute: String,
    @SerializedName("addToTemplate") val addToTemplate: Boolean,
    @SerializedName("prn") val prn: Boolean,
    @SerializedName("specialInstructions") val specialInstructions: String,
    @SerializedName("freeText") val freeText: Int,
    @SerializedName("sig") val _sig: String?,
    @SerializedName("combineSig") val combineSig: String,
    @SerializedName("icdCodeId") val icdCodeId: Int,
    @SerializedName("icdCode") val icdCode: String?,
    @SerializedName("icdCodeDescription") val icdCodeDescription: String?,
    @SerializedName("strengthFormId") val strengthFormId: Int,
    @SerializedName("strengthFormName") val strengthFormName: String?,
    @SerializedName("compoundMedicinesList") val compoundMedicinesList: List<CompoundMedicine>,
    val _isSelected: Boolean? = false,
) : Parcelable {

    val title get() = _title.orEmpty()
    val doseUnitName get() = _doseUnitName.orEmpty()
    val directionName get() = _directionName.orEmpty()
    val routeName get() = _routeName.orEmpty()
    val diagnosisText get() = icdCodeDescription.plus(" (").plus(icdCode).plus(")")

    val isSelected get() = _isSelected ?: false
    val medicineName get() = _medicineName.orEmpty()
    val sig get() = _sig.orEmpty()
    fun toggleSelection(): MedicineData {
        return copy(_isSelected = _isSelected?.not() ?: true)
    }

    @Parcelize
    data class CompoundMedicine(
        @SerializedName("id") val id: Int,
        @SerializedName("medicineId") val medicineId: Int,
        @SerializedName("medicineName") val medicineName: String?,
        @SerializedName("productCode") val productCode: String?,
        @SerializedName("productQualifier") val productQualifier: String?,
        @SerializedName("quantity") val quantity: String,
        @SerializedName("quantityCodeListQualifier") val quantityCodeListQualifier: String?,
        @SerializedName("quantityUnitId") val quantityUnitId: Int,
        @SerializedName("quantityUnit") val quantityUnit: String
    ) : Parcelable
}

fun MedicineData.toMedicine(isHomeMeds: Boolean = false): AddPendingMedicineRequest.Medicine {
    return AddPendingMedicineRequest.Medicine(
        isControlledSubstance = isControlledSubstance.toString(),
        title = title,
        medicineId = medicineId.toString(),
        medicineName = medicineName,
        dosage = dosage,
        doseUnit = doseUnit,
        route = route,
        direction = direction,
        quantity = quantity,
        totalQuantityUnit = quantityUnit,
        strengthFormId = strengthFormId.toString(),
        icdCodeId = icdCodeId.toString(),
        refill = refill,
        maySubstitute = maySubstitute.toInt(),
        addToTemplate = if (addToTemplate) 1 else 0,
        prn = if (prn) 1 else 0,
        specialInstructions = specialInstructions,
        freeText = freeText,
        _sig = sig,
        compoundMedicinesList = compoundMedicinesList.map { it.medicineName ?: "" },
        isHomeMed = isHomeMeds
    )
}
