package com.raven.odyssey.ui.screens.todo.list

import android.content.res.Configuration
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raven.odyssey.domain.model.Todo
import com.raven.odyssey.ui.theme.OdysseyTheme
import java.util.Locale

@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        uiState.todos.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "All done for today! \uD83C\uDF89",
                )
            }
        }

        else -> {
            TodoListUI(uiState, onLongPress = { todo -> viewModel.deleteTodo(todo) })
        }
    }
}

@Composable
fun TodoListUI(uiState: TodoListUiState, onLongPress: (Todo) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(uiState.todos) { todo ->
            TodoCard(todo, onLongPress)
        }
    }
}

@Composable
fun TodoCard(todo: Todo, onLongPress: (Todo) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongPress(todo) }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {

                Text(
                    text = todo.title,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

            }
            if (todo.hour != null && todo.minute != null) {
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", todo.hour, todo.minute),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun TodoCardPreview() {
    OdysseyTheme {
        TodoCard(
            todo = Todo(
                1,
                "Sample Todo",
                "This is a sample description",
                5, 7, false,
                1234
            ),
            onLongPress = {}
        )
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
fun TodoListPreview() {
    OdysseyTheme {
        Scaffold {
            it
            val sampleTodos = listOf(
                Todo(
                    1,
                    "Sample Todo 1",
                    "This is a sample description",
                    5, 7, false,
                    1234
                ),
                Todo(2, "Sample Todo 2", null, 10, 50, true, 1002)
            )
            val uiState = TodoListUiState(todos = sampleTodos, isLoading = false)

            TodoListUI(uiState, onLongPress = {})
        }
    }
}
