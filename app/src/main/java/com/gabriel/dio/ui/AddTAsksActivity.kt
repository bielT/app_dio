package com.gabriel.dio.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gabriel.dio.R
import com.gabriel.dio.application.TaskApplication
import com.gabriel.dio.databinding.ActivityAddTaskBinding
import com.gabriel.dio.extensions.format
import com.gabriel.dio.extensions.text
import com.gabriel.dio.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class AddTAsksActivity : AppCompatActivity()  {

    private lateinit var binding : ActivityAddTaskBinding
    private val db = TaskApplication.instance.helperDB
    private var idTask: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.getStringExtra("mode")=="Edit"){
            idTask =  intent.getIntExtra(TASK_ID, 0)
            val taskId = idTask
            val task = db?.getTask(taskId)
            binding.toolbar.title  ="Editar Tarefa"
            binding

            binding.tilTitle.text = task?.title?:""
            binding.tilData.text =  task?.date?:""
            binding.tilHour.text =  task?.hour?:""
            binding.tilDescription.text =  task?.description?:""

        }

        insertListeners()

    }

    private fun insertListeners() {
        binding.tilData.editText?. setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
               val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time)*-1
                binding.tilData.text =  Date(it + offset).format()

            }
            datePicker.show(supportFragmentManager,"DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().
                setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener{
                val minute = if(timePicker.minute in 0..9)"0${timePicker.minute}" else timePicker.minute

                val hour = if(timePicker.hour in 0..9)"0${timePicker.hour}" else timePicker.hour

                binding.tilHour.text = "${hour}:${minute}"
            }

            timePicker.show(supportFragmentManager, null)
        }

        binding.btCancel.setOnClickListener{
            this.finish()
        }
        binding.toolbar.setNavigationOnClickListener{
            this.finish()
        }
        binding.btNewTask.setOnClickListener{
            val mode = intent.getStringExtra("mode")
            val task =                Task(
                    title = binding.tilTitle.text ,
                    date = binding.tilData.text,
                    hour = binding.tilHour.text,
                    description = binding.tilDescription.text,
                    id  =   if(mode =="Edit") idTask else db?.sizeTask()?:0
                )

            if(task.title!="" && task.date !="" && task.date!="" ){
                if(intent.getStringExtra("mode")=="Edit")
                    task.let { it1 -> TaskApplication.instance.helperDB?.updateTask(it1) }
                else
                    task.let { it1 -> TaskApplication.instance.helperDB?.addTask(it1) }
                this.finish()


            }else {
                val text = getString(R.string.Warning)
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
        }
    }
    companion object {
        const val TASK_ID = "task_id"
    }


}