package ru.tvhelp.akruglov.criminalintent

import android.content.Context
import ru.tvhelp.akruglov.criminalintent.data.db.CrimeDb
import java.io.File
import java.util.*

class CrimeLab private constructor(context: Context) {

    private val database = CrimeDb(context.applicationContext)
    private val context = context.applicationContext

    fun getCrimes() = database.requestCrimes()


    fun add(crime: Crime) {
        database.addCrime(crime)
    }

    fun update(crime: Crime) {
        database.updateCrime(crime)
    }

    operator fun get(id: UUID): Crime? {
        return database.requestCrimeByUUID(id)
    }

    fun getPhotoFile(crime: Crime) = File(context.filesDir, crime.photoFilename)

    companion object: SingletonHolder<CrimeLab, Context>(::CrimeLab)
}