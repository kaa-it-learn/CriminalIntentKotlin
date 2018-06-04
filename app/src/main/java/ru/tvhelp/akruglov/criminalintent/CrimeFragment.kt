package ru.tvhelp.akruglov.criminalintent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_crime.*
import java.text.FieldPosition
import java.util.*

class CrimeFragment: Fragment() {

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val ARG_CRIME_POSITION = "crime_position"

        fun newInstance(crimeId: UUID, crimePosition: Int): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)
            args.putInt(ARG_CRIME_POSITION, crimePosition)

            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var crime: Crime
    //private var crimePosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        val crimePosition = arguments?.getInt(ARG_CRIME_POSITION) as Int
        crime = CrimeLab.getInstance(activity as Context)[crimeId]!!
        setActivityResult(crimePosition)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeTitle.setText(crime.title)
        crimeSolved.isChecked = crime.solved

        crimeTitle.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })

        crimeDate.text = crime.date.toString()
        crimeDate.isEnabled = false

        crimeSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.solved = isChecked
        }
    }

    private fun setActivityResult(crimePosition: Int) {
        val data = Intent()
        data.putExtra(CrimeActivity.EXTRA_CRIME_POSITION, crimePosition)
        activity?.setResult(Activity.RESULT_OK, data)
    }
}