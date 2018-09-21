package com.ebd.lib.data

import android.os.Environment


fun getMyPath() = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/libs/Ebook/")