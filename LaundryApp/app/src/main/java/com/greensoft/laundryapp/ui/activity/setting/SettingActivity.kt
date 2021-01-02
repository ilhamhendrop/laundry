package com.greensoft.laundryapp.ui.activity.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.ui.activity.barang.BarangActivity
import com.greensoft.laundryapp.ui.activity.pembayaran.BankActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cvBarang.setOnClickListener(this)
        cvRekening.setOnClickListener(this)

    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.cvBarang -> {
                val intent = Intent(applicationContext, BarangActivity::class.java)
                startActivity(intent)
            }

            R.id.cvRekening -> {
                val intent = Intent(applicationContext, BankActivity::class.java)
                startActivity(intent)
            }

        }
    }
}