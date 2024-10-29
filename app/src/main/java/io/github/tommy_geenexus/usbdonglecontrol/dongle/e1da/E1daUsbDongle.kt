package io.github.tommy_geenexus.usbdonglecontrol.dongle.e1da

import io.github.tommy_geenexus.usbdonglecontrol.dongle.UsbDongle

abstract class E1daUsbDongle(
    override val modelName: String,
    override val productId: Int
) : UsbDongle(
    manufacturerName = "E1DA",
    modelName = modelName,
    vendorId = VENDOR_ID,
    productId = productId
) {

    companion object {

        const val VENDOR_ID = 12230
    }
}
