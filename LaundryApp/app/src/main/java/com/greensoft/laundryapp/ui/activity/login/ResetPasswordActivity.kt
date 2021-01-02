package com.greensoft.laundryapp.ui.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.greensoft.laundryapp.R
import kotlinx.android.synthetic.main.activity_reset_password.*


class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        btnReset.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnReset -> prosesResetPassword()
        }
    }

    private fun prosesResetPassword() {
        val email = edEmail.text.toString()

        if (email.isEmpty()){
            edEmail.error = "Email tidak boleh kosong"
            edEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edEmail.error = "Email tidak valid"
            edEmail.requestFocus()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(applicationContext, "Cek Email Untuk Reset Password", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}