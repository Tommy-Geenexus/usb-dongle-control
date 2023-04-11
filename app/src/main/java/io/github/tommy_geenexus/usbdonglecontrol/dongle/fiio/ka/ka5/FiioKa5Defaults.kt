/*
 * Copyright (c) 2022-2023, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5

object FiioKa5Defaults {

    const val CHANNEL_BALANCE_MIN = -12
    const val CHANNEL_BALANCE_MAX = 12
    const val DISPLAY_BRIGHTNESS_MIN = 1
    const val DISPLAY_BRIGHTNESS_MAX = 10
    const val DISPLAY_TIMEOUT_MIN = 0
    const val DISPLAY_TIMEOUT_MAX = 60
    const val VOLUME_LEVEL_MIN = 0
    const val VOLUME_LEVEL_A_MAX = 120
    const val VOLUME_LEVEL_B_MAX = 60

    const val CHANNEL_BALANCE = CHANNEL_BALANCE_MAX + CHANNEL_BALANCE_MIN
    const val DISPLAY_BRIGHTNESS = DISPLAY_BRIGHTNESS_MIN
    const val DISPLAY_TIMEOUT = DISPLAY_TIMEOUT_MIN
    const val FW_VERSION = "01.00"
    const val SAMPLE_RATE = "48kHz"
    const val VOLUME_LEVEL = VOLUME_LEVEL_MIN
}
