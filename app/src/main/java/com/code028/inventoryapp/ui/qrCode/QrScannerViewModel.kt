package com.code028.inventoryapp.ui.qrCode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrScannerViewModel : ViewModel() {

    private val _scannedValue = MutableLiveData<String?>()
    val scannedValue: LiveData<String?> get() = _scannedValue

    fun setScannedValue(value: String) {
        _scannedValue.value = value
    }

    fun clearScannedValue() {
        _scannedValue.value = null
    }

    fun isValidFirestoreId(value: String): Boolean {
        // Firestore dokument ID je obično UUID (36 karaktera sa crtama)
        val regex = Regex("^[a-fA-F0-9\\-]{36}$")
        return regex.matches(value)
    }

}
