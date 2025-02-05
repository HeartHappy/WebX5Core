#### 离线加载腾讯X5内核。（根据不同设备ABI架构拆分的离线SDK。主要解决腾讯X5服务线上拉取内核受限问题）


##### 一、Gradle集成（本框架暂时分2个部分，后续将armeabi架构和所有架构做出来。根据各自需求，选取自己对应的sdk集成使用）

###### 1、armeabi_v7a架构

```apl
implementation 'com.github.HeartHappy.webX5Core:webx5core_armeabi_v7a:1.0.2'
```

###### 2、arm64_v8a架构

```apl
implementation 'com.github.HeartHappy.webX5Core:webx5core_arm64_v8a:1.0.2'
```

##### 二、代码调用

```kotlin
//在自定义的Application中初始化
X5CoreManager.initX5Core(baseContext,listener = object : X5CoreListener {
            override fun onCoreInitFinished() {
                Log.d(TAG, "onCoreInitFinished: ")
            }

            override fun onViewInitFinished(isX5: Boolean) {
                Log.d(TAG, "onViewInitFinished: ")
            }

            override fun onInstallFinish(stateCode: Int) {
                Log.d(TAG, "onInstallFinish: $stateCode")
               //stateCode返回200则安装成功,需要重启app生效
            }
        })
```

