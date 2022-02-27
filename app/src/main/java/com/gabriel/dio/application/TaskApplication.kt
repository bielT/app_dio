package com.gabriel.dio.application

import android.app.Application
import com.gabriel.dio.halpers.HelperDB

class TaskApplication : Application() {
    var helperDB : HelperDB? = null
        private set
    companion object {
        lateinit var instance : TaskApplication
    }
    override fun onCreate(){
        super.onCreate()
        instance = this
        helperDB = HelperDB(this)
    }
}