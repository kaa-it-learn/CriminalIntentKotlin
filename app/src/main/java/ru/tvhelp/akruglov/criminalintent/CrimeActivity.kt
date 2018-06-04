package ru.tvhelp.akruglov.criminalintent

import android.content.Intent
import android.support.v4.app.Fragment
import java.util.*

class CrimeActivity : SingleFragmentActivity() {

    companion object {
        const val EXTRA_CRIME_ID = "ru.tvhelp.akruglov.criminalintent.crime_id"
        const val EXTRA_CRIME_POSITION = "ru.tvhelp.akruglov.criminalintent.crime.position"

        fun crimePosition(result: Intent) = result.getIntExtra(EXTRA_CRIME_POSITION, 0)
    }

    override fun createFragment(): Fragment  {
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        val crimePosition = intent.getIntExtra(EXTRA_CRIME_POSITION, 0)
        return CrimeFragment.newInstance(crimeId, crimePosition)
    }
}
