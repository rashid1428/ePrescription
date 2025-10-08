package com.doctor.eprescription.features.homescreen.prescription.createprescription

import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.model.response.*
import com.doctor.eprescription.features.homescreen.prescription.createprescription.model.CompoundingModel

data class CreatePrescriptionState(
    val loginData: LoginResponse? = null,

    val swFreeTextMedicine: Boolean = false,
    val cbCompounding: Boolean = false,
    val cbMedicalSupply: Boolean = false,
    val medicineText: String = "",
    val compoundMedicationText: String = "",

    val swSig: Boolean = false,
    val swPRN: Boolean = false,
    val swMaySubstitute: Boolean = false,
    val swAddToMyTemplate: Boolean = false,

    val dynamicSig: String = "",
    val freeText: String = "",
    val doseText: String = "",
    val specialInstructions: String = "",
    val title: String = "",

    val compoundingList: List<CompoundingModel> = emptyList(),

    val refillList: List<SelectionModel> = emptyList(),
    val selectedRefill: SelectionModel? = null,

    val doseList: List<GetDoseListResponse.Dose> = emptyList(),
    val selectedDoseUnit: GetDoseListResponse.Dose? = null,

    val routes: List<GetRouteListResponse.Route> = emptyList(),
    val selectedRoute: GetRouteListResponse.Route? = null,

    val icdCodes: List<GetIcdCodeListResponse.IcdCode> = emptyList(),
    val selectedIcdCode: GetIcdCodeListResponse.IcdCode? = null,

    val quantities: List<GetQuantitiesResponse.Quantity> = emptyList(),
    val selectedQuantity: GetQuantitiesResponse.Quantity? = null,

    val directions: List<GetDirectionResponse.Direction> = emptyList(),
    val selectedDirection: GetDirectionResponse.Direction? = null,

    val selectedMedicine: GetMedicineResponse.Medicine? = null,

    val dispenseQuantityText: String = "",

    val medicineError: String = "",
    val dynamicSigError: String = "",
    val doseError: String = "",
    val doseUnitError: String = "",
    val routesError: String = "",
    val frequencyError: String = "",
    val dispenseQuantityError: String = "",
    val quantityTextError: String = "",
    val quantityUnitError: String = "",
    val refillError: String = "",
)
