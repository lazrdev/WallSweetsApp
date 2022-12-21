@file:Suppress("DEPRECATION")

package dev.jahir.frames.ui.activities.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import com.google.android.material.appbar.AppBarLayout
import dev.jahir.frames.R
import dev.jahir.frames.data.Preferences
import dev.jahir.frames.extensions.context.findView
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.utils.SafeHandler
import dev.jahir.frames.extensions.views.gone
import dev.jahir.frames.extensions.views.setMarginTop
import dev.jahir.frames.extensions.views.visible
import dev.jahir.frames.ui.widgets.FramesBottomNavigationView

abstract class BaseSystemUIVisibilityActivity<out P : Preferences> :
    BaseStoragePermissionRequestActivity<P>() {

    internal val appbar: AppBarLayout? by findView(R.id.appbar)
    val bottomNavigation: FramesBottomNavigationView? by findView(R.id.bottom_navigation)
    val bottomNavigation2: LinearLayout? by findView(R.id.bottom_navigation2)

    private var visibleSystemUI: Boolean = true

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(VISIBLE_SYSTEM_UI_KEY, visibleSystemUI)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (!canToggleSystemUIVisibility()) return
        setSystemUIVisibility(savedInstanceState.getBoolean(VISIBLE_SYSTEM_UI_KEY, true))
    }

    internal fun toggleSystemUI() {
        if (!canToggleSystemUIVisibility()) return
        setSystemUIVisibility(!visibleSystemUI)
    }

    private fun setSystemUIVisibility(visible: Boolean, withSystemBars: Boolean = true) {
        if (!canToggleSystemUIVisibility()) return
        SafeHandler().post {
            if (withSystemBars) {
                window.decorView.systemUiVisibility = if (visible)
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                else
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_IMMERSIVE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
            changeBarsVisibility(visible)
            visibleSystemUI = visible
        }
    }

    private fun changeBarsVisibility(show: Boolean) {
        if (!canToggleSystemUIVisibility()) return
        changeAppBarVisibility(show)
        changeBottomBarVisibility(show)
    }

    private fun changeAppBarVisibility(show: Boolean) {
        if (!canToggleSystemUIVisibility()) return
        val extra = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.rootWindowInsets.systemWindowInsetTop
            } else {
                25.dpToPx
            }
        } catch (e: Exception) {
            0
        }
        appbar?.setMarginTop(if (show) extra else 0)
        val transY = (if (show) 0 else -(appbar?.height ?: 0 * 3)).toFloat()
        appbar?.animate()?.translationY(transY)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.withStartAction { if (show) appbar?.visible() }
            ?.withEndAction { if (!show) appbar?.gone() }
            ?.start()
    }

    private fun changeBottomBarVisibility(show: Boolean) {
        if (!canToggleSystemUIVisibility()) return
        val extra = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.rootWindowInsets.systemWindowInsetBottom
            } else {
                48.dpToPx
            }
        } catch (e: Exception) {
            0
        }
        val transY = (if (show) 0 else ((bottomNavigation?.height ?: 0 * 2) + extra)).toFloat()
        bottomNavigation?.animate()?.translationY(transY)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.start()
    }

    open fun canToggleSystemUIVisibility(): Boolean = false

    companion object {
        private const val VISIBLE_SYSTEM_UI_KEY = "visible_system_ui"
    }
}
