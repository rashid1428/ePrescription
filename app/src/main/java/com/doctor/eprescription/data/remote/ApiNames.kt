package com.doctor.eprescription.data.remote

object ApiNames {
    const val BASE_URL = "https://meftii.com:8443/eprescription/"
    const val LOGIN = "${BASE_URL}doctor/login"
    const val GET_MENU = "${BASE_URL}dashboard/getmenu"

    //Pharmacy
    const val GET_PHARMACY = "${BASE_URL}pharmacy/getpharmacylist"

    //Drop down
    const val GET_DOSE_LIST = "${BASE_URL}dropdown/getdoselist"
    const val GET_ROUTE_LIST = "${BASE_URL}dropdown/getroutelist"
    const val GET_DIRECTION_LIST = "${BASE_URL}dropdown/getdirectionlist"
    const val GET_QUANTITIES = "${BASE_URL}dropdown/getquantitylist"
    const val GET_STATE_LIST = "${BASE_URL}dropdown/getstateslist"
    const val GET_ICD_CODE_LIST = "${BASE_URL}dropdown/getIcdCodesList"
    const val GET_FOOD_AND_ALLERGIES_LIST = "${BASE_URL}dropdown/getfoodallergieslist"

    //Medicine
    const val ADD_PENDING_MEDICINE = "${BASE_URL}medicines/addpendingmedicine"
    const val GET_MEDICINES = "${BASE_URL}medicines/getmedicines"
    const val TEMPLATE_LIST = "${BASE_URL}medicines/gettemplates"
    const val DELETE_MY_TEMPLATE_MEDICINE = "${BASE_URL}medicines/deletemytemplatemedicine"
    const val GET_PENDING_MEDICINE = "${BASE_URL}medicines/getpendingdmedicines"
    const val DELETE_PENDING_MEDICINE = "${BASE_URL}medicines/deletependingmedicine"
    const val GET_SIG = "${BASE_URL}medicines/getsig"
    const val UPDATE_PENDING_MEDICINE = "${BASE_URL}medicines/updatependingmedicine"
    const val ADD_MY_TEMPLATE_MEDICINE = "${BASE_URL}medicines/addmytemplatemedicine"

    //Patient
    const val PATIENT_LIST = "${BASE_URL}patient/patientlist"
    const val ADD_PATIENT = "${BASE_URL}patient/registerprofile"
    const val UPDATE_PATIENT = "${BASE_URL}patient/updateprofile"
    const val DELETE_PATIENT = "${BASE_URL}patient/deletepatient"
    const val UPDATE_PHARMACY = "${BASE_URL}patient/updatepharmacy"

    //erx
    const val NEW_ERX = "${BASE_URL}erx/newrx"
    const val GET_SESSION_PRESCRIPTION = "${BASE_URL}erx/getsessionprescriptions"
    const val GET_PRESCRIPTION_SESSION = "${BASE_URL}erx/getprescriptionsessions"
    const val GET_PRESCRIPTION_DETAILS = "${BASE_URL}erx/getprescriptiondetails"
    const val CANCEL_ERX = "${BASE_URL}erx/cancelrx"

    //doctor
    const val UPDATE_PATIENT_FOOD_AND_DRUG_ALLERGIES = "${BASE_URL}doctor/updatepatientfoodanddrugallergies"

    const val ECPS_VERIFICATION_REQUEST = "https://vfycred.com/2fa/index.php/epcsVerification"
}

object HeadersNames {
    const val EMAIL = "email"
    const val LANGUAGE = "language"
    const val CHANNEL = "channel"
}

object HeaderValues {
    const val LANGUAGE = "english"
    const val CHANNEL = "android"
}