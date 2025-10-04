# USB dongle control
[![License](https://img.shields.io/github/license/Tommy-Geenexus/usb-dongle-control)](https://mit-license.org/)
![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/Tommy-Geenexus/usb-dongle-control/total)<p>
![GitHub Release](https://img.shields.io/github/v/release/Tommy-Geenexus/usb-dongle-control)
![GitHub Downloads (all assets, latest release)](https://img.shields.io/github/downloads/Tommy-Geenexus/usb-dongle-control/latest/total)
[![Get it at IzzyOnDroid](https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/io.github.tommygeenexus.usbdonglecontrol)](https://apt.izzysoft.de/packages/io.github.tommygeenexus.usbdonglecontrol)
[![RB Status](https://shields.rbtlog.dev/simple/io.github.tommygeenexus.usbdonglecontrol)](https://shields.rbtlog.dev/io.github.tommygeenexus.usbdonglecontrol)<p>
[![Assemble](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/assemble.yml/badge.svg)](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/assemble.yml)
[![Detekt](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/detekt.yml/badge.svg)](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/detekt.yml)
[![Ktlint](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/ktlint.yml/badge.svg)](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/ktlint.yml)
[![Spotless](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/spotless.yml/badge.svg)](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/spotless.yml)
[![Lint](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/lint.yml/badge.svg)](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/lint.yml)
[![Deploy](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/deploy.yml/badge.svg)](https://github.com/Tommy-Geenexus/usb-dongle-control/actions/workflows/deploy.yml)

## Description
Android application for controlling USB audio dongles.

## Supported devices
### Full support
- FiiO KA5
- Moondrop Dawn 3.5mm
- Moondrop Dawn 4.4mm
- Moondrop Dawn Pro
- Moondrop Moonriver 2 TI

### Partial support
- FiiO KA13 (writing works but reading is not implemented)

### E1DA #9038 series support (enable at your own risk!)
Not included in releases, but partially implemented in app (my device died so no further testing is possible).
You can build your own E1DA enabled releases by reverting [this commit](https://github.com/Tommy-Geenexus/usb-dongle-control/commit/a96a246af82ab05494a40ecbf03a141564bffc9e).

## Download
<a href='https://github.com/Tommy-Geenexus/usb-dongle-control/releases/latest'><img alt='Get it on GitHub' height='80' src='https://s1.ax1x.com/2023/01/12/pSu1a36.png'/></a>
<a href='https://apt.izzysoft.de/packages/io.github.tommygeenexus.usbdonglecontrol'><img alt='Get it at IzzyOnDroid' height='80' src='https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png'/></a>
  
You can [verify](https://developer.android.com/tools/apksigner#usage-verify) the signing certificate on the APK matches this SHA256 fingerprint:

```E9:3B:0B:2E:AF:EE:95:0D:EF:8B:E0:48:98:D3:45:25:22:13:41:57:B0:35:ED:EC:7E:84:F4:2E:F5:F9:38:E6```
