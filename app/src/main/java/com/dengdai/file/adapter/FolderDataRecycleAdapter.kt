package com.dengdai.file.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dengdai.file.R
import com.dengdai.file.model.FileInfo
import com.dengdai.file.utils.FileUtil

/**
 * 使用遍历文件夹的方式
 */
class FolderDataRecycleAdapter(private val mContext: Context?, private val data: List<FileInfo>?, isPhoto: Boolean) : RecyclerView.Adapter<FolderDataRecycleAdapter.ViewHolder>() {
    private var isPhoto = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_folder_data_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_content.text = data!![position].fileName
        holder.tv_size.text = FileUtil.FormetFileSize(data[position].fileSize)
        holder.tv_time.text = data[position].time

        //封面图
        if (isPhoto) {
            Glide.with(mContext!!).load(data[position].filePath).into(holder.iv_cover)
        } else {
            val requestOptions = RequestOptions()
                    .fitCenter()
            Glide.with(mContext!!).load(FileUtil.getFileTypeImageId( data[position].filePath)).apply(requestOptions).into(holder.iv_cover)
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlMain: RelativeLayout
        var tv_content: TextView
        var tv_size: TextView
        var tv_time: TextView
        var iv_cover: ImageView

        init {
            rlMain = itemView.findViewById(R.id.rl_main)
            tv_content = itemView.findViewById(R.id.tv_content)
            tv_size = itemView.findViewById(R.id.tv_size)
            tv_time = itemView.findViewById(R.id.tv_time)
            iv_cover = itemView.findViewById(R.id.iv_cover)
        }
    }

    init {
        this.isPhoto = isPhoto
    }
}