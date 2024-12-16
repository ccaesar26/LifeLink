package com.example.lifelink.ui.appointments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.lifelink.ApplicationController
import com.example.lifelink.R
import com.example.lifelink.data.local.entity.Clinic
import com.example.lifelink.data.repository.ClinicRepository
import com.example.lifelink.databinding.FragmentScanQrAppointmentBinding
import com.google.gson.Gson
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.launch

class ScanQrAppointment : Fragment() {
    private var _binding: FragmentScanQrAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var barcodeView: CompoundBarcodeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanQrAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeView = binding.cameraPreview
    }

    private fun startCameraPreview() {
        barcodeView.decodeContinuous(callback)  // Start the camera preview
    }

    private val callback = BarcodeCallback { result: BarcodeResult? ->
        result?.let {
            // Stop scanning after a successful scan
            barcodeView.pause()

            // Retrieve the QR code contents as a string
            val qrContent = it.text

            // Process the JSON content from the QR code as needed here
            handleQrContent(qrContent)
        }
    }

    private fun handleQrContent(qrContent: String) {
        try {
            // Deserialize JSON into a Clinic object
            val qrClinicData = Gson().fromJson(qrContent, Clinic::class.java)

            // Fetch the clinic from the database
            val database = ApplicationController.instance.appDatabase
            val clinicDao = database.clinicDao()
            val repository = ClinicRepository(clinicDao)

            lifecycleScope.launch {
                // Fetch the clinic by name and address
                val clinic =
                    repository.getClinicByNameAndAddress(qrClinicData.name, qrClinicData.address)

                if (clinic != null) {
                    // Navigate to the appointment creation screen
                    val action =
                        ScanQrAppointmentDirections.actionScanQrAppointmentToNewAppointmentFragment2(
                            clinic.id
                        )
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Clinic not found in the database.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            // Handle JSON parsing errors
            Toast.makeText(requireContext(), "Invalid QR code format.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()  // Resume scanning when the fragment is visible
        startCameraPreview()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()  // Pause scanning when the fragment is not visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}