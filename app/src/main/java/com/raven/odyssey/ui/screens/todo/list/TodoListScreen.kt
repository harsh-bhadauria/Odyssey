package com.raven.odyssey.ui.screens.todo.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raven.odyssey.domain.model.Todo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        WeekDateSelector(
            weekStart = uiState.weekStart,
            selectedDate = uiState.selectedDate,
            onDateSelected = { viewModel.updateUiState(selectedDate = it) },
            onWeekChanged = { viewModel.updateUiState(weekStart = it) }
        )
        TodoListStateContent(uiState = uiState, onDeleteTodo = { viewModel.deleteTodo(it) })
    }
}

@Composable
fun TodoListStateContent(uiState: TodoListUiState, onDeleteTodo: (Todo) -> Unit) {
    val tz = java.util.TimeZone.getDefault()
    val startOfToday = Calendar.getInstance(tz).apply {
        timeInMillis = uiState.selectedDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val startOfTomorrow = Calendar.getInstance(tz).apply {
        timeInMillis = startOfToday
        add(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis

    val dueTodos = uiState.todos.filter { todo ->
        todo.dueTime in 1 until startOfToday
    }

    val todayTodos = uiState.todos.filter { todo ->
        todo.dueTime in startOfToday until startOfTomorrow
    }

    val noDueDateTodos = uiState.todos.filter { it.dueTime <= 0L }

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (todayTodos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Today's Todos",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(todayTodos) { todo ->
                        TodoCard(todo, onLongPress = { onDeleteTodo(it) })
                    }
                } else if (dueTodos.isEmpty() && noDueDateTodos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(16.dp), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "All done for today! \uD83C\uDF89",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "No todos scheduled for this day.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (dueTodos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Due before selected date",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(dueTodos) { todo ->
                        TodoCard(todo, onLongPress = { onDeleteTodo(it) })
                    }
                }

                if (noDueDateTodos.isNotEmpty()) {
                    item {
                        Text(
                            text = "No Due Date",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(noDueDateTodos) { todo ->
                        TodoCard(todo, onLongPress = { onDeleteTodo(it) })
                    }
                }
            }
        }
    }
}


@Composable
fun WeekDateSelector(
    weekStart: Long,
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    onWeekChanged: (Long) -> Unit
) {
    val daysOfWeek = remember(weekStart) {
        List(7) { i ->
            Calendar.getInstance().apply {
                timeInMillis = weekStart + TimeUnit.DAYS.toMillis(i.toLong())
            }.timeInMillis
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(weekStart) {
                detectHorizontalDragGestures { change: PointerInputChange, dragAmount: Float ->
                    if (dragAmount > 40f) {
                        // Swipe right: previous week
                        onWeekChanged(weekStart - TimeUnit.DAYS.toMillis(7))
                    } else if (dragAmount < -40f) {
                        // Swipe left: next week
                        onWeekChanged(weekStart + TimeUnit.DAYS.toMillis(7))
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { dateMillis ->
            val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
            val dayLabel = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)
            val dayNum = cal.get(Calendar.DAY_OF_MONTH)
            val isSelected = dateMillis == selectedDate
            val isToday = Calendar.getInstance().let {
                it.timeInMillis = System.currentTimeMillis()
                it.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                        it.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
            }
            Surface(
                shape = CircleShape,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .then(
                        if (isSelected) Modifier else Modifier
                    )
                    .combinedClickable(onClick = { onDateSelected(dateMillis) }, onLongClick = {}),
                shadowElevation = if (isSelected) 8.dp else 0.dp,
                border = if (isSelected) BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary
                ) else null
            ) {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayLabel,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isToday) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dayNum.toString(),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isToday) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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
            // Show due time if set
            if (todo.dueTime > 0L) {
                val cal = Calendar.getInstance().apply { timeInMillis = todo.dueTime }
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE)
                    ),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp
                )
            }
        }
    }
}
