package com.greensoft.laundryapp.ui.activity.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Barang
import com.greensoft.laundryapp.adapter.viewadapter.BarangAdapter
import com.greensoft.laundryapp.adapter.viewadapter.HistoryBarangAdapter
import com.greensoft.laundryapp.ui.activity.barang.DetailBarangActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_history_barang.*


class HistoryBarangActivity : AppCompatActivity() {
    private val listBarang = GroupAdapter<GroupieViewHolder>()
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_barang)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handler = Handler()

        prosesViewHistoryBarang()

    }

    private fun prosesViewHistoryBarang() {
        val adminID = FirebaseAuth.getInstance().uid

        var barang = FirebaseDatabase.getInstance().getReference("/history/${adminID}/barang")
        barang.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressLoading.visibility = View.INVISIBLE

                snapshot.children.forEach {
                    val item = it.getValue(Barang::class.java)
                    if (item != null){
                        listBarang.add(HistoryBarangAdapter(item, applicationContext))
                    }
                }

                with(rvBarang){
                    layoutManager = GridLayoutManager(this@HistoryBarangActivity, 2)
                    setHasFixedSize(true)
                    adapter = listBarang
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })

        swipeRefresh.setOnRefreshListener {
            handler.postDelayed({
                swipeRefresh.isRefreshing = false

                barang = FirebaseDatabase.getInstance().getReference("/history/${adminID}/barang")
                barang.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        listBarang.clear()
                        snapshot.children.forEach {
                            val item = it.getValue(Barang::class.java)
                            if (item != null){
                                listBarang.add(HistoryBarangAdapter(item, applicationContext))
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    }

                })

            }, 5000)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}