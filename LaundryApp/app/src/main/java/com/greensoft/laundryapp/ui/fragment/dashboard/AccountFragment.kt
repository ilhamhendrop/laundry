package com.greensoft.laundryapp.ui.fragment.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Admin
import com.greensoft.laundryapp.preference.PreferenceHelper
import com.greensoft.laundryapp.ui.activity.login.LoginActivity
import com.greensoft.laundryapp.ui.activity.profile.ProfileActivity
import com.greensoft.laundryapp.ui.activity.setting.SettingActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {

    private var admin: Admin? = null
    private lateinit var prefHelper: PreferenceHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "Account"

        prefHelper = PreferenceHelper((activity as AppCompatActivity).applicationContext)

        setHasOptionsMenu(true)

        btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            prefHelper.saveLogin(PreferenceHelper.LOGIN, false)

            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            (activity as AppCompatActivity).finish()
        }

        prosesViewUser()

    }

    private fun prosesViewUser() {
        progressLoading.visibility = View.VISIBLE

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val data = FirebaseDatabase.getInstance().getReference("/admin/$userID")
        data.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                admin = snapshot.getValue(Admin::class.java)

                try {
                    progressLoading.visibility = View.INVISIBLE
                    txtNama.text = admin?.name
                    txtEmail.text = FirebaseAuth.getInstance().currentUser!!.email

                    Picasso.get()
                        .load(admin?.foto)
                        .placeholder(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_broken_image)
                        .into(imgPhoto)

                    cardProfile.setOnClickListener {
                        val intent = Intent(context, ProfileActivity::class.java)
                        startActivity(intent)
                    }

                } catch (e: Exception){
                    Log.d("error", e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_setting_account, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuSetting -> {
                val intent = Intent(context, SettingActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}