package com.greensoft.laundryapp.ui.activity.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Barang
import com.greensoft.laundryapp.adapter.viewadapter.BarangAdapter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_barang.*

class BarangActivity : AppCompatActivity() {

    private val listBarang = GroupAdapter<GroupieViewHolder>()
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barang)

        handler = Handler()

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prosesViewBarang()
    }

    private fun prosesViewBarang() {

        var barang = FirebaseDatabase.getInstance().getReference("/barang")
        barang.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                progressLoading.visibility = View.INVISIBLE

                snapshot.children.forEach {
                    val item = it.getValue(Barang::class.java)
                    if (item != null){
                        listBarang.add(BarangAdapter(item, applicationContext))
                    }
                }

                with(rvBarang){
                    layoutManager = GridLayoutManager(this@BarangActivity, 2)
                    setHasFixedSize(true)
                    adapter = listBarang
                }

                listBarang.setOnItemClickListener { item, view ->
                    item as BarangAdapter
                    val intent = Intent(applicationContext, DetailBarangActivity::class.java).apply {
                        putExtra(DetailBarangActivity.USER_KEY, item.barang)
                    }

                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })

        swipeRefresh.setOnRefreshListener {
            handler.postDelayed({
                swipeRefresh.isRefreshing = false

                barang = FirebaseDatabase.getInstance().getReference("/barang")
                barang.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        listBarang.clear()
                        snapshot.children.forEach {
                            val item = it.getValue(Barang::class.java)
                            if (item != null){
                                listBarang.add(BarangAdapter(item, applicationContext))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting_barang, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuBarang -> {
                val intent = Intent(applicationContext, InputBarangActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}