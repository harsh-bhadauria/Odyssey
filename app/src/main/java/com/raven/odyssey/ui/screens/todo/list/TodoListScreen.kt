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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import java.util.Calendar
import java.util.Locale

@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TodoListContent(
        overdueTodos = uiState.overdueTodos,
        todayTodos = uiState.todayTodos,
        onTodoClick = { viewModel.deleteTodo(it) }
    )
}

@Composable
fun TodoListContent(
    overdueTodos: List<Todo>,
    todayTodos: List<Todo>,
    onTodoClick: (Todo) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            GreetingHeader(
                name = "Ravenous",
                subtitle = "Top ~~ramen~~ o' the morning"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Overdue Section (only show if there are overdue todos)
            if (overdueTodos.isNotEmpty()) {
                TodoSection(
                    title = "Overdue",
                    count = overdueTodos.size,
                    todos = overdueTodos,
                    backgroundColor = AppColors.Red,
                    countColor = AppColors.White,
                    contentColor = Color.White,
                    onTodoClick = onTodoClick
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Today Section (only show if there are today todos)
            if (todayTodos.isNotEmpty()) {
                TodoSection(
                    title = "Today",
                    count = todayTodos.size,
                    todos = todayTodos,
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    countColor = AppColors.Teal,
                    onTodoClick = onTodoClick
                )
            } else if (overdueTodos.isNotEmpty()) {
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

            Spacer(modifier = Modifier.height(100.dp)) // Space for FAB
        }

        // Truly centered empty-state when there are no todos at all.
        if (todayTodos.isEmpty() && overdueTodos.isEmpty()) {
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

// Reusable Header
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
    todos: List<Todo>,
    backgroundColor: Color,
    countColor: Color,
    contentColor: Color,
    onTodoClick: (Todo) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 4.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
            Spacer(modifier = Modifier.height(8.dp))

            todos.forEach { todo ->
                TodoItem(
                    todo = todo,
                    textColor = contentColor,
                    onClick = { onTodoClick(todo) }
                )
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    textColor: Color,
    onClick: (Todo) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(todo) })
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(1.5.dp, textColor, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = todo.title,
            style = Typo.Body,
            color = textColor
        )

        Spacer(modifier = Modifier.weight(1f))

        if (todo.dueTime > 0L) {
            val cal = Calendar.getInstance().apply { timeInMillis = todo.dueTime }
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE)
                ),
                style = Typo.Time,
                color = textColor
            )
        }
    }
}
