package ru.tvhelp.akruglov.criminalintent.data.db

import android.content.ContentValues
import android.content.Context
import ru.tvhelp.akruglov.criminalintent.Crime
import ru.tvhelp.akruglov.criminalintent.extensions.*
import org.jetbrains.anko.db.*
import java.util.*
import kotlin.collections.HashMap


class CrimeDb(private val context: Context,
              private val crimeDbHelper: CrimeDbHelper = CrimeDbHelper.instance(context.applicationContext),
              private val dataMapper: DbDataMapper = DbDataMapper()) {

    val crimeRequest = "${CrimeTable.UUID} = ?"

    fun addCrime(c: Crime) = crimeDbHelper.use {
        with(dataMapper.convertFromDomain(c)) {
            insert(CrimeTable.NAME, *map.toVarargArray())
        }
    }

    fun updateCrime(c: Crime) = crimeDbHelper.use {
        with(dataMapper.convertFromDomain(c)) {
            update(CrimeTable.NAME, *map.toVarargArray())
                    .whereSimple(crimeRequest, uuid).exec()
        }
    }

    fun requestCrimeByUUID(uuid: UUID) = crimeDbHelper.use {
        val crime = select(CrimeTable.NAME)
                .whereSimple(crimeRequest, uuid.toString())
                .parseOpt { CrimeData(HashMap(it))}

        crime?.let { dataMapper.convertToDomain(it)}
    }

    fun requestCrimes() = crimeDbHelper.use {
        val crimes = select(CrimeTable.NAME).parseList { CrimeData(HashMap(it)) }

        crimes?.let { it.map { dataMapper.convertToDomain(it)}}
    }
}