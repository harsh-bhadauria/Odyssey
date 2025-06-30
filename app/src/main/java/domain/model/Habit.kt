package domain.model


data class Habit(
    val id: Long,
    val name: String,
    val description: String? = null,
    val frequency: Frequency,
    val intervalValue: Int? = null,
    val isActive: Boolean = true
)

enum class Frequency {
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}
