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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data

import android.hardware.usb.UsbDeviceConnection
import io.github.tommy_geenexus.usbdonglecontrol.di.DispatcherIo
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbTransfer
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5Defaults
import io.github.tommy_geenexus.usbdonglecontrol.suspendRunCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class FiioKa5UsbCommunicationRepository @Inject constructor(
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher
) : UsbTransfer {

    private companion object {

        const val DELAY_READ = 50L
        const val DELAY_WRITE = 100L
        const val TIMEOUT_MS = 1000

        const val USB_ENDPOINT = 0

        const val REQUEST_PAYLOAD_INDEX_SET = 3
        const val REQUEST_PAYLOAD_SIZE = 7
        const val REQUEST_TYPE_WRITE = 67
        const val REQUEST_TYPE_READ = 195
        const val REQUEST_ID_READ = 161
        const val REQUEST_ID_WRITE = 160
        const val REQUEST_INDEX = 2464

        const val REQUEST_RESULT_INDEX_FILTER = 3
        const val REQUEST_RESULT_INDEX_SPDIF_OUT = 3
        const val REQUEST_RESULT_INDEX_VERSION = 3
        const val REQUEST_RESULT_INDEX_SAMPLE_RATE = 3
        const val REQUEST_RESULT_INDEX_GAIN = 4
        const val REQUEST_RESULT_INDEX_VOLUME_LEVEL = 4
        const val REQUEST_RESULT_INDEX_DISPLAY_TIMEOUT = 4
        const val REQUEST_RESULT_INDEX_DISPLAY_BRIGHTNESS = 5
        const val REQUEST_RESULT_INDEX_DAC_MODE = 5
        const val REQUEST_RESULT_INDEX_VOLUME_MODE = 5
        const val REQUEST_RESULT_INDEX_CHANNEL_BAL_R = 5
        const val REQUEST_RESULT_INDEX_CHANNEL_BAL_L = 6
        const val REQUEST_RESULT_INDEX_HW_MUTE = 6
        const val REQUEST_RESULT_INDEX_HID_MODE = 6
        const val REQUEST_RESULT_INDEX_DISPLAY_INVERT = 6
    }

    override val mutex = Mutex()

    private val volume120m = listOf(
        0,
        65,
        8,
        4, 4, 4, 4, 4, 4, 4, 4,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1
    )
    private val volume60m = listOf(
        0,
        65,
        8, 8, 8, 8, 8, 8, 8, 8,
        6, 6, 6, 6, 6, 6,
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
    )
    private val volume120 = mutableMapOf<Int, Int>().apply {
        var level = 255
        for (i in 0..120) {
            level -= volume120m[i]
            put(i, level)
        }
    }
    private val volume60 = mutableMapOf<Int, Int>().apply {
        var level = 255
        for (i in 0..60) {
            level -= volume60m[i]
            put(i, level)
        }
    }
    private val displayBrightness = mapOf(
        1 to 0,
        2 to 10,
        3 to 25,
        4 to 40,
        5 to 60,
        6 to 80,
        7 to 105,
        8 to 130,
        9 to 160,
        10 to 200
    )
    private val sampleRates = mapOf(
        0 to "44.1kHz",
        1 to "48kHz",
        2 to "88.2kHz",
        3 to "96kHz",
        4 to "176.4kHz",
        5 to "192kHz",
        6 to "352.8kHz",
        7 to "384kHz",
        8 to "705.6kHz",
        9 to "768kHz",
        16 to "DoP64",
        17 to "DoP64",
        18 to "DoP128",
        19 to "DoP128",
        20 to "DoP256",
        21 to "DoP256",
        32 to "Native64",
        33 to "Native64",
        34 to "Native128",
        35 to "Native128",
        36 to "Native256",
        37 to "Native256",
        38 to "Native512",
        39 to "Native512"
    )

    override suspend fun getCurrentState(connection: UsbDeviceConnection): FiioKa5? {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                val fiioKa5 = FiioKa5()
                val commands = listOf(
                    fiioKa5.getVersion,
                    fiioKa5.getSampleRate,
                    fiioKa5.getVolumeLevel,
                    fiioKa5.getFilter,
                    fiioKa5.getOtherState
                )
                var gain: Gain = Gain.default()
                var dacMode: DacMode = DacMode.default()
                var hardwareMuteEnabled = false
                var hidMode: HidMode = HidMode.default()
                var volumeMode: VolumeMode = VolumeMode.default()
                var firmwareVersion = FiioKa5Defaults.FW_VERSION
                var sampleRate = FiioKa5Defaults.SAMPLE_RATE
                var volumeLevel = FiioKa5Defaults.VOLUME_LEVEL
                var channelBalance = FiioKa5Defaults.CHANNEL_BALANCE
                var filter: Filter = Filter.default()
                var spdifOutEnabled = false
                var displayTimeout = FiioKa5Defaults.DISPLAY_TIMEOUT
                var displayBrightness = FiioKa5Defaults.DISPLAY_BRIGHTNESS
                var displayInvertEnabled = false
                commands.forEach { command ->
                    data.fill(0)
                    command.copyInto(data)
                    mutex.withLock {
                        var result = connection.controlTransfer(
                            REQUEST_TYPE_WRITE,
                            REQUEST_ID_WRITE,
                            USB_ENDPOINT,
                            REQUEST_INDEX,
                            data,
                            REQUEST_PAYLOAD_SIZE,
                            TIMEOUT_MS
                        )
                        if (result != REQUEST_PAYLOAD_SIZE) {
                            error("USB control transfer $REQUEST_TYPE_WRITE failed")
                        }
                        delay(DELAY_READ)
                        result = connection.controlTransfer(
                            REQUEST_TYPE_READ,
                            REQUEST_ID_READ,
                            USB_ENDPOINT,
                            REQUEST_INDEX,
                            data,
                            REQUEST_PAYLOAD_SIZE,
                            TIMEOUT_MS
                        )
                        if (result != REQUEST_PAYLOAD_SIZE) {
                            error("USB control transfer $REQUEST_TYPE_READ failed")
                        }
                        delay(DELAY_WRITE)
                    }
                    if (command.contentEquals(fiioKa5.getVersion)) {
                        hidMode = HidMode.findByIdOrDefault(
                            id = data[REQUEST_RESULT_INDEX_HID_MODE]
                        )
                        volumeMode = VolumeMode.findByIdOrDefault(
                            id = data[REQUEST_RESULT_INDEX_VOLUME_MODE]
                        )
                        val rawFirmwareVersion = data[REQUEST_RESULT_INDEX_VERSION]
                        firmwareVersion = if (rawFirmwareVersion >= 100) {
                            StringBuilder(rawFirmwareVersion.toString())
                                .insert(2, ".")
                                .toString()
                        } else if (rawFirmwareVersion >= 10) {
                            StringBuilder(rawFirmwareVersion.toString())
                                .insert(0, "0")
                                .insert(2, ".")
                                .append("0")
                                .toString()
                        } else {
                            StringBuilder(rawFirmwareVersion.toString())
                                .insert(0, "00.")
                                .append("0")
                                .toString()
                        }
                    } else if (command.contentEquals(fiioKa5.getSampleRate)) {
                        val rawSampleRate = data[REQUEST_RESULT_INDEX_SAMPLE_RATE].toInt()
                        sampleRate = sampleRates
                            .filter { (key, _) ->
                                rawSampleRate == key
                            }
                            .values
                            .firstOrNull()
                            ?: FiioKa5Defaults.SAMPLE_RATE
                    } else if (command.contentEquals(fiioKa5.getVolumeLevel)) {
                        val rawVolumeLevel = data[REQUEST_RESULT_INDEX_VOLUME_LEVEL]
                        val volumeLevels = if (volumeMode == VolumeMode.S120) {
                            volume120
                        } else {
                            volume60
                        }
                        volumeLevel = volumeLevels
                            .entries
                            .find { (_, value) ->
                                value == rawVolumeLevel.toInt()
                            }
                            ?.key
                            ?: FiioKa5Defaults.VOLUME_LEVEL
                        val channelBalanceR = data[REQUEST_RESULT_INDEX_CHANNEL_BAL_R].toInt()
                        val channelBalanceL = data[REQUEST_RESULT_INDEX_CHANNEL_BAL_L].toInt()
                        channelBalance = if (channelBalanceR > 0) {
                            channelBalanceR
                        } else if (channelBalanceL > 0) {
                            -channelBalanceL
                        } else {
                            FiioKa5Defaults.CHANNEL_BALANCE
                        }
                    } else if (command.contentEquals(fiioKa5.getFilter)) {
                        dacMode = DacMode.findByIdOrDefault(
                            id = data[REQUEST_RESULT_INDEX_DAC_MODE]
                        )
                        gain = Gain.findByIdOrDefault(
                            id = data[REQUEST_RESULT_INDEX_GAIN]
                        )
                        filter = Filter.findByIdOrDefault(
                            id = data[REQUEST_RESULT_INDEX_FILTER]
                        )
                        hardwareMuteEnabled = data[REQUEST_RESULT_INDEX_HW_MUTE].toInt() == 1
                    } else if (command.contentEquals(fiioKa5.getOtherState)) {
                        spdifOutEnabled = data[REQUEST_RESULT_INDEX_SPDIF_OUT].toInt() == 1
                        displayTimeout =
                            data[REQUEST_RESULT_INDEX_DISPLAY_TIMEOUT].toInt()
                        val rawDisplayBrightness =
                            data[REQUEST_RESULT_INDEX_DISPLAY_BRIGHTNESS].toInt()
                        displayBrightness = this@FiioKa5UsbCommunicationRepository
                            .displayBrightness
                            .filter { (_, value) -> rawDisplayBrightness == value }
                            .keys
                            .firstOrNull()
                            ?: FiioKa5Defaults.DISPLAY_BRIGHTNESS
                        displayInvertEnabled =
                            data[REQUEST_RESULT_INDEX_DISPLAY_INVERT].toInt() == 1
                    }
                }
                connection.close()
                fiioKa5.copy(
                    channelBalance = channelBalance,
                    dacMode = dacMode,
                    displayBrightness = displayBrightness,
                    displayInvertEnabled = displayInvertEnabled,
                    displayTimeout = displayTimeout,
                    filter = filter,
                    firmwareVersion = firmwareVersion,
                    gain = gain,
                    hardwareMuteEnabled = hardwareMuteEnabled,
                    hidMode = hidMode,
                    sampleRate = sampleRate,
                    spdifOutEnabled = spdifOutEnabled,
                    volumeLevel = volumeLevel,
                    volumeMode = volumeMode
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                connection.close()
                null
            }
        }
    }

    override suspend fun closeConnection(connection: UsbDeviceConnection) {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                connection.close()
            }
        }
    }

    suspend fun setFilter(
        connection: UsbDeviceConnection,
        filter: Filter
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setFilter.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = filter.id
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setGain(
        connection: UsbDeviceConnection,
        gain: Gain
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setGain.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = gain.id
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setVolumeLevel(
        connection: UsbDeviceConnection,
        volumeMode: VolumeMode,
        @androidx.annotation.IntRange(
            from = FiioKa5Defaults.VOLUME_LEVEL_MIN.toLong(),
            to = FiioKa5Defaults.VOLUME_LEVEL_A_MAX.toLong()
        ) volumeLevel: Int
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setVolumeLevel.copyInto(data)
                val volumeLevels = if (volumeMode == VolumeMode.S120) {
                    volume120
                } else {
                    volume60
                }
                data[REQUEST_PAYLOAD_INDEX_SET] = volumeLevels
                    .entries
                    .find { (key, _) ->
                        key == volumeLevel
                    }
                    ?.value
                    ?.toFloat()
                    ?.toInt()
                    ?.toByte()
                    ?: FiioKa5Defaults.VOLUME_LEVEL.toByte()
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setChannelBalance(
        connection: UsbDeviceConnection,
        @androidx.annotation.IntRange(
            from = FiioKa5Defaults.CHANNEL_BALANCE_MIN.toLong(),
            to = FiioKa5Defaults.CHANNEL_BALANCE_MAX.toLong()
        ) channelBalance: Int
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setChannelBalance.copyInto(data)
                if (channelBalance < 0) {
                    data[REQUEST_PAYLOAD_INDEX_SET] = 0
                    data[REQUEST_PAYLOAD_INDEX_SET + 1] = abs(
                        channelBalance.coerceIn(
                            minimumValue = FiioKa5Defaults.CHANNEL_BALANCE_MIN,
                            maximumValue = FiioKa5Defaults.CHANNEL_BALANCE_MAX
                        )
                    ).toByte()
                } else if (channelBalance > 0) {
                    data[REQUEST_PAYLOAD_INDEX_SET] = abs(
                        channelBalance.coerceIn(
                            minimumValue = FiioKa5Defaults.CHANNEL_BALANCE_MIN,
                            maximumValue = FiioKa5Defaults.CHANNEL_BALANCE_MAX
                        )
                    ).toByte()
                    data[REQUEST_PAYLOAD_INDEX_SET + 1] = 0
                } else {
                    data[REQUEST_PAYLOAD_INDEX_SET] = 0
                    data[REQUEST_PAYLOAD_INDEX_SET + 1] = 0
                }
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setDacMode(
        connection: UsbDeviceConnection,
        dacMode: DacMode
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setDacMode.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = dacMode.id
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setHardwareMuteEnabled(
        connection: UsbDeviceConnection,
        hardwareMuteEnabled: Boolean
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setHardwareMute.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = if (hardwareMuteEnabled) 1 else 0
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setSpdifOutEnabled(
        connection: UsbDeviceConnection,
        spdifOutEnabled: Boolean
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setSpdifOut.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = if (spdifOutEnabled) 1 else 0
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setDisplayTimeout(
        connection: UsbDeviceConnection,
        @androidx.annotation.IntRange(
            from = FiioKa5Defaults.DISPLAY_TIMEOUT_MIN.toLong(),
            to = FiioKa5Defaults.DISPLAY_BRIGHTNESS_MAX.toLong()
        ) displayTimeout: Int
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setDisplayTimeout.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = displayTimeout.coerceIn(
                    minimumValue = FiioKa5Defaults.DISPLAY_TIMEOUT_MIN,
                    maximumValue = FiioKa5Defaults.DISPLAY_TIMEOUT_MAX
                ).toByte()
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setHidMode(
        connection: UsbDeviceConnection,
        hidMode: HidMode
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setHidMode.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = hidMode.id
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setDisplayBrightness(
        connection: UsbDeviceConnection,
        @androidx.annotation.IntRange(
            from = FiioKa5Defaults.DISPLAY_BRIGHTNESS_MIN.toLong(),
            to = FiioKa5Defaults.DISPLAY_BRIGHTNESS_MAX.toLong()
        ) displayBrightness: Int
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setDisplayBrightness.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = this@FiioKa5UsbCommunicationRepository
                    .displayBrightness
                    .entries
                    .find { (key, _) -> displayBrightness == key }
                    ?.value
                    ?.toByte()
                    ?: FiioKa5Defaults.DISPLAY_BRIGHTNESS.toByte()
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setDisplayInvertEnabled(
        connection: UsbDeviceConnection,
        displayInvertEnabled: Boolean
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setDisplayInvert.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = if (displayInvertEnabled) 1 else 0
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setVolumeMode(
        connection: UsbDeviceConnection,
        volumeMode: VolumeMode
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                FiioKa5().setVolumeMode.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = volumeMode.id
                mutex.withLock {
                    val result = connection.controlTransfer(
                        REQUEST_TYPE_WRITE,
                        REQUEST_ID_WRITE,
                        USB_ENDPOINT,
                        REQUEST_INDEX,
                        data,
                        REQUEST_PAYLOAD_SIZE,
                        TIMEOUT_MS
                    )
                    if (result != REQUEST_PAYLOAD_SIZE) {
                        error("USB control transfer $REQUEST_TYPE_WRITE failed")
                    }
                    delay(DELAY_WRITE)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }
}
