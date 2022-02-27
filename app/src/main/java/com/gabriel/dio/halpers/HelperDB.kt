package com.gabriel.dio.halpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.gabriel.dio.model.Task


class HelperDB(
    context: Context?

) : SQLiteOpenHelper(context, BANK_NAME,null, CURRENT_VERSION) {
    companion object {
        private const val BANK_NAME = "task"
        private const val CURRENT_VERSION = 1
        private const val TABLE_NAME      =   "task"
        private const val COLUMN_ID       =   "id"
        private const val COLUMN_TITLE    =   "title"
        private const val COLUMN_DATE     =   "date"
        private const val COLUMN_HOUR     =   "hour"
        private const val COLUMN_DESCRIPTION= "description"
        private const val DROP_TABLE      =   "DROP TABLE IF EXISTS $TABLE_NAME"
        private const val CREATE_TABLE    =    "CREATE TABLE $TABLE_NAME(" +
                            "$COLUMN_ID       INTEGER PRIMARY KEY," +
                            "$COLUMN_TITLE    TEXT NOT NULL," +
                            "$COLUMN_DATE    TEXT NOT NULL," +
                            "$COLUMN_HOUR     TEXT NOT NULL," +
                            "$COLUMN_DESCRIPTION TEXT)"
    }
    


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun addTask(task: Task){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_ID,task.id)
            put(COLUMN_TITLE,task.title)
            put(COLUMN_DATE ,task.date)
            put(COLUMN_HOUR,task.hour)
            put(COLUMN_DESCRIPTION ,task.description)
        }
        db.insert(TABLE_NAME,null, values)
    }

    fun getTask(id : Int):Task{
        val db      = readableDatabase
        val sql     = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $id;"
        val cursor  = db.rawQuery(sql,null)
        cursor?.moveToFirst()
        val task = populatingTask(cursor)
        cursor.close()
        return task

    }
    private fun populatingTask(cursor : Cursor):Task = Task(
            cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)?:0),
            cursor.getString(cursor.getColumnIndex(COLUMN_HOUR)?:0),
            cursor.getString(cursor.getColumnIndex(COLUMN_DATE)?:0),
            cursor.getInt(cursor.getColumnIndex(COLUMN_ID)?:0),
            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)?:0)?: ""
        )



    fun updateTask(task : Task){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TITLE,task.title)
            put(COLUMN_ID,task.id)
            put(COLUMN_DATE ,task.date)
            put(COLUMN_HOUR,task.hour)
            put(COLUMN_DESCRIPTION ,task.description)
        }
        db.update(TABLE_NAME, values,"$COLUMN_ID = ?",arrayOf(task.id.toString()))
    }
    fun delTask(id: Int){
        val db      = writableDatabase
        db.delete(TABLE_NAME,"$COLUMN_ID = ?" , arrayOf(id.toString()) )
    }
    fun sizeTask(): Int {
        return searchTask("").size


    }
    fun searchTask(search :  String) : ArrayList<Task> {
        val db      = readableDatabase?:return ArrayList()
        val list   = ArrayList<Task>()
        val sql     = "SELECT * FROM $TABLE_NAME WHERE " +
                "$COLUMN_TITLE LIKE '%$search%' OR " +
                "$COLUMN_DATE LIKE '%$search%' OR " +
                "$COLUMN_HOUR LIKE '$search%' OR " +
                "$COLUMN_DESCRIPTION LIKE '%$search%' " +
                "ORDER BY $COLUMN_TITLE;"
        val cursor  = db.rawQuery(sql,null)?:return ArrayList()
        while (cursor.moveToNext()){
            val task  = populatingTask(cursor)
            list.add(task)
        }
        return list
    }


}