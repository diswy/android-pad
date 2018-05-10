package gorden.library.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.LinearLayout
import gorden.library.R
import kotlinx.android.synthetic.main.layout_album_hint.view.*
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * 图片加载进度提示
 */
internal class AlbumHintView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var mode: Mode? = null

    init {
        View.inflate(context, R.layout.layout_album_hint, this)
        visibility = View.GONE
    }


    fun loading() {
        setProgress(0, 0)
        if (mode != Mode.LOADING) {
            if (mode == Mode.HINT) {
                viewSwitcher.setInAnimation(context, android.R.anim.slide_in_left)
                viewSwitcher.setOutAnimation(context, android.R.anim.slide_out_right)
            } else {
                viewSwitcher.inAnimation = null
                viewSwitcher.outAnimation = null
            }
            mode = Mode.LOADING
            button_action.visibility = View.INVISIBLE
            viewSwitcher.displayedChild = mode!!.index
            visibility = View.VISIBLE
        }
    }

    fun setProgress(progress: Int, total: Int) {
        if (progress <= 0) {
            text_hint.text = null
        } else {
            val ratio = (progress.toFloat() / total * 100).toInt()
            text_hint.text = String.format("%d%%", ratio)
        }
    }


    private fun goneViewByAlpha(view: View, duration: Long, isBanClick: Boolean, animationListener: AnimationListener?) {
        if (view.visibility != View.GONE) {
            view.visibility = View.GONE
            val hiddenAlphaAnimation = getHiddenAlphaAnimation(duration)
            hiddenAlphaAnimation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (isBanClick) {
                        view.isClickable = false
                    }
                    animationListener?.onAnimationStart(animation)
                }

                override fun onAnimationRepeat(animation: Animation) {
                    animationListener?.onAnimationRepeat(animation)
                }

                override fun onAnimationEnd(animation: Animation) {
                    if (isBanClick) {
                        view.isClickable = true
                    }
                    animationListener?.onAnimationEnd(animation)
                }
            })
            view.startAnimation(hiddenAlphaAnimation)
        }
    }

    private fun getHiddenAlphaAnimation(duration: Long, animationListener: AnimationListener? = null): AlphaAnimation {
        return getAlphaAnimation(1.0f, 0.0f, duration, animationListener);
    }

    private fun getAlphaAnimation(from: Float, to: Float, duration: Long, animationListener: AnimationListener?): AlphaAnimation {
        val alphaAnimation = AlphaAnimation(from, to)
        alphaAnimation.duration = duration
        if (animationListener != null) {
            alphaAnimation.setAnimationListener(animationListener);
        }
        return alphaAnimation;
    }

    private fun goneViewByAlpha(view: View, isBanClick: Boolean) {
        goneViewByAlpha(view, 400, isBanClick, null)
    }

    private fun visibleViewByAlpha(view: View, isBanClick: Boolean) {
        visibleViewByAlpha(view, 400, isBanClick, null)
    }

    private fun visibleViewByAlpha(view: View, duration: Long, isBanClick: Boolean, animationListener: AnimationListener?) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            val showAlphaAnimation = getShowAlphaAnimation(duration)
            showAlphaAnimation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (isBanClick) {
                        view.isClickable = false
                    }
                    animationListener?.onAnimationStart(animation)
                }

                override fun onAnimationRepeat(animation: Animation) {
                    animationListener?.onAnimationRepeat(animation)
                }

                override fun onAnimationEnd(animation: Animation) {
                    if (isBanClick) {
                        view.isClickable = true
                    }
                    animationListener?.onAnimationEnd(animation)
                }
            })
            view.startAnimation(showAlphaAnimation)
        }
    }

    private fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
    }

    fun isConnectedByState(context: Context): Boolean {
        val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return networkInfo != null && networkInfo.state == NetworkInfo.State.CONNECTED
    }

    fun hint(iconId: Int, hintText: String, buttonName: String, buttonClickListener: View.OnClickListener?, transparent: Boolean) {
        if (iconId > 0) {
            val drawables = text_hint.getCompoundDrawables()
            text_hint.setCompoundDrawablesWithIntrinsicBounds(drawables[0], resources.getDrawable(iconId), drawables[2], drawables[3])
        } else {
            val drawables = text_hint.getCompoundDrawables()
            text_hint.setCompoundDrawablesWithIntrinsicBounds(drawables[0], null, drawables[2], drawables[3])
        }

        if (hintText.isNotEmpty()) {
            text_hint.text = hintText
        } else {
            text_hint.text = null
        }

        if (buttonName.isNotEmpty() && buttonClickListener != null) {
            button_action.setText(buttonName)
            button_action.setOnClickListener(buttonClickListener)
            visibleViewByAlpha(button_action, true)
        } else {
            button_action.setText(null)
            button_action.setOnClickListener(null)
            button_action.setVisibility(View.INVISIBLE)
        }

        isClickable = !transparent

        if (mode != Mode.HINT) {
            if (mode != null) {
                viewSwitcher.setInAnimation(context, android.R.anim.slide_in_left)
                viewSwitcher.setOutAnimation(context, android.R.anim.slide_out_right)
            } else {
                viewSwitcher.setInAnimation(null)
                viewSwitcher.setOutAnimation(null)
            }
            mode = Mode.HINT
            viewSwitcher.setDisplayedChild(1)
            visibility = View.VISIBLE
        }
    }

    fun hint(iconId: Int, hintText: String, buttonName: String, buttonClickListener: View.OnClickListener) {
        hint(iconId, hintText, buttonName, buttonClickListener, false)
    }

    fun hint(iconId: Int, hintText: String) {
        hint(iconId, hintText, "", null, false)
    }

    fun hint(hintText: String, buttonName: String, buttonClickListener: View.OnClickListener, transparent: Boolean) {
        hint(-1, hintText, buttonName, buttonClickListener, transparent)
    }

    fun hint(hintText: String, buttonName: String, buttonClickListener: View.OnClickListener) {
        hint(-1, hintText, buttonName, buttonClickListener, false)
    }

    fun hint(hintText: String, transparent: Boolean) {
        hint(-1, hintText, "", null, transparent)
    }

    fun hint(hintText: String) {
        hint(-1, hintText, "", null, false)
    }

    fun failed(exception: Throwable?, reloadButtonClickListener: View.OnClickListener) {
        val message: String
        if (exception == null) {
            message = "网络连接异常【909】"
        } else if (exception is SecurityException) {
            message = "网络连接异常【101】"
        } else if (exception is UnknownHostException) {
            if (isConnectedByState(context)) {
                message = "网络连接异常【202】"
            } else {
                message = "没有网络连接"
            }
        } else if (exception is SocketTimeoutException || exception is IOException) {
            message = "网络连接超时"
        } else if (exception is FileNotFoundException) {
            message = "网络连接异常【404】"
        } else {
            message = "网络连接异常【909】"
        }
        hint(R.drawable.ic_error, message, "重试", reloadButtonClickListener, false)
    }

    fun empty(message: String) {
        hint(R.drawable.ic_error, message, "", null, false)
    }

    fun hidden() {
        when (viewSwitcher.displayedChild) {
            0 -> goneViewByAlpha(this, true)
            1 -> visibility = View.GONE
        }
        mode = null
    }

    private enum class Mode(val index: Int) {
        LOADING(0),
        HINT(1)
    }
}