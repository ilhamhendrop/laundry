package com.greensoft.laundryapp.ui.activity.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Admin
import com.greensoft.laundryapp.ui.activity.dashboard.DashboardActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_update_profile.*
import java.util.*

class UpdateProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var admin: Admin? = null
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Update"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prosesViewData()

        btnUpdate.setOnClickListener(this)
        btnUpdateFoto.setOnClickListener(this)
        imgPhoto.setOnClickListener(this)

    }

    private fun prosesViewData() {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        val data = FirebaseDatabase.getInstance().getReference("/admin/$userID")
        data.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                admin = snapshot.getValue(Admin::class.java)
                progressLoading.visibility = View.INVISIBLE

                try {
                    edName.setText(admin?.name)
                    edAlamat.setText(admin?.alamat)
                    edPhone.setText(admin?.phone)

                    Picasso.get()
                        .load(admin?.foto)
                        .error(R.drawable.ic_broken_image)
                        .into(imgPhoto)

                } catch (e: Exception){
                    Log.d("error", e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnUpdate -> prosesUpdateProfile()
            R.id.btnUpdateFoto -> prosesUpdateFoto()
            R.id.imgPhoto -> prosesImage()
        }
    }

    private fun prosesUpdateFoto() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val database = FirebaseStorage.getInstance().getReference("/foto/$filename")

        database.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    database.downloadUrl.addOnSuccessListener {
                        updateFoto(it.toString())
                    }
                }
    }

    private fun updateFoto(photo: String) {
        progressLoading.visibility = View.VISIBLE
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val name = admin?.name
        val alamat = admin?.alamat
        val phone = admin?.phone
        val database = FirebaseDatabase.getInstance().getReference("/admin/$userID")

        admin = Admin(userID, name, alamat, phone, photo)

        database.setValue(admin)
                .addOnSuccessListener {
                    progressLoading.visibility = View.INVISIBLE
                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "Succes", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                }
    }

    private fun prosesImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            imgPhoto.setImageBitmap(bitmap)
        }
    }

    private fun prosesUpdateProfile() {
        progressLoading.visibility = View.VISIBLE
        val name = edName.text.toString()
        val alamat = edAlamat.text.toString()
        val phone = edPhone.text.toString()

        val adminID = FirebaseAuth.getInstance().currentUser!!.uid
        val photo = admin?.foto
        val database = FirebaseDatabase.getInstance().getReference("/admin/$adminID")

        admin = Admin(adminID, name, alamat, phone, photo)

        database.setValue(admin)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        progressLoading.visibility = View.INVISIBLE
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(applicationContext, "succes", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}