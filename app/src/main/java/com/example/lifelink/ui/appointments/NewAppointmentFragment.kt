package com.example.lifelink.ui.appointments

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.lifelink.ApplicationController
import com.example.lifelink.R
import com.example.lifelink.data.local.entity.Appointment
import com.example.lifelink.data.repository.AppointmentRepository
import com.example.lifelink.data.repository.ClinicRepository
import com.example.lifelink.ui.clinics.ClinicViewModel
import com.example.lifelink.ui.clinics.ClinicViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Locale

class NewAppointmentFragment : Fragment() {

    private val args: NewAppointmentFragmentArgs by navArgs()
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var clinicViewModel: ClinicViewModel

    private lateinit var tietAppointmentDate: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_new_appointment, container, false)
        tietAppointmentDate = view.findViewById(R.id.tiet_appointment_date)

        val btnCancelAppointment = view.findViewById<MaterialButton>(R.id.btn_cancel_appointment)
        val btnCreateAppointment = view.findViewById<MaterialButton>(R.id.btn_save_appointment)

        tietAppointmentDate.setOnClickListener{
            showDatePickerDialog()
        }

        btnCancelAppointment.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnCreateAppointment.setOnClickListener {
            val appointmentDate = tietAppointmentDate.text.toString()

            val appointment = Appointment(
                clinicId = args.donationCenterId,
                date = tietAppointmentDate.text.toString(),
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            )

            appointmentViewModel.insertAppointment(appointment)
            showAppointmentNotification(appointmentDate)
            parentFragmentManager.popBackStack()
        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clinicId = args.donationCenterId

        val donationCenterTextView = view.findViewById<MaterialTextView>(R.id.til_donation_center)

        val database = ApplicationController.instance.appDatabase
        val appointmentFactory =
            AppointmentViewModelFactory(AppointmentRepository(database.appointmentDao()))
        val clinicFactory = ClinicViewModelFactory(ClinicRepository(database.clinicDao()))

        appointmentViewModel =
            ViewModelProvider(this, appointmentFactory)[AppointmentViewModel::class.java]
        clinicViewModel = ViewModelProvider(this, clinicFactory)[ClinicViewModel::class.java]

        clinicViewModel.allClinics.observe(viewLifecycleOwner) { clinics ->
            val clinic = clinics.find { it.id == clinicId }
            if (clinic != null) {
                donationCenterTextView.text = clinic.name
            }
        }


    }

    private fun showAppointmentNotification(appointmentDate: String) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "appointment_channel_id"
        val channelName = "Appointment Notifications"

        // Create notification channel (required for Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new appointments"
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create the notification
        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_launcher_monochrome) // Replace with your app's notification icon
            .setContentTitle("Appointment Created")
            .setContentText("Your appointment on $appointmentDate has been successfully created.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }


    private fun showDatePickerDialog() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selectedDate ->
            // Convert the selected date to the desired format
            val calendar = android.icu.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selectedDate
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            tietAppointmentDate.setText(formattedDate)
        }

        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

}