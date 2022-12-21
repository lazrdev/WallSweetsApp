/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.jahir.frames.ui.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import dev.jahir.frames.ui.graphics.ImageLoadingColorMatrix
import kotlin.math.roundToLong

private val fastOutSlowInInterpolator = FastOutSlowInInterpolator()

fun saturateDrawableAnimator(
    current: Drawable,
    duration: Long = SATURATION_ANIMATION_DURATION,
    view: View? = null
): Animator {
    current.mutate()
    view?.setHasTransientState(true)

    val cm = ImageLoadingColorMatrix()

    val satAnim = ObjectAnimator.ofFloat(cm, ImageLoadingColorMatrix.PROP_SATURATION, 0F, 1F)
    satAnim.duration = duration
    satAnim.addUpdateListener {
        current.colorFilter = ColorMatrixColorFilter(cm)
    }

    val alphaAnim = ObjectAnimator.ofFloat(cm, ImageLoadingColorMatrix.PROP_ALPHA, 0F, 1F)
    alphaAnim.duration = duration / 2

    val darkenAnim = ObjectAnimator.ofFloat(cm, ImageLoadingColorMatrix.PROP_BRIGHTNESS, 0.8F, 1F)
    darkenAnim.duration = (duration * 0.75F).roundToLong()

    return AnimatorSet().apply {
        playTogether(satAnim, alphaAnim, darkenAnim)
        interpolator = fastOutSlowInInterpolator
        doOnEnd {
            current.clearColorFilter()
            view?.setHasTransientState(false)
        }
    }
}

const val SATURATION_ANIMATION_DURATION = 1000L