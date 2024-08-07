package com.hearthappy.webx5core

import android.app.Application
import android.util.Log
import com.hearthappy.x5core.X5CoreManager
import com.hearthappy.x5core.interfaces.X5CoreListener

class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        
        X5CoreManager.initX5Core(baseContext,listener = object : X5CoreListener {
            override fun onCoreInitFinished() {
                Log.d(TAG, "onCoreInitFinished: ")
            }

            override fun onViewInitFinished(isX5: Boolean) {
                Log.d(TAG, "onViewInitFinished: ")
            }

            override fun onInstallFinish(stateCode: Int) {
                Log.d(TAG, "onInstallFinish: $stateCode")
            }
        })
    }
    companion object{
        private const val TAG = "MyApp"
    }
}