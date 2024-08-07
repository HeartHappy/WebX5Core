package com.hearthappy.x5core.interfaces

interface X5CoreListener {

    fun onCoreInitFinished()

    fun onViewInitFinished(isX5: Boolean)

    fun onInstallFinish(stateCode: Int)
}