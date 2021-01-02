package com.greensoft.laundryapp.ui.activity.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Barang
import com.greensoft.laundryapp.adapter.model.Pembayaran
import com.greensoft.laundryapp.ui.activity.pembayaran.PembayaranActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_barang.*
import kotlinx.android.synthetic.main.item_keterangan.view.*

class DetailBarangActivity : AppCompatActivity() {
    companion object {
        const val USER_KEY = "user_key"
        const val LANGGANAN = "langganan"
        const val TOTAL = "total"
        const val sharedFile = "sharedFile"
    }

    private var barang: Barang? = null
    private var pembayaran: Pembayaran? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_barang)

        barang = intent.getParcelableExtra(USER_KEY)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prosesDetailBarang()
        prosesLangganan()

    }

    private fun prosesLangganan() {
        val listLangganan = resources.getStringArray(R.array.langganan)
        val tipe = ArrayAdapter(this, android.R.layout.simple_spinner_item, listLangganan)

        with(spLangganan){
            adapter = tipe
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, long: Long) {
                    val langganan = listLangganan[position]
                    prosesBerapaKali(langganan.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    private fun prosesBerapaKali(langganan: String) {
        val listKali = resources.getStringArray(R.array.kali)
        val tipeKali = ArrayAdapter(this, android.R.layout.simple_spinner_item, listKali)

        with(spKali){
            adapter = tipeKali
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    val kali = listKali[position]
                    val total = StringBuilder("${barang?.harga!! * kali.toInt()}").toString()

                    txtTotal.text = StringBuilder("Rp. $total")

                    //Log.d("total", total)

                    prosesPembayaran(langganan, kali)

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }
    }

    private fun prosesPembayaran(langganan: String, kali: String) {
        btnBayar.setOnClickListener {

            progressLoading.visibility = View.VISIBLE

            val hari = edHari.text.toString()
            val total = StringBuilder("${barang?.harga!! * kali.toInt()}").toString()

            if (hari.isEmpty()){
                edHari.error = StringBuilder("tidak boleh kosong")
                edHari.requestFocus()
                progressLoading.visibility = View.INVISIBLE
            }

            val proses = "unverifed"
            val idBarang = barang?.id.toString()
            val adminID = FirebaseAuth.getInstance().currentUser!!.uid

            database = FirebaseDatabase.getInstance().getReference("/pembayaran/${adminID}/${idBarang}")
            val id = database.push().key

            pembayaran = Pembayaran(id, idBarang, adminID, langganan, kali.toInt(), total.toInt(), hari, proses)

            if (id != null){
                database.child(id).setValue(pembayaran)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            progressLoading.visibility = View.INVISIBLE
                            Toast.makeText(applicationContext, "Succes", Toast.LENGTH_SHORT).show()

                            val intent = Intent(applicationContext, PembayaranActivity::class.java).apply {
                                putExtra(PembayaranActivity.BARANG_KEY,
                                    Barang(
                                        barang?.id,
                                        barang?.name,
                                        barang?.keterangan,
                                        barang?.kategori,
                                        barang?.harga,
                                        barang?.photo))
                            }
                            startActivity(intent)

                        }
                    }
                    .addOnFailureListener {
                        Log.d("error_pembayaran", it.message.toString())
                    }
            }

        }
    }

    private fun prosesDetailBarang() {
        with(collapsingToolbar){
            title = barang?.name
            setCollapsedTitleTextColor(ContextCompat.getColor(this@DetailBarangActivity, R.color.white))
            setExpandedTitleColor(ContextCompat.getColor(this@DetailBarangActivity, R.color.white))
        }

        Picasso.get()
            .load(barang?.photo)
            .error(R.drawable.ic_broken_image)
            .into(imgBarang)

        txtHarga.text = StringBuilder("Harga: Rp${barang?.harga.toString()}")
        txtKategori.text = StringBuilder("Kategori: ${barang?.kategori}")
        txtKeterangan.text = barang?.keterangan

        cvKetarangan.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.item_keterangan, null)

            view.txtKeterangan.text = barang?.keterangan

            builder.setView(view)
                    .setTitle("Keterangan")
                    .setNegativeButton("Cancel"){p0, p1 ->

                    }
                    .create()
                    .show()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_barang, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_Edit -> {
                val intent = Intent(this, UpdateBarangActivity::class.java).apply {
                    putExtra(UpdateBarangActivity.USER_KEY, barang?.id)
                }

                startActivity(intent)
            }

            R.id.menu_delete -> {
                val barangID = barang?.id
                val adminID = FirebaseAuth.getInstance().currentUser?.uid
                val items = FirebaseDatabase.getInstance().getReference("/barang").child(barangID.toString())
                val history = FirebaseDatabase.getInstance().getReference("/admin/history/${adminID}/barang")
                     .child(barangID.toString())
                val img = barang?.photo
                val photo = FirebaseStorage.getInstance().getReferenceFromUrl(img.toString())

                items.removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            onBackPressed()
                            Toast.makeText(this, "Succes", Toast.LENGTH_SHORT).show()
                        }
                    }

                history.removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            onBackPressed()
                        }
                    }

                photo.delete()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            onBackPressed()
                        }
                    }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}