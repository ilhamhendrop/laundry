package com.greensoft.laundryapp.ui.activity.barang

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.greensoft.laundryapp.adapter.model.Barang
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_update_barang.*
import java.util.*

class UpdateBarangActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val USER_KEY = "user_key"
    }

    private var items: Barang? = null
    private lateinit var extra: Bundle
    private var selectedBarangUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_barang)

        setSupportActionBar(toolbar)
        supportActionBar?.title = StringBuilder("Update")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        extra = intent.extras!!
        val id = extra.getString(USER_KEY)

        prosesViewData()

        btnUpdateData.setOnClickListener(this)
        btnUpdateFoto.setOnClickListener(this)
        imgInput.setOnClickListener(this)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    private fun prosesViewData() {
        progressLoading.visibility = View.VISIBLE

        extra = intent.extras!!
        val id = extra.getString(USER_KEY)

        val barang = FirebaseDatabase.getInstance().getReference("/barang").child(id.toString())
        barang.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                progressLoading.visibility = View.INVISIBLE

                items = snapshot.getValue(Barang::class.java)

                if (items != null){
                    Picasso.get()
                        .load(items?.photo)
                        .error(R.drawable.ic_broken_image)
                        .into(imgPhoto)

                    edName.setText(items?.name)
                    edHarga.setText(items?.harga.toString())
                    edKategori.setText(items?.kategori)
                    edKeterangan.setText(items?.keterangan)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateBarangActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnUpdateData -> prosesUpdateData()
            R.id.btnUpdateFoto -> prosesUpdateFoto()
            R.id.imgInput -> {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }

                startActivityForResult(intent, 0)
            }
        }
    }

    private fun prosesUpdateFoto() {
        progressLoading.visibility = View.VISIBLE
        if (selectedBarangUri == null) return

        val filename = UUID.randomUUID().toString()
        val photo = FirebaseStorage.getInstance().getReference("/item").child(filename)

        photo.putFile(selectedBarangUri!!)
                .addOnSuccessListener {
                    photo.downloadUrl.addOnSuccessListener {
                        updateFoto(it.toString())
                    }
                }
    }

    private fun updateFoto(photo: String) {
        extra = intent.extras!!
        val id = extra.getString(USER_KEY)
        val name = items?.name
        val keterangan = items?.keterangan
        val kategori = items?.kategori
        val price = items?.harga!!.toInt()

        val data = FirebaseDatabase.getInstance().getReference("/barang").child(id.toString())

        items = Barang(id, name, keterangan, kategori, price, photo)

        data.setValue(items)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        progressLoading.visibility = View.INVISIBLE
                        Toast.makeText(this, "Succes", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, BarangActivity::class.java)
                        startActivity(intent)

                        historyUpdateBarang(id.toString(), name.toString(), keterangan.toString(), kategori.toString(), price, photo)
                    }
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedBarangUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedBarangUri)
            imgInput.setImageBitmap(bitmap)
            imgPhoto.alpha = 0f
        }
    }

    private fun prosesUpdateData() {
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

        extra = intent.extras!!
        val id = extra.getString(USER_KEY)
        val photo = items?.photo

        val data = FirebaseDatabase.getInstance().getReference("/barang").child(id.toString())

        items = Barang(id, name, keterangan, kategori, price, photo)

        data.setValue(items)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        progressLoading.visibility = View.INVISIBLE
                        Toast.makeText(this, "Succes", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, BarangActivity::class.java)
                        startActivity(intent)

                        historyUpdateBarang(id.toString(), name, keterangan, kategori, price, photo.toString())
                    }
                }
    }



    private fun historyUpdateBarang(id: String, name: String, keterangan: String, kategori: String, price: Int, photo: String) {
        val adminID = FirebaseAuth.getInstance().currentUser?.uid
        val data = FirebaseDatabase.getInstance().getReference("/history/${adminID}/barang").child(id)

        items = Barang(id, name, keterangan, kategori, price, photo)

        data.setValue(items)
    }

}