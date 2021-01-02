package com.greensoft.laundryapp.ui.activity.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.ui.fragment.dashboard.AccountFragment
import com.greensoft.laundryapp.ui.fragment.dashboard.HistoryFragment
import com.greensoft.laundryapp.ui.fragment.dashboard.HomeFragment
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> {
                    val home = HomeFragment.getIntance()
                    addFragment(home)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_history -> {
                    val hitory = HistoryFragment()
                    addFragment(hitory)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_account -> {
                    val account = AccountFragment()
                    addFragment(account)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        val home = HomeFragment.getIntance()
        addFragment(home)
    }

    private fun addFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
                .replace(R.id.frameDashboard, fragment, fragment.javaClass.simpleName)
                .commit()
    }
}