package ru.tvhelp.akruglov.criminalintent

import android.support.v4.app.Fragment
import java.util.*

class CrimeActivity : SingleFragmentActivity() {

    companion object {
        const val EXTRA_CRIME_ID = "ru.tvhelp.akruglov.criminalintent.crime_id"
    }

    override fun createFragment(): Fragment  {
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        return CrimeFragment.newInstance(crimeId)
    }
}
