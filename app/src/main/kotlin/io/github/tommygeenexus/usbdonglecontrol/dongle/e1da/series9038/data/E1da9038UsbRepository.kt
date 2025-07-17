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

package io.github.tommygeenexus.usbdonglecontrol.dongle.e1da.series9038.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.tommygeenexus.usbdonglecontrol.core.data.UsbRepository
import io.github.tommygeenexus.usbdonglecontrol.core.di.DispatcherIo
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.E1da9038
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Filter
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.FirmwareVersion
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.HardwareMute
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.HardwareType
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.MasterClockDivider
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.SampleRate
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.Standby
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.VolumeLevel
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.create
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.e1da.series9038.feature.createFromPayload
import io.github.tommygeenexus.usbdonglecontrol.core.extension.suspendRunCatching
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class E1da9038UsbRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:DispatcherIo private val dispatcherIo: CoroutineDispatcher
) : UsbRepository(context, dispatcherIo) {

    private companion object {

        const val USB_IF_ID = 6
        const val USB_EP_IN = 1
        const val USB_EP_OUT = 0

        const val DELAY_MS = 100L
        const val TIMEOUT_MS = 1000
    }

    suspend fun getCurrentState(usbDongle: E1da9038): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                val filters: MutableList<Filter> = mutableListOf()
                val hardwareType: HardwareType
                val firmwareVersion: FirmwareVersion
                val hardwareMute: HardwareMute
                val masterClockDividersPcm: MutableList<MasterClockDivider> = mutableListOf()
                val masterClockDividersDsd: MutableList<MasterClockDivider> = mutableListOf()
                val sampleRate: SampleRate
                val standby: Standby
                val volumeLevel: VolumeLevel
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbDongle.getHwTypeVersionMuteStandby.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    hardwareType = HardwareType.findByIdOrDefault(
                        id = data[REQUEST_DATA_BYTE_1]
                    )
                    firmwareVersion = FirmwareVersion.createFromPayload(
                        payload = data[REQUEST_DATA_BYTE_2]
                    )
                    hardwareMute = HardwareMute(
                        isEnabled = data[REQUEST_DATA_BYTE_3].toInt() == 1
                    )
                    standby = Standby(
                        isEnabled = data[REQUEST_DATA_BYTE_4].toInt() == 1
                    )
                    data.fill(0)
                    usbDongle.getAudioFormat.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    sampleRate = SampleRate.create(
                        key = data[REQUEST_DATA_BYTE_1].toInt()
                    )
                    data.fill(0)
                    usbDongle.getVolumeLeftRightMinMax.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    volumeLevel = VolumeLevel.createFromPayload(
                        payload = data[REQUEST_DATA_BYTE_1]
                    )
                    data.fill(0)
                    usbDongle.getFilterPcm44To96.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    filters.addAll(
                        elements = listOf(
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_1]
                            ),
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_2]
                            ),
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_3]
                            ),
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_4]
                            )
                        )
                    )
                    data.fill(0)
                    usbDongle.getFilterPcm176To384.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    filters.addAll(
                        elements = listOf(
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_1]
                            ),
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_2]
                            ),
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_3]
                            ),
                            Filter.findByIdOrDefault(
                                id = data[REQUEST_DATA_BYTE_4]
                            )
                        )
                    )
                    data.fill(0)
                    usbDongle.getMasterClockDividerPcm44To96.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    masterClockDividersPcm.addAll(
                        elements = listOf(
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_1]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_2]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_3]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_4])
                        )
                    )
                    data.fill(0)
                    usbDongle.getMasterClockDividerPcm176To384.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    masterClockDividersPcm.addAll(
                        elements = listOf(
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_1]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_2]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_3]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_4])
                        )
                    )
                    data.fill(0)
                    usbDongle.getMasterClockDividerDsd.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    masterClockDividersDsd.addAll(
                        elements = listOf(
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_1]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_2]),
                            MasterClockDivider.findByIdOrDefault(id = data[REQUEST_DATA_BYTE_3])
                        )
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(
                    value = usbDongle.copy(
                        filters = filters,
                        firmwareVersion = firmwareVersion,
                        hardwareMute = hardwareMute,
                        hardwareType = hardwareType,
                        masterClockDividersDsd = masterClockDividersDsd,
                        masterClockDividersPcm = masterClockDividersPcm,
                        sampleRate = sampleRate,
                        standby = standby,
                        volumeLevel = volumeLevel
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun getVolumeLevelLeftRightMinMax(usbDongle: E1da9038): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                usbDongle.getVolumeLeftRightMinMax.copyInto(data)
                val volumeLevel: VolumeLevel
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbDongle.getVolumeLeftRightMinMax.copyInto(data)
                    usbConnection.bulkWriteAndRead(
                        usbEndpointRead = usbInterface.getEndpoint(USB_EP_IN),
                        usbEndpointWrite = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    volumeLevel = VolumeLevel.createFromPayload(
                        payload = data[REQUEST_DATA_BYTE_1]
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = usbDongle.copy(volumeLevel = volumeLevel))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setFilter(e1da9038: E1da9038, filter: Filter, index: Int): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                if (index < REQUEST_DATA_SIZE) {
                    e1da9038.setFilterPcm44To96.copyInto(data)
                    val payload = ByteArray(size = REQUEST_DATA_SIZE) { payloadIndex ->
                        if (payloadIndex == index) {
                            filter.id
                        } else {
                            e1da9038.filters[payloadIndex].id
                        }
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                } else {
                    e1da9038.setFilterPcm176To384.copyInto(data)
                    val payload = ByteArray(size = REQUEST_DATA_SIZE) { payloadIndex ->
                        if (payloadIndex == index - REQUEST_DATA_SIZE) {
                            filter.id
                        } else {
                            e1da9038.filters[payloadIndex + REQUEST_DATA_SIZE].id
                        }
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                }
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(
                    value = e1da9038.copy(
                        filters = e1da9038.filters.toMutableList().apply {
                            set(index, filter)
                        }.toList()
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setHardwareMute(e1da9038: E1da9038, hardwareMute: HardwareMute): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                e1da9038.setHardwareMute.copyInto(data)
                data[REQUEST_DATA_BYTE_1] = hardwareMute.payload
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = e1da9038.copy(hardwareMute = hardwareMute))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setMasterClockDividerDsd(
        e1da9038: E1da9038,
        masterClockDivider: MasterClockDivider,
        index: Int
    ): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                e1da9038.setMasterClockDividerDsd.copyInto(data)
                val payload = ByteArray(size = REQUEST_DATA_SIZE) { payloadIndex ->
                    if (payloadIndex == index) {
                        masterClockDivider.id
                    } else {
                        e1da9038.masterClockDividersDsd.getOrNull(payloadIndex)?.id ?: 0
                    }
                }
                payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(
                    value = e1da9038.copy(
                        masterClockDividersDsd = e1da9038
                            .masterClockDividersDsd
                            .toMutableList()
                            .apply { set(index, masterClockDivider) }
                            .toList()
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setMasterClockDividerPcm(
        e1da9038: E1da9038,
        masterClockDivider: MasterClockDivider,
        index: Int
    ): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                if (index < REQUEST_DATA_SIZE) {
                    e1da9038.setMasterClockDividerPcm44To96.copyInto(data)
                    val payload = ByteArray(size = REQUEST_DATA_SIZE) { payloadIndex ->
                        if (payloadIndex == index) {
                            masterClockDivider.id
                        } else {
                            e1da9038.masterClockDividersPcm[payloadIndex].id
                        }
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                } else {
                    e1da9038.setMasterClockDividerPcm176To384.copyInto(data)
                    val payload = ByteArray(size = REQUEST_DATA_SIZE) { payloadIndex ->
                        if (payloadIndex == index - REQUEST_DATA_SIZE) {
                            masterClockDivider.id
                        } else {
                            e1da9038.masterClockDividersPcm[payloadIndex + REQUEST_DATA_SIZE].id
                        }
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                }
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(
                    value = e1da9038.copy(
                        masterClockDividersPcm = e1da9038
                            .masterClockDividersPcm
                            .toMutableList()
                            .apply { set(index, masterClockDivider) }
                            .toList()
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setStandby(e1da9038: E1da9038, standby: Standby): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                e1da9038.setStandby.copyInto(data)
                data[REQUEST_DATA_BYTE_1] = standby.payload
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = e1da9038.copy(standby = standby))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setVolumeLevel(e1da9038: E1da9038, volumeLevel: VolumeLevel): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val data = ByteArray(REQUEST_PACKET_SIZE)
                e1da9038.setVolumeLevel.copyInto(data)
                data[REQUEST_DATA_BYTE_1] = volumeLevel.payload
                data[REQUEST_DATA_BYTE_2] = volumeLevel.payload
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(value = e1da9038.copy(volumeLevel = volumeLevel))
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }

    suspend fun setAll(
        e1da9038: E1da9038,
        filters: List<Filter>,
        hardwareMute: HardwareMute,
        masterClockDividersDsd: List<MasterClockDivider>,
        masterClockDividersPcm: List<MasterClockDivider>,
        standby: Standby,
        volumeLevel: VolumeLevel
    ): Result<E1da9038> {
        return withContext(dispatcherIo) {
            val (usbDevice, usbConnection) = coroutineContext.suspendRunCatching {
                openFirstAttachedUsbDongleOrThrow()
            }.getOrElse { exception ->
                Timber.e(exception)
                return@withContext Result.failure(exception)
            }
            coroutineContext.suspendRunCatching(onReleaseResources = { usbConnection.close() }) {
                val index = AtomicInteger()
                val data = ByteArray(REQUEST_PACKET_SIZE)
                e1da9038.setFilterPcm44To96.copyInto(data)
                var payload = ByteArray(size = REQUEST_DATA_SIZE) {
                    filters.getOrNull(index.andIncrement)?.id ?: 0
                }
                payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                mutex.withLock {
                    val usbInterface = usbDevice.getInterface(USB_IF_ID)
                    check(usbConnection.claimInterface(usbInterface, true))
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setFilterPcm176To384.copyInto(data)
                    payload = ByteArray(size = REQUEST_DATA_SIZE) {
                        filters.getOrNull(index.andIncrement)?.id ?: 0
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setHardwareMute.copyInto(data)
                    data[REQUEST_DATA_BYTE_1] = hardwareMute.payload
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    index.set(0)
                    e1da9038.setMasterClockDividerDsd.copyInto(data)
                    payload = ByteArray(size = REQUEST_DATA_SIZE) {
                        masterClockDividersDsd.getOrNull(index.andIncrement)?.id ?: 0
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    index.set(0)
                    e1da9038.setMasterClockDividerPcm44To96.copyInto(data)
                    payload = ByteArray(size = REQUEST_DATA_SIZE) {
                        masterClockDividersPcm.getOrNull(index.andIncrement)?.id ?: 0
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setMasterClockDividerPcm176To384.copyInto(data)
                    payload = ByteArray(size = REQUEST_DATA_SIZE) {
                        masterClockDividersPcm.getOrNull(index.andIncrement)?.id ?: 0
                    }
                    payload.copyInto(destination = data, destinationOffset = REQUEST_DATA_BYTE_1)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setInit.copyInto(data)
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setStandby.copyInto(data)
                    data[REQUEST_DATA_BYTE_1] = standby.payload
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    data.fill(0)
                    e1da9038.setVolumeLevel.copyInto(data)
                    data[REQUEST_DATA_BYTE_1] = volumeLevel.payload
                    data[REQUEST_DATA_BYTE_2] = volumeLevel.payload
                    usbConnection.bulkWrite(
                        usbEndpoint = usbInterface.getEndpoint(USB_EP_OUT),
                        payload = data,
                        payloadSize = REQUEST_PACKET_SIZE,
                        transferTimeout = TIMEOUT_MS,
                        delayInMillisecondsAfterTransfer = DELAY_MS
                    )
                    check(usbConnection.releaseInterface(usbInterface))
                }
                Result.success(
                    value = e1da9038.copy(
                        filters = filters,
                        hardwareMute = hardwareMute,
                        masterClockDividersDsd = masterClockDividersDsd,
                        masterClockDividersPcm = masterClockDividersPcm,
                        standby = standby,
                        volumeLevel = volumeLevel
                    )
                )
            }.getOrElse { exception ->
                Timber.e(exception)
                Result.failure(exception)
            }
        }
    }
}
