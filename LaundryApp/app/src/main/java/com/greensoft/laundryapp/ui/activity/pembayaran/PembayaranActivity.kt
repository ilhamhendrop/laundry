package com.greensoft.laundryapp.ui.activity.pembayaran

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Barang
import com.greensoft.laundryapp.adapter.model.Pembayaran
import com.greensoft.laundryapp.adapter.viewadapter.PembayaranAdapter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_pembayaran.*
import kotlin.math.log

class PembayaranActivity : AppCompatActivity() {
    companion object {
        const val BARANG_KEY = "barang_key"
        const val USER_KEY = "user_key"
    }

    private var barang: Barang? = null
    private var pembayaran: Pembayaran? = null
    private val listPembayaran = GroupAdapter<GroupieViewHolder>()
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pembayaran"

        barang = intent.getParcelableExtra(BARANG_KEY)

        handler = Handler()

        prosesPembayaran()

    }

    private fun prosesPembayaran() {
        progressLoading.visibility = View.VISIBLE

        val adminID = FirebaseAuth.getInstance().currentUser!!.uid
        val barangID = barang?.id

        var buy = FirebaseDatabase.getInstance().getReference("/pembayaran/${adminID}/${barangID}")
        buy.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressLoading.visibility = View.INVISIBLE

                snapshot.children.forEach {
                    val pembayaran = it.getValue(Pembayaran::class.java)
                    if (pembayaran != null){
                        listPembayaran.add(PembayaranAdapter(pembayaran, applicationContext))
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error_buy", error.message)
            }

        })

        swipeTotal.setOnRefreshListener {
            handler.postDelayed({
                swipeTotal.isRefreshing = false

                buy = FirebaseDatabase.getInstance().getReference("/pembayaran/${adminID}/${barangID}")
                buy.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        progressLoading.visibility = View.INVISIBLE

                        listPembayaran.clear()
                        snapshot.children.forEach {
                            val pembayaran = it.getValue(Pembayaran::class.java)
                            if (pembayaran != null){
                                listPembayaran.add(PembayaranAdapter(pembayaran, applicationContext))
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("error_buy", error.message)
                    }

                })
            }, 5000)
        }

        with(rvPembayaran){
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = listPembayaran
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}