package ru.tvhelp.akruglov.criminalintent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
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
        const val SAVED_SUBTITLE_VISIBLE = "subtitle"
    }

    public interface Callbacks {
        fun onCrimeSelected(crime: Crime, position: Int)
    }

    private var adapter: CrimeAdapter? = null
    private var subtitleVisible = false
    private var callbacks: Callbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        savedInstanceState?.let {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_crime_list, menu)

        val subtitleItem = menu?.findItem(R.id.show_subtitle)
        if (subtitleVisible) {
            subtitleItem?.setTitle(R.string.hide_subtitle)
        } else {
            subtitleItem?.setTitle(R.string.show_subtitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.new_crime -> {
            val crime =  Crime()
            CrimeLab.getInstance(activity as Context).add(crime)
            updateUI(-1)
            callbacks?.onCrimeSelected(crime, -1)
            true
        }
        R.id.show_subtitle -> {
            subtitleVisible = !subtitleVisible
            activity?.invalidateOptionsMenu()
            updateSubtitle()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun updateSubtitle() {
        val crimeCount = CrimeLab.getInstance(activity as Context).getCrimes().size
        var subtitle: String? = resources.getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount)
        if (!subtitleVisible) {
            subtitle = null
        }
        (activity as AppCompatActivity).supportActionBar?.subtitle = subtitle
    }

    fun updateUI(crimePosition: Int = -1) {
        val crimeLab = CrimeLab.getInstance(activity as Context)

        if (adapter == null) {
            adapter = CrimeAdapter(crimeLab.getCrimes())
            crimeRecyclerView.adapter = adapter
        } else {
            (adapter as CrimeAdapter).crimes = CrimeLab.getInstance(activity!!).getCrimes()
            if (crimePosition == -1)
                (adapter as CrimeAdapter).notifyDataSetChanged()
            else
                (adapter as CrimeAdapter).notifyItemChanged(crimePosition)
        }

        updateSubtitle()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CRIME) {
            if (data == null) {
                return
            }

            val crimePosition = CrimePagerActivity.crimePosition(data)
            //updateUI(crimePosition)
            updateUI(-1)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible)
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
            callbacks?.onCrimeSelected(crime, adapterPosition)
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>() {

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