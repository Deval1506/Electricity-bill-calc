package com.electricitybill.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.electricitybill.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Keep fragment instances alive so state is preserved across tab switches
    private val calcFragment    = CalculatorFragment()
    private val historyFragment = HistoryFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load all fragments, but only show calculator initially
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, calcFragment, "calc")
            .add(R.id.fragmentContainer, historyFragment, "hist").hide(historyFragment)
            .add(R.id.fragmentContainer, settingsFragment, "set").hide(settingsFragment)
            .commit()

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calculator -> showFragment(calcFragment)
                R.id.nav_history    -> showFragment(historyFragment)
                R.id.nav_settings   -> showFragment(settingsFragment)
            }
            true
        }
    }

    private fun showFragment(target: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            listOf(calcFragment, historyFragment, settingsFragment).forEach {
                if (it == target) show(it) else hide(it)
            }
        }.commit()
    }
}
