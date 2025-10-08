package com.doctor.eprescription.features.homescreen.addeditpatient

import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.model.response.GetStateListResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy

data class AddEditPatientState(
    val title: String = "",

    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String = "",
    val height: String = "",
    val weight: String = "",
    val street: String = "",
    val zipCode: String = "",
    val phone: String = "",

    val stateList: List<GetStateListResponse.State> = emptyList(),
    val selectedState: GetStateListResponse.State? = null,

    val gender: Gender = Gender.IDLE,
    val dobMillis: Long = 0L,
    val dobString: String = "",

    val pharmacies: List<Pharmacy> = emptyList(),
    val selectedPharmacy: Pharmacy? = null,

    val weightUnits: List<SelectionModel> = listOf(
        SelectionModel("1", "kg"),
        SelectionModel("2", "lb"),
    ),
    val selectedWeightUnit: SelectionModel? = null,

    val heightUnits: List<SelectionModel> = listOf(
        SelectionModel("1", "cm"),
        SelectionModel("2", "in"),
    ),
    val selectedHeightUnit: SelectionModel? = null
)

enum class Gender(val value: String) {
    MALE("M"), FEMALE("F"), IDLE("")
}
