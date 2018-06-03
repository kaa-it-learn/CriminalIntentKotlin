package ru.tvhelp.akruglov.criminalintent

import android.support.v4.app.Fragment

class CrimeListActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment = CrimeListFragment()
}