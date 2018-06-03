package ru.tvhelp.akruglov.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

abstract class SingleFragmentActivity: AppCompatActivity() {
    abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragmentContainer)

        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit()
        }
    }
}