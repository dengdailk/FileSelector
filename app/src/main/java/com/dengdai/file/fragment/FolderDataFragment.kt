package com.dengdai.file.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dengdai.file.R
import com.dengdai.file.adapter.FolderDataRecycleAdapter
import com.dengdai.file.model.FileInfo
import java.util.ArrayList

/**
 *
 */
class FolderDataFragment : Fragment() {
    private var rvDoc: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_doc, container, false)
        rvDoc = rootView.findViewById(R.id.rv_doc)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    private fun initData() {
        val bundle = this.arguments
        val data: ArrayList<FileInfo>? = bundle!!.getParcelableArrayList("file_data")
        val isImage = bundle.getBoolean("is_image")
        val linearLayoutManager = LinearLayoutManager(activity)
        //设置RecyclerView 布局
        rvDoc!!.layoutManager = linearLayoutManager
        val pptListAdapter = FolderDataRecycleAdapter(activity, data, isImage)
        rvDoc!!.adapter = pptListAdapter
    }
}