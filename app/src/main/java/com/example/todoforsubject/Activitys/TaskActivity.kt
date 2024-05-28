package com.example.todoforsubject.Activitys

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todoforsubject.DBhelpers.TaskDataHelperForActivity
import com.example.todoforsubject.Model.TaskForActivity
import com.example.todoforsubject.R
import com.example.todoforsubject.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private var stateTask = 0

    private lateinit var dbHelperForActivity: TaskDataHelperForActivity
    private lateinit var db: SQLiteDatabase
    private lateinit var taskForActivity: TaskForActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelperForActivity = TaskDataHelperForActivity(this)
        db = dbHelperForActivity.writableDatabase

        val taskTitle = intent.getStringExtra("taskTitle")

        if (taskTitle != null) {
            loadTaskData(taskTitle)
        }

        // Initialize icon based on stateTask
        updateTaskStateIcon()

        binding.imageState.setOnClickListener {
            handleStateChange()
        }
    }

    private fun loadTaskData(taskTitle: String) {
        val cursor: Cursor = db.query(
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
            stateTask = taskForActivity.stateImage

            cursor.close()
        }
    }

    private fun updateTaskStateIcon() {
        when (stateTask) {
            0 -> binding.imageState.setImageResource(R.drawable.ic_create_task)
            1 -> {
                binding.imageState.setImageResource(R.drawable.ic_complete_task)
                binding.deleteBt.setImageResource(R.drawable.ic_delete_task)
            }
            2 -> binding.imageState.setImageResource(R.drawable.ic_delete_task)
        }
    }

    private fun handleStateChange() {
        when (stateTask) {
            0 -> {
                if (checkValidation()) {
                    taskForActivity = createTaskForActivity()
                    stateTask = 1
                    db.insert(TaskDataHelperForActivity.TASK_TABLE, null, contentValuesForActivity(taskForActivity))
                    finish()
                }
            }
            1 -> {
                taskForActivity.stateImage = R.drawable.ic_delete_task
                binding.imageState.setImageResource(taskForActivity.stateImage)
                stateTask = 2
                finish()
            }
            2 -> {
                // Handle deletion if necessary
            }
        }
    }

    private fun contentValuesForActivity(task: TaskForActivity): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(TaskDataHelperForActivity.TASK_NAME, task.task_title)
        contentValues.put(TaskDataHelperForActivity.TASK_INFO, task.infoTask)
        contentValues.put(TaskDataHelperForActivity.IMAGE_STATE, task.stateImage)
        return contentValues
    }

    private fun createTaskForActivity(): TaskForActivity {
        return TaskForActivity(
            task_title = binding.taskTitle.text.toString(),
            infoTask = binding.infoOfTask.text.toString(),
            stateImage = R.drawable.ic_create_task
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
