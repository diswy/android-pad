package gorden.library.core

import android.support.v4.view.ViewPager
import android.view.View

internal class ZoomOutPageTransformer : ViewPager.PageTransformer {
    private val minScale = 0.85f
    private val minAlpha = 0.5f
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height

        when {
            position < -1 -> view.alpha = 0f
            position <= 1 -> {
                val scaleFactor = Math.max(minScale, 1 - Math.abs(position))
                val verMargin = pageHeight * (1 - scaleFactor) / 2
                val horMargin = pageWidth * (1 - scaleFactor) / 2
                if (position < 0) {
                    view.translationX = horMargin - verMargin / 2
                } else {
                    view.translationX = -horMargin + verMargin / 2
                }
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.alpha = minAlpha + (scaleFactor - minScale) / (1 - minScale) * (1 - minAlpha)
            }
            else -> view.alpha = 0f
        }
    }
}