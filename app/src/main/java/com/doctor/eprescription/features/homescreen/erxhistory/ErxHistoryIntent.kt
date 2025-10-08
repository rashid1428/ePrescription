package com.doctor.eprescription.features.homescreen.erxhistory

import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse

sealed class ErxHistoryIntent {
    data class OnCancelClick(val item: GetSessionPrescriptionResponse.Medicine): ErxHistoryIntent()
}
