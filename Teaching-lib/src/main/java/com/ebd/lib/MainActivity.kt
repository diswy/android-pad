package com.ebd.lib

import com.xiaofu.lib_base_xiaofu.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun getView(): Int = R.layout.activity_main

    override fun initialize() {

//        val path = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/libs/Ebook/")
//
//        val bookFile = File(path)
//        if (bookFile.exists()) {
//            val files = bookFile.listFiles()
//            for (book in files) {
//                if (book.isDirectory) {
//                    println("---->>>文件夹：${book.name}")
//                    val btn = Button(this)
//                    btn.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                    btn.text = book.name
//                    mContainer.addView(btn)
//                }
//            }
//        }
//
//
//        val xml = XMLUtils()
//        val string = xml.getXmlToJson(path.plus("test/index"))
//        val book = Gson().fromJson<Book>(string, Book::class.java)
//        println("------")
//        println("书籍名称：${book.Ebook.info.name}")
//        println("书籍封面文件：${book.Ebook.info.cover}")
//        println("书籍出版社：${book.Ebook.info.publish}")
//        println("------")
//
//        if (book.Ebook.chapters.chapter != null) {
//            for (section in book.Ebook.chapters.chapter) {
//                println("这是：${section.name}")
//                if (section.sections.section != null) {
//                    for (item in section.sections.section) {
//                        println("section:名字：${item.name} 页码：${item.page}")
//                        if(item.items != null){
//                            for (i in item.items.item) {
//                                println("item:名字：${i.content} 页码：${i.page}")
//                            }
//                        }
//                    }
//                }
//            }
//        }

    }

    override fun bindEvent() {
//        testBtn.setOnClickListener {
//            val path = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/libs/")
//            ZipProgressUtil.UnZipFile(path.plus("中文.zip"), path.plus("/Ebook/中文/"), object : ZipListener {
//                override fun zipSuccess() {
//                    println("------------>>>zipSuccess")
//                }
//
//                override fun zipStart() {
//                    println("------------>>>zipStart")
//                }
//
//                override fun zipProgress(progress: Int) {
//                    println("------------>>>$progress")
//                }
//
//                override fun zipFail() {
//                    println("------------>>>zipFail")
//                }
//            })
//        }
    }
}
