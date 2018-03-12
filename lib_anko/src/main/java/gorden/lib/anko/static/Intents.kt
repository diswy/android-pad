package gorden.lib.anko.static

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import java.io.Serializable
import android.os.Parcelable
import android.support.v4.app.Fragment

/**
 * 描述
 * Created by gorden on 2017/11/13.
 */
inline fun <reified T:Activity> Context.startActivity(vararg params: Pair<String, Any?>){
    this.startActivity(createIntent(this,T::class.java,params))
}

inline fun <reified T: Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) =
        this.startActivity(createIntent(activity,T::class.java,params))

inline fun <reified T: Activity> Activity.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) =
        this.startActivityForResult(createIntent(this,T::class.java,params),requestCode)

inline fun <reified T: Activity> Fragment.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) =
        this.startActivityForResult(createIntent(activity,T::class.java,params),requestCode)


fun <T> createIntent(ctx: Context?, clazz: Class<out T>, params: Array<out Pair<String, Any?>>):Intent{
    val intent = Intent(ctx, clazz)
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    return intent
}

private fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
    params.forEach {
        val value = it.second
        when (value) {
            null -> intent.putExtra(it.first, null as Serializable?)
            is Int -> intent.putExtra(it.first, value)
            is Long -> intent.putExtra(it.first, value)
            is CharSequence -> intent.putExtra(it.first, value)
            is String -> intent.putExtra(it.first, value)
            is Float -> intent.putExtra(it.first, value)
            is Double -> intent.putExtra(it.first, value)
            is Char -> intent.putExtra(it.first, value)
            is Short -> intent.putExtra(it.first, value)
            is Boolean -> intent.putExtra(it.first, value)
            is Serializable -> intent.putExtra(it.first, value)
            is Bundle -> intent.putExtra(it.first, value)
            is Parcelable -> intent.putExtra(it.first, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                else -> throw ClassFormatError("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> intent.putExtra(it.first, value)
            is LongArray -> intent.putExtra(it.first, value)
            is FloatArray -> intent.putExtra(it.first, value)
            is DoubleArray -> intent.putExtra(it.first, value)
            is CharArray -> intent.putExtra(it.first, value)
            is ShortArray -> intent.putExtra(it.first, value)
            is BooleanArray -> intent.putExtra(it.first, value)
            else -> throw ClassFormatError("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
        return@forEach
    }
}