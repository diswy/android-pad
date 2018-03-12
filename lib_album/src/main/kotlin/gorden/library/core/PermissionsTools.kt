package gorden.library.core

import android.app.Activity
import gorden.library.ui.PermissionsFragment

/**
 * 权限申请
 */
class PermissionsTools private constructor(activity: Activity) {
    companion object {
        fun build(activity: Activity): PermissionsTools {
            return PermissionsTools(activity)
        }
    }
    private var permissionsFragment: PermissionsFragment? = null
    fun request(vararg permissions: String, callback: (Boolean) -> Unit) {
        permissionsFragment?.callBack(callback)
        permissionsFragment?.requestPermissions(*permissions)
    }

    init {
        permissionsFragment = activity.fragmentManager.findFragmentByTag("album_permissions") as? PermissionsFragment
        if (permissionsFragment == null) {
            permissionsFragment = PermissionsFragment()
            activity.fragmentManager.apply {
                beginTransaction().add(permissionsFragment, "album_permissions").commitAllowingStateLoss()
                executePendingTransactions()
            }
        }
    }
}