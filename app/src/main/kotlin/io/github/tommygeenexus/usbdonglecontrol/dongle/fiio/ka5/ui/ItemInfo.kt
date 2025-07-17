/*
 * Copyright (c) 2022-2025, Tom Geiselmann (tomgapplicationsdevelopment@gmail.com)
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

package io.github.tommygeenexus.usbdonglecontrol.dongle.fiio.ka5.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import io.github.tommygeenexus.usbdonglecontrol.R
import io.github.tommygeenexus.usbdonglecontrol.core.contract.ActivityResultContractViewUrl
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.FiioKa5
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.FirmwareVersion
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.SampleRate
import io.github.tommygeenexus.usbdonglecontrol.core.dongle.fiio.ka5.feature.default
import io.github.tommygeenexus.usbdonglecontrol.core.ui.UsbDongleControlIconRow
import io.github.tommygeenexus.usbdonglecontrol.theme.cardPadding

@Composable
fun ItemInfo(
    modifier: Modifier = Modifier,
    firmwareVersion: String = FirmwareVersion.default().displayValue,
    firmwareUri: Uri = FiioKa5.FIRMWARE_URL.toUri(),
    sampleRate: String = SampleRate.default().displayValue
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = cardPadding)) {
            val viewUrl = rememberLauncherForActivityResult(ActivityResultContractViewUrl()) { }
            UsbDongleControlIconRow(
                textRes = R.string.fw_version,
                textFormatArg = firmwareVersion,
                contentDescriptionRes = R.string.fw_version,
                imageVector = Icons.Outlined.BuildCircle,
                modifier = Modifier.clickable { viewUrl.launch(firmwareUri) }
            )
            UsbDongleControlIconRow(
                textRes = R.string.sample_rate,
                textFormatArg = sampleRate,
                contentDescriptionRes = R.string.sample_rate,
                imageVector = Icons.Outlined.MusicNote
            )
        }
    }
}

@Preview
@Composable
private fun ItemInfoPreview() {
    ItemInfo()
}
