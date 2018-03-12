@file:Suppress("unused")

package gorden.lib.anko

import java.io.File

/**
 * 文件工具类
 */
object KFile {
    fun exist(path: String) = File(path).exists()

    fun isFile(path: String) = File(path).isFile

    fun isDirectory(path: String) = File(path).isDirectory

    fun reName(path: String, name: String) = File(path).renameTo(File(name))

    fun clearDir(path: String, delRoot: Boolean): Boolean {
        val file = File(path)
        if (file.isFile) return file.delete()

        val fileList = file.list() ?: return false

        for (fileName in fileList) {
            clearDir("$path/$fileName", true)
        }

        return !delRoot || file.delete()
    }

    fun clearDir(path: String) = clearDir(path, false)

    fun mkdirs(path: String) = exist(path) || File(path).mkdirs()

    fun delete(path: String) = clearDir(path, true)

    fun getSize(path: String): Long {
        if (!exist(path)) return 0

        var file = File(path)

        if (!file.isDirectory) return file.length()

        val fileList = file.list()
        var size = 0L

        for (filePath in fileList) {
            file = File(path + "/" + filePath)
            size += if (file.isDirectory) getSize(filePath) else file.length()
        }
        return size
    }

}