package com.doctor.eprescription.constants

object Constants {
}

object ArgsConstant {
    const val PATIENT = "PATIENT"
    const val LOGIN_RESPONSE = "login_response"
}
enum class MenuOptions {
    PRESCRIBE_MEDS,
    MY_TEMPLATE,
    MEFTII_TEMPLATE,
    FOOD_AND_DRUG_ALLERGIES,
    HOME_MEDS,
    PHARMACY,
    ERX_STATUS,
    ORGANIZATION_TEMPLATE,
}

object MedicineItemOption {
    const val EDIT_BUTTON = 1
    const val DELETE_BUTTON = 2
    const val MEDICINE_ITEM = 3
}
object TemplateTypes {
    const val MY_TEMPLATE = 1
    const val MEFTII_TEMPLATE = 2
    const val HOME_MEDS = 3
    const val ORGANIZATION_TEMPLATE = 4
}