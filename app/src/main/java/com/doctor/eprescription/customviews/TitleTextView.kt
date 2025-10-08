package com.doctor.eprescription.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.doctor.eprescription.R

class TitleTextView(
    context: Context,
    attributeSet: AttributeSet?
) : AppCompatTextView(context, attributeSet) {

    init {

//        textSize = resources.getDimension(com.intuit.sdp.R.dimen._20sdp)
        typeface = Typeface.SANS_SERIF

    }

}