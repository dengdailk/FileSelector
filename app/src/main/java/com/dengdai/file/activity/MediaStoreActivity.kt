package com.dengdai.file.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.dengdai.file.R
import com.dengdai.file.adapter.PublicTabViewPagerAdapter
import com.dengdai.file.fragment.FolderDataFragment
import com.dengdai.file.model.FileInfo
import com.dengdai.file.utils.FileUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.io.File
import java.util.*

/**
 * 使用 Media Store 多媒体库
 */
@Suppress("DEPRECATION")
class MediaStoreActivity : AppCompatActivity() {
    private var tlFile: TabLayout? = null
    private var vpFile: ViewPager? = null
    private var mTabTitle: MutableList<String> = ArrayList()
    private var mFragment: MutableList<Fragment> = ArrayList()
    private val imageData = ArrayList<FileInfo?>()
    private val wordData = ArrayList<FileInfo?>()
    private val xlsData = ArrayList<FileInfo?>()
    private val txtData = ArrayList<FileInfo?>()
    private val pdfData = ArrayList<FileInfo?>()
    private var progressDialog: ProgressDialog? = null

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                initData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeMessages(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        tlFile = findViewById(R.id.tl_file)
        vpFile = findViewById(R.id.vp_file)
        progressDialog = ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT)
        progressDialog!!.setMessage("正在加载中...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
        object : Thread() {
            override fun run() {
                super.run()
                folderData
            }
        }.start()
    }

    /**
     * 遍历文件夹中资源
     */
    val folderData: Unit
        get() {
            images
            getDocumentData(1)
            getDocumentData(2)
            getDocumentData(3)
            getDocumentData(4)
            handler.sendEmptyMessage(1)
        }

    private fun initData() {
        mTabTitle = ArrayList()
        mFragment = ArrayList()
        mTabTitle.add("image")
        mTabTitle.add("word")
        mTabTitle.add("xls")
        mTabTitle.add("text")
        mTabTitle.add("pdf")
        val imageFragment = FolderDataFragment()
        val imageBundle = Bundle()
        imageBundle.putParcelableArrayList("file_data", imageData)
        imageBundle.putBoolean("is_image", true)
        imageFragment.arguments = imageBundle
        mFragment.add(imageFragment)
        val wordFragment = FolderDataFragment()
        val wordBundle = Bundle()
        wordBundle.putParcelableArrayList("file_data", wordData)
        wordBundle.putBoolean("is_image", false)
        wordFragment.arguments = wordBundle
        mFragment.add(wordFragment)
        val xlsFragment = FolderDataFragment()
        val xlsBundle = Bundle()
        xlsBundle.putParcelableArrayList("file_data", xlsData)
        xlsBundle.putBoolean("is_image", false)
        xlsFragment.arguments = xlsBundle
        mFragment.add(xlsFragment)
        val pptFragment = FolderDataFragment()
        val pptBundle = Bundle()
        pptBundle.putParcelableArrayList("file_data", txtData)
        pptBundle.putBoolean("is_image", false)
        pptFragment.arguments = pptBundle
        mFragment.add(pptFragment)
        val pdfFragment = FolderDataFragment()
        val pdfBundle = Bundle()
        pdfBundle.putParcelableArrayList("file_data", pdfData)
        pdfBundle.putBoolean("is_image", false)
        pdfFragment.arguments = pdfBundle
        mFragment.add(pdfFragment)
        val fragmentManager = supportFragmentManager
        val tabViewPagerAdapter = PublicTabViewPagerAdapter(fragmentManager, mFragment, mTabTitle)
        vpFile!!.adapter = tabViewPagerAdapter
        tlFile!!.setupWithViewPager(vpFile)
        tlFile!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vpFile!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        progressDialog!!.dismiss()
    }//asc 按升序排列
    //desc 按降序排列
    //projection 是定义返回的数据，selection 通常的sql 语句，例如  selection=MediaStore.Images.ImageColumns.MIME_TYPE+"=? " 那么 selectionArgs=new String[]{"jpg"};

    /**
     * 加载图片
     */
    private val images: Unit
        get() {
            val projection = arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME)

            //asc 按升序排列
            //desc 按降序排列
            //projection 是定义返回的数据，selection 通常的sql 语句，例如  selection=MediaStore.Images.ImageColumns.MIME_TYPE+"=? " 那么 selectionArgs=new String[]{"jpg"};
            val mContentResolver = this.contentResolver
            var cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED + "  desc")
            lateinit var imageId: String
            var fileName: String
            var filePath: String
            while (cursor!!.moveToNext()) {
                imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                Log.e("photo", "$imageId -- $fileName -- $filePath")
                val fileInfo = FileUtil.getFileInfoFromFile(File(filePath))
                imageData.add(fileInfo)
            }
            cursor.close()
        }

    /**
     * 获取手机文档数据
     *
     * @param selectType
     */
    private fun getDocumentData(selectType: Int) {
        val columns = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA)
        var select = ""
        when (selectType) {
            1 -> select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.doc'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.docx'" + ")"
            2 -> select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.xls'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.xlsx'" + ")"
            3 -> select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.txt'" + ")"
            4 -> select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.pdf'" + ")"
        }

//        List<FileInfo> dataList = new ArrayList<>();
        val contentResolver = contentResolver
        val cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), columns, select, null, null)
        //        Cursor cursor = contentResolver.query(Uri.parse(Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/"), columns, select, null, null);
        val columnIndexOrThrow_DATA = cursor!!.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        while (cursor.moveToNext()) {
            val path = cursor.getString(columnIndexOrThrow_DATA)
            val document = FileUtil.getFileInfoFromFile(File(path))
            when (selectType) {
                1 -> wordData.add(document)
                2 -> xlsData.add(document)
                3 -> txtData.add(document)
                4 -> pdfData.add(document)
            }
        }
        cursor.close()
    }
}