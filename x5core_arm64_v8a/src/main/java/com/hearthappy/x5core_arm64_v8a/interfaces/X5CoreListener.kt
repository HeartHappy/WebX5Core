package com.hearthappy.x5_arm64_v8a.interfaces

interface X5CoreListener {

    fun onCoreInitFinished()

    fun onViewInitFinished(isX5: Boolean)

    fun onInstallFinish(stateCode: Int)
}