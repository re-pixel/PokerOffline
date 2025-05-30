package com.example.pokeroffline

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var hostGameButton: Button
    private lateinit var joinGameButton: Button
    private lateinit var bluetoothActions: BluetoothActions

    private val requestBluetoothPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        bluetoothActions.handlePermissionResult(permissions)
    }

    private val requestDiscoverable = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        bluetoothActions.handleDiscoverabilityResult(result.resultCode, discoverabilityCallback)
    }

    private val discoverabilityCallback = object : BluetoothActions.DiscoverabilityCallback {
        override fun onDiscoverabilityEnabled(duration: Int) {
            Toast.makeText(this@MainActivity, "Device is now discoverable for $duration seconds", Toast.LENGTH_LONG).show()

        }

        override fun onDiscoverabilityDenied() {
            Toast.makeText(this@MainActivity, "Device discoverability was denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val discoveryCallback = object : BluetoothActions.DeviceDiscoveryCallback {
        override fun onDeviceFound(device: BluetoothDevice, deviceName: String, deviceAddress: String) {
            Toast.makeText(this@MainActivity, "Found device: $deviceName ($deviceAddress)", Toast.LENGTH_SHORT).show()

        }

        override fun onDiscoveryStarted() {
            Toast.makeText(this@MainActivity, "Searching for nearby games...", Toast.LENGTH_SHORT).show()
            joinGameButton.text = "Searching..."
            joinGameButton.isEnabled = false
        }

        override fun onDiscoveryFinished() {
            joinGameButton.text = "Join Game"
            joinGameButton.isEnabled = true
        }

        override fun onDiscoveryFailed(error: String) {
            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            joinGameButton.text = "Join Game"
            joinGameButton.isEnabled = true
        }
    }

    private val permissionCallback = object : BluetoothActions.PermissionCallback {
        override fun onPermissionsGranted() {
            when (currentAction) {
                Action.HOST -> makeDeviceDiscoverable()
                Action.JOIN -> startDeviceDiscovery()
                else -> {}
            }
        }

        override fun onPermissionsDenied() {
            Toast.makeText(this@MainActivity, "Bluetooth permissions are required", Toast.LENGTH_LONG).show()
        }
    }

    private enum class Action { HOST, JOIN, NONE }
    private var currentAction = Action.NONE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothActions = BluetoothActions(this)
        bluetoothActions.setPermissionCallback(permissionCallback)

        hostGameButton = findViewById<Button>(R.id.button_host_game)
        joinGameButton = findViewById<Button>(R.id.button_join_game)

        hostGameButton.setOnClickListener {
            hostGame()
        }

        joinGameButton.setOnClickListener {
            joinGame()
        }

        if (!bluetoothActions.isBluetoothSupported()) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG)
                .show()
            hostGameButton.isEnabled = false
            joinGameButton.isEnabled = false
        }
    }


    private fun hostGame(){
        currentAction = Action.HOST

        if(!bluetoothActions.isBluetoothEnabled()){
            Toast.makeText(this, "Please enable Bluetooth first", Toast.LENGTH_SHORT).show()
            return
        }
        if(bluetoothActions.checkPermissions()){
            makeDeviceDiscoverable()
        }
        else{
            requestBluetoothPermission.launch(bluetoothActions.getRequiredPermissions())
        }
    }

    private fun joinGame(){
        currentAction = Action.JOIN

        if(!bluetoothActions.isBluetoothEnabled()){
            Toast.makeText(this, "Please enable Bluetooth first", Toast.LENGTH_SHORT).show()
            return
        }

        if(bluetoothActions.checkPermissions()){
            startDeviceDiscovery()
        }
        else{
            requestBluetoothPermission.launch(bluetoothActions.getRequiredPermissions())
        }
    }

    private fun makeDeviceDiscoverable() {
        bluetoothActions.makeDiscoverable(requestDiscoverable)
    }

    private fun startDeviceDiscovery() {
        bluetoothActions.startDiscovery(discoveryCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothActions.cleanup()
    }
}