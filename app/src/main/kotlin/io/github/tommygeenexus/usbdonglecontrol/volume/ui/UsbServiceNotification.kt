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

package io.github.tommygeenexus.usbdonglecontrol.volume.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.RemoteException
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.UsbDongle
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.productName
import io.github.tommygeenexus.usbdonglecontrol.core.util.TOP_LEVEL_PACKAGE_NAME
import io.github.tommygeenexus.usbdonglecontrol.core.volume.HardwareVolumeControl
import timber.log.Timber

internal object UsbServiceNotification {

    private const val REQUEST_CODE = 0
    private const val ID_NOTIFICATION_CHANNEL = TOP_LEVEL_PACKAGE_NAME + "NOTIFICATION_CHANNEL"
    const val ID_NOTIFICATION = 1

    const val VOLUME_STEP_SIZE_MIN = 1
    const val VOLUME_STEP_SIZE_MAX = 4

    const val INTENT_ACTION_VOLUME_UP = TOP_LEVEL_PACKAGE_NAME + "VOLUME_UP"
    const val INTENT_ACTION_VOLUME_DOWN = TOP_LEVEL_PACKAGE_NAME + "VOLUME_DOWN"
    const val INTENT_ACTION_VOLUME_STEP_SIZE = TOP_LEVEL_PACKAGE_NAME + "VOLUME_STEP_SIZE"
    const val INTENT_EXTRA_USB_DONGLE = TOP_LEVEL_PACKAGE_NAME + "EXTRA_USB_DONGLE"
    const val INTENT_EXTRA_VOLUME_STEP_SIZE = TOP_LEVEL_PACKAGE_NAME + "EXTRA_VOLUME_STEP_SIZE"

    fun createNotificationChannel(context: Context): NotificationChannel? {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return try {
            var channel = nm.getNotificationChannel(ID_NOTIFICATION_CHANNEL)
            if (channel != null) {
                return channel
            }
            channel = NotificationChannel(
                ID_NOTIFICATION_CHANNEL,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            nm.createNotificationChannel(channel)
            channel
        } catch (e: RemoteException) {
            Timber.e(e)
            null
        }
    }

    fun <D> buildAndShow(
        context: Context,
        usbDongle: D,
        volumeStepSize: Int
    ) where D : UsbDongle, D : HardwareVolumeControl {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            nm.notify(ID_NOTIFICATION, build(context, usbDongle, volumeStepSize))
        } catch (e: RemoteException) {
            Timber.e(e)
        }
    }

    fun <D> build(
        context: Context,
        usbDongle: D,
        volumeStepSize: Int
    ): Notification where D : UsbDongle, D : HardwareVolumeControl = Notification
        .Builder(context, ID_NOTIFICATION_CHANNEL)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(usbDongle.productName())
        .setContentText(
            context.getString(
                R.string.volume_level_steps,
                usbDongle.displayVolumeLevel,
                volumeStepSize
            )
        )
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                REQUEST_CODE,
                context.packageManager.getLaunchIntentForPackage(context.packageName),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .addAction(
            Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_volume_up),
                context.getString(R.string.volume_up),
                PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODE,
                    Intent(INTENT_ACTION_VOLUME_UP).apply {
                        setPackage(context.packageName)
                        putExtra(INTENT_EXTRA_USB_DONGLE, usbDongle)
                        putExtra(INTENT_EXTRA_VOLUME_STEP_SIZE, volumeStepSize)
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            ).build()
        )
        .addAction(
            Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_volume_down),
                context.getString(R.string.volume_down),
                PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODE,
                    Intent(INTENT_ACTION_VOLUME_DOWN).apply {
                        setPackage(context.packageName)
                        putExtra(INTENT_EXTRA_USB_DONGLE, usbDongle)
                        putExtra(INTENT_EXTRA_VOLUME_STEP_SIZE, volumeStepSize)
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            ).build()
        )
        .addAction(
            Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_step),
                context.getString(R.string.volume_steps_up),
                PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODE,
                    Intent(INTENT_ACTION_VOLUME_STEP_SIZE).apply {
                        setPackage(context.packageName)
                        putExtra(INTENT_EXTRA_USB_DONGLE, usbDongle)
                        putExtra(INTENT_EXTRA_VOLUME_STEP_SIZE, volumeStepSize)
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            ).build()
        )
        .setOnlyAlertOnce(true)
        .setOngoing(true)
        .setVisibility(Notification.VISIBILITY_PUBLIC)
        .build()
}

internal fun <D> UsbService.startForeground(
    context: Context,
    usbDongle: D,
    volumeStepSize: Int
) where D : UsbDongle, D : HardwareVolumeControl {
    startForeground(
        UsbServiceNotification.ID_NOTIFICATION,
        UsbServiceNotification.build(
            context,
            usbDongle,
            volumeStepSize
        )
    )
}
