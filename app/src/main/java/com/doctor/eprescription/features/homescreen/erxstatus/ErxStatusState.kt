package com.doctor.eprescription.features.homescreen.erxstatus

import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse

data class ErxStatusState(
    val tabs: List<String>,
    val tabsSelection: List<Boolean>,
    val erxStatusList: List<GetSessionPrescriptionResponse.Medicine> = emptyList()
)
