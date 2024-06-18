package com.example.todoforsubject.Activitys

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todoforsubject.DBhelpers.TaskDataHelperForActivity
import com.example.todoforsubject.DBhelpers.TaskDataHelperForRV
import com.example.todoforsubject.Model.TaskForActivity
import com.example.todoforsubject.Model.TaskForRecycleView
import com.example.todoforsubject.R
import com.example.todoforsubject.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private var stateTaskForActivity = R.drawable.ic_create_task

    private lateinit var dbHelperForActivity: TaskDataHelperForActivity
    private lateinit var dbHelperForRV: TaskDataHelperForRV

    private lateinit var dbForActivity: SQLiteDatabase
    private lateinit var dbForRV: SQLiteDatabase

    private lateinit var taskForActivity: TaskForActivity
    private lateinit var taskForRV: TaskForRecycleView

    private var isNewTask = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelperForActivity = TaskDataHelperForActivity(this)
        dbHelperForRV = TaskDataHelperForRV(this)
        dbForActivity = dbHelperForActivity.writableDatabase
        dbForRV = dbHelperForRV.writableDatabase

        binding.saveButton.setImageResource(R.drawable.ic_save_task)
        binding.saveButton.visibility = View.INVISIBLE

        val taskTitle = intent.getStringExtra("taskTitle")
        if (taskTitle != null) {
            isNewTask = false
            loadTaskData(taskTitle)
            binding.saveButton.visibility = View.VISIBLE
        } else {
            binding.saveButton.visibility = View.GONE
            updateTaskStateIcon()
        }

        binding.imageState.setOnClickListener {
            handleStateChange(taskTitle)
        }

        binding.saveButton.setOnClickListener {
            if (checkValidation()) {
                if (isNewTask) {
                    saveNewTask()
                } else {
                    updateTaskData(taskTitle)
                }
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun loadTaskData(taskTitle: String) {
        val cursor: Cursor = dbForActivity.query(
            TaskDataHelperForActivity.TASK_TABLE,
            null,
            "${TaskDataHelperForActivity.TASK_NAME} = ?",
            arrayOf(taskTitle),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val infoIndex = cursor.getColumnIndex(TaskDataHelperForActivity.TASK_INFO)
            val stateIndex = cursor.getColumnIndex(TaskDataHelperForActivity.IMAGE_STATE)

            val info = cursor.getString(infoIndex)
            val state = cursor.getInt(stateIndex)

            taskForActivity = TaskForActivity(taskTitle, info, state)
            binding.taskTitle.setText(taskForActivity.task_title)
            binding.infoOfTask.setText(taskForActivity.infoTask)
            stateTaskForActivity = taskForActivity.stateImage

            updateTaskStateIcon()

            cursor.close()
        }
    }

    private fun updateTaskStateIcon() {
        binding.imageState.setImageResource(stateTaskForActivity)
    }

    private fun handleStateChange(taskTitle: String?) {
        when (stateTaskForActivity) {
            R.drawable.ic_create_task -> {
                if (checkValidation()) {
                    if (taskTitle == null) {
                        stateTaskForActivity = R.drawable.ic_complete_task
                        saveNewTask()
                    } else {
                        updateTaskState(taskTitle, R.drawable.ic_complete_task)
                    }
                }
            }
            R.drawable.ic_complete_task -> {
                stateTaskForActivity = R.drawable.ic_delete_task
                updateTaskState(taskTitle!!, R.drawable.ic_delete_task)
            }
            R.drawable.ic_delete_task -> {
                deleteTask(taskTitle!!)
                finish()
            }
        }
    }

    private fun saveNewTask() {
        taskForActivity = createTaskForActivity()
        taskForRV = createTaskForRV()

        dbForActivity.insert(TaskDataHelperForActivity.TASK_TABLE, null, contentValuesForActivity(taskForActivity))
        dbForRV.insert(TaskDataHelperForRV.TASK_TABLE, null, contentValuesForRecycleView(taskForRV))
    }

    private fun updateTaskState(taskTitle: String, newState: Int) {
        val contentValues = ContentValues().apply {
            put(TaskDataHelperForActivity.IMAGE_STATE, newState)
        }
        dbForActivity.update(
            TaskDataHelperForActivity.TASK_TABLE,
            contentValues,
            "${TaskDataHelperForActivity.TASK_NAME} = ?",
            arrayOf(taskTitle)
        )
        stateTaskForActivity = newState
        updateTaskStateIcon()
        setResult(RESULT_OK)
    }

    private fun deleteTask(taskTitle: String) {
        dbForActivity.delete(
            TaskDataHelperForActivity.TASK_TABLE,
            "${TaskDataHelperForActivity.TASK_NAME} = ?",
            arrayOf(taskTitle)
        )
        dbForRV.delete(
            TaskDataHelperForRV.TASK_TABLE,
            "${TaskDataHelperForRV.TASK_NAME} = ?",
            arrayOf(taskTitle)
        )
        setResult(RESULT_OK)
    }

    private fun updateTaskData(taskTitle: String?) {
        taskForActivity = createTaskForActivity()
        taskForRV = createTaskForRV()

        val contentValuesActivity = contentValuesForActivity(taskForActivity)
        val contentValuesRV = contentValuesForRecycleView(taskForRV)
        dbForActivity.update(
            TaskDataHelperForActivity.TASK_TABLE,
            contentValuesActivity,
            "${TaskDataHelperForActivity.TASK_NAME} = ?",
            arrayOf(taskTitle)
        )
        dbForRV.update(
            TaskDataHelperForRV.TASK_TABLE,
            contentValuesRV,
            "${TaskDataHelperForRV.TASK_NAME} = ?",
            arrayOf(taskTitle)
        )
    }

    private fun contentValuesForActivity(task: TaskForActivity): ContentValues {
        return ContentValues().apply {
            put(TaskDataHelperForActivity.TASK_NAME, task.task_title)
            put(TaskDataHelperForActivity.TASK_INFO, task.infoTask)
            put(TaskDataHelperForActivity.IMAGE_STATE, stateTaskForActivity)
        }
    }

    private fun contentValuesForRecycleView(task: TaskForRecycleView): ContentValues {
        return ContentValues().apply {
            put(TaskDataHelperForRV.TASK_NAME, task.title)
            put(TaskDataHelperForRV.IMAGE_STATE, task.stateButton)
        }
    }

    private fun createTaskForActivity(): TaskForActivity {
        return TaskForActivity(
            task_title = binding.taskTitle.text.toString(),
            infoTask = binding.infoOfTask.text.toString(),
            stateImage = stateTaskForActivity
        )
    }

    private fun createTaskForRV(): TaskForRecycleView {
        return TaskForRecycleView(
            title = binding.taskTitle.text.toString(),
            stateButton = R.drawable.ic_state_pending
        )
    }

    private fun checkValidation(): Boolean {
        val titleText = binding.taskTitle.text.toString()
        val infoText = binding.infoOfTask.text.toString()
        if (titleText.isEmpty() || infoText.isEmpty()) {
            Toast.makeText(this, "Invalid info", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
