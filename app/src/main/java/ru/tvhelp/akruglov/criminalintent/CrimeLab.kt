package ru.tvhelp.akruglov.criminalintent

import android.content.Context
import java.util.*

class CrimeLab private constructor(context: Context) {

    val crimes = hashMapOf<UUID, Crime>()//mutableListOf<Crime>()

    init {
        for (i in 0..99) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.solved = i % 2 == 0
            crime.requirePolice = i % 2 != 0
            crimes[crime.id] = crime
        }
    }

    operator fun get(id: UUID): Crime? {
        return crimes[id]
    }

    companion object: SingletonHolder<CrimeLab, Context>(::CrimeLab)
}