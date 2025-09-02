# 📱 Contacto

**Contacto** is a modern Android application built with **Kotlin** and **Jetpack Compose**.
It follows best practices in Android development: declarative UI, dependency injection with Hilt, Room persistence, Retrofit networking, and a clean modular structure.

---

## 🚀 Features

* 100% **Jetpack Compose UI**
* **Navigation Compose** for in-app navigation
* **Hilt** for dependency injection
* **Room** database for offline persistence
* **Retrofit + OkHttp** for networking
* **Coil 3** for image loading
* **Lottie** for animations
* Built-in **unit tests** with JUnit 5, MockK, and Truth

---

## 🛠 Tech Stack

### Core

* [Kotlin](https://kotlinlang.org/) – Main programming language
* [Jetpack Compose](https://developer.android.com/jetpack/compose) – Modern declarative UI
* [AndroidX](https://developer.android.com/jetpack/androidx) – Core Android libraries

### Dependency Injection

* [Hilt](https://dagger.dev/hilt/) – Android dependency injection

### Networking

* [Retrofit](https://square.github.io/retrofit/) – HTTP client
* [OkHttp Logging Interceptor](https://square.github.io/okhttp/) – Network logging

### Database

* [Room](https://developer.android.com/jetpack/androidx/releases/room) – Local persistence

### UI & Media

* [Coil 3](https://coil-kt.github.io/coil/) – Image loading
* [Lottie Compose](https://airbnb.io/lottie/#/) – Animations

### Tooling & Linting

* [Ktlint](https://github.com/pinterest/ktlint) – Kotlin code style
* [KSP](https://github.com/google/ksp) – Symbol processing

### Testing

* [JUnit 5](https://junit.org/junit5/) – Unit testing
* [MockK](https://mockk.io/) – Mocking framework
* [Truth](https://truth.dev/) – Assertions
* [Coroutines Test](https://kotlinlang.org/docs/coroutines-test.html) – Coroutine testing

---

## 📂 Project Structure

```
app/
 ├─ build.gradle.kts        # Gradle config
 └─ src/main/kotlin/com/bvic/contacto/
     ├─ core/               # Core utilities (network, Result wrapper, Error handling)
     │
     ├─ data/               # Data layer
     │   ├─ core/           # Shared data helpers
     │   ├─ local/          # Local persistence (Room DB, DAO)
     │   ├─ mapper/         # DTO ↔ domain model mappers
     │   ├─ remote/         # API definitions (Retrofit services)
     │   └─ repository/     # Repository implementations
     │
     ├─ di/                 # Dependency Injection (Hilt modules)
     │
     ├─ domain/             # Domain layer (business logic)
     │   ├─ model/          # Domain models
     │   ├─ repository/     # Repository interfaces
     │   └─ usecase/        # Use cases
     │
     └─ ui/                 # Presentation layer (Jetpack Compose)
```

* **UI layer** – Built entirely with Compose
* **Data layer** – Retrofit for remote, Room for local
* **DI** – Hilt modules provide dependencies

---

## ⚙️ Build & Run

### Prerequisites

* Android Studio **Koala+** (AGP 8.1+ recommended)
* JDK 17
* Gradle 8+

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/contacto.git
   cd contacto
   ```
2. Create a `local.properties` file in the project root (if not already present) and configure signing properties:

   ```properties
   STORE_FILE_PATH=/path/to/keystore.jks
   STORE_PASSWORD=yourPassword
   STORE_KEY=yourAlias
   ```
3. Build and install:

   ```bash
   ./gradlew installDebug
   ```

---

## ✅ Code Quality

The project enforces **Ktlint** on every build:

```bash
./gradlew ktlintCheck
```

---

## 📜 License

This project is licensed under the **Apache 2.0 License**.