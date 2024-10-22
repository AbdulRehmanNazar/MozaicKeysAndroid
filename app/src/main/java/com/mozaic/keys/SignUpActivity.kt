package com.mozaic.keys

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mozaic.keys.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignupBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.countryCodePicker.setArrowSize(0)
        binding.countryCodePicker.textView_selectedCountry.setTextColor(
            ContextCompat.getColor(
                this, R.color.gray
            )
        )

        binding.registerButton.setOnClickListener {
            val countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
            val phoneNumber = binding.phoneNumberEditText.text
            if (phoneNumber.isNullOrEmpty() || phoneNumber.toString().length != 8) {
                Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "$countryCode $phoneNumber", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@SignUpActivity, BeaconActivity::class.java)
            intent.putExtra("phoneNumber",phoneNumber.toString())
            startActivity(intent)
        }

        binding.countryCodePicker.selectedCountryCode.toString()


    }
}