package com.dengdai.file.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.storage.StorageManager
import com.dengdai.file.R
import com.dengdai.file.model.FileInfo
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 */
object FileUtil {
    /****
     * 计算文件大小
     *
     * @param length
     * @return
     */
    fun getFileSzie(length: Long): String {
        return if (length >= 1048576) {
            (length / 1048576).toString() + "MB"
        } else if (length >= 1024) {
            (length / 1024).toString() + "KB"
        } else if (length < 1024) {
            length.toString() + "B"
        } else {
            "0KB"
        }
    }

    fun FormetFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        return when {
            fileS < 1024 -> {
                df.format(fileS.toDouble()) + "B"
            }
            fileS < 1048576 -> {
                df.format(fileS.toDouble() / 1024) + "KB"
            }
            fileS < 1073741824 -> {
                df.format(fileS.toDouble() / 1048576) + "MB"
            }
            else -> {
                df.format(fileS.toDouble() / 1073741824) + "GB"
            }
        }
    }

    /**
     * 字符串时间戳转时间格式
     *
     */
    fun getStrTime(timeStamp: String): String? {
        lateinit var timeString: String
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy年MM月dd日 hh:mm")
        val l = timeStamp.toLong() * 1000
        timeString = sdf.format(Date(l))
        return timeString
    }

    /**
     * 读取文件的最后修改时间的方法
     */
    fun getFileLastModifiedTime(f: File): String {
        val cal = Calendar.getInstance()
        val time = f.lastModified()
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        cal.timeInMillis = time
        return formatter.format(cal.time)
    }

    /**
     * 获取扩展内存的路径
     *
     */
    fun getStoragePath(mContext: Context): String? {
        val mStorageManager = mContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        lateinit var storageVolumeClazz: Class<*>
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
            val getPath = storageVolumeClazz.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")
            val result = getVolumeList.invoke(mStorageManager)
            val length = java.lang.reflect.Array.getLength(result)
            for (i in 0 until length) {
                val storageVolumeElement = java.lang.reflect.Array.get(result, i)
                val path = getPath.invoke(storageVolumeElement) as String
                val removable = isRemovable.invoke(storageVolumeElement) as Boolean
                if (removable) {
                    return path
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }

    fun getFileTypeImageId(fileName: String?): Int {
        return when {
            checkSuffix(fileName, arrayOf("doc", "docx")) -> {
                R.mipmap.word
            }
            checkSuffix(fileName, arrayOf("ppt", "pptx")) -> {
                R.mipmap.ppt
            }
            checkSuffix(fileName, arrayOf("xls", "xlsx")) -> {
                R.mipmap.xls
            }
            checkSuffix(fileName, arrayOf("pdf")) -> {
                R.mipmap.pdf
            }
            checkSuffix(fileName, arrayOf("txt")) -> {
                R.mipmap.word
            }
            else -> R.mipmap.image
        }
    }

    fun checkSuffix(fileName: String?,
                    fileSuffix: Array<String>): Boolean {
        for (suffix in fileSuffix) {
            if (fileName != null) {
                if (fileName.toLowerCase(Locale.ROOT).endsWith(suffix)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 文件过滤,将手机中隐藏的文件给过滤掉
     */
    fun fileFilter(file: File): Array<File> {
        return file.listFiles { pathname -> !pathname.isHidden }
    }

    private fun FilesInfo(fileDir: File, mContext: Context): List<FileInfo> {
        val videoFilesInfo: MutableList<FileInfo> = ArrayList()
        val listFiles = fileFilter(fileDir)
        for (file in listFiles) {
            if (file.isDirectory) {
                FilesInfo(file, mContext)
            } else {
                val fileInfo = getFileInfoFromFile(file)
                videoFilesInfo.add(fileInfo)
            }
        }
        return videoFilesInfo
    }

    fun getFileInfosFromFileArray(files: Array<File>): List<FileInfo> {
        val fileInfos: MutableList<FileInfo> = ArrayList()
        for (file in files) {
            val fileInfo = getFileInfoFromFile(file)
            fileInfos.add(fileInfo)
        }
        Collections.sort(fileInfos, FileNameComparator())
        return fileInfos
    }

    //
    fun getFileInfoFromFile(file: File): FileInfo {
        val fileInfo = FileInfo()
        fileInfo.fileName = file.name
        fileInfo.filePath = file.path
        fileInfo.fileSize = file.length()
        fileInfo.time = getFileLastModifiedTime(file)
        val lastDotIndex = file.name.lastIndexOf(".")
        if (lastDotIndex > 0) {
            val fileSuffix = file.name.substring(lastDotIndex + 1)
        }
        return fileInfo
    }

    /**
     * 根据文件名进行比较排序
     */
    open class FileNameComparator : Comparator<FileInfo> {
        override fun compare(lhs: FileInfo, rhs: FileInfo): Int {
            return lhs.fileName!!.compareTo(rhs.fileName!!, ignoreCase = true)
        }

        companion object {
            protected const val FIRST = -1
            protected const val SECOND = 1
        }
    }
}