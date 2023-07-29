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

package io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data

import android.hardware.usb.UsbDeviceConnection
import io.github.tommy_geenexus.usbdonglecontrol.di.DispatcherIo
import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbTransfer
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Filter
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.Gain
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.MoondropDawn44
import io.github.tommy_geenexus.usbdonglecontrol.suspendRunCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoondropDawn44UsbCommunicationRepository @Inject constructor(
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher
) : UsbTransfer {

    private companion object {

        const val DELAY_MS = 100L
        const val TIMEOUT_MS = 100

        const val USB_ENDPOINT = 0

        const val REQUEST_PAYLOAD_INDEX_SET = 3
        const val REQUEST_PAYLOAD_SIZE = 7
        const val REQUEST_TYPE_WRITE = 67
        const val REQUEST_TYPE_READ = 195
        const val REQUEST_ID_READ = 161
        const val REQUEST_ID_WRITE = 160
        const val REQUEST_INDEX = 2464

        const val REQUEST_RESULT_INDEX_FILTER = 3
        const val REQUEST_RESULT_INDEX_GAIN = 4
        const val REQUEST_RESULT_INDEX_INDICATOR_STATE = 5
    }

    override val mutex = Mutex()

    override suspend fun getCurrentState(
        connection: UsbDeviceConnection
    ): MoondropDawn44? {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                val moondropDawn44 = MoondropDawn44()
                moondropDawn44.getAny.copyInto(data)
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
                    delay(DELAY_MS)
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
                    delay(DELAY_MS)
                }
                connection.close()
                val filter = Filter.findByIdOrDefault(
                    id = data[REQUEST_RESULT_INDEX_FILTER]
                )
                val gain = Gain.findByIdOrDefault(
                    id = data[REQUEST_RESULT_INDEX_GAIN]
                )
                val indicatorState = IndicatorState.findByIdOrDefault(
                    id = data[REQUEST_RESULT_INDEX_INDICATOR_STATE]
                )
                moondropDawn44.copy(
                    filter = filter,
                    gain = gain,
                    indicatorState = indicatorState
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
                MoondropDawn44().setFilter.copyInto(data)
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
                    delay(DELAY_MS)
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
                MoondropDawn44().setGain.copyInto(data)
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
                    delay(DELAY_MS)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun setIndicatorState(
        connection: UsbDeviceConnection,
        indicatorState: IndicatorState
    ): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                MoondropDawn44().setIndicatorState.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = indicatorState.id
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
                    delay(DELAY_MS)
                }
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }
}
