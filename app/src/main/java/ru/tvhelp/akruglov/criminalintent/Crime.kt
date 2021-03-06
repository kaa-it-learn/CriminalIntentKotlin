package ru.tvhelp.akruglov.criminalintent

import java.util.*

data class Crime(val _id: Long = 0,
                 val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 var date: Date = Date(),
                 var solved: Boolean = false,
                 var requirePolice: Boolean = false,
                 var suspect: String = "") {

    val photoFilename = "IMG_" + id.toString() + ".jpg"
}