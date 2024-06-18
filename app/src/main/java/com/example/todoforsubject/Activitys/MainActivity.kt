package com.example.todoforsubject.Activitys

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoforsubject.Adapters.TaskAdapter
import com.example.todoforsubject.DBhelpers.TaskDataHelperForActivity
import com.example.todoforsubject.Model.TaskForRecycleView
import com.example.todoforsubject.databinding.ActivityMainBinding
import com.example.todoforsubject.DBhelpers.TaskDataHelperForRV

class MainActivity : AppCompatActivity(), TaskAdapter.onItemClickListener {

    lateinit var binding: ActivityMainBinding

    val taskList = mutableListOf<TaskForRecycleView>()
    lateinit var adapter: TaskAdapter

    lateinit var dbDataHelperForRV: TaskDataHelperForRV
    lateinit var dbDataHelperForActivity: TaskDataHelperForActivity
    lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        adapter = TaskAdapter(this, taskList, this)

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter

            addTaskButton.setOnClickListener {
                val intent = Intent(this@MainActivity, TaskActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_ADD_TASK)
            }
        }
    }

    private fun updateDataFromDataBase() {
        dbDataHelperForRV = TaskDataHelperForRV(this)
        db = dbDataHelperForRV.writableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${TaskDataHelperForRV.TASK_TABLE}", null)

        if (cursor.moveToFirst()) {
            taskList.clear()
            do {
                val titleIndex = cursor.getColumnIndex(TaskDataHelperForRV.TASK_NAME)
                val stateButtonIndex = cursor.getColumnIndex(TaskDataHelperForRV.IMAGE_STATE)

                val title = cursor.getString(titleIndex)
                val stateButton = cursor.getInt(stateButtonIndex)

                val task = TaskForRecycleView(title, stateButton)
                taskList.add(task)
            } while (cursor.moveToNext())
        } else {
            taskList.clear() // Добавляем очистку taskList если курсор пуст
        }
        cursor.close()
        db.close()
    }

    override fun onStart() {
        super.onStart()
        updateDataFromDataBase()
        adapter.setData(taskList)
    }

    override fun onItemClick(task: TaskForRecycleView) {
        val intent = Intent(this, TaskActivity::class.java)
        intent.putExtra("taskTitle", task.title)
        startActivity(intent)
    }

    override fun onDeleteTask(task: TaskForRecycleView) {
        val dbDataHelperForRV = TaskDataHelperForRV(this)
        val db = dbDataHelperForRV.writableDatabase
        db.delete(
            TaskDataHelperForRV.TASK_TABLE,
            "${TaskDataHelperForRV.TASK_NAME} = ?",
            arrayOf(task.title)
        )
        db.close()
        updateDataFromDataBase()
        adapter.setData(taskList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK) {
            updateDataFromDataBase()
            adapter.setData(taskList)
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_TASK = 1
    }
}
