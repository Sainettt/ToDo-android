package com.example.todoforsubject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoforsubject.databinding.TaskInActivityBinding
import java.util.zip.Inflater

class TaskAdapter(var taskList:MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskHolder>() {

// holder - этот класс описывает логику сохранения данных которые мы будем заполнять в onBindViewHolder
    class TaskHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = TaskInActivityBinding.bind(view)
        fun bind(task: Task) = with(binding) {
            titleTask.text = task.title
            buttonStateTask.setImageResource(R.drawable.ic_state_pending)
        }
    }
// этот метод создает в памяти и возвращает элемент RecycleView для onBindViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_in_activity, parent, false)
        return TaskHolder(view)
    }
// этот метод уже конкретно заполняет данными 1 элемент функция bind принадлежит классу TaskHolder
    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
    fun addTask(task:Task){
        taskList.add(task)
        notifyDataSetChanged()
    }
}