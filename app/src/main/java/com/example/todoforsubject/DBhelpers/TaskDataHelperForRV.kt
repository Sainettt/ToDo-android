package com.example.todoforsubject.DBhelpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDataHelperForRV (context: Context): SQLiteOpenHelper(context, TASK_TABLE, null, DATA_BASE_VERSION) {
    companion object {
        const val TASK_TABLE = "task_for_rv_data_base"
        const val DATA_BASE_VERSION = 1
        const val ID = "_id"
        const val TASK_NAME = "task_name"
        const val IMAGE_STATE = "image_state"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
        CREATE TABLE IF NOT EXISTS $TASK_TABLE (
            $ID INTEGER PRIMARY KEY,
            $TASK_NAME TEXT NOT NULL,
            $IMAGE_STATE INTEGER NOT NULL
        )
    """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS task_for_rv_data_base")
        onCreate(db)
    }
    fun clearTable(tableName: String) {
        val db = this.writableDatabase
        db.delete(tableName, null, null)
        db.close()
    }
}
