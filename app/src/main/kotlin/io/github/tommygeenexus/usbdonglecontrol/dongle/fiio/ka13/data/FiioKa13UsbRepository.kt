/*
 * Copyright (c) 2024, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka13.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommygeenexus.usbdonglecontrol.core.data.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.core.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.FiioKa13
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.FirmwareVersion
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.IndicatorState
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka13.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.SpdifOut
import io.github.tommygeenexus.usbdonglecontrol.core.extension.claimInterface
import io.github.tommygeenexus.usbdonglecontrol.core.extension.suspendRunCatching
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class FiioKa13UsbRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher
) : UsbRepository(context, dispatcherIo) {

    private companion object {

        const val DELAY_MS = 100L
        const val TIMEOUT_MS = 200

        const val REQUEST_INDEX_SET_FILTER_1 = 0
        const val REQUEST_INDEX_SET_FILTER_VOLUME_2 = 11
        const val REQUEST_INDEX_SET_INDICATOR_STATE_SPDIF_OUT = 2
        const val REQUEST_INDEX_SET_VOLUME_1 = 7

        private const val REQUEST_TYPE = 33
        private const val REQUEST_ID = 9
        private const val REQUEST_VALUE = 512
        private const val REQUEST_INDEX = 0
    }

    // FIXME: Reading current state is not working properly (firmware issue?)
    suspend fun getCurrentState(usbDongle: FiioKa13): Result<FiioKa13> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                Result.success(
                    value = usbDongle.copy(
                        firmwareVersion = FirmwareVersion(displayValue = usbDevice.version)
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    // TODO: Is it actually working?
    suspend fun setFilter(fiioKa13: FiioKa13, filter: Filter): Result<FiioKa13> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val usbInterface = usbDevice.getInterface(0)
                mutex.withLock {
                    check(usbConnection.claimInterface(usbInterface))
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setFilter.first().apply {
                            set(REQUEST_INDEX_SET_FILTER_1, filter.payload)
                        },
                        payloadSize = fiioKa13.setFilter.size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setFilter.last().apply {
                            set(REQUEST_INDEX_SET_FILTER_VOLUME_2, filter.payload)
                        },
                        payloadSize = fiioKa13.setFilter.size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = fiioKa13.copy(filter = filter))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setIndicatorState(
        fiioKa13: FiioKa13,
        indicatorState: IndicatorState
    ): Result<FiioKa13> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val usbInterface = usbDevice.getInterface(0)
                mutex.withLock {
                    check(usbConnection.claimInterface(usbInterface))
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = indicatorState.payload.copyInto(
                            destination = fiioKa13.setIndicatorState,
                            destinationOffset = REQUEST_INDEX_SET_INDICATOR_STATE_SPDIF_OUT
                        ),
                        payloadSize = fiioKa13.setIndicatorState.size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = fiioKa13.copy(indicatorState = indicatorState))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setSpdifOut(fiioKa13: FiioKa13, spdifOut: SpdifOut): Result<FiioKa13> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val usbInterface = usbDevice.getInterface(0)
                mutex.withLock {
                    check(usbConnection.claimInterface(usbInterface))
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = byteArrayOf(spdifOut.payload).copyInto(
                            destination = fiioKa13.setSpdifOut,
                            destinationOffset = REQUEST_INDEX_SET_INDICATOR_STATE_SPDIF_OUT
                        ),
                        payloadSize = fiioKa13.setSpdifOut.size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = fiioKa13.copy(spdifOut = spdifOut))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setVolumeLevel(fiioKa13: FiioKa13, volumeLevel: VolumeLevel): Result<FiioKa13> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val usbInterface = usbDevice.getInterface(0)
                val payload = volumeLevel.displayValueAndPayload.toByte()
                mutex.withLock {
                    check(usbConnection.claimInterface(usbInterface))
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setVolumeLevel.first().apply {
                            set(REQUEST_INDEX_SET_VOLUME_1, payload)
                        },
                        payloadSize = fiioKa13.setVolumeLevel.first().size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setVolumeLevel[1].apply {
                            set(REQUEST_INDEX_SET_VOLUME_1, payload)
                        },
                        payloadSize = fiioKa13.setVolumeLevel.first().size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setVolumeLevel[2].apply {
                            set(REQUEST_INDEX_SET_FILTER_VOLUME_2, payload)
                        },
                        payloadSize = fiioKa13.setVolumeLevel.first().size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setVolumeLevel[3].apply {
                            set(REQUEST_INDEX_SET_FILTER_VOLUME_2, payload)
                        },
                        payloadSize = fiioKa13.setVolumeLevel.first().size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setVolumeLevel[4].apply {
                            set(REQUEST_INDEX_SET_FILTER_VOLUME_2, payload)
                        },
                        payloadSize = fiioKa13.setVolumeLevel.first().size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    usbConnection.controlWrite(
                        requestType = REQUEST_TYPE,
                        requestId = REQUEST_ID,
                        requestValue = REQUEST_VALUE,
                        requestIndex = REQUEST_INDEX,
                        payload = fiioKa13.setVolumeLevel.last().apply {
                            set(REQUEST_INDEX_SET_FILTER_VOLUME_2, payload)
                        },
                        payloadSize = fiioKa13.setVolumeLevel.first().size,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = fiioKa13.copy(volumeLevel = volumeLevel))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setAll(
        fiioKa13: FiioKa13,
        filter: Filter,
        indicatorState: IndicatorState,
        spdifOut: SpdifOut,
        volumeLevel: VolumeLevel
    ): Result<FiioKa13> = withContext(dispatcherIo) {
        setFilter(fiioKa13, filter)
        setIndicatorState(fiioKa13, indicatorState)
        setSpdifOut(fiioKa13, spdifOut)
        setVolumeLevel(fiioKa13, volumeLevel)
    }
}
