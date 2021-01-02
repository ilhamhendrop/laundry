package com.greensoft.laundryapp.ui.activity.barang

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Barang
import kotlinx.android.synthetic.main.activity_input_barang.*
import java.lang.StringBuilder
import java.util.*

class InputBarangActivity : AppCompatActivity(), View.OnClickListener {
    private var selectedBarangUri: Uri? = null
    private var barang: Barang? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_barang)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Input Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSave.setOnClickListener(this)
        imgInput.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnSave -> prosesInputBarang()
            R.id.imgInput -> prosesPhoto()
        }
    }

    private fun prosesInputBarang() {
        progressLoading.visibility = View.VISIBLE

        if (selectedBarangUri == null) return

        val filename = UUID.randomUUID().toString()
        val photo = FirebaseStorage.getInstance().getReference("/item/$filename")

        photo.putFile(selectedBarangUri!!)
                .addOnSuccessListener {
                    photo.downloadUrl.addOnSuccessListener {
                        saveData(it.toString())
                    }
                }
    }

    private fun saveData(photo: String) {
        val name = edName.text.toString()
        val keterangan = edKeterangan.text.toString()
        val kategori = edKategori.text.toString()
        val harga = edHarga.text.toString()
        val price = harga.toInt()

        if (name.isEmpty()){
            edName.error = StringBuilder("Nama tidak boleh kosong")
            edName.requestFocus()
            progressLoading.visibility = View.INVISIBLE
            return
        }

        if (keterangan.isEmpty()){
            edKeterangan.error = StringBuilder("Keterangan tidak boleh kosong")
            edKeterangan.requestFocus()
            progressLoading.visibility = View.INVISIBLE
            return
        }

        if (kategori.isEmpty()){
            edKategori.error = StringBuilder("Kategori tidak boleh kosong")
            edKategori.requestFocus()
            progressLoading.visibility = View.INVISIBLE
            return
        }

        if (harga.isEmpty()){
            edHarga.error = StringBuilder("Harga tidak boleh kosong")
            edHarga.requestFocus()
            progressLoading.visibility = View.INVISIBLE
            return
        }

        database = FirebaseDatabase.getInstance().getReference("/barang")
        val id = database.push().key
        barang = Barang(id, name, keterangan, kategori, price, photo)

        if (id != null){
            database.child(id).setValue(barang)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            progressLoading.visibility = View.INVISIBLE
                            onBackPressed()
                            Toast.makeText(applicationContext, "Succes", Toast.LENGTH_SHORT).show()

                            edName.text?.clear()
                            edHarga.text?.clear()
                            edKategori.text?.clear()
                            edKeterangan.text?.clear()

                            historyBarang(id, name, keterangan, kategori, price, photo)
                        }
                    }
                    .addOnFailureListener {
                        progressLoading.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    }
        }
    }

    private fun historyBarang(id: String, name: String, keterangan: String, kategori: String, price: Int, photo: String) {
        val adminID = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("/history/${adminID}/barang").child(id)

        barang = Barang(id, name, keterangan, kategori, price, photo)

        database.child(id).setValue(barang)
    }

    private fun prosesPhoto() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedBarangUri = data.data
            try {
                selectedBarangUri?.let {
                    if (Build.VERSION.SDK_INT < 28){
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedBarangUri)
                        imgInput.setImageBitmap(bitmap)
                        imgPhoto.alpha = 0f
                    } else {
                        val source = ImageDecoder.createSource(contentResolver, selectedBarangUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        imgInput.setImageBitmap(bitmap)
                        imgPhoto.alpha = 0f
                    }

                }
            } catch (e: Exception){
                Log.d("Input_IMAGE", e.printStackTrace().toString())
            }
        }
    }



}