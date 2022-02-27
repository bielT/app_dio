package com.gabriel.dio.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gabriel.dio.adapter.TaskListAdapter
import com.gabriel.dio.databinding.ActivityMainBinding
import com.gabriel.dio.model.Task
import com.gabriel.dio.application.TaskApplication



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private  val adapter by lazy { TaskListAdapter() }
    private  val db =TaskApplication.instance.helperDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTask.adapter = adapter
        onClickSearch()
        insertListeners()

    }

    override fun onResume() {
        onClickSearch()
        super.onResume()
    }

    private fun insertListeners() {
        binding.inTask.ivSearch.setOnClickListener {
            binding.inTask.toolbar.visibility =  View.GONE
            binding.inSearch.toolbar.visibility = View.VISIBLE
        }
        fun setVisibilityTask(){
            binding.inTask.toolbar.visibility =  View.VISIBLE
            binding.inSearch.toolbar.visibility = View.GONE
        }

        binding.inSearch.ivNav.setOnClickListener {
            setVisibilityTask()
        }
        binding.inSearch.ivSearch.setOnClickListener {
            setVisibilityTask()
            onClickSearch()
        }

        binding.btNewTask.setOnClickListener {
            val intent = Intent(this, AddTAsksActivity::class.java)
            startActivity(intent)
        }

        adapter.listenerEdit = {
            val intent = Intent(this, AddTAsksActivity::class.java)
                intent.putExtra(AddTAsksActivity.TASK_ID , it.id)
                intent.putExtra( "mode","Edit")
                startActivity(intent)
                onClickSearch()
        }
        adapter.listenerDelete = {
            db?.delTask(it.id)
            onClickSearch()
        }


    }
    private fun updateList(list: ArrayList<Task>){

        binding.inEmpty.emptyState.visibility =if(list.isEmpty())   View.VISIBLE
        else View.GONE
        adapter.submitList(list)
    }
    private fun searchTask():ArrayList<Task>{
        val search  = binding.inSearch.edSearch.text.toString()
        var filtering = ArrayList<Task>()
        try {
            filtering = db?.searchTask(search)?: ArrayList()
        }catch (ex: Exception){
            ex.printStackTrace()
        }
        return filtering

    }
    private fun onClickSearch(){
        val filtering= searchTask()
        binding.rvTask.adapter = adapter
        updateList(filtering)
    }

}


