package ru.tvhelp.akruglov.criminalintent

import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_twopane.*
import org.jetbrains.anko.startActivityForResult

class CrimeListActivity: SingleFragmentActivity(), CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    override fun createFragment(): Fragment = CrimeListFragment()

    override val layoutResId = R.layout.activity_masterdetail

    override fun onCrimeSelected(crime: Crime, position: Int) {
        if (detailFragmentContainer == null) {
            startActivityForResult<CrimePagerActivity>(CrimeListFragment.REQUEST_CRIME,
                    CrimePagerActivity.EXTRA_CRIME_ID to crime.id,
                    CrimePagerActivity.EXTRA_CRIME_POSITION to position)
        } else {
            val newDetail = CrimeFragment.newInstance(crime.id, position)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.detailFragmentContainer, newDetail)
                    .commit()
        }
    }

    override fun onCrimeUpdated(crime: Crime, position: Int) {
        val listFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as CrimeListFragment
        listFragment.updateUI(position)
    }
}