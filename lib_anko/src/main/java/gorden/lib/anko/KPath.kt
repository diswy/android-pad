@file:Suppress("NOTHING_TO_INLINE")

package gorden.lib.anko

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * document
 * Created by Gordn on 2017/7/11.
 */
object KPath {
    val rootDir: String? = path(Environment.getRootDirectory())
    val dataDir: String? = path(Environment.getDataDirectory())

    val appCacheDir: (Context)->String? = { path(it.cacheDir) }
    val appRootDir:(Context)->String? = { path(it.cacheDir.parentFile) }

    val externalCacheDir:(Context)->String? = {
        var tempPath = path(it.externalCacheDir)
        if (tempPath.isNullOrEmpty()){
            tempPath = appCacheDir(it)
        }
        tempPath
    }

    val externalRootDir:(Context)->String? = {
        var path = path(it.externalCacheDir?.parentFile)
        if (path.isNullOrEmpty()){
            path = appRootDir(it)
        }
        path
    }

    val externalDir = path(Environment.getExternalStorageDirectory())

    private inline fun path(file: File?): String? {
        file?.let {
            if (file.exists()) return file.absolutePath
            if (file.mkdirs()) return file.absolutePath
        }
        return null
    }
}