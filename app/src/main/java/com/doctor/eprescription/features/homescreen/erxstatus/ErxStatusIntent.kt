package com.doctor.eprescription.features.homescreen.erxstatus

import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse

sealed class ErxStatusIntent {

    data class OnTabChanged(val position: Int) : ErxStatusIntent()
    data class OnItemClick(val item: GetSessionPrescriptionResponse.Medicine) : ErxStatusIntent()

}
