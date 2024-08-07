package com.hearthappy.webx5core

import android.app.Application
import android.content.Intent
import android.os.Process
import android.util.Log
import android.widget.Toast
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
                restartApp()
            }
        })

    }

    private fun restartApp() {
        Toast.makeText(applicationContext, "X5内核安装成功，正在重启生效！", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 清除当前的Activity栈
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

        // 结束当前应用程序
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
    companion object{
        private const val TAG = "MyApp"


    }
}