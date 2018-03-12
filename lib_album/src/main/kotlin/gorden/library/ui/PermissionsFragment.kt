package gorden.library.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.Fragment
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog


internal class PermissionsFragment : Fragment() {

    @Suppress("PrivatePropertyName")
    private val PERMISSIONS_REQUEST_CODE = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissions(vararg permissions: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }else{
        permissionsCallBack?.invoke(true)
    }
}

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            var hasPermission = true
            var tipMessage: StringBuilder? = null
            permissions.forEachIndexed { index, permission ->
                val shouldTip = shouldShowRequestPermissionRationale(permission)
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    hasPermission = false
                    if (shouldTip) {
                        if (tipMessage.isNullOrEmpty()) {
                            tipMessage = StringBuilder("应用需要")
                        }
                        tipMessage?.append(when (permission) {
                            Manifest.permission.READ_EXTERNAL_STORAGE -> "访问文件权限,用于获取手机图片信息;"
                            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "读写文件权限,用于保存图片;"
                            Manifest.permission.CAMERA -> "拍照权限,用于拍摄照片;"
                            else -> "权限,否则将无法正常运行"
                        })
                    }
                }
            }

            tipMessage?.apply {
                AlertDialog.Builder(activity).setMessage(this)
                        .setPositiveButton("OK", null).show()
                        .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            }

            permissionsCallBack?.invoke(hasPermission)
        }
    }

    private var permissionsCallBack: ((Boolean) -> Unit)? = null
    fun callBack(callback: (Boolean) -> Unit) {
        this.permissionsCallBack = callback
    }
}