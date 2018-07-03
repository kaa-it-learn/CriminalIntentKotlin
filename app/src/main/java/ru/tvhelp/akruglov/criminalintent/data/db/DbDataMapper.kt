package ru.tvhelp.akruglov.criminalintent.data.db

import ru.tvhelp.akruglov.criminalintent.Crime
import java.util.*

class DbDataMapper {

    fun convertFromDomain(crime: Crime) = with(crime) {
        CrimeData(id.toString(), title, date.time, if (solved) 1 else 0, if (requirePolice) 1 else 0)
    }

    fun convertToDomain(crime: CrimeData) = with(crime) {
        Crime(_id, UUID.fromString(uuid), title, Date(date), solved == 1, requirePolice == 1)
    }
}