package com.doctor.eprescription.dialog

import androidx.annotation.StringRes
import com.doctor.eprescription.R
import kotlinx.parcelize.Parcelize

val STANDARD_DIALOG_CONFIG = DialogUiConfig(
    title = R.string.error_title,
    message = R.string.error_message,
    positiveButtonText = R.string.error_retry,
    negativeButtonText = R.string.error_cancel
)

@Parcelize
data class DialogUiConfig(
    @StringRes override val title: Int,
    @StringRes override val message: Int,
    @StringRes override val positiveButtonText: Int? = null,
    @StringRes override val negativeButtonText: Int? = null
) : IDialogUiConfig