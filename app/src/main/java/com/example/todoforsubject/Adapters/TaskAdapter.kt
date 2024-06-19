package com.example.todoforsubject.Adapters

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoforsubject.DBhelpers.TaskDataHelperForRV
import com.example.todoforsubject.Model.TaskForRecycleView
import com.example.todoforsubject.R
import com.example.todoforsubject.databinding.TaskInActivityBinding

class TaskAdapter(
    private val context: Context,
    private var taskList: MutableList<TaskForRecycleView>,
    private val clickListener: onItemClickListener
) : RecyclerView.Adapter<TaskAdapter.TaskHolder>() {

    interface onItemClickListener {
        fun onItemClick(task: TaskForRecycleView)
        fun onDeleteTask(task: TaskForRecycleView)
    }

    inner class TaskHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = TaskInActivityBinding.bind(view)

        fun bind(task: TaskForRecycleView) = with(binding) {
            titleTask.text = task.title
            buttonStateTask.setImageResource(task.stateButton)

            itemView.setOnClickListener {
                clickListener.onItemClick(task)
            }

            buttonStateTask.setOnClickListener {
                when (task.stateButton) {
                    R.drawable.ic_state_pending -> {
                        task.stateButton = R.drawable.ic_completed
                    }
                    R.drawable.ic_completed -> {
                        task.stateButton = R.drawable.ic_delete_rv_task
                    }
                    R.drawable.ic_delete_rv_task -> {
                        clickListener.onDeleteTask(task)
                        return@setOnClickListener
                    }
                }
                saveStateToDatabase(task)
                notifyItemChanged(adapterPosition)
            }
        }

        private fun saveStateToDatabase(task: TaskForRecycleView) {
            val dbHelper = TaskDataHelperForRV(context)
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val contentValues = ContentValues().apply {
                put(TaskDataHelperForRV.IMAGE_STATE, task.stateButton)
            }
            db.update(
                TaskDataHelperForRV.TASK_TABLE,
                contentValues,
                "${TaskDataHelperForRV.TASK_NAME} = ?",
                arrayOf(task.title)
            )
            db.close()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_in_activity, parent, false)
        return TaskHolder(view)
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setData(newList: MutableList<TaskForRecycleView>) {
        taskList = newList
        notifyDataSetChanged()
    }
}
