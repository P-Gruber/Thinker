package org.thinker.thinker

import android.app.Application
import android.content.Context

class MyApplication : Application()
{
    init
    {
        instance = this
    }

    companion object
    {

        private lateinit var instance: MyApplication
        fun applicationContext(): Context
        {
            return instance.applicationContext
        }
    }
}
