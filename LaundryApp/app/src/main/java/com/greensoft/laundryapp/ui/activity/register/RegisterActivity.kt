package com.greensoft.laundryapp.ui.activity.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.greensoft.laundryapp.R
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.StringBuilder

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnRegister -> prosesRegister()
        }
    }

    private fun prosesRegister() {
        val email = edEmail.text.toString()
        val password = edPassword.text.toString()
        val confirm = edPasswordConfrim.text.toString()

        progressLoading.visibility = View.VISIBLE

        if (email.isEmpty()){
            edEmail.error = StringBuilder("Password Harus Sama")
            edEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edEmail.error = StringBuilder("Email tidak valid")
            edEmail.requestFocus()
            return
        }

        if (password.isEmpty() || password.length < 6){
            edPassword.error = StringBuilder("Password harus lebih dari 6 karakter")
            edPassword.requestFocus()
            return
        }

        if (confirm != password){
            edPasswordConfrim.error = StringBuilder("Password Harus Sama")
            edPasswordConfrim.requestFocus()

            return
        }

        if (confirm == password){

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){
                        if (it.isSuccessful){
                            progressLoading.visibility = View.INVISIBLE

                            startActivity(Intent(applicationContext, DataAkunActivity::class.java))
                        }
                    }
                    .addOnFailureListener(this){
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    }

            return
        }


    }

}