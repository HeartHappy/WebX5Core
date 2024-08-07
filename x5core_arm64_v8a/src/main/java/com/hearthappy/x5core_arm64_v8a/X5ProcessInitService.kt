package com.hearthappy.x5core_arm64_v8a

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback

class X5ProcessInitService : Service() {
    override fun onCreate() {/* 只进行本地内核的预加载、不做版本检测及内核下载 */
        QbSdk.preInit(this.applicationContext, object : PreInitCallback {
            override fun onCoreInitFinished() {}
            override fun onViewInitFinished(b: Boolean) {
                Log.i(TAG, "init web process x5: $b")
            }
        })
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "Service OnBind")
        return null
    }

    companion object {
        private const val TAG = "X5ProcessInitService"
    }
}
