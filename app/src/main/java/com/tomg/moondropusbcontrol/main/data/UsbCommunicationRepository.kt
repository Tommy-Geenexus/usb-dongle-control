/*
 * Copyright (c) 2022, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package com.tomg.moondropusbcontrol.main.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.tomg.moondropusbcontrol.INTENT_ACTION_USB_PERMISSION
import com.tomg.moondropusbcontrol.main.Filter
import com.tomg.moondropusbcontrol.main.Gain
import com.tomg.moondropusbcontrol.main.IndicatorState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsbCommunicationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private companion object {

        const val ID_VENDOR = 12230
        const val ID_PRODUCT = 61543

        const val DELAY_MS = 100L
        const val TIMEOUT_MS = 100

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

    suspend fun getAttachedDeviceOrNull(): UsbDevice? {
        return withContext(Dispatchers.IO) {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            manager.deviceList?.values?.find { device ->
                device.vendorId == ID_VENDOR && device.productId == ID_PRODUCT
            }
        }
    }

    suspend fun hasUsbPermission(device: UsbDevice): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
                manager.hasPermission(device)
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun requestUsbPermission(device: UsbDevice): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
                if (!hasUsbPermission(device)) {
                    manager.requestPermission(
                        device,
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent(INTENT_ACTION_USB_PERMISSION),
                            PendingIntent.FLAG_MUTABLE
                        )
                    )
                    true
                } else {
                    false
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun getCurrentState(
        connection: UsbDeviceConnection
    ): Triple<Filter, Gain, IndicatorState>? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                MoondropDawnUsbCommand.getAny.copyInto(data)
                var result = connection.controlTransfer(
                    REQUEST_TYPE_WRITE,
                    REQUEST_ID_WRITE,
                    0,
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
                    0,
                    REQUEST_INDEX,
                    data,
                    REQUEST_PAYLOAD_SIZE,
                    TIMEOUT_MS
                )
                if (result != REQUEST_PAYLOAD_SIZE) {
                    error("USB control transfer $REQUEST_TYPE_READ failed")
                }
                connection.close()
                val filter = Filter.findById(data[REQUEST_RESULT_INDEX_FILTER])
                val gain = Gain.findById(data[REQUEST_RESULT_INDEX_GAIN])
                val indicatorState = IndicatorState.findById(
                    data[REQUEST_RESULT_INDEX_INDICATOR_STATE]
                )
                if (filter != null && gain != null && indicatorState != null) {
                    return@runCatching Triple(filter, gain, indicatorState)
                }
                null
            }.getOrElse { exception ->
                Timber.e(exception)
                connection.close()
                null
            }
        }
    }

    suspend fun setFilter(
        connection: UsbDeviceConnection,
        filter: Filter
    ): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                MoondropDawnUsbCommand.setFilter.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = filter.id
                val result = connection.controlTransfer(
                    REQUEST_TYPE_WRITE,
                    REQUEST_ID_WRITE,
                    0,
                    REQUEST_INDEX,
                    data,
                    REQUEST_PAYLOAD_SIZE,
                    TIMEOUT_MS
                )
                if (result != REQUEST_PAYLOAD_SIZE) {
                    error("USB control transfer $REQUEST_TYPE_WRITE failed")
                }
                connection.close()
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                connection.close()
                false
            }
        }
    }

    suspend fun setGain(
        connection: UsbDeviceConnection,
        gain: Gain
    ): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                MoondropDawnUsbCommand.setGain.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = gain.id
                val result = connection.controlTransfer(
                    REQUEST_TYPE_WRITE,
                    REQUEST_ID_WRITE,
                    0,
                    REQUEST_INDEX,
                    data,
                    REQUEST_PAYLOAD_SIZE,
                    TIMEOUT_MS
                )
                if (result != REQUEST_PAYLOAD_SIZE) {
                    error("USB control transfer $REQUEST_TYPE_WRITE failed")
                }
                connection.close()
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                connection.close()
                false
            }
        }
    }

    suspend fun setIndicatorState(
        connection: UsbDeviceConnection,
        indicatorState: IndicatorState
    ): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val data = ByteArray(REQUEST_PAYLOAD_SIZE) { 0 }
                MoondropDawnUsbCommand.setIndicatorState.copyInto(data)
                data[REQUEST_PAYLOAD_INDEX_SET] = indicatorState.id
                val result = connection.controlTransfer(
                    REQUEST_TYPE_WRITE,
                    REQUEST_ID_WRITE,
                    0,
                    REQUEST_INDEX,
                    data,
                    REQUEST_PAYLOAD_SIZE,
                    TIMEOUT_MS
                )
                if (result != REQUEST_PAYLOAD_SIZE) {
                    error("USB control transfer $REQUEST_TYPE_WRITE failed")
                }
                connection.close()
                true
            }.getOrElse { exception ->
                Timber.e(exception)
                connection.close()
                false
            }
        }
    }

    suspend fun openDeviceOrNull(device: UsbDevice): UsbDeviceConnection? {
        return withContext(Dispatchers.IO) {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            manager.openDevice(device)
        }
    }
}
