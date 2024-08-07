package com.hearthappy.x5core_arm64_v8a.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * IO工具
 */
object  IOTools{
    fun copyAssetsToSDCard(context: Context, sourceFolder: String, destinationFolder: String?) {
        val assetManager = context.assets
        val files: Array<String>? = try { // 获取assets文件夹下的所有文件和子文件夹
            assetManager.list(sourceFolder)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        // 创建目标文件夹
        val destFolder = File(destinationFolder)
        if (!destFolder.exists()) {
            destFolder.mkdirs()
        }
        for (filename in files!!) {
            var `in`: InputStream? = null
            var out: OutputStream? = null
            try { // 从assets中打开文件
                `in` = assetManager.open("$sourceFolder/$filename") // 指定输出目标文件
                val outFile = File(destinationFolder, filename)
                out = FileOutputStream(outFile) // 将文件内容复制到目标文件
                Log.i("Qbsdk", "copy 开始")
                copyFile(`in`, out)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `in`?.close()
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    @Throws(IOException::class) private fun copyFile(`in`: InputStream, out: OutputStream) {
        try {
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
