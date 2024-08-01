package com.hearthappy.webx5core

import android.app.Application
import android.util.Log
import com.hearthappy.x5_arm64_v8a.X5CoreManager.initX5Arm64_v8a
import com.hearthappy.x5_arm64_v8a.interfaces.X5CoreListener

class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        
        baseContext.initX5Arm64_v8a(listener = object : X5CoreListener {
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