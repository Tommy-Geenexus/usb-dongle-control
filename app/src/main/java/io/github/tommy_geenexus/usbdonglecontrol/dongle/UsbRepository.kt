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

package io.github.tommy_geenexus.usbdonglecontrol.dongle

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommy_geenexus.usbdonglecontrol.INTENT_ACTION_USB_PERMISSION
import io.github.tommy_geenexus.usbdonglecontrol.di.DispatcherIo
import io.github.tommy_geenexus.usbdonglecontrol.suspendRunCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsbRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher
) {

    suspend fun getAttachedDeviceOrNull(): UsbDevice? {
        return withContext(dispatcherIo) {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            manager.deviceList?.values?.find { device ->
                device.toUsbDongleOrNull() != null
            }
        }
    }

    suspend fun openDeviceOrNull(device: UsbDevice): UsbDeviceConnection? {
        return withContext(dispatcherIo) {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            manager.openDevice(device)
        }
    }

    suspend fun hasUsbPermission(device: UsbDevice): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
                val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
                manager.hasPermission(device)
            }.getOrElse { exception ->
                Timber.e(exception)
                false
            }
        }
    }

    suspend fun requestUsbPermission(device: UsbDevice): Boolean {
        return withContext(dispatcherIo) {
            coroutineContext.suspendRunCatching {
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
}
