package com.doctor.eprescription.features.homescreen.erxhistory

import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse

data class ErxHistoryState(
    val erxStatusList: List<GetSessionPrescriptionResponse.Medicine> = emptyList()
)
