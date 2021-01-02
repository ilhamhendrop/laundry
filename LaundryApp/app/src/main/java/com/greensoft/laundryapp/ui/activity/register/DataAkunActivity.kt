package com.greensoft.laundryapp.ui.activity.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Admin
import com.greensoft.laundryapp.ui.activity.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_data_akun.*
import java.util.*

class DataAkunActivity : AppCompatActivity(), View.OnClickListener {

    private var selectedPhotoUri: Uri? = null
    private var admin: Admin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_akun)

        btnSave.setOnClickListener(this)
        imgInput.setOnClickListener(this)


    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnSave -> prosesDataAkun()
            R.id.imgInput-> prosesInputFoto()
        }
    }

    private fun prosesInputFoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            imgInput.setImageBitmap(bitmap)
            imgPhoto.alpha = 0f
        }
    }

    private fun prosesDataAkun() {
        progressLoading.visibility = View.VISIBLE
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val database = FirebaseStorage.getInstance().getReference("/foto").child(filename)

        database.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                database.downloadUrl.addOnSuccessListener {

                    saveFoto(it.toString())
                }
            }

    }


    private fun saveFoto(foto: String) {
        val id = FirebaseAuth.getInstance().uid ?: ""
        val database = FirebaseDatabase.getInstance().getReference("/admin").child(id)

        val name = edName.text.toString()
        val alamat = edAlamat.text.toString()
        val phone = edPhone.text.toString()

        if (name.isEmpty()) {
            edName.error = "Nama Tidak Boleh Kosong"
            edName.requestFocus()
            return
        }

        if (alamat.isEmpty()){
            edAlamat.error = "Alamat Tidak Boleh Kosong"
            edAlamat.requestFocus()
            return
        }

        if (phone.isEmpty()){
            edPhone.error = "Phone Tidak Boleh Kosong"
            edPhone.requestFocus()
            return
        }

        admin = Admin(id, name, alamat, phone, foto)

        database.setValue(admin)
                .addOnSuccessListener {
                    progressLoading.visibility = View.INVISIBLE

                    startActivity(Intent(applicationContext, DashboardActivity::class.java))

                    prosesVerified()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
    }

    private fun prosesVerified() {
        val database = FirebaseAuth.getInstance()
        val user = database.currentUser

        user?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(applicationContext, "Cek Email Untuk Verified", Toast.LENGTH_SHORT).show()
            }
        }
    }


}