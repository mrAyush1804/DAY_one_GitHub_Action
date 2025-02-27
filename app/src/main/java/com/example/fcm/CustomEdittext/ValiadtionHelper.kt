package com.example.fcm.CustomEdittext


import android.util.Patterns

object ValidationHelper {


    data class ValidationResult(val isValid: Boolean, val errorMessage: String? = null)


    fun validateInput(input: String, inputType: InputType): ValidationResult {
        return when {
            input.isEmpty() -> ValidationResult(false, "Field khali nahi chhod sakte!")
            inputType == InputType.EMAIL && !Patterns.EMAIL_ADDRESS.matcher(input).matches() ->
                ValidationResult(false, "Valid email daalo bhai!")
            inputType == InputType.PHONE && !Patterns.PHONE.matcher(input).matches() ->
                ValidationResult(false, "Valid phone number daalo!")
            inputType == InputType.PASSWORD && !isStrongPassword(input) ->
                ValidationResult(false, "Password mein kam se kam 8 char, 1 number, 1 special char chahiye!")
            else -> ValidationResult(true)
        }
    }


    private fun isStrongPassword(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return hasMinLength && hasNumber && hasSpecialChar
    }


    enum class InputType {
        NAME, EMAIL, PHONE, PASSWORD
    }
}