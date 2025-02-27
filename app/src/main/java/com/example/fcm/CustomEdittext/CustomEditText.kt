package com.example.fcm.CustomEdittext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.example.fcm.R

@SuppressLint("ResourceAsColor")
class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var errorTextView: TextView? = null
    private var currentInputType: ValidationHelper.InputType = ValidationHelper.InputType.NAME

    companion object {

        private val allEditTexts = mutableListOf<CustomEditText>()
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText, defStyleAttr, 0)
        val dwableble=typedArray.getResourceId(R.styleable.CustomEditText_defaultDrawable,R.drawable.editextfocusunfocud_degin)
        try {

            setBackgroundColor(typedArray.getColor(R.styleable.CustomEditText_customBackground, Color.WHITE,))
            setBackgroundResource(dwableble)
            setTypeface(ResourcesCompat.getFont(getContext(),R.font.poppins_bold))



            val customTextSizePx = typedArray.getDimension(R.styleable.CustomEditText_customTextSize, 14f)
            val customTextSizeSp = customTextSizePx / resources.displayMetrics.scaledDensity
            setTextSize(TypedValue.COMPLEX_UNIT_SP, customTextSizeSp)


            val errorColor = typedArray.getColor(R.styleable.CustomEditText_errorTextColor, R.color.red)


            val inputTypeValue = typedArray.getInt(R.styleable.CustomEditText_inputType, 0)
            currentInputType = when (inputTypeValue) {
                1 -> ValidationHelper.InputType.EMAIL
                2 -> ValidationHelper.InputType.PASSWORD
                else -> ValidationHelper.InputType.NAME
            }
            when (inputTypeValue) {
                1 -> setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                2 -> setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)
                else -> setInputType(android.text.InputType.TYPE_CLASS_TEXT)
            }


            errorTextView = TextView(context).apply {
                setTextColor(errorColor)
                textSize = 12f
                visibility = View.GONE
            }

            isEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true


        } finally {
            typedArray.recycle()
        }

        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                allEditTexts.forEach { editText ->
                    if (editText != this) {
                        editText.clearFocus()
                        editText.setBackgroundResource(R.drawable.edittextdifaultdrawable)
                    }
                }
            }
        }


        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val result = ValidationHelper.validateInput(s.toString(), currentInputType)
                if (!result.isValid) showError(result.errorMessage ?: "") else hideError()
            }
        })
    }

    private fun showError(message: String) {
        errorTextView?.text = message
        errorTextView?.visibility = View.VISIBLE
    }

    private fun hideError() {
        errorTextView?.visibility = View.GONE
    }



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? android.view.ViewGroup)?.let { parent ->
            parent.addView(errorTextView)

        }
    }
}