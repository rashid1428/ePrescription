package com.doctor.eprescription.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.doctor.eprescription.R
import com.doctor.eprescription.extension.goneViews
import com.doctor.eprescription.extension.visibleViews

class FormDropDownItem @JvmOverloads constructor(context: Context, val attr: AttributeSet? = null) :
    FrameLayout(context, attr) {

    private lateinit var tvText: TextView
    private lateinit var tvError: TextView
    private lateinit var ivError: ImageView

    private var hint = ""


    init {
        inflate(context, R.layout.comp_dropdown_selector, this)

        if (!isInEditMode) {
            tvText = findViewById(R.id.tvText)
            tvError = findViewById(R.id.tvError)
            ivError = findViewById(R.id.ivError)

            val typedArray = context.obtainStyledAttributes(attr, R.styleable.FormDropDownItem)
            try {
                hint = typedArray.getString(R.styleable.FormEditText_formHint) ?: ""
            } finally {
                typedArray.recycle()
            }

            tvText.hint = hint

        }
    }

    fun setText(text: String) {
        tvText.text = text
    }

    fun setError(error: String) {
        if (error.isNotEmpty()) {
            visibleViews(tvError, ivError)
            tvError.text = error
        } else {
            goneViews(tvError, ivError)
        }
    }
}