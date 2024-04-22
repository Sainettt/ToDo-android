package com.example.todoforsubject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoforsubject.databinding.ActivityMainBinding
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val taskList = mutableListOf<Task>()
    private val adapter = TaskAdapter(taskList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter

            addTaskButton.setOnClickListener{
                Toast.makeText(this@MainActivity, "HAHAJDADHAJSDHJADH", Toast.LENGTH_SHORT).show()
                adapter.addTask(Task("Play dota", "NOW", R.drawable.ic_state_pending ))
            }
        }
    }
}