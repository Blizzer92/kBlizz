# kBlizz

Ein interpreter-Projekt basierend auf dem Buch "Crafting Interpreters" von Robert Nystrom, implementiert in Kotlin.

## Über das Projekt

kBlizz ist eine Programmiersprache, die im Rahmen eines Buchclubs zu "Crafting Interpreters" entwickelt wird. Das Projekt folgt den Prinzipien und Techniken aus dem Buch, um einen vollständigen interpreter von Grund auf zu bauen.

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
│   ├── main/kotlin/
│   │   ├── Main.kt              # Entry Point
│   │   ├── scanner/             # Lexikalische Analyse
│   │   │   └── Scanner.kt       # Token-Scanner
│   │   ├── token/               # Token-Definitionen
│   │   │   ├── Token.kt         # Token-Klasse
│   │   │   └── TokenType.kt     # Token-Typen
│   │   ├── ast/                 # Abstract Syntax Tree
│   │   │   ├── Expr.kt          # Expression-AST-Klassen
│   │   │   └── AstPrinter.kt    # AST Pretty-Printer
│   │   └── tool/                # Code-Generierungs-Tools
│   │       └── GenerateAst.kt   # AST-Klassen-Generator
│   └── test/kotlin/             # Tests
│       ├── MainTest.kt
│       └── scanner/
│           └── ScannerTest.kt
├── build.gradle.kts             # Build-Konfiguration
└── settings.gradle.kts          # Projekt-Einstellungen
```

## Funktionalität

### Scanner (Lexikalische Analyse)
Der Scanner (`src/main/kotlin/scanner/Scanner.kt`) wandelt Quellcode in eine Sequenz von Tokens um:
- Erkennt alle Lox-Sprachkonstrukte (Operatoren, Keywords, Literale)
- Unterstützt einzeilige Kommentare (`//`)
- Verarbeitet Strings, Zahlen und Identifikatoren
- Trackt Zeilennummern für Fehlerberichte

**Beispiel:**
```kotlin
val scanner = Scanner("var x = 42;")
val tokens = scanner.scanTokens()
```

### Abstract Syntax Tree (AST)
Die AST-Implementierung ermöglicht die strukturierte Darstellung von Ausdrücken:

#### Expr-Klassen (`src/main/kotlin/ast/Expr.kt`)
- `Binary`: Binäre Operationen (z.B. `1 + 2`, `x * y`)
- `Unary`: Unäre Operationen (z.B. `-5`, `!true`)
- `Literal`: Literalwerte (Zahlen, Strings, Booleans)
- `Grouping`: Geklammerte Ausdrücke

Alle Expr-Klassen implementieren das Visitor-Pattern für erweiterbare Verarbeitung.

#### AST-Generator (`src/main/kotlin/tool/GenerateAst.kt`)
Ein Code-Generator-Tool, das automatisch AST-Klassen erstellt:
```bash
./gradlew run --args="src/main/kotlin/ast"
```

Generiert Kotlin-Code mit:
- Sealed Classes für typ-sichere AST-Hierarchien
- Data Classes für strukturelle Gleichheit
- Visitor-Pattern-Interface für AST-Traversierung

#### AST-Printer (`src/main/kotlin/ast/AstPrinter.kt`)
Pretty-Printer für AST-Darstellung in S-Expression-Format:
```kotlin
val expr = Binary(Literal(1), Token(PLUS, "+", null, 1), Literal(2))
println(AstPrinter().print(expr))  // Ausgabe: (+ 1 2)
```

## Entwicklungsstand

**Implementiert:**
- ✅ Scanner mit vollständiger Token-Erkennung
- ✅ AST-Datenstrukturen für Ausdrücke
- ✅ AST-Generator-Tool
- ✅ AST-Pretty-Printer
- ✅ Visitor-Pattern für AST-Traversierung

**In Arbeit:**
- Parser-Implementierung
- Evaluator/interpreter
- Fehlerbehandlung und -meldung

## Referenzen

- [Crafting Interpreters](https://craftinginterpreters.com/) von Robert Nystrom
- [Kotlin Dokumentation](https://kotlinlang.org/docs/home.html)

## Lizenz

TBD
