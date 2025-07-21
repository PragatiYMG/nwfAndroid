package com.app.nwf.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class BaseUtils {

    companion object{

        private val PREF_NAME = "nwf"

        // set user login status
        fun putUserLogIn(context: Context, status: Boolean, name:String, phone:String, email:String, token:String) {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("is_login", status)
            editor.putString("name", name)
            editor.putString("phone", email)
            editor.putString("email", phone)
            editor.putString("token", token)
            editor.apply()
        }

        fun putSelectLanguage(context: Context, status: Boolean, langId:String, langName:String, langCode:String){
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("lang_select", status)
            editor.putString("lang_id", langId)
            editor.putString("lang_name", langName)
            editor.putString("lang_code", langCode)
            editor.apply()
        }

        fun getLangId(context: Context): String? {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getString("lang_id", "N/A")
        }

        fun getLangCode(context: Context): String? {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getString("lang_code", "N/A")
        }

        fun putLangData(context: Context, langData: String){
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("lang_data", langData)
            editor.apply()
        }

        fun getLangData(context: Context): String? {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getString("lang_data", "")
        }

        fun getLangStatus(context: Context): Boolean {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getBoolean("lang_select", false)
        }


        fun getUserLogIn(context: Context): Boolean {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getBoolean("is_login", false)
        }

        fun getToken(context: Context): String? {
           // return "JDJ5JDEwJGhWMUxyNXVCZHpWRHV3M0dwSmxFQ2VENW5SSkZjYy9HQ0QwTzVybUIuNHRkdUo4TXppLjlh"
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getString("token", "")
        }

        fun getUserName(context: Context): String? {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getString("name", "")
        }

        fun getUserEmail(context: Context): String? {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getString("email", "")
        }

        fun getUserPhone(context: Context): String? {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getString("phone", "")
        }

        fun convertDateTime(inputDateTime: String): String {
            return try {
                // Parse the input date-time string
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
                val date = inputFormat.parse(inputDateTime)

                // Format the date into the desired output format
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                e.printStackTrace()
                inputDateTime
            }
        }



    }

}