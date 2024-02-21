package com.example.chordprogressionmemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordInputScreen(itemName: String = "", onClick: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = itemName)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),

        ) {
            InputForm()


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputForm() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RootNoteDropdownMenuBox()
        ChordQualityDropdownMenuBox()
        BassNoteDropdownMenuBox()
        OctaveDropdownMenuBox()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNoteDropdownMenuBox() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        val noteOptions = NoteName.entries

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("根音") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            noteOptions.forEach {
                DropdownMenuItem(
                    text = { Text(it.displayName()) },
                    onClick = {
                        selectedOptionText = it.displayName()
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordQualityDropdownMenuBox() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        val qualityOptions = ChordQuality.entries

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("種類") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            qualityOptions.forEach {
                DropdownMenuItem(
                    text = { Text(it.displayName()) },
                    onClick = {
                        selectedOptionText = it.displayName()
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BassNoteDropdownMenuBox() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        val noteOptions = listOf(null) + NoteName.entries

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("ベース音") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            noteOptions.forEach { noteName ->

                val displayName = noteName?.let { "on" + it.displayName() } ?: ""
                DropdownMenuItem(
                    text = {
                        Text(displayName)
                    },
                    onClick = {
                        selectedOptionText = displayName
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OctaveDropdownMenuBox() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("4") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        var octaveOptions = (0..7).toList()

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("オクターブ") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            octaveOptions.forEach { octave ->

                val displayName = octave.toString()
                DropdownMenuItem(
                    text = {
                        Text(displayName)
                    },
                    onClick = {
                        selectedOptionText = displayName
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }

        }

    }
}