package com.doctor.eprescription.customviews

import android.content.Context
import android.content.res.ColorStateList
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.doctor.eprescription.R
import com.doctor.eprescription.databinding.CompFormEdittextBinding
import com.doctor.eprescription.extension.gone
import com.doctor.eprescription.extension.visible
import com.doctor.eprescription.utils.ClickViaTouchListener

class FormEditText @JvmOverloads constructor(
    context: Context,
    val attributes: AttributeSet? = null
) : FrameLayout(context, attributes) {

    private lateinit var mEditText: EditText
    private lateinit var mTvError: TextView
    private lateinit var ivError: ImageView
    private lateinit var ivArrow: ImageView
    private lateinit var clickView: View

    private var mInputType = InputType.TYPE_NULL

    private var onClick: (() -> Unit)? = null

    private var hint = ""

    private var _binding: CompFormEdittextBinding? = null
    val binding get() = _binding!!


    init {
        _binding = CompFormEdittextBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)


        if (!isInEditMode) {
            mEditText = findViewById(R.id.etForm)
            mTvError = findViewById(R.id.tvError)
            ivError = findViewById(R.id.ivError)
            ivArrow = findViewById(R.id.ivArrow)
            clickView = findViewById(R.id.clickView)

            clickView.setOnClickListener {
                onClick?.invoke()
            }

            val typedArray = context.obtainStyledAttributes(attributes, R.styleable.FormEditText)
            try {
                hint = typedArray.getString(R.styleable.FormEditText_formHint) ?: ""
                val inputType = typedArray.getInt(
                    R.styleable.FormEditText_android_inputType,
                    EditorInfo.TYPE_CLASS_TEXT
                )
                mInputType = inputType
                mEditText.inputType = inputType
            } finally {
                typedArray.recycle()
            }

            mEditText.hint = hint

            mEditText.doOnTextChanged { text, start, before, count ->
                if (text?.isNotEmpty() == true) {
                    setError("")
                }
            }

            hideRightArrow()
        }
    }

    fun getEditText() = mEditText

    fun getText(): String {
        return mEditText.text.toString()
    }

    fun setText(text: String) {
        mEditText.setText(text)
        setError("")
    }

    fun enableInput(enable: Boolean) {
        mEditText.isEnabled = enable
    }

    private fun showRightArrow() {
        ivArrow.visible()
    }

    fun actAsClickItem(onClick: () -> Unit) {
        this@FormEditText.onClick = onClick
        showRightArrow()
        mEditText.inputType = InputType.TYPE_NULL
        mEditText.isEnabled = false
        clickView.isVisible = true
    }

    fun bindInputWithSwitch(isChecked: Boolean, showKeyboard: Boolean = true) {
        mEditText.isEnabled = false
        clickView.isVisible = true
        showRightArrow()

        clickView.isVisible = isChecked.not()
        mEditText.isEnabled = isChecked

        if (this.isVisible) {
            if (isChecked) {
                mEditText.inputType = mInputType
                if (showKeyboard) {
                    mEditText.showKeyboard()
                }
            } else {
                mEditText.inputType = InputType.TYPE_NULL
                mEditText.hideKeyboard()
            }
        }
    }

    fun onClick(action: () -> Unit) {
        onClick = action
    }

    private fun hideRightArrow() {
        ivArrow.gone()
    }

    fun setError(error: String) {
        if (error.isNotEmpty()) {
            ivError.visibility = View.VISIBLE
            mTvError.visibility = View.VISIBLE
            mTvError.text = error

//            mEditText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            binding.line.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
//            mEditText.showKeyboard()
        } else {
            ivError.visibility = View.GONE
            mTvError.visibility = View.GONE
//            mEditText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            binding.line.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            /*val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)

            // Get the accent color value
            val accentColor = typedValue.data

            mEditText.backgroundTintList =
                ColorStateList.valueOf(accentColor)*/
        }
    }
}

fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}
