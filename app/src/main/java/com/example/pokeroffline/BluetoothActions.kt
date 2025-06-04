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
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class BluetoothActions(private val context: Context) {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var discoveryCallback: DeviceDiscoveryCallback? = null
    private var permissionCallback: PermissionCallback? = null
    private var isReceiverRegistered = false

    init {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    interface DeviceDiscoveryCallback {
        fun onDeviceFound(device: BluetoothDevice, deviceName: String, deviceAddress: String)
        fun onDiscoveryStarted()
        fun onDiscoveryFinished()
        fun onDiscoveryFailed(error: String)
    }

    interface PermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
    }

    interface DiscoverabilityCallback {
        fun onDiscoverabilityEnabled(duration: Int)
        fun onDiscoverabilityDenied()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }

                    device?.let {
                        val deviceName = getDeviceName(it)
                        val deviceAddress = it.address
                        discoveryCallback?.onDeviceFound(it, deviceName, deviceAddress)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    discoveryCallback?.onDiscoveryFinished()
                }
            }
        }
    }

    fun isBluetoothSupported(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun checkPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12 and above
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            // Below Android 12
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getRequiredPermissions(): Array<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12 and above
            arrayOf(
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            // Below Android 12
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    fun setPermissionCallback(callback: PermissionCallback) {
        this.permissionCallback = callback
    }

    fun handlePermissionResult(permissions: Map<String, Boolean>) {
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            permissionCallback?.onPermissionsGranted()
        } else {
            permissionCallback?.onPermissionsDenied()
        }
    }

    fun startDiscovery(callback: DeviceDiscoveryCallback) {
        if (!isBluetoothEnabled()) {
            callback.onDiscoveryFailed("Bluetooth is not enabled")
            return
        }

        if (!checkPermissions()) {
            callback.onDiscoveryFailed("Required permissions not granted")
            return
        }

        this.discoveryCallback = callback

        try {
            if (!isReceiverRegistered) {
                val filter = IntentFilter().apply {
                    addAction(BluetoothDevice.ACTION_FOUND)
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                }
                context.registerReceiver(receiver, filter)
                isReceiverRegistered = true
            }

            bluetoothAdapter?.let { adapter ->
                if (adapter.isDiscovering) {
                    adapter.cancelDiscovery()
                }

                val discoveryStarted = adapter.startDiscovery()

                if (discoveryStarted) {
                    callback.onDiscoveryStarted()
                } else {
                    callback.onDiscoveryFailed("Failed to start device discovery")
                }
            } ?: callback.onDiscoveryFailed("Bluetooth adapter not available")

        } catch (e: SecurityException) {
            callback.onDiscoveryFailed("Permission denied for Bluetooth operations")
        }
    }

    fun stopDiscovery() {
        try {
            bluetoothAdapter?.let { adapter ->
                if (adapter.isDiscovering) {
                    adapter.cancelDiscovery()
                }
            }
        } catch (e: SecurityException) {
            // Handle permission error silently for stop operation
        }
    }

    fun makeDiscoverable(launcher: ActivityResultLauncher<Intent>) {
        if (!isBluetoothEnabled()) {
            return
        }

        if (!checkPermissions()) {
            return
        }

        try {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) // 5 minutes
            }
            launcher.launch(discoverableIntent)
        } catch (e: SecurityException) {
            // Handle permission error
        }
    }

    fun handleDiscoverabilityResult(resultCode: Int, callback: DiscoverabilityCallback) {
        if (resultCode > 0) {
            callback.onDiscoverabilityEnabled(resultCode)
        } else {
            callback.onDiscoverabilityDenied()
        }
    }

    private fun getDeviceName(device: BluetoothDevice): String {
        return try {
            if (checkPermissions()) {
                device.name ?: "Unknown Device"
            } else {
                "Unknown Device"
            }
        } catch (e: SecurityException) {
            "Unknown Device"
        }
    }

    fun cleanup() {
        try {
            stopDiscovery()
            if (isReceiverRegistered) {
                context.unregisterReceiver(receiver)
                isReceiverRegistered = false
            }
        } catch (e: Exception) {
            // Handle cleanup errors silently
        }
    }
}