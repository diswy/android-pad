package gorden.library.core

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory

/**
 * 描述
 * Created by gorden on 2018/2/1.
 */
class BackgroundCrossFadeFactory(private val duration: Int, private val isCrossFadeEnabled: Boolean):TransitionFactory<Drawable>{

    private var resourceTransition: DrawableCrossFadeTransition? = null

    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        if (resourceTransition == null) {
            resourceTransition = DrawableCrossFadeTransition(duration, isCrossFadeEnabled)
        }
        return resourceTransition!!
    }

    class Builder
    /**
     * @param durationMillis The duration of the cross fade animation in milliseconds.
     */
    @JvmOverloads constructor(private val durationMillis: Int = DEFAULT_DURATION_MS) {
        private var isCrossFadeEnabled: Boolean = false

        /**
         * Enables or disables animating the alpha of the [Drawable] the cross fade will animate
         * from.
         *
         *
         * Defaults to `false`.
         *
         * @param isCrossFadeEnabled If `true` the previous [Drawable]'s alpha will be
         * animated from 100 to 0 while the new [Drawable]'s alpha is
         * animated from 0 to 100. Otherwise the previous [Drawable]'s
         * alpha will remain at 100 throughout the animation. See
         * [android.graphics.drawable.TransitionDrawable.setCrossFadeEnabled]
         */
        fun setCrossFadeEnabled(isCrossFadeEnabled: Boolean): Builder {
            this.isCrossFadeEnabled = isCrossFadeEnabled
            return this
        }

        fun build(): BackgroundCrossFadeFactory {
            return BackgroundCrossFadeFactory(durationMillis, isCrossFadeEnabled)
        }

        companion object {
            private val DEFAULT_DURATION_MS = 300
        }
    }
}