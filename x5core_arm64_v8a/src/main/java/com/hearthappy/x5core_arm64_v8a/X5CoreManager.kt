package com.hearthappy.x5_arm64_v8a

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.hearthappy.x5_arm64_v8a.interfaces.X5CoreListener
import com.hearthappy.x5core_arm64_v8a.X5ProcessInitService
import com.hearthappy.x5core_arm64_v8a.utils.IOUtils
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsListener
import com.tencent.smtt.sdk.WebView
import java.io.File

object X5CoreManager {


    fun Context.initX5Arm64_v8a(isDownloadWithoutWifi:Boolean=true,coreMinVersion:Int=QbSdk.CORE_VER_ENABLE_202207,listener: X5CoreListener) {

        /* [new] 独立Web进程演示 */
        if (!startX5WebProcessPreinitService()) {
            return
        }
        loadX5Core(isDownloadWithoutWifi,coreMinVersion,listener)
    }

    /**
     * 启动X5内核
     */
    private fun Context.loadX5Core(isDownloadWithoutWifi:Boolean,coreMinVersion:Int,listener: X5CoreListener) {

        /* 设置允许移动网络下进行内核下载。默认不下载，会导致部分一直用移动网络的用户无法使用x5内核 */
        QbSdk.setDownloadWithoutWifi(isDownloadWithoutWifi)
        QbSdk.setCoreMinVersion(coreMinVersion)


        /* 此过程包括X5内核的下载、预初始化，接入方不需要接管处理x5的初始化流程，希望无感接入 */QbSdk.initX5Environment(this, object : PreInitCallback {
            override fun onCoreInitFinished() { // 内核初始化完成，可能为系统内核，也可能为系统内核
                Log.i(TAG, "onCoreInitFinished:")
                listener.onCoreInitFinished()
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖wifi网络下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * 内核下发请求发起有24小时间隔，卸载重装、调整系统时间24小时后都可重置
             * 调试阶段建议通过 WebView 访问 debugtbs.qq.com -> 安装线上内核 解决
             * @param isX5 是否使用X5内核
             */
            override fun onViewInitFinished(isX5: Boolean) {
                listener.onViewInitFinished(isX5)
                val tbsVersion = QbSdk.getTbsVersion(this@loadX5Core)
                Log.i(TAG, "onViewInitFinished isX5:" + isX5 + ",tbsVersion:" + tbsVersion + ",abi:" + Build.SUPPORTED_ABIS[0])
                if (!isX5 && tbsVersion == 0) checkLocalTbs(listener) // hint: you can use QbSdk.getX5CoreLoadHelp(context) anytime to get help.
            }
        })
    }

    private fun Context.checkLocalTbs(listener: X5CoreListener) {
        val path= applicationContext.cacheDir.absolutePath + "/tbs_core_046285_20240613152541_nolog_fs_obfs_arm64-v8a_release.tbs.apk"
        val file = File(path)
        if(!file.exists()){
            IOUtils.copyAssetsToSDCard(applicationContext, "tbs", cacheDir.absolutePath)
            installLocalTbs(path,listener)
        }else installLocalTbs(path,listener)
    }

    private fun Context.installLocalTbs(path: String,listener: X5CoreListener) {

        //armeabi架构
        //        QbSdk.installLocalTbsCore(getApplicationContext(), 46514, getApplicationContext().getCacheDir().getAbsolutePath() + "/ttbs_core_046514_20230612114949_nolog_fs_obfs_armeabi_release.tbs.apk")

        //arm64-v8a
        QbSdk.installLocalTbsCore(applicationContext, 46285, path)

        /* SDK内核初始化周期回调，包括 下载、安装、加载 */
        QbSdk.setTbsListener(object : TbsListener {
            /**
             * @param stateCode 用户可处理错误码请参考[com.tencent.smtt.sdk.TbsCommonCode]
             */
            override fun onDownloadFinish(stateCode: Int) {
                Log.i(TAG, "onDownloadFinished: $stateCode")
            }

            /**
             * @param stateCode 用户可处理错误码请参考[com.tencent.smtt.sdk.TbsCommonCode]
             */
            override fun onInstallFinish(stateCode: Int) {
                listener.onInstallFinish(stateCode)
                Log.i(TAG, "onInstallFinished: $stateCode") //                            restartApp();
            }

            /**
             * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
             * @param progress 0 - 100
             */
            override fun onDownloadProgress(progress: Int) {
                Log.i(TAG, "Core Downloading: $progress")
            }
        })
    }

    /**
     * 启动X5 独立Web进程的预加载服务。优点：
     * 1、后台启动，用户无感进程切换
     * 2、启动进程服务后，有X5内核时，X5预加载内核
     * 3、Web进程Crash时，不会使得整个应用进程crash掉
     * 4、隔离主进程的内存，降低网页导致的App OOM概率。
     *
     * 缺点：
     * 进程的创建占用手机整体的内存，demo 约为 150 MB
     */
    private fun Context.startX5WebProcessPreinitService(): Boolean {
        val currentProcessName = QbSdk.getCurrentProcessName(this) // 设置多进程数据目录隔离，不设置的话系统内核多个进程使用WebView会crash，X5下可能ANR
        WebView.setDataDirectorySuffix(QbSdk.getCurrentProcessName(this))
        Log.i(TAG, currentProcessName)
        if (currentProcessName == this.packageName) {
            startService(Intent(this, X5ProcessInitService::class.java))
            return true
        }
        return false
    }

    private const val TAG = "X5CoreManager"
}