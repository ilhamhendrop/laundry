package com.greensoft.laundryapp.ui.activity.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Admin
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

import java.lang.StringBuilder
import java.util.*

class ProfileActivity : AppCompatActivity(){
    private var admin: Admin? = null
    private var selectedPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prosesLoadProfile()


    }

    private fun prosesLoadProfile() {
        progressLoading.visibility = View.VISIBLE

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        val data = FirebaseDatabase.getInstance().getReference("/admin/$userID")
        data.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                admin = snapshot.getValue(Admin::class.java)

                try {
                    progressLoading.visibility = View.INVISIBLE

                    txtNama.text = admin?.name
                    txtEmail.text = StringBuilder("Email: ${FirebaseAuth.getInstance().currentUser!!.email}")
                    txtAlamat.text = StringBuilder("Alamat: ${admin?.alamat}")
                    txtPhone.text = StringBuilder("Phone: ${admin?.phone}")

                    Picasso.get()
                        .load(admin?.foto)
                        .error(R.drawable.ic_broken_image)
                        .into(imgPhoto)

                } catch (e: Exception){
                    Log.d("error", e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_Edit -> {
                val intent = Intent(applicationContext, UpdateProfileActivity::class.java)
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