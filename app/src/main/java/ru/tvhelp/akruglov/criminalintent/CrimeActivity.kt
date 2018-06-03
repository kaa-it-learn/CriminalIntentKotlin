package ru.tvhelp.akruglov.criminalintent

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment

class CrimeActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment  = CrimeFragment()
}
