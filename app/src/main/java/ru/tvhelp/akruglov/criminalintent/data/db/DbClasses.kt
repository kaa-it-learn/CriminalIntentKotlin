package ru.tvhelp.akruglov.criminalintent.data.db

import kotlin.collections.HashMap

class CrimeData(val map: MutableMap<String, Any?>) {

    var _id: Long by map
    var uuid: String by map
    var title: String by map
    var date: Long by map
    var solved: Int by map
    var requirePolice: Int by map

    constructor(uuid: String, title: String, date: Long, solved: Int, requirePolice: Int)
        : this(HashMap()) {
        this.uuid = uuid
        this.title = title
        this.date = date
        this.solved = solved
        this.requirePolice = requirePolice
    }
}