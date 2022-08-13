package com.simonediberardino.pokmoniquii.sqlite

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import com.simonediberardino.pokmoniquii.R
import android.content.ContentValues
import android.database.Cursor
import com.simonediberardino.pokmoniquii.entities.Pokemon


class DbHandler(activity: AppCompatActivity) : SQLiteOpenHelper(
    activity,
    "pokemon",
    null,
    1
) {
    private lateinit var sqldb: SQLiteDatabase
    private val TABLE_NAME = "savedpokemons"
    private val ID_COL = "id"
    private val POK_COL = "pokref"

    override fun onCreate(db: SQLiteDatabase?) {
        sqldb = db ?: return

        val query = "CREATE TABLE $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$POK_COL INTEGER UNIQUE NOT NULL)"

        sqldb.execSQL(query)
    }

    fun addNewPokemon(pokemon: Pokemon) {
        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        val db = this.writableDatabase

        // on below line we are creating a
        // variable for content values.
        val values = ContentValues()

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(POK_COL, pokemon.id)

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values)

        // at last we are closing our
        // database after adding database.
        db.close()
    }

    fun deletePokemon(pokemon: Pokemon) {
        val db = this.writableDatabase

        db.delete(TABLE_NAME, "$POK_COL=?", arrayOf(pokemon.id.toString()))

        db.close()
    }

    fun isPokemonFavorite(pokemon: Pokemon): Boolean {
        return checkIsDataAlreadyInDBorNot(POK_COL, pokemon.id.toString())
    }

    fun checkIsDataAlreadyInDBorNot(
        dbfield: String,
        fieldValue: String
    ): Boolean {
        val db = this.writableDatabase

        val query = "SELECT * FROM $TABLE_NAME where $dbfield = $fieldValue"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.count <= 0) {
            cursor.close()
            return false
        }

        cursor.close()
        return true
    }

    fun getSavedPokemonIds(): MutableList<String> {
        // database for reading our database.
        val db = this.readableDatabase

        // on below line we are creating a cursor with query to read data from database.
        val cursorCourses = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        // on below line we are creating a new array list.
        val resultList = mutableListOf<String>()
        // moving our cursor to first position.

        if (cursorCourses.moveToFirst()) {
            do {
                resultList.add(cursorCourses.getString(1))
            } while (cursorCourses.moveToNext())
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close()
        return resultList
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME");
        onCreate(db)
    }
}

/*
    fun getSavedPokemonIds(): MutableList<String> {
        // database for reading our database.
        val db = this.readableDatabase

        // on below line we are creating a cursor with query to read data from database.
        val cursorCourses = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        // on below line we are creating a new array list.
        val resultList = mutableListOf<String>()
        // moving our cursor to first position.

        if (cursorCourses.moveToFirst()) {
            do {
                resultList.add(cursorCourses.getString(1))
            } while (cursorCourses.moveToNext())
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close()
        return resultList
    }
 */