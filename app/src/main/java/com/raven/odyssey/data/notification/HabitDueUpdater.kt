package com.raven.odyssey.data.notification

import com.raven.odyssey.data.dao.HabitDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class HabitDueUpdater @Inject constructor(
    private val habitDao: HabitDao
) {
    suspend fun updateHabitsNextDueToFuture() = withContext(Dispatchers.IO) {
        val nowCal = Calendar.getInstance()
        val habits = habitDao.getAllHabitsList()
        for (habit in habits) {
            var nextDue = habit.nextDue
            val nextDueCal = Calendar.getInstance().apply { timeInMillis = nextDue }
            val intervalDays = habit.intervalDays ?: 1
            when (habit.frequencyType) {
                "Daily" -> {
                    // While nextDue's date is before today, add 1 day
                    while (
                        nextDueCal.get(Calendar.YEAR) < nowCal.get(Calendar.YEAR) ||
                        (nextDueCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                         nextDueCal.get(Calendar.DAY_OF_YEAR) < nowCal.get(Calendar.DAY_OF_YEAR))
                    ) {
                        nextDueCal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    nextDue = nextDueCal.timeInMillis
                }
                "Weekly" -> {
                    // While nextDue's week is before current week, add 7 days
                    while (
                        nextDueCal.get(Calendar.YEAR) < nowCal.get(Calendar.YEAR) ||
                        (nextDueCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                         nextDueCal.get(Calendar.WEEK_OF_YEAR) < nowCal.get(Calendar.WEEK_OF_YEAR))
                    ) {
                        nextDueCal.add(Calendar.DAY_OF_YEAR, 7)
                    }
                    nextDue = nextDueCal.timeInMillis
                }
                "Custom" -> {
                    // While nextDue's custom period is before current, add intervalDays
                    val customPeriodMillis = intervalDays * 24 * 60 * 60 * 1000
                    val nowPeriodStart = nowCal.timeInMillis - (nowCal.timeInMillis % customPeriodMillis)
                    var nextDuePeriodStart = nextDueCal.timeInMillis - (nextDueCal.timeInMillis % customPeriodMillis)
                    while (nextDuePeriodStart < nowPeriodStart) {
                        nextDueCal.add(Calendar.DAY_OF_YEAR, intervalDays)
                        nextDuePeriodStart = nextDueCal.timeInMillis - (nextDueCal.timeInMillis % customPeriodMillis)
                    }
                    nextDue = nextDueCal.timeInMillis
                }
            }
            if (nextDue != habit.nextDue) {
                habitDao.updateHabit(habit.copy(nextDue = nextDue))
            }
        }
    }
}
