package com.example.pokeroffline

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var hostGameButton: Button
    
    private val requestBluetoothPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            makeDeviceDiscoverable()
        } else {
            Toast.makeText(this, "Bluetooth permissions are required to host games", Toast.LENGTH_LONG).show()
        }
    }

    private val requestDiscoverable = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val duration = result.resultCode
        if (duration > 0) {
            Toast.makeText(this, "Device is now discoverable for $duration seconds", Toast.LENGTH_LONG).show()
            // Here you can add logic to start listening for incoming connections
        } else {
            Toast.makeText(this, "Device discoverability was denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        val hostButton = findViewById<Button>(R.id.button_host_game)
        val joinButton = findViewById<Button>(R.id.button_join_game)

        hostButton.setOnClickListener {
            hostGame()
        }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show()
            hostGameButton.isEnabled = false
        }

        joinButton.setOnClickListener {
            // TODO: Start join game logic
        }
    }
    private fun hostGame(){
        if(!bluetoothAdapter.isEnabled){
            Toast.makeText(this, "Please enable Bluetooth first", Toast.LENGTH_SHORT).show()
        }

        if(checkBluetoothPermissions()){
            makeDeviceDiscoverable()
        }
        else{
            requestBluetoothPermissions()
        }
    }

    private fun requestBluetoothPermissions(){
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12 and above
            arrayOf(
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            // Below Android 12
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

        requestBluetoothPermission.launch(permissions)
    }

    private fun checkBluetoothPermissions(): Boolean{
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12 and above
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            // Below Android 12
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun makeDeviceDiscoverable(){
        try {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) // 5 minutes
            }
            requestDiscoverable.launch(discoverableIntent)
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission denied for Bluetooth operations", Toast.LENGTH_SHORT).show()
        }
    }
}