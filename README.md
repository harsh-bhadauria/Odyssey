<img width="1622" height="324" alt="image" src="https://github.com/user-attachments/assets/1bbe506b-42e8-4fcf-adc7-e29e5f1f962c" />

<h1></h1>

<br>

Odyssey is a minimalist Android habit tracker designed for people who check their phones every five seconds but still forget to drink water.

<br>

It pairs a zero-clutter interface with a relentless notification system. The goal isn't just to list tasks; it's to close the gap between *intending* to do something and actually *remembering* to do it.

<br>

<img src="https://github.com/user-attachments/assets/501fc22a-1660-48bc-a11d-a3581e22143b" width="24%" />
<img src="https://github.com/user-attachments/assets/d8572634-e599-4feb-bf53-0ad0cb4eb914" width="24%" />
<img src="https://github.com/user-attachments/assets/4ecae023-7e8d-43be-bf0f-014d5c1fe146" width="24%" />
<img src="https://github.com/user-attachments/assets/f4c5329d-a080-474d-ad83-695082c53b80" width="24%" />


## The Philosophy: Why this exists

Most habit trackers share a fatal flaw: **Out of sight, out of mind.** You add a task, close the app, and forget it exists until you feel guilty at 11:00 PM.

Odyssey is built on the **"Permanent Notification"** principle.

* **You can't swipe it away.** A habit notification remains pinned to your tray until you either complete it or intentionally reschedule it.
* **You can't forget.** Since you look at your notification shade constantly, your habits live alongside your texts and emails.
* **Peaceful Interaction.** While the notifications are strict, the app itself is calm. No clutter, no ads- just you and your tasks.

## Core Features

### 🔔 The Notification Engine

The heart of the app. Notifications are treated as part of the habit's lifecycle, not just a fire-and-forget event.

* **State-Aware:** When you update a habit (e.g., complete it), the app cancels the old notification and immediately schedules the next one based on the new `nextDue` time.
* **Sticky:** These aren't suggestions. They are persistent reminders that require an action (Complete or Reschedule) to dismiss.

### 🌿 Areas of Life

Habits aren't just chores; they build character. Odyssey organizes your life into (DnD inspired) domains:

* **Vitality**: Health, Fitness, Diet
* **Intellect**: Learning, Reading
* **Spirit**: Meditation, Spirituality
* **Charisma**: Social Life
* **Flow**: Creating Stuff, Artistic Outlets
* **Order**: Chores and "Must Do"s

### 📊 Measurable Progress

Not everything is a boolean checkmark.

* **Binary Habits:** Simple "Yes/No" completion (e.g., "Take meds").
* **Measurable Habits:** Increment/decrement progress bars (e.g., "Drank 4/8 cups of water").

## Technical Architecture

Odyssey is built with modern Android standards, strictly adhering to **Clean Architecture** and **MVVM**.

### Tech Stack

* **Language:** Kotlin (100%)
* **DI:** Hilt (`@HiltViewModel`)
* **Async:** Coroutines + Flow (`StateFlow` for UI state)
* **Persistence:** Room Database
* **UI:** Jetpack Compose

### The Repository Shape

The codebase is separated into three distinct layers to ensure separation of concerns:

| Layer | Package | Responsibility |
| --- | --- | --- |
| **UI** | `com.raven.odyssey.ui` | Screens & ViewModels. Observes `StateFlow` from the domain layer and maps it to View States. |
| **Domain** | `com.raven.odyssey.domain` | The source of truth. Contains `Habit` models, the `HabitNotificationScheduler` interface, and business logic. |
| **Data** | `com.raven.odyssey.data` | Implementation details. Maps persistence entities to domain models (`toDomain()`, `toEntity()`) and handles DB transactions. |

## Future Roadmap 🔮

* **Gamification:** A reward system for consistent streaks.
* **Domains Expansion:** Deeper stats per "Area of Life."

## Building the project

1. Clone the repo.
2. Open in Android Studio (Ladybug or newer recommended).
3. Sync Gradle.
4. Run on device (Notifications permission required for the magic to work).

<h1></h1>

*"Top ~~ramen~~ o' the morning."* 🍜

<h1></h1>
