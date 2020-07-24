package com.dengdai.file.model

import android.os.Parcel
import android.os.Parcelable

/**
 *
 */
open class FileInfo : Parcelable {
    var fileName: String? = null
    var filePath: String? = null
    var fileSize: Long = 0
    var time: String? = null

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(fileName)
        dest.writeString(filePath)
        dest.writeLong(fileSize)
        dest.writeString(time)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        fileName = `in`.readString()
        filePath = `in`.readString()
        fileSize = `in`.readLong()
        time = `in`.readString()
    }

    companion object {
        val CREATOR: Parcelable.Creator<FileInfo?> = object : Parcelable.Creator<FileInfo?> {
            override fun createFromParcel(source: Parcel): FileInfo? {
                return FileInfo(source)
            }

            override fun newArray(size: Int): Array<FileInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}