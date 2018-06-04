package ru.tvhelp.akruglov.criminalintent

import android.content.Context
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
import org.jetbrains.anko.support.v4.toast

class CrimeListFragment: Fragment() {

    private lateinit var adapter: CrimeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        updateUI()
    }

    private fun updateUI() {
        val crimeLab = CrimeLab.getInstance(activity as Context)
        adapter = CrimeAdapter(crimeLab.crimes)
        crimeRecyclerView.adapter = adapter
    }

    private inner class CrimeHolder: RecyclerView.ViewHolder, LayoutContainer, View.OnClickListener {
        private lateinit var crime: Crime
        override val containerView: View?
            get() = itemView

        constructor(inflater: LayoutInflater, parent: ViewGroup) :
            super(inflater.inflate(R.layout.list_item_crime, parent, false)) {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            crimeItemTitle .text = crime.title
            crimeItemDate.text = crime.date.toString()
        }

        override fun onClick(v: View?) {
            toast(crime.title + " clicked!")
        }
    }

    private inner class CrimeAdapter(val crimes: MutableList<Crime>): RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val layoutInflater = LayoutInflater.from(activity)
            return CrimeHolder(layoutInflater, parent)
        }

        override fun getItemCount(): Int = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(crimes[position])
        }

    }
}