package com.raven.odyssey.ui.screens.todo.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.odyssey.domain.model.Domain
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoAddMenu(
    onDismiss: () -> Unit = {},
    viewModel: TodoAddViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val titleFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val refocusTitle = remember(titleFocusRequester, keyboardController) {
        {
            titleFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Submit using the ViewModel, and dismiss the sheet when the add succeeds.
    val submit = remember(onDismiss, viewModel) {
        {
            keyboardController?.hide()
            viewModel.addTodo(onAdded = onDismiss)
        }
    }

    LaunchedEffect(Unit) {
        refocusTitle()
    }

    // Local dialog state for picking a time.
    val timePickerDialogVisible = rememberSaveable { mutableStateOf(false) }
    val timePickerState = remember(uiState.selectedHour, uiState.selectedMinute) {
        TimePickerState(uiState.selectedHour, uiState.selectedMinute, false)
    }

    val domainPickerVisible = rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .wrapContentHeight(),
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(24.dp))

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            CustomTransparentTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateUiState(title = it, error = null) },
                placeholder = "What would you like to do?",
                textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester)
                    .onFocusChanged { if (it.isFocused) keyboardController?.show() },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { submit() }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTransparentTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateUiState(description = it) },
                placeholder = "Description",
                textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time Picker Dialog
            if (timePickerDialogVisible.value) {
                TimePickerDialog(
                    state = timePickerState,
                    onConfirm = {
                        viewModel.updateUiState(
                            selectedHour = timePickerState.hour,
                            selectedMinute = timePickerState.minute
                        )
                        timePickerDialogVisible.value = false
                        refocusTitle()
                    },
                    onDismiss = {
                        timePickerDialogVisible.value = false
                        refocusTitle()
                    }
                )
            }

            // Remove the domain selection dialog; we now use an inline picker.
            // (Domain picker is handled via AnimatedVisibility below)

            // 3. Action Row (Date, Flags, Tags, Mic)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TimeChip(
                        hour = uiState.selectedHour,
                        minute = uiState.selectedMinute,
                        onClick = { timePickerDialogVisible.value = true },
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    DateChip()

                    Spacer(modifier = Modifier.width(8.dp))

                    DomainChip(
                        domain = uiState.selectedDomain,
                        onClick = { domainPickerVisible.value = !domainPickerVisible.value }
                    )
                }
            }

            // Inline domain picker (2 rows), shown only after tapping the chip.
            AnimatedVisibility(
                visible = domainPickerVisible.value,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    DomainChoiceGrid2x3(
                        selected = uiState.selectedDomain,
                        onSelect = {
                            viewModel.updateUiState(selectedDomain = it)
                            domainPickerVisible.value = false
                            refocusTitle()
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun CustomTransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val cursorColor = MaterialTheme.colorScheme.primary
    val selectionColors = remember(cursorColor) {
        TextSelectionColors(
            handleColor = cursorColor,
            backgroundColor = cursorColor.copy(alpha = 0.35f)
        )
    }

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        Box(modifier = modifier) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle,
                modifier = Modifier.fillMaxWidth(),
                singleLine = singleLine,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                cursorBrush = SolidColor(cursorColor),
            )
        }
    }
}

// Helper: The "Today" Chip/Button
@Composable
fun DateChip() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = Color(0xFF4285F4), // Google Blue
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Today",
                color = Color(0xFF4285F4),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    state: TimePickerState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Pick Time") },
        text = {
            TimePicker(state = state)
        }
    )
}

@Composable
private fun DomainChoiceGrid2x3(
    selected: Domain,
    onSelect: (Domain) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = Domain.entries
        .filterNot { it == Domain.Void }
        .take(6)

    Column(modifier = modifier.fillMaxWidth()) {
        options.chunked(3).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { domain ->
                    DomainChoiceChip(
                        domain = domain,
                        isSelected = domain == selected,
                        onClick = { onSelect(domain) },
                        modifier = Modifier.weight(1f),
                    )
                }

                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            if (rowIndex != 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DomainChoiceChip(
    domain: Domain,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) domain.color else domain.color.copy(alpha = 0.18f),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else domain.color),
            )
            Text(
                text = domain.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
fun TimeChip(
    hour: Int,
    minute: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = Color(0xFF4285F4),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute),
                color = Color(0xFF4285F4),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DomainChip(
    domain: Domain,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(domain.color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = domain.name,
                color = Color(0xFF4285F4),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
