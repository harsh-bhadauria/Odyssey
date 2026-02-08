package com.raven.odyssey.ui.screens.habit.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.odyssey.domain.model.Domain
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType
import com.raven.odyssey.ui.theme.AppColors

@Composable
fun HabitAddMenu(
    onDismiss: () -> Unit = {},
    viewModel: HabitAddViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val nameFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val refocusName = remember(nameFocusRequester, keyboardController) {
        {
            nameFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    val submit = remember(onDismiss, viewModel, keyboardController) {
        {
            keyboardController?.hide()
            viewModel.addHabit(onAdded = onDismiss)
        }
    }

    val targetFocusRequester = remember { FocusRequester() }

    val scrollState = rememberScrollState()

    val targetText = rememberSaveable { mutableStateOf("") }
    val hasUserEditedTarget = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.type) {
        if (uiState.type !is HabitType.Measurable) {
            targetText.value = ""
            hasUserEditedTarget.value = false
        }
    }

    LaunchedEffect(Unit) {
        refocusName()
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(bottom = 16.dp)
            .wrapContentHeight()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        CustomTransparentTextField(
            value = uiState.name,
            onValueChange = { viewModel.updateUiState(name = it) },
            placeholder = "What habit do you want to build?",
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nameFocusRequester)
                .onFocusChanged { if (it.isFocused) keyboardController?.show() },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }),
        )

//        Spacer(modifier = Modifier.height(8.dp))
//
//        CustomTransparentTextField(
//            value = uiState.description,
//            onValueChange = { viewModel.updateUiState(description = it) },
//            placeholder = "Description",
//            textStyle = TextStyle(fontSize = 14.sp, color = Color.Gray),
//            modifier = Modifier.fillMaxWidth(),
//        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Structured rows (inspired by your HabitAddSheet) ---
        SectionLabel("AREA OF LIFE")
        DomainChoiceGrid2x3(
            selected = uiState.domain,
            onSelect = { viewModel.updateUiState(domain = it) },
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel("FREQUENCY")
        FrequencySegmented(
            selected = uiState.frequency,
            onSelect = { freq ->
                when (freq) {
                    is HabitFrequency.Weekly -> {
                        viewModel.updateUiState(
                            frequency = freq,
                            intervalDays = null,
                        )
                    }

                    is HabitFrequency.Custom -> {
                        viewModel.updateUiState(frequency = freq, intervalDays = freq.intervalDays)
                    }

                    else -> {
                        viewModel.updateUiState(frequency = freq, intervalDays = null)
                    }
                }
            },
        )

        if (uiState.frequency is HabitFrequency.Custom) {
            Spacer(modifier = Modifier.height(12.dp))
            SimpleOutlinedInput(
                value = (uiState.intervalDays ?: 1).toString(),
                onValueChange = { value ->
                    val days = value.toIntOrNull() ?: 1
                    viewModel.updateUiState(
                        intervalDays = days,
                        frequency = HabitFrequency.Custom(days)
                    )
                },
                label = "Interval days",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel("TRACKING TYPE")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HabitTypeChip(
                label = "Yes / No",
                isSelected = uiState.type is HabitType.Binary,
                onClick = {
                    viewModel.updateUiState(
                        type = HabitType.Binary,
                        target = null,
                        unit = null,
                    )
                },
                modifier = Modifier.weight(1f),
            )

            HabitTypeChip(
                label = "Measurable",
                isSelected = uiState.type is HabitType.Measurable,
                onClick = {
                    val unit = uiState.unit

                    viewModel.updateUiState(
                        type = HabitType.Measurable(target = 1, unit = unit),
                        unit = unit,
                        target = null,
                    )

                    targetText.value = ""
                    hasUserEditedTarget.value = false
                },
                modifier = Modifier.weight(1f),
            )
        }

        AnimatedVisibility(
            visible = uiState.type is HabitType.Measurable,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                SimpleOutlinedInput(
                    value = uiState.target,
                    onValueChange = { value ->
                        viewModel.updateUiState(
                            target = value
                        )
                    },
                    label = "Target",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.weight(1f),
                )

                SimpleOutlinedInput(
                    value = uiState.unit,
                    onValueChange = { value ->
                        viewModel.updateUiState(
                            unit = value
                        )
                    },
                    label = "Unit",
                    modifier = Modifier.weight(1f),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )
            }
        }
    }
}

@Composable
private fun CustomTransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Box(modifier = modifier) {
        if (value.isEmpty()) {
            Text(text = placeholder, style = textStyle.copy(color = Color.LightGray))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
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

                // If the last row has fewer than 3, fill remaining space so chips keep same size.
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
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else domain.color),
            )
            Text(
                text = domain.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.Black,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
private fun FrequencySegmented(
    selected: HabitFrequency,
    onSelect: (HabitFrequency) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf("Daily", "Weekly", "Custom")

    val selectedIndex = remember(selected) {
        when (selected) {
            is HabitFrequency.Daily -> 0
            is HabitFrequency.Weekly -> 1
            is HabitFrequency.Custom -> 2
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F0F0))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable {
                        when (index) {
                            0 -> onSelect(HabitFrequency.Daily)
                            1 -> onSelect(HabitFrequency.Weekly)
                            2 -> onSelect(HabitFrequency.Custom(1))
                        }
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.Black,
                )
            }
        }
    }
}

@Composable
private fun HabitTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color.Black else Color(0xFFF0F0F0),
        modifier = modifier,
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun SimpleOutlinedInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    onFocusLost: (() -> Unit)? = null,

    ) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .onFocusChanged { state ->
                    if (!state.isFocused) {
                        onFocusLost?.invoke()
                    }
                }
                .padding(top = 4.dp)
                .background(AppColors.White, RoundedCornerShape(16.dp))
                .padding(12.dp)
        )
    }
}