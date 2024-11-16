package com.example.lifelink.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifelink.ui.AppActivity
import com.example.lifelink.R
import com.example.lifelink.helpers.extensions.logErrorMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage("Logging in...")
            setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        val button = findViewById<Button>(R.id.btn_login_login)
        button.setOnClickListener {
            val email = findViewById<EditText>(R.id.et_email_login).text.toString()
            val password = findViewById<EditText>(R.id.et_password_login).text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog.show()

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser

                        if (user != null) {
                            val uid = user.uid
                            firebaseFirestore.collection("users").document(uid).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val fullName = document.getString("fullName")
                                        val dateOfBirth = document.getString("dateOfBirth")
                                        val gender = document.getString("gender")
                                        val bloodType = document.getString("bloodType")

                                        val intent =
                                            Intent(this@LoginActivity, AppActivity::class.java)
                                        intent.putExtra("fullName", fullName)
                                        intent.putExtra("dateOfBirth", dateOfBirth)
                                        intent.putExtra("gender", gender)
                                        intent.putExtra("bloodType", bloodType)
                                        startActivity(intent)
                                        finish()

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Invalid credentials!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                        return@addOnCompleteListener
                    }
                }
                .addOnFailureListener { exception ->
                    "Login failed: ${exception.message}".logErrorMessage()
                    exception.printStackTrace()
                    progressDialog.dismiss()
                }
        }
    }
}