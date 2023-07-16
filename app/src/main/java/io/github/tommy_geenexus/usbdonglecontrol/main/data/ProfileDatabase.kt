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

package io.github.tommy_geenexus.usbdonglecontrol.main.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5Converters
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.fiio.ka.ka5.data.db.FiioKa5ProfileDao
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.db.MoondropDawn44Converters
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.db.MoondropDawn44Profile
import io.github.tommy_geenexus.usbdonglecontrol.dongle.moondrop.dawn.dawn44.data.db.MoondropDawn44ProfileDao

@Database(
    entities = [
        FiioKa5Profile::class,
        MoondropDawn44Profile::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    FiioKa5Converters::class,
    MoondropDawn44Converters::class
)
abstract class ProfileDatabase : RoomDatabase() {

    abstract fun fiioKa5ProfileDao(): FiioKa5ProfileDao

    abstract fun moondropDawn44ProfileDao(): MoondropDawn44ProfileDao
}
