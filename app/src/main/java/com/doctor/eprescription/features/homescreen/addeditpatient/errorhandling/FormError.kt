package com.doctor.eprescription.features.homescreen.addeditpatient.errorhandling

sealed class FormError {
    object Idle: FormError()
    data class FirstNameError(val error: String = "First name is mandatory"): FormError()
    data class LastNameError(val error: String = "Last name is mandatory") : FormError()
    data class EmailError(val error: String = "Email is mandatory") : FormError()
    data class Pharmacy(val error: String = "Pharmacy is mandatory") : FormError()
    data class GenderError(val error: String = "Gender is mandatory") : FormError()
    data class DateOfBirthError(val error: String = "Date of birth is mandatory") : FormError()
    data class PhoneNoError(val error: String = "PhoneNo is mandatory") : FormError()
    data class StreetError(val error: String = "Street is mandatory") : FormError()
    data class CityError(val error: String = "City is mandatory") : FormError()
    data class StateError(val error: String = "State is mandatory") : FormError()
    data class ZipCodeError(val error: String = "ZipCode is mandatory") : FormError()
    data class WeightError(val error: String = "Weight is mandatory") : FormError()
    data class HeightError(val error: String = "Height is mandatory") : FormError()
    data class WeightUnitError(val error: String = "Weight Unit is mandatory") : FormError()
    data class HeightUnitError(val error: String = "Weight Unit is mandatory") : FormError()


}
