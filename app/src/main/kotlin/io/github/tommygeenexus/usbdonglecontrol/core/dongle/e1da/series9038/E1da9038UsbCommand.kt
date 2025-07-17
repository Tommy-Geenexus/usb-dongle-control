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

package io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038

interface E1da9038UsbCommand {

    val getAudioFormat: ByteArray
    val getFilterDsd: ByteArray
    val getFilterPcm44To96: ByteArray
    val getFilterPcm176To384: ByteArray
    val getHwTypeVersionMuteStandby: ByteArray
    val getMasterClockDividerDsd: ByteArray
    val getMasterClockDividerPcm44To96: ByteArray
    val getMasterClockDividerPcm176To384: ByteArray
    val getThdRanges: ByteArray
    val getThdRange1: ByteArray
    val getThdRange2: ByteArray
    val getThdRange3: ByteArray
    val getVolumeLeftRightMinMax: ByteArray

    val setFilterDsd: ByteArray
    val setFilterPcm44To96: ByteArray
    val setFilterPcm176To384: ByteArray
    val setHardwareMute: ByteArray
    val setInit: ByteArray
    val setMasterClockDividerDsd: ByteArray
    val setMasterClockDividerPcm44To96: ByteArray
    val setMasterClockDividerPcm176To384: ByteArray
    val setRestore: ByteArray
    val setStandby: ByteArray
    val setVolumeLevel: ByteArray
    val setVolumeLevelMin: ByteArray
    val setVolumeLevelMax: ByteArray
}
