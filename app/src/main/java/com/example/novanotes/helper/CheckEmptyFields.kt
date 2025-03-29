package com.example.novanotes.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object  CheckEmptyFields {

    fun checkSignUpFields(name : String , email : String , password : String , confirmPassword : String) : String{


        if(name.trim().isEmpty()){
            return "name"
        }
        else if(email.trim().isEmpty() ||  !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return "email"
        }
        else if(password.trim().isEmpty() || password.length < 6)
            return "password"
        else if(confirmPassword != password)
            return "wrongmatch"
        else
            return "done"
    }


    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getTimeAgo(timeString: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS", Locale.getDefault())

        try {
            val pastDate = dateFormat.parse(timeString) ?: return "Invalid date"
            val currentDate = Date()

            val diff = currentDate.time - pastDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "$minutes minutes ago"
                hours < 24 -> "$hours hours ago"
                else -> "$days days ago"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Invalid date"
        }
    }
}