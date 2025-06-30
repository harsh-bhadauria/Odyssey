package domain.model

import java.time.LocalDate

data class Todo(
    val id: Long,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val dueDate: LocalDate? = null
)
