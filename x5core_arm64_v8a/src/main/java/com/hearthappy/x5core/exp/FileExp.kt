package com.hearthappy.x5core.exp

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * 获取外部缓存目录
 * @receiver Context
 * @return String
 */
fun Context.getCacheDirPath(): String{
    return cacheDir.absolutePath
}

/**
 * 将Assets目录中的文件copy到内部或外部缓存目录
 * @receiver Context
 * @param context Context
 * @param assetFileName String
 * @return File?
 */
fun Context.copyAssetFileToCache(assetsDir:String, assetFileName: String): File? {
    val cacheDir =  cacheDir // 获取外部缓存目录或者内部缓存目录
    val outputFile = File(cacheDir, assetFileName)

    try {
        assets.open(assetsDir.plus(File.separator).plus(assetFileName)).use { inputStream ->
            FileOutputStream(outputFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return outputFile
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

/**
 * 检测文件是否存在
 * @receiver Context
 * @param fileName String
 * @return Boolean
 */
fun Context.checkFileExists(fileName:String): Boolean {
    val cacheDir = externalCacheDir ?: cacheDir // 获取外部缓存目录或者内部缓存目录
    val filePath = cacheDir.absolutePath.plus(File.separator).plus(fileName)//获取文件路径
    return File(filePath).exists()

}