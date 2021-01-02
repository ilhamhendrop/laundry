package com.greensoft.laundryapp.ui.activity.pembayaran

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.greensoft.laundryapp.R
import kotlinx.android.synthetic.main.activity_bank.*

class BankActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Bank"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }
}