/*
 * Copyright (c) 2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY,WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.tommygeenexus.usbdonglecontrol.core.extension

import android.app.Activity
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun Activity.setCutoutForegroundColor(@ColorInt color: Int) {
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, windowInsetsCompat ->
        val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.displayCutout())
        val drawable = if (insets.left > 0) {
            LayerDrawable(arrayOf(color.toDrawable())).apply {
                setLayerGravity(0, Gravity.START)
                setLayerWidth(0, insets.left)
            }
        } else if (insets.right > 0) {
            LayerDrawable(arrayOf(color.toDrawable())).apply {
                setLayerGravity(0, Gravity.END)
                setLayerWidth(0, insets.right)
            }
        } else {
            null
        }
        window.decorView.foreground = drawable
        windowInsetsCompat
    }
}
