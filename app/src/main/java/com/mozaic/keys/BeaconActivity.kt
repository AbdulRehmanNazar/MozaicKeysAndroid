package com.mozaic.keys

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mozaic.keys.databinding.ActivityBeaconBinding
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter

class BeaconActivity : AppCompatActivity() {
    private var UUID = ""
    private lateinit var binding: ActivityBeaconBinding
    private var phoneNumber = ""
    private var beaconTransmitter: BeaconTransmitter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBeaconBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        phoneNumber = intent.getStringExtra("phoneNumber")!!

        UUID = "$phoneNumber-1234-1234-1234-123456789abc"

        binding.uuidText.text = UUID
        binding.keyIdEditTextCardView.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", UUID)
            clipboard.setPrimaryClip(clip)

            // Show a toast to indicate that the text has been copied
            Toast.makeText(this@BeaconActivity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.uuidText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var currentText = ""
            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (isFormatting) return
//
//                isFormatting = true
//                val input = query.toString().replace("-", "") // Remove dashes to work on plain input
//                val formatted = StringBuilder()
//
//                // Format the input as 12345678-1234-1234-1234-123456789abc
//                for (i in input.indices) {
//                    formatted.append(input[i])
//                    if ((i == 7 || i == 11 || i == 15 || i == 19) && i != input.length - 1) {
//                        formatted.append("-") // Insert dash at specific positions
//                    }
//                }
//
//                currentText = formatted.toString().take(36)
//                binding.uuidEditText.setText(currentText)
//                binding.uuidEditText.setSelection(currentText.length)
            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        binding.activateKeyLayout.setOnClickListener {
//            if (binding.uuidEditText.text.length != 36) {
//                Toast.makeText(this, "Invalid UUID", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter?.isEnabled == false) {
                Log.d("BluetoothState", "Bluetooth is turned off.")
                Toast.makeText(this, "Bluetooth is turned off. please turn on", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                Log.d("BluetoothState", "Bluetooth is still on.")
            }

            binding.activateKeyLayout.visibility = View.GONE
            binding.disableKeyLayout.visibility = View.VISIBLE
            binding.keyIdTextView.visibility = View.VISIBLE
            binding.keyIdEditTextCardView.visibility = View.VISIBLE
            if (!BeaconScanPermissionsActivity.allPermissionsGranted(
                    this, false
                )
            ) {
                startPermissionActivity()
            } else {
                startBeaconAdvertising()
            }
        }
        binding.disableKeyLayout.setOnClickListener {
            beaconTransmitter?.let {
                it.stopAdvertising()
                Toast.makeText(this@BeaconActivity, "Advertising stoped", Toast.LENGTH_SHORT).show()
            }
            binding.activateKeyLayout.visibility = View.VISIBLE
            binding.disableKeyLayout.visibility = View.INVISIBLE
            binding.keyIdTextView.visibility = View.INVISIBLE
            binding.keyIdEditTextCardView.visibility = View.INVISIBLE
        }
    }

    private fun startPermissionActivity() {
        val intent = Intent(this, BeaconScanPermissionsActivity::class.java)
        intent.putExtra("backgroundAccessRequested", false)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (!BeaconScanPermissionsActivity.allPermissionsGranted(
                this, false
            )
        ) {
            startPermissionActivity()
            return
        }
    }


    private fun startBeaconAdvertising() {
        val beaconParser =
            BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")

//        beaconManager.removeAllMonitorNotifiers()

        BeaconManager.setDebug(true)
        val message = "Hello Abdullah"
        val beacon = Beacon.Builder().setId1(UUID) // UUID for your beacon
            .setId2("1")  // Major identifier
            .setId3("2")  // Minor identifier
            .setManufacturer(0x004c).setTxPower(-59).setDataFields(listOf(123L, 456L, 789L)).build()

        beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)

        beaconTransmitter?.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                println("Advertising failed with error code: $errorCode")
                Toast.makeText(
                    this@BeaconActivity,
                    "Advertising failed with error code: $errorCode",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Toast.makeText(this@BeaconActivity, "Advertising started", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}