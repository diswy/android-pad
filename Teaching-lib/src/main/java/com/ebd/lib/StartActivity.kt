package com.ebd.lib

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.startActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted)
                        startActivity<MainActivity>()
                    else
                        finish()
                }
    }
}
