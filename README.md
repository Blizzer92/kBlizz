# kBlizz

Ein Interpreter-Projekt basierend auf dem Buch "Crafting Interpreters" von Robert Nystrom, implementiert in Kotlin.

## Über das Projekt

kBlizz ist eine Programmiersprache, die im Rahmen eines Buchclubs zu "Crafting Interpreters" entwickelt wird. Das Projekt folgt den Prinzipien und Techniken aus dem Buch, um einen vollständigen Interpreter von Grund auf zu bauen.

## Technologie-Stack

- **Sprache:** Kotlin 2.3.0
- **Build-Tool:** Gradle
- **JVM:** Version 25
- **Testing:** JUnit Platform

## Voraussetzungen

- JDK 25 oder höher
- Gradle (wird über den Gradle Wrapper bereitgestellt)

## Installation

Repository klonen:
```bash
git clone <repository-url>
cd kBlizz
```

## Verwendung

### Projekt bauen
```bash
./gradlew build
```

### Anwendung ausführen
```bash
./gradlew run
```

### Tests ausführen
```bash
./gradlew test
```

## Projektstruktur

```
kBlizz/
├── src/
│   ├── main/kotlin/        # Hauptquellcode
│   │   └── Main.kt         # Entry Point
│   └── test/kotlin/        # Tests
│       └── MainTest.kt
├── build.gradle.kts        # Build-Konfiguration
└── settings.gradle.kts     # Projekt-Einstellungen
```

## Entwicklungsstand

Das Projekt befindet sich in der frühen Entwicklungsphase. Der grundlegende Setup mit Kotlin und Gradle ist abgeschlossen.

## Referenzen

- [Crafting Interpreters](https://craftinginterpreters.com/) von Robert Nystrom
- [Kotlin Dokumentation](https://kotlinlang.org/docs/home.html)

## Lizenz

TBD
