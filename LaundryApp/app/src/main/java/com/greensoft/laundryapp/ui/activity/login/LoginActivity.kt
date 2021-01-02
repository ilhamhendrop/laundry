package com.greensoft.laundryapp.ui.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.preference.PreferenceHelper
import com.greensoft.laundryapp.ui.activity.dashboard.DashboardActivity
import com.greensoft.laundryapp.ui.activity.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val ID = "id"
    }

    private lateinit var prefHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefHelper = PreferenceHelper(this)

        if (prefHelper.getLogin()){
            val intent = Intent(applicationContext, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener(this)
        txtRegister.setOnClickListener(this)
        txtReset.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnLogin -> prosesLogin()
            R.id.txtRegister -> prosesRegister()
            R.id.txtReset -> prosesResetPassword()
        }
    }

    private fun prosesResetPassword() {
        startActivity(Intent(applicationContext, ResetPasswordActivity::class.java))
    }

    private fun prosesRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun prosesLogin() {
        val email = edEmail.text.toString()
        val password = edPassword.text.toString()

        progressLoading.visibility = View.VISIBLE

        if (email.isEmpty()){
            edEmail.error = "Email tidak boleh kosong"
            edEmail.requestFocus()
            progressLoading.visibility = View.INVISIBLE
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edEmail.error = "Email tidak valid"
            edEmail.requestFocus()
            progressLoading.visibility = View.INVISIBLE
            return
        }

        if (password.isEmpty() || password.length < 6){
            edPassword.error = "Password harus lebih dari 6 karakter"
            edPassword.requestFocus()
            progressLoading.visibility = View.INVISIBLE
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful){
                    prefHelper.saveLogin(PreferenceHelper.LOGIN, true)

                    val intent = Intent(applicationContext, DashboardActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)

                    Log.d(ID, "${it.result?.user?.uid}")
                    Toast.makeText(applicationContext, "Welcome ${it.result?.user?.email}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                progressLoading.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

}