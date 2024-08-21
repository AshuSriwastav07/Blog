package com.TLC_Developer.functions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class function {

    // Convert String to Date
    fun convertStringToDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("dd/M/yyyy HH:mm", Locale.getDefault())
            format.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
}