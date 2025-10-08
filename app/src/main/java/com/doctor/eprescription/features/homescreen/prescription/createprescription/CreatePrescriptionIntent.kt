package com.doctor.eprescription.features.homescreen.prescription.createprescription

import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.model.response.GetMedicineResponse

sealed class CreatePrescriptionIntent {
    object OnAddCompoundingItem : CreatePrescriptionIntent()
    data class OnFreeTextMedicineSwitch(val isChecked: Boolean) : CreatePrescriptionIntent()
    data class OnCompoundingCheck(val isChecked: Boolean) : CreatePrescriptionIntent()
    data class OnMedicalSupplyCheck(val isChecked: Boolean) : CreatePrescriptionIntent()
    data class OnMaySubstituteCheck(val isChecked: Boolean) : CreatePrescriptionIntent()
    data class OnAddToMyTemplateCheck(val isChecked: Boolean) : CreatePrescriptionIntent()

    data class OnRemoveCompoundingItem(val position: Int) : CreatePrescriptionIntent()
    object OnDoseUnitClick : CreatePrescriptionIntent()
    data class OnDoseSelected(val item: SelectionModel) : CreatePrescriptionIntent()
    object OnRouteClick : CreatePrescriptionIntent()
    data class OnRouteSelected(val item: SelectionModel) : CreatePrescriptionIntent()
    object OnQuantityClick : CreatePrescriptionIntent()
    data class OnQuantitySelected(val item: SelectionModel) : CreatePrescriptionIntent()
    object OnFrequencyClick : CreatePrescriptionIntent()
    object OnSkip : CreatePrescriptionIntent()
    data class OnFrequencySelected(val item: SelectionModel) : CreatePrescriptionIntent()

    object OnDiagnosisClick : CreatePrescriptionIntent()
    data class OnDiagnosisSelected(val item: SelectionModel) : CreatePrescriptionIntent()

    object OnRefillClick : CreatePrescriptionIntent()
    data class OnRefillSelected(val item: SelectionModel) : CreatePrescriptionIntent()

    data class OnNextClick(
        val dynamicSig: String,
        val doseText: String,
        val dispenseQuantityText: String,
        val specialInstructions: String,
        val freeText: String = ""
    ) : CreatePrescriptionIntent()

    data class OnMedicineSelected(val medicine: GetMedicineResponse.Medicine) :
        CreatePrescriptionIntent()

    data class OnDoseTextChanged(val text: String) : CreatePrescriptionIntent()
    data class OnSigSwitch(val isChecked: Boolean) : CreatePrescriptionIntent()
    data class OnControlSubstanceConfirm(val confirm: Boolean) : CreatePrescriptionIntent()
}
