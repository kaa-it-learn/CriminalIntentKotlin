package ru.tvhelp.akruglov.criminalintent.data.db

import android.content.ContentValues
import android.content.Context
import ru.tvhelp.akruglov.criminalintent.Crime
import ru.tvhelp.akruglov.criminalintent.extensions.*
import org.jetbrains.anko.db.*
import java.util.*
import kotlin.collections.HashMap


fun Array<out Pair<String, Any?>>.toContentValues(): ContentValues {
    val values = ContentValues()
    for ((key, value) in this) {
        when(value) {
            null -> values.putNull(key)
            is Boolean -> values.put(key, value)
            is Byte -> values.put(key, value)
            is ByteArray -> values.put(key, value)
            is Double -> values.put(key, value)
            is Float -> values.put(key, value)
            is Int -> values.put(key, value)
            is Long -> values.put(key, value)
            is Short -> values.put(key, value)
            is String -> values.put(key, value)
            else -> throw IllegalArgumentException("Non-supported value type: ${value.javaClass.name}")
        }
    }
    return values
}

class CrimeDb(private val context: Context,
              private val crimeDbHelper: CrimeDbHelper = CrimeDbHelper.instance(context.applicationContext),
              private val dataMapper: DbDataMapper = DbDataMapper()) {

    val crimeRequest = "${CrimeTable.UUID} = ?"

    fun addCrime(c: Crime) = crimeDbHelper.use {
        with(dataMapper.convertFromDomain(c)) {
            insertOrThrow(CrimeTable.NAME, null, map.toVarargArray().toContentValues())
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