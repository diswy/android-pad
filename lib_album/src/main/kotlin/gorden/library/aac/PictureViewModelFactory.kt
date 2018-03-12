package gorden.library.aac

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle

class PictureViewModelFactory(private val extras: Bundle) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Bundle::class.java).newInstance(extras)
    }
}