package com.code028.inventoryapp.ui.qrCode

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.R
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.data.repository.AuthRepository
import com.code028.inventoryapp.ui.inventory.InventoryViewModel
import com.google.android.material.card.MaterialCardView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScannerFragment : Fragment() {

    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var tvResult: TextView
    private lateinit var resultCard: MaterialCardView

    private val viewModel: QrScannerViewModel by viewModels()
    private val inventoryViewModel: InventoryViewModel by viewModels()

    companion object {
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_qr_scanner, container, false)
        previewView = root.findViewById(R.id.previewView)
        tvResult = root.findViewById(R.id.tvResult)
        resultCard = root.findViewById<MaterialCardView>(R.id.resultCard)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.scannedValue.observe(viewLifecycleOwner) { value ->
            if (!value.isNullOrEmpty()) {

                if (!viewModel.isValidFirestoreId(value)) {
                    resultCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.errorMessage))
                    tvResult.text = "QR код није валидан!"
                    viewModel.clearScannedValue()
                    return@observe
                }

                inventoryViewModel.getItem(value).addOnSuccessListener { document ->
                    val item = document.toObject(Equipment::class.java)
                    val uid = inventoryViewModel.getUserId()

                    if (item != null && item.userId == uid) {
                        val navController = findNavController()
                        if (navController.currentDestination?.id == R.id.qrScanFragment) {
                            val action = QrScannerFragmentDirections
                                .actionQrScanFragmentToItemDetailFragment(item.id)
                            navController.navigate(action)
                            viewModel.clearScannedValue()
                        }
                    } else {
                        tvResult.text = "Ставка непостоји у вашем инвентару!"
                        resultCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_600))

                    }
                }.addOnFailureListener {
                    tvResult.text = "Грешка! Покушајте поново"
                    resultCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.errorMessage))
                }
            }
        }

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview za kameru
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Analyzer za QR kod
            val barcodeScanner = BarcodeScanning.getClient()
            val analysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                    processImageProxy(barcodeScanner, imageProxy)
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (!rawValue.isNullOrEmpty()) {
                            viewModel.setScannedValue(rawValue)
                        }
                    }
                }
                .addOnFailureListener { e -> e.printStackTrace() }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}
