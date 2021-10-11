package com.jovel.appchangev10.utils

import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jovel.appchangev10.R

fun emailValidator(text: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(text).matches()
}

fun lengthString(text: String, n: Int): Boolean {
    return text.length >= n
}

fun compareStrings(text1: String, text2: String): Boolean {
    return text1==text2
}

fun notEmptyFields(text1: String, text2: String, text3: String, text4: String, activity: AppCompatActivity): Boolean {
    if(!(text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty() && text4.isNotEmpty()))
        Toast.makeText(activity, R.string.missing_data, Toast.LENGTH_SHORT).show()
    return text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty() && text4.isNotEmpty()
}
