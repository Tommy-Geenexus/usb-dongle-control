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

package io.github.tommygeenexus.usbdonglecontrol.dongle.moondrop.dawn.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommygeenexus.usbdonglecontrol.core.data.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.core.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.Gain
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn35
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawn44
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.MoondropDawnPro
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.IndicatorState
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.moondrop.dawn.feature.createFromPayload
import io.github.tommygeenexus.usbdonglecontrol.core.extension.suspendRunCatching
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class MoondropDawnUsbRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher
) : UsbRepository(context, dispatcherIo) {

    private companion object {

        const val DELAY_MS = 100L
        const val TIMEOUT_MS = 100

        const val REQUEST_RESULT_INDEX_FILTER = 3
        const val REQUEST_RESULT_INDEX_GAIN_VOLUME_LEVEL = 4
        const val REQUEST_RESULT_INDEX_INDICATOR_STATE = 5
    }

    suspend fun getCurrentState(usbDongle: MoondropDawn): Result<MoondropDawn> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                usbDongle.getAny.copyInto(data)
                val filter: Filter
                val gain: Gain
                val indicatorState: IndicatorState
                mutex.withLock {
                    usbConnection.controlWriteAndRead(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    filter = Filter.findByIdOrDefault(
                        id = data[REQUEST_RESULT_INDEX_FILTER]
                    )
                    gain = Gain.findByIdOrDefault(
                        id = data[REQUEST_RESULT_INDEX_GAIN_VOLUME_LEVEL]
                    )
                    indicatorState = IndicatorState.findByIdOrDefault(
                        id = data[REQUEST_RESULT_INDEX_INDICATOR_STATE]
                    )
                    data.fill(0)
                    usbDongle.getVolumeLevel.copyInto(data)
                    usbConnection.controlWriteAndRead(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                val volumeLevel = VolumeLevel.createFromPayload(
                    payload = data[REQUEST_RESULT_INDEX_GAIN_VOLUME_LEVEL].toInt()
                )
                when (usbDongle) {
                    is MoondropDawn35 -> {
                        Result.success(
                            value = usbDongle.copy(
                                filter = filter,
                                gain = gain,
                                indicatorState = indicatorState,
                                volumeLevel = volumeLevel
                            )
                        )
                    }
                    is MoondropDawn44 -> {
                        Result.success(
                            value = usbDongle.copy(
                                filter = filter,
                                gain = gain,
                                indicatorState = indicatorState,
                                volumeLevel = volumeLevel
                            )
                        )
                    }
                    is MoondropDawnPro -> {
                        Result.success(
                            value = usbDongle.copy(
                                filter = filter,
                                gain = gain,
                                indicatorState = indicatorState,
                                volumeLevel = volumeLevel
                            )
                        )
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setFilter(moondropDawn: MoondropDawn, filter: Filter): Result<MoondropDawn> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                moondropDawn.setFilter.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = filter.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                when (moondropDawn) {
                    is MoondropDawn35 -> {
                        Result.success(value = moondropDawn.copy(filter = filter))
                    }
                    is MoondropDawn44 -> {
                        Result.success(value = moondropDawn.copy(filter = filter))
                    }
                    is MoondropDawnPro -> {
                        Result.success(value = moondropDawn.copy(filter = filter))
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setGain(moondropDawn: MoondropDawn, gain: Gain): Result<MoondropDawn> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                moondropDawn.setGain.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = gain.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                when (moondropDawn) {
                    is MoondropDawn35 -> {
                        Result.success(value = moondropDawn.copy(gain = gain))
                    }
                    is MoondropDawn44 -> {
                        Result.success(value = moondropDawn.copy(gain = gain))
                    }
                    is MoondropDawnPro -> {
                        Result.success(value = moondropDawn.copy(gain = gain))
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setIndicatorState(
        moondropDawn: MoondropDawn,
        indicatorState: IndicatorState
    ): Result<MoondropDawn> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                moondropDawn.setIndicatorState.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = indicatorState.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                when (moondropDawn) {
                    is MoondropDawn35 -> {
                        Result.success(value = moondropDawn.copy(indicatorState = indicatorState))
                    }
                    is MoondropDawn44 -> {
                        Result.success(value = moondropDawn.copy(indicatorState = indicatorState))
                    }
                    is MoondropDawnPro -> {
                        Result.success(value = moondropDawn.copy(indicatorState = indicatorState))
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setVolumeLevel(
        moondropDawn: MoondropDawn,
        volumeLevel: VolumeLevel
    ): Result<MoondropDawn> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                moondropDawn.setVolumeLevel.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = volumeLevel.displayValueAndPayload.toByte()
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                when (moondropDawn) {
                    is MoondropDawn35 -> {
                        Result.success(value = moondropDawn.copy(volumeLevel = volumeLevel))
                    }
                    is MoondropDawn44 -> {
                        Result.success(value = moondropDawn.copy(volumeLevel = volumeLevel))
                    }
                    is MoondropDawnPro -> {
                        Result.success(value = moondropDawn.copy(volumeLevel = volumeLevel))
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setAll(
        moondropDawn: MoondropDawn,
        filter: Filter,
        gain: Gain,
        indicatorState: IndicatorState,
        volumeLevel: VolumeLevel
    ): Result<MoondropDawn> {
        return withContext(dispatcherIo) {
            val usbConnection = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                moondropDawn.setFilter.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = filter.id
                mutex.withLock {
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    moondropDawn.setGain.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = gain.id
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    moondropDawn.setIndicatorState.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = indicatorState.id
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    moondropDawn.setVolumeLevel.copyInto(data)
                    data[REQUEST_PAYLOAD_INDEX_SET] = volumeLevel.displayValueAndPayload.toByte()
                    usbConnection.controlWrite(
                        payload = data,
                        payloadSize = REQUEST_PAYLOAD_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                }
                when (moondropDawn) {
                    is MoondropDawn35 -> {
                        Result.success(
                            value = moondropDawn.copy(
                                filter = filter,
                                gain = gain,
                                indicatorState = indicatorState,
                                volumeLevel = volumeLevel
                            )
                        )
                    }
                    is MoondropDawn44 -> {
                        Result.success(
                            value = moondropDawn.copy(
                                filter = filter,
                                gain = gain,
                                indicatorState = indicatorState,
                                volumeLevel = volumeLevel
                            )
                        )
                    }
                    is MoondropDawnPro -> {
                        Result.success(
                            value = moondropDawn.copy(
                                filter = filter,
                                gain = gain,
                                indicatorState = indicatorState,
                                volumeLevel = volumeLevel
                            )
                        )
                    }
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }
}
