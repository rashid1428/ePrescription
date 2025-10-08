package com.doctor.eprescription.features.homescreen.addeditpatient

import com.doctor.eprescription.components.listselection.SelectionModel

sealed class AddEditPatientIntent {
    object OnStateClick : AddEditPatientIntent()
    data class OnStateSelected(val item: SelectionModel) : AddEditPatientIntent()
    data class OnDobSelected(
        val dayOfMonth: Int,
        val month: Int,
        val year: Int
    ) : AddEditPatientIntent()

    data class OnPharmacySelected(val pharmacy: SelectionModel) : AddEditPatientIntent()
    object OnMaleClick : AddEditPatientIntent()
    object OnFemaleClick : AddEditPatientIntent()
    object OnDobClick : AddEditPatientIntent()
    object OnPharmacyClick : AddEditPatientIntent()
    object OnHeightUnitClick : AddEditPatientIntent()
    data class OnHeightUnitSelected(val item: SelectionModel) : AddEditPatientIntent()
    object OnWeightUnitClick : AddEditPatientIntent()
    data class OnWeightUnitSelected(val item: SelectionModel) : AddEditPatientIntent()
}
