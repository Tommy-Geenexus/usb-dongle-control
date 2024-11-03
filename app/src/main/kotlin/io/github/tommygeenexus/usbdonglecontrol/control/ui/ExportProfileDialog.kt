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

package io.github.tommygeenexus.usbdonglecontrol.control.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tommygeenexus.usbdonglecontrol.R

@Composable
fun ExportProfileDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: (profileName: String) -> Unit = {}
) {
    val textFieldState = remember { mutableStateOf(TextFieldValue()) }
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(textFieldState.value.text.trim()) },
                enabled = textFieldState.value.text.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.export))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.profile_export),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = textFieldState.value.text,
                    onValueChange = { text ->
                        textFieldState.value = TextFieldValue(text = text)
                    },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .focusRequester(focusRequester),
                    label = { Text(text = stringResource(id = R.string.profile_name)) },
                    singleLine = true
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    )
}

@Preview
@Composable
private fun ExportProfileDialogPreview() {
    ExportProfileDialog()
}
