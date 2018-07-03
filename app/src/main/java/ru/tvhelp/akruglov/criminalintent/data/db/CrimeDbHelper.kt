package ru.tvhelp.akruglov.criminalintent.data.db

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class CrimeDbHelper(private val context: Context): ManagedSQLiteOpenHelper(context,
        CrimeDbHelper.DB_NAME, null, CrimeDbHelper.DB_VERSION) {

    companion object {
        const val DB_NAME = "crime.db"
        const val DB_VERSION = 1

        fun instance(context: Context) = CrimeDbHelper(context)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(CrimeTable.NAME, true,
                CrimeTable.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                CrimeTable.UUID to TEXT,
                CrimeTable.TITLE to TEXT,
                CrimeTable.DATE to INTEGER,
                CrimeTable.SOLVED to INTEGER,
                CrimeTable.REQUIRE_POLICE to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(CrimeTable.NAME, true)
        onCreate(db)
    }
}