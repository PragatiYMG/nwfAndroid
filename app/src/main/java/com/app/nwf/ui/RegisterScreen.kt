package com.app.nwf.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.app.nwf.R
import com.app.nwf.databinding.RegisterScreenBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class RegisterScreen : AppCompatActivity(), OnClickListener {

    private lateinit var binding: RegisterScreenBinding
    private val calendar = Calendar.getInstance()
    private var isPassVisible = false
    private val emailPattern: Pattern = Pattern.compile(
        "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }


    private fun initView() {
        binding.rsDob.setOnClickListener(this)
        binding.rsGender.setOnClickListener(this)
        binding.rsRegion.setOnClickListener(this)
        binding.rsCareer.setOnClickListener(this)
        binding.rsSubmit.setOnClickListener(this)
        binding.rsLogin.setOnClickListener(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        return true
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.rsSubmit ->{
                if(isValidate()){
                    Toast.makeText(this@RegisterScreen, "Login", Toast.LENGTH_LONG).show()
                }
            }
            R.id.rsGender ->{
                showGenderDialog()
            }
            R.id.rsDob ->{
                showDatePickerDialog()
            }
            R.id.lsSignUp ->{
                startActivity(Intent(this@RegisterScreen, RegisterScreen::class.java))
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showGenderDialog() {
        val builder = AlertDialog.Builder(this@RegisterScreen)
        val view = layoutInflater.inflate(R.layout.gender_dialog, null)
        builder.setView(view)
        val dialog = builder.create()
        val rMale = view.findViewById<TextView>(R.id.gdMale)
        val rFemale = view.findViewById<TextView>(R.id.gdFemale)
        val rOther = view.findViewById<TextView>(R.id.gdOther)

        rMale.setOnClickListener {
            binding.rsGender.text = "Male"
            dialog.dismiss()
        }

        rFemale.setOnClickListener {
            binding.rsGender.text = "Female"
            dialog.dismiss()
        }

        rOther.setOnClickListener {
            binding.rsGender.text = "Other"
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        dialog.show()
    }

    private fun showDatePickerDialog() {
        val datePickerListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.rsDob.text = dateFormat.format(calendar.time)
        }

        val datePickerDialog = DatePickerDialog(
            this@RegisterScreen,
            R.style.CalenderTheme, // Apply the custom theme
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Restrict future dates
        datePickerDialog.show()
    }

    private fun isValidate(): Boolean {
        return true
    }
}