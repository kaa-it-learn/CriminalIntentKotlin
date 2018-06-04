package ru.tvhelp.akruglov.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crime_pager.*
import java.util.*

class CrimePagerActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_CRIME_ID = "ru.tvhelp.akruglov.criminalintent.crime_id"
        const val EXTRA_CRIME_POSITION = "ru.tvhelp.akruglov.criminalintent.crime.position"

        fun crimePosition(result: Intent) = result.getIntExtra(EXTRA_CRIME_POSITION, 0)
    }

    private lateinit var crimes: MutableList<Crime>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        val crimeLab = CrimeLab.getInstance(this)
        crimes = crimeLab.crimes.values.toMutableList()

        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        val crimePosition = intent.getIntExtra(EXTRA_CRIME_POSITION, 0)

        crimeViewPager.adapter = object: FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return crimes.size
            }

            override fun getItem(position: Int): Fragment {
                return CrimeFragment.newInstance(crimes[position].id, position)
            }
        }

        for ((i, crime) in crimes.withIndex()) {
            if (crime.id == crimeId) {
                crimeViewPager.currentItem = i
                break
            }
        }
    }
}