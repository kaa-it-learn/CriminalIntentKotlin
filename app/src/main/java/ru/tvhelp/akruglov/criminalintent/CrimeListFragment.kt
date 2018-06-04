package ru.tvhelp.akruglov.criminalintent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_crime_list.*
import kotlinx.android.synthetic.main.list_item_crime.*
import org.jetbrains.anko.support.v4.startActivityForResult

enum class CrimeType {
    SIMPLE_CRIME_TYPE, POLICE_CRIME_TYPE;

    companion object {
        fun from(value: Int) = when(value) {
            0 -> SIMPLE_CRIME_TYPE
            1 -> POLICE_CRIME_TYPE
            else -> throw IllegalArgumentException("Wrong Crime Type")
        }
    }

}

class CrimeListFragment: Fragment() {

    companion object {
        const val REQUEST_CRIME = 0
    }

    private var adapter: CrimeAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        updateUI()
    }

    /*override fun onResume() {
        super.onResume()
        updateUI()
    }*/

    private fun updateUI(crimePosition: Int = -1) {
        val crimeLab = CrimeLab.getInstance(activity as Context)

        if (adapter == null) {
            adapter = CrimeAdapter(crimeLab.crimes.values.toMutableList())
            crimeRecyclerView.adapter = adapter
        } else {
            if (crimePosition == -1)
                (adapter as CrimeAdapter).notifyDataSetChanged()
            else
                (adapter as CrimeAdapter).notifyItemChanged(crimePosition)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CRIME) {
            if (data == null) {
                return
            }

            val crimePosition = CrimeActivity.crimePosition(data)
            updateUI(crimePosition)
        }
    }

    private inner class CrimeHolder: RecyclerView.ViewHolder, LayoutContainer, View.OnClickListener {
        private lateinit var crime: Crime
        override val containerView: View?
            get() = itemView

        constructor(inflater: LayoutInflater, parent: ViewGroup, crimeType: CrimeType) :
            super(inflater.inflate(when(crimeType) {
                CrimeType.SIMPLE_CRIME_TYPE -> R.layout.list_item_crime
                CrimeType.POLICE_CRIME_TYPE -> R.layout.list_item_crime_police
            },parent, false)) {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            crimeItemTitle .text = crime.title
            crimeItemDate.text = crime.date.toString()
            crimeItemSolved.visibility = if (crime.solved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            startActivityForResult<CrimeActivity>(REQUEST_CRIME, CrimeActivity.EXTRA_CRIME_ID to crime.id,
                    CrimeActivity.EXTRA_CRIME_POSITION to adapterPosition)
        }
    }

    private inner class CrimeAdapter(val crimes: MutableList<Crime>): RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val layoutInflater = LayoutInflater.from(activity)
            return CrimeHolder(layoutInflater, parent, CrimeType.from(viewType))
        }

        override fun getItemCount(): Int = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(crimes[position])
        }

        override fun getItemViewType(position: Int): Int {
            return if (crimes[position].requirePolice)
                CrimeType.POLICE_CRIME_TYPE.ordinal
            else CrimeType.SIMPLE_CRIME_TYPE.ordinal
        }
    }
}