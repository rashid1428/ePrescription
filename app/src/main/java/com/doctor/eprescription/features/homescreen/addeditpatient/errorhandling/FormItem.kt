package com.doctor.eprescription.features.homescreen.addeditpatient.errorhandling

sealed class FormItem {
    data class FirstName(val firstName: String): FormItem()
    data class LastName(val lastName: String) : FormItem()
    data class Email(val email: String) : FormItem()
    data class PhoneNo(val phoneNo: String) : FormItem()
    data class Street(val street: String) : FormItem()
    data class City(val city: String) : FormItem()
    data class ZipCode(val zipCode: String) : FormItem()
    data class Weight(val weight: String) : FormItem()
    data class Height(val height: String) : FormItem()
}
