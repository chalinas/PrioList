package com.chalinas.priolist.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.chalinas.priolist.R
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

enum class StatusResult {
    Added,
    Updated,
    Deleted
}

fun Context.hideKeyBoard(view: View) {
    try {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.longToastShow(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Dialog.setupDialog(layoutResId: Int) {
    setContentView(layoutResId)
    window!!.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    setCancelable(false)
}
 fun showTimePicker(context: Context,callback: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        callback(calendar.timeInMillis)
    }, hour, minute, true).show()
}


fun validateInput(category: String, time: String, context: Context): Boolean {
    return when {
        category.isEmpty() -> {
            Toast.makeText(context, "Category is required", Toast.LENGTH_SHORT).show()
            false
        }

        !isValidTimeFormat(time) -> {
            Toast.makeText(context, "Invalid time format. Use HH:MM AM/PM", Toast.LENGTH_SHORT)
                .show()
            false
        }

        else -> true
    }
}

fun isValidTimeFormat(time: String): Boolean {
    val regex = Regex("\\d{1,2}:\\d{2} [APMapm]{2}")
    return regex.matches(time)
}

fun convertTimeToMillis(time: String): Long {
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.parse(time)?.time ?: 0L
}


fun validateEditText(editText: EditText, textTextInputLayout: TextInputLayout): Boolean {
    return when {
        editText.text.toString().trim().isEmpty() -> {
            textTextInputLayout.error = "Required"
            false
        }

        else -> {
            textTextInputLayout.error = null
            true
        }
    }
}

fun clearEditText(editText: EditText, textTextInputLayout: TextInputLayout) {
    editText.text = null
    textTextInputLayout.error = null
}
