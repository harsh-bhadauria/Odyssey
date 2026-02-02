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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.odyssey.domain.model.Todo
import com.raven.odyssey.ui.theme.AppColors
import com.raven.odyssey.ui.theme.OdysseyTheme
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

        if (todayTodos.isEmpty() && overdueTodos.isEmpty()) {
            // No todos at all – keep header visible and show an empty-state in the body area.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

            Spacer(modifier = Modifier.height(100.dp))
            return
        }

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
        } else {
            // No todos for today (but there are overdue ones) – show message under header.
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

        // Parsing the "~~" for strikethrough logic
        val annotatedString = buildAnnotatedString {
            append("Top ")
            withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                append("ramen")
            }
            append(" o' the morning")
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
        Column(modifier = Modifier.padding(all = 12.dp)) {
            // Section Header with Count Badge
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


            // Todo Items
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
            .padding(top = 16.dp)
            .clickable(onClick = { onClick(todo) })
    ) {
        // Custom Radio Button Look
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

        // TODO: Rewards Display
//        if (todo.tagIcon != null) {
//            // Placeholder for the custom icons in the image
//            Icon(
//                imageVector = todo.tagIcon,
//                contentDescription = null,
//                tint = AppColors.PurpleTag, // Example tint
//                modifier = Modifier.size(16.dp)
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//        }

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

private fun previewTodos(): List<Todo> =
    listOf(
        Todo(id = 1, title = "Finalize Odyssey UI", description = "Polish spacing", dueTime = 930, domain = com.raven.odyssey.domain.model.Domain.Void),
        Todo(id = 2, title = "Reply to emails", description = null, dueTime = 1045, domain = com.raven.odyssey.domain.model.Domain.Void),
        Todo(id = 3, title = "Grocery run", description = "Ramen (ironically)", dueTime = 1300, domain = com.raven.odyssey.domain.model.Domain.Void),
        Todo(id = 4, title = "Ship release build", description = "v0.1.0", dueTime = 1700, domain = com.raven.odyssey.domain.model.Domain.Void)
    )

@Preview(name = "Todo list – Light", showBackground = true)
@Composable
private fun TodoListScreenPreview_Light() {
    OdysseyTheme(darkTheme = false, dynamicColor = false) {
        Surface {
            TodoListContent(
                overdueTodos = previewTodos(),
                todayTodos = previewTodos(),
                onTodoClick = {}
            )
        }
    }
}

@Preview(name = "Todo list – Dark", showBackground = true)
@Composable
private fun TodoListScreenPreview_Dark() {
    OdysseyTheme(darkTheme = true, dynamicColor = false) {
        Surface {
            TodoListContent(
                overdueTodos = previewTodos(),
                todayTodos = previewTodos(),
                onTodoClick = {}
            )
        }
    }
}

@Preview(name = "Todo list – Empty", showBackground = true)
@Composable
private fun TodoListScreenPreview_Empty() {
    OdysseyTheme(darkTheme = false, dynamicColor = false) {
        Surface {
            TodoListContent(
                overdueTodos = emptyList(),
                todayTodos = emptyList(),
                onTodoClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoListPreview() {
    val sampleTodos = listOf(
        Todo(id = 1, title = "Finalize Odyssey UI", description = "Polish spacing", dueTime = 930, domain = com.raven.odyssey.domain.model.Domain.Void),
        Todo(id = 2, title = "Reply to emails", description = null, dueTime = 1045, domain = com.raven.odyssey.domain.model.Domain.Void),
        Todo(id = 3, title = "Grocery run", description = "Ramen (ironically)", dueTime = 1300, domain = com.raven.odyssey.domain.model.Domain.Void),
        Todo(id = 4, title = "Ship release build", description = "v0.1.0", dueTime = 1700, domain = com.raven.odyssey.domain.model.Domain.Void)
    )

    OdysseyTheme {
        Surface {
            TodoListContent(
                overdueTodos = sampleTodos,
                todayTodos = sampleTodos,
                onTodoClick = {}
            )
        }
    }
}
