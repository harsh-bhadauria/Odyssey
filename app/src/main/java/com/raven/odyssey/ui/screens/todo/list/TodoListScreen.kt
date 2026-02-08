package com.raven.odyssey.ui.screens.todo.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.odyssey.domain.model.Todo
import com.raven.odyssey.ui.theme.AppColors
import com.raven.odyssey.ui.theme.Typo

@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TodoListContent(
        uiState = uiState,
        onTodoClick = { viewModel.toggleTodo(it) },
        onToggleOverdue = viewModel::toggleOverdueExpanded,
        onToggleToday = viewModel::toggleTodayExpanded,
        onToggleCompleted = viewModel::toggleCompletedExpanded
    )
}

@Composable
fun TodoListContent(
    uiState: TodoListUiState,
    onTodoClick: (Todo) -> Unit,
    onToggleOverdue: () -> Unit,
    onToggleToday: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            GreetingHeader(
                name = uiState.greetingName,
                subtitle = uiState.greetingSubtitle
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Overdue Section
            if (uiState.overdueTodos.isNotEmpty()) {
                TodoSection(
                    title = "Overdue",
                    count = uiState.overdueTodos.size,
                    todos = uiState.overdueTodos,
                    backgroundColor = AppColors.Red,
                    countColor = AppColors.White,
                    contentColor = Color.White,
                    isExpanded = uiState.isOverdueExpanded,
                    onHeaderClick = onToggleOverdue,
                    onTodoClick = onTodoClick
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Today Section
            if (uiState.todayTodos.isNotEmpty()) {
                TodoSection(
                    title = "Today",
                    count = uiState.todayTodos.size,
                    todos = uiState.todayTodos,
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    countColor = AppColors.Teal,
                    isExpanded = uiState.isTodayExpanded,
                    onHeaderClick = onToggleToday,
                    onTodoClick = onTodoClick
                )
            } else if (uiState.showTodayEmptyMessage) {
                // No todos for today (but there are overdue ones)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nothing scheduled for today.",
                        style = Typo.Subtitle,
                        color = AppColors.Black
                    )
                }
            }

            // Completed Section
            if (uiState.completedTodos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                TodoSection(
                    title = "Completed",
                    count = uiState.completedTodos.size,
                    todos = uiState.completedTodos,
                    backgroundColor = AppColors.White,
                    contentColor = Color.Gray,
                    countColor = Color.Gray,
                    isExpanded = uiState.isCompletedExpanded,
                    onHeaderClick = onToggleCompleted,
                    onTodoClick = onTodoClick
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Space for FAB
        }

        // Truly centered empty-state when there are no todos at all.
        if (uiState.isAllEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nothing scheduled for today",
                        style = Typo.Title,
                        color = AppColors.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add a task, or enjoy the free space.",
                        style = Typo.Subtitle,
                        color = AppColors.Black
                    )
                }
            }
        }
    }
}


@Composable
fun GreetingHeader(
    name: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = "Howdy, $name!",
            style = Typo.Headline
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Keep support for the "~~" strikethrough style used by the caller.
        val annotatedString = buildAnnotatedString {
            val parts = subtitle.split("~~")
            if (parts.size == 1) {
                append(subtitle)
            } else {
                // Alternate normal/strikethrough segments
                parts.forEachIndexed { index, part ->
                    if (index % 2 == 1) {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            append(part)
                        }
                    } else {
                        append(part)
                    }
                }
            }
        }

        Text(
            text = annotatedString,
            style = Typo.Subtitle
        )
    }
}


@Composable
fun TodoSection(
    title: String,
    count: Int,
    todos: List<TodoItemUiState>,
    backgroundColor: Color,
    countColor: Color,
    contentColor: Color,
    isExpanded: Boolean = true,
    onHeaderClick: () -> Unit = {},
    onTodoClick: (Todo) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = if (isExpanded) 4.dp else 12.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHeaderClick() }
            ) {
                Text(
                    text = title,
                    style = Typo.Title,
                    color = contentColor
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(20.dp)
                        .background(countColor, CircleShape)
                ) {
                    Text(
                        text = count.toString(),
                        style = Typo.Number,
                        color = backgroundColor,
                    )
                }
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                todos.forEach { item ->
                    TodoItem(
                        item = item,
                        textColor = contentColor,
                        backgroundColor = backgroundColor,
                        onClick = { onTodoClick(item.todo) }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    item: TodoItemUiState,
    textColor: Color,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        val circleColor = if (item.isCompleted) textColor else Color.Transparent

        Box(
            modifier = Modifier
                .size(20.dp)
                .border(1.5.dp, textColor, CircleShape)
                .background(circleColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (item.isCompleted) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Completed",
                    tint = backgroundColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.title,
            style = Typo.Body,
            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
            color = textColor
        )

        Spacer(modifier = Modifier.weight(1f))

        if (item.formattedTime != null) {
            Text(
                text = item.formattedTime,
                style = Typo.Time,
                color = textColor
            )
        }
    }
}
