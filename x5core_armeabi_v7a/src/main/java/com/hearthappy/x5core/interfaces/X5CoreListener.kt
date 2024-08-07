package com.hearthappy.x5core.interfaces

/**
 * 监听
 */
interface X5CoreListener {

    fun onCoreInitFinished()

    fun onViewInitFinished(isX5: Boolean)

    fun onInstallFinish(stateCode: Int)
}