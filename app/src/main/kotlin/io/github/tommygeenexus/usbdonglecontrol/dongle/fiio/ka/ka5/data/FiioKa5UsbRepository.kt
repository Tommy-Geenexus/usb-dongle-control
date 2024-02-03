/*
 * Copyright (c) 2022-2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommygeenexus.usbdonglecontrol.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.dongle.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.ChannelBalance
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DacMode
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DisplayBrightness
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DisplayInvert
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.DisplayTimeout
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.FirmwareVersion
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.HardwareMute
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.HidMode
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.SampleRate
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.SpdifOut
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.VolumeMode
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.create
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.createFromPayload
import io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.suspendRunCatching
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class FiioKa5UsbRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher
) : UsbRepository(context, dispatcherIo) {

    private companion object {

        const val DELAY_MS = 100L
        const val TIMEOUT_MS = 1000

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

    suspend fun getCurrentState(usbDongle: FiioKa5): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                val commands = listOf(
                    usbDongle.getVersion,
                    usbDongle.getSampleRate,
                    usbDongle.getVolumeLevel,
                    usbDongle.getFilter,
                    usbDongle.getOtherState
                )
                var gain: Gain = Gain.default()
                var dacMode: DacMode = DacMode.default()
                var hardwareMute: HardwareMute = HardwareMute.default()
                var hidMode: HidMode = HidMode.default()
                var volumeMode: VolumeMode = VolumeMode.default()
                var firmwareVersion: FirmwareVersion = FirmwareVersion.default()
                var sampleRate: SampleRate = SampleRate.default()
                var volumeLevel: VolumeLevel = VolumeLevel.default()
                var channelBalance: ChannelBalance = ChannelBalance.default()
                var filter: Filter = Filter.default()
                var spdifOut: SpdifOut = SpdifOut.default()
                var displayTimeout: DisplayTimeout = DisplayTimeout.default()
                var displayBrightness: DisplayBrightness = DisplayBrightness.default()
                var displayInvert: DisplayInvert = DisplayInvert.default()
                mutex.withLock {
                    commands.forEach { command ->
                        data.fill(0)
                        command.copyInto(data)
                        usbConnection.controlWriteAndRead(
                            payload = data,
                            payloadSize = REQUEST_PAYLOAD_SIZE,
                            transferTimeout = TIMEOUT_MS,
                            delayInMillisecondsAfterTransfer = DELAY_MS
                        )
                        if (command.contentEquals(usbDongle.getVersion)) {
                            hidMode = HidMode.findByIdOrDefault(
                                id = data[REQUEST_RESULT_INDEX_HID_MODE]
                            )
                            volumeMode = VolumeMode.findByIdOrDefault(
                                id = data[REQUEST_RESULT_INDEX_VOLUME_MODE]
                            )
                            firmwareVersion = FirmwareVersion.createFromPayload(
                                payload = data[REQUEST_RESULT_INDEX_VERSION]
                            )
                        } else if (command.contentEquals(usbDongle.getSampleRate)) {
                            sampleRate = SampleRate.create(
                                key = data[REQUEST_RESULT_INDEX_SAMPLE_RATE].toInt()
                            )
                        } else if (command.contentEquals(usbDongle.getVolumeLevel)) {
                            volumeLevel = VolumeLevel.createFromPayload(
                                payload = data[REQUEST_RESULT_INDEX_VOLUME_LEVEL].toUByte().toInt(),
                                volumeMode = volumeMode
                            )
                            channelBalance = ChannelBalance.createFromPayload(
                                channelRight = data[REQUEST_RESULT_INDEX_CHANNEL_BAL_R].toInt(),
                                channelLeft = data[REQUEST_RESULT_INDEX_CHANNEL_BAL_L].toInt()
                            )
                        } else if (command.contentEquals(usbDongle.getFilter)) {
                            dacMode = DacMode.findByIdOrDefault(
                                id = data[REQUEST_RESULT_INDEX_DAC_MODE]
                            )
                            gain = Gain.findByIdOrDefault(
                                id = data[REQUEST_RESULT_INDEX_GAIN]
                            )
                            filter = Filter.findByIdOrDefault(
                                id = data[REQUEST_RESULT_INDEX_FILTER]
                            )
                            hardwareMute = HardwareMute(
                                isEnabled = data[REQUEST_RESULT_INDEX_HW_MUTE].toInt() == 1
                            )
                        } else if (command.contentEquals(usbDongle.getOtherState)) {
                            spdifOut = SpdifOut(
                                isEnabled = data[REQUEST_RESULT_INDEX_SPDIF_OUT].toInt() == 1
                            )
                            displayTimeout = DisplayTimeout.createFromPayload(
                                payload = data[REQUEST_RESULT_INDEX_DISPLAY_TIMEOUT].toInt()
                            )
                            displayBrightness = DisplayBrightness.createFromPayload(
                                payload = data[REQUEST_RESULT_INDEX_DISPLAY_BRIGHTNESS]
                                    .toUByte()
                                    .toInt()
                            )
                            displayInvert = DisplayInvert(
                                isEnabled = data[REQUEST_RESULT_INDEX_DISPLAY_INVERT].toInt() == 1
                            )
                        }
                    }
                }
                Result.success(
                    value = usbDongle.copy(
                        channelBalance = channelBalance,
                        dacMode = dacMode,
                        displayBrightness = displayBrightness,
                        displayInvert = displayInvert,
                        displayTimeout = displayTimeout,
                        filter = filter,
                        firmwareVersion = firmwareVersion,
                        gain = gain,
                        hardwareMute = hardwareMute,
                        hidMode = hidMode,
                        sampleRate = sampleRate,
                        spdifOut = spdifOut,
                        volumeLevel = volumeLevel,
                        volumeMode = volumeMode
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun getVolumeLevelAndMode(usbDongle: FiioKa5): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                val commands = listOf(
                    usbDongle.getVersion,
                    usbDongle.getVolumeLevel
                )
                var volumeMode: VolumeMode = VolumeMode.default()
                var volumeLevel: VolumeLevel = VolumeLevel.default()
                mutex.withLock {
                    commands.forEach { command ->
                        data.fill(0)
                        command.copyInto(data)
                        usbConnection.controlWriteAndRead(
                            payload = data,
                            payloadSize = REQUEST_PAYLOAD_SIZE,
                            transferTimeout = TIMEOUT_MS,
                            delayInMillisecondsAfterTransfer = DELAY_MS
                        )
                        if (command.contentEquals(usbDongle.getVersion)) {
                            volumeMode = VolumeMode.findByIdOrDefault(
                                id = data[REQUEST_RESULT_INDEX_VOLUME_MODE]
                            )
                        } else if (command.contentEquals(usbDongle.getVolumeLevel)) {
                            volumeLevel = VolumeLevel.createFromPayload(
                                payload = data[REQUEST_RESULT_INDEX_VOLUME_LEVEL].toUByte().toInt(),
                                volumeMode = volumeMode
                            )
                        }
                    }
                }
                Result.success(
                    value = usbDongle.copy(
                        volumeLevel = volumeLevel,
                        volumeMode = volumeMode
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setChannelBalance(
        fiioKa5: FiioKa5,
        channelBalance: ChannelBalance
    ): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setChannelBalance.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = channelBalance.payload.first.toByte()
                data[REQUEST_PAYLOAD_INDEX_SET + 1] = channelBalance.payload.second.toByte()
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(channelBalance = channelBalance))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setDacMode(fiioKa5: FiioKa5, dacMode: DacMode): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setDacMode.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = dacMode.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(dacMode = dacMode))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setDisplayBrightness(
        fiioKa5: FiioKa5,
        displayBrightness: DisplayBrightness
    ): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setDisplayBrightness.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = displayBrightness.payload.toByte()
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(displayBrightness = displayBrightness))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setDisplayInvert(fiioKa5: FiioKa5, displayInvert: DisplayInvert): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setDisplayInvert.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = displayInvert.payload
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(displayInvert = displayInvert))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setDisplayTimeout(
        fiioKa5: FiioKa5,
        displayTimeout: DisplayTimeout
    ): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setDisplayTimeout.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = displayTimeout.payload.toByte()
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(displayTimeout = displayTimeout))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setFilter(fiioKa5: FiioKa5, filter: Filter): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setFilter.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = filter.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(filter = filter))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setGain(fiioKa5: FiioKa5, gain: Gain): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setGain.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = gain.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(gain = gain))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setHardwareMute(fiioKa5: FiioKa5, hardwareMute: HardwareMute): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setHardwareMute.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = hardwareMute.payload
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(hardwareMute = hardwareMute))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setHidMode(fiioKa5: FiioKa5, hidMode: HidMode): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setHidMode.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = hidMode.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(hidMode = hidMode))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setSpdifOut(fiioKa5: FiioKa5, spdifOut: SpdifOut): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setSpdifOut.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = spdifOut.payload
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(spdifOut = spdifOut))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setVolumeMode(fiioKa5: FiioKa5, volumeMode: VolumeMode): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setVolumeMode.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = volumeMode.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(volumeMode = volumeMode))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setVolumeLevel(fiioKa5: FiioKa5, volumeLevel: VolumeLevel): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setVolumeLevel.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = volumeLevel.payload.toByte()
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(value = fiioKa5.copy(volumeLevel = volumeLevel))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setAll(
        fiioKa5: FiioKa5,
        channelBalance: ChannelBalance,
        dacMode: DacMode,
        displayBrightness: DisplayBrightness,
        displayInvert: DisplayInvert,
        displayTimeout: DisplayTimeout,
        filter: Filter,
        gain: Gain,
        hardwareMute: HardwareMute,
        hidMode: HidMode,
        spdifOut: SpdifOut,
        volumeLevel: VolumeLevel,
        volumeMode: VolumeMode
    ): Result<FiioKa5> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                fiioKa5.setChannelBalance.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = channelBalance.payload.first.toByte()
                data[REQUEST_PAYLOAD_INDEX_SET + 1] = channelBalance.payload.second.toByte()
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setDacMode.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = dacMode.id
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setDisplayBrightness.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = displayBrightness.payload.toByte()
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setDisplayInvert.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = displayInvert.payload
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setDisplayTimeout.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = displayTimeout.payload.toByte()
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setFilter.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = filter.id
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setGain.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = gain.id
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setHardwareMute.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = hardwareMute.payload
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setHidMode.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = hidMode.id
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setSpdifOut.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = spdifOut.payload
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setVolumeLevel.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = volumeLevel.payload.toByte()
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    fiioKa5.setVolumeMode.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = volumeMode.id
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                Result.success(
                    value = fiioKa5.copy(
                        channelBalance = channelBalance,
                        dacMode = dacMode,
                        displayBrightness = displayBrightness,
                        displayInvert = displayInvert,
                        displayTimeout = displayTimeout,
                        filter = filter,
                        gain = gain,
                        hardwareMute = hardwareMute,
                        hidMode = hidMode,
                        spdifOut = spdifOut,
                        volumeLevel = volumeLevel,
                        volumeMode = volumeMode
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }
}
