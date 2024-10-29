package io.github.tommy_geenexus.usbdonglecontrol.dongle.e1da._9038.sg3

import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbServiceDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.e1da.E1daUsbDongle
import io.github.tommy_geenexus.usbdonglecontrol.dongle.e1da._9038.sg3.data.E1da9038Sg3UsbCommand
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class E1da9038Sg3(
    val volumeLevel: Int = E1da9038Sg3Defaults.VOLUME_LEVEL
) : E1daUsbDongle(
    modelName = "#9038SG3",
    productId = PRODUCT_ID
), E1da9038Sg3UsbCommand,
    UsbServiceDongle {

    companion object {

        const val PRODUCT_ID = 24595
    }

    override val getVolumeLevel: ByteArray
        get() = TODO("Not yet implemented")

    @IgnoredOnParcel
    override val setVolumeLevel = byteArrayOf(6, 0, 2)

    override fun displayVolumeLevel(): String {
        TODO("Not yet implemented")
    }
}
