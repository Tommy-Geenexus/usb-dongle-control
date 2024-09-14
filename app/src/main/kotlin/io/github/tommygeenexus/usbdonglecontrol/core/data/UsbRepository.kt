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

package io.github.tommygeenexus.usbdonglecontrol.core.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommygeenexus.usbdonglecontrol.core.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UnsupportedUsbDongleException
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.isSupportedUsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.toUsbDongleOrNull
import io.github.tommygeenexus.usbdonglecontrol.core.extension.suspendRunCatching
import io.github.tommygeenexus.usbdonglecontrol.core.util.INTENT_ACTION_USB_PERMISSION
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.IllegalStateException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
open class UsbRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher
) {

    protected companion object {

        const val REQUEST_PAYLOAD_SIZE = 7
        const val REQUEST_PAYLOAD_INDEX_SET = 3

        private const val REQUEST_TYPE_WRITE = 67
        private const val REQUEST_TYPE_READ = 195
        private const val REQUEST_ID_READ = 161
        private const val REQUEST_ID_WRITE = 160
        private const val REQUEST_INDEX = 2464
        private const val REQUEST_VALUE = 0
    }

    protected val mutex = Mutex()

    suspend fun getFirstAttachedUsbDongle(): Result<Triple<UsbDevice, UsbDongle, Boolean>> =
        withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val device = (context.getSystemService(Context.USB_SERVICE) as UsbManager)
                    .deviceList
                    ?.values
                    ?.toList()
                    ?.find { device -> device.isSupportedUsbDongle() }
                val usbDongle = device.toUsbDongleOrNull()
                val isUsbPermissionGranted = device != null && hasUsbPermission(device)
                if (device != null && usbDongle != null) {
                    Result.success(Triple(device, usbDongle, isUsbPermissionGranted))
                } else {
                    error(UnsupportedUsbDongleException())
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }

    private suspend fun hasUsbPermission(device: UsbDevice): Boolean = withContext(dispatcherIo) {
        coroutineContext.suspendRunCatching {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            manager.hasPermission(device)
        }.getOrElse { exception ->
            Timber.e(exception)
            false
        }
    }

    @Throws(UnsupportedUsbDongleException::class, IllegalStateException::class)
    suspend fun openFirstAttachedUsbDongleOrThrow(): UsbDeviceConnection =
        withContext(dispatcherIo) {
            val (usbDevice, _, _) = getFirstAttachedUsbDongle().getOrThrow()
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            manager.openDevice(usbDevice).also { connection ->
                if (connection == null) {
                    error("manager.openDevice() failed")
                }
            }
        }

    suspend fun requestUsbPermission(device: UsbDevice): Result<Boolean> =
        withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
                if (!hasUsbPermission(device)) {
                    manager.requestPermission(
                        device,
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent(INTENT_ACTION_USB_PERMISSION).apply {
                                setPackage(context.packageName)
                            },
                            PendingIntent.FLAG_MUTABLE
                        )
                    )
                    Result.success(true)
                } else {
                    Result.success(false)
                }
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }

    @Throws(IllegalStateException::class)
    suspend fun UsbDeviceConnection.controlWriteAndRead(
        payload: ByteArray,
        payloadSize: Int,
        transferTimeout: Int,
        delayInMillisecondsAfterTransfer: Long
    ) = withContext(dispatcherIo) {
        var result = controlTransfer(
            REQUEST_TYPE_WRITE,
            REQUEST_ID_WRITE,
            REQUEST_VALUE,
            REQUEST_INDEX,
            payload,
            payloadSize,
            transferTimeout
        )
        if (result != payloadSize) {
            error("USB control transfer $REQUEST_TYPE_WRITE failed")
        }
        delay(delayInMillisecondsAfterTransfer)
        result = controlTransfer(
            REQUEST_TYPE_READ,
            REQUEST_ID_READ,
            REQUEST_VALUE,
            REQUEST_INDEX,
            payload,
            payloadSize,
            transferTimeout
        )
        if (result != payloadSize) {
            error("USB control transfer $REQUEST_TYPE_READ failed")
        }
        delay(delayInMillisecondsAfterTransfer)
    }

    @Throws(IllegalStateException::class)
    suspend fun UsbDeviceConnection.controlWrite(
        payload: ByteArray,
        payloadSize: Int,
        transferTimeout: Int,
        delayInMillisecondsAfterTransfer: Long
    ) = withContext(dispatcherIo) {
        val result = controlTransfer(
            REQUEST_TYPE_WRITE,
            REQUEST_ID_WRITE,
            REQUEST_VALUE,
            REQUEST_INDEX,
            payload,
            payloadSize,
            transferTimeout
        )
        if (result != payloadSize) {
            error("USB control transfer $REQUEST_TYPE_WRITE failed")
        }
        delay(delayInMillisecondsAfterTransfer)
    }
}
