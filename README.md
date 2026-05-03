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

Interaktiver REPL-Modus:
```bash
./gradlew run
```

Skript-Datei ausführen:
```bash
./gradlew run --args="pfad/zur/datei.lox"
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
│   │   ├── Main.kt                    # Entry Point
│   │   ├── KBlizz.kt                  # Interpreter-Orchestrierung (REPL & Datei)
│   │   ├── ErrorReporter.kt           # Fehler- und Laufzeitfehler-Reporting
│   │   ├── scanner/
│   │   │   └── Scanner.kt             # Lexikalische Analyse
│   │   ├── token/
│   │   │   ├── Token.kt               # Token-Klasse
│   │   │   └── TokenType.kt           # Token-Typen
│   │   ├── ast/
│   │   │   ├── Expr.kt                # Expression-AST-Klassen (Visitor-Pattern)
│   │   │   └── AstPrinter.kt          # AST Pretty-Printer (S-Expression-Format)
│   │   ├── parser/
│   │   │   └── Parser.kt              # Rekursiv-Abstieg-Parser
│   │   ├── interpreter/
│   │   │   └── Interpreter.kt         # Tree-Walk-Interpreter
│   │   ├── error/
│   │   │   └── RuntimeError.kt        # Laufzeitfehler-Klasse
│   │   └── tool/
│   │       └── GenerateAst.kt         # AST-Klassen-Generator
│   └── test/kotlin/
│       ├── MainTest.kt
│       ├── ast/
│       │   └── AstPrinterTest.kt
│       ├── interpreter/
│       │   └── InterpreterTest.kt
│       ├── parser/
│       │   └── ParserTest.kt
│       ├── scanner/
│       │   ├── ScannerTest.kt
│       │   └── OrKeywordValidationTest.kt
│       └── tool/
│           └── GenerateAstTest.kt
├── build.gradle.kts
└── settings.gradle.kts
```

## Funktionalität

### Scanner (Lexikalische Analyse)
Der Scanner (`src/main/kotlin/scanner/Scanner.kt`) wandelt Quellcode in eine Sequenz von Tokens um:
- Erkennt alle Lox-Sprachkonstrukte (Operatoren, Keywords, Literale)
- Unterstützt einzeilige Kommentare (`//`)
- Verarbeitet Strings, Zahlen und Identifikatoren
- Trackt Zeilennummern für Fehlerberichte

### Abstract Syntax Tree (AST)
Die AST-Implementierung ermöglicht die strukturierte Darstellung von Ausdrücken:

#### Expr-Klassen (`src/main/kotlin/ast/Expr.kt`)
- `Binary`: Binäre Operationen (z.B. `1 + 2`, `"a" + "b"`)
- `Unary`: Unäre Operationen (z.B. `-5`, `!true`)
- `Literal`: Literalwerte (Zahlen, Strings, Booleans, `nil`)
- `Grouping`: Geklammerte Ausdrücke

Alle Expr-Klassen implementieren das Visitor-Pattern für erweiterbare Verarbeitung.

#### AST-Printer (`src/main/kotlin/ast/AstPrinter.kt`)
Pretty-Printer für AST-Darstellung in S-Expression-Format:
```kotlin
val expr = Binary(Literal(1.0), Token(PLUS, "+", null, 1), Literal(2.0))
println(AstPrinter().print(expr))  // Ausgabe: (+ 1 2)
```

### Parser (`src/main/kotlin/parser/Parser.kt`)
Rekursiv-Abstieg-Parser, der Token-Sequenzen in einen AST umwandelt:
- Wertet Ausdrücke nach Operatorpräzedenz aus (Gleichheit → Vergleich → Term → Faktor → Unär → Primär)
- Erkennt geklammerte Ausdrücke, Literale und Identifikatoren
- Fehlertoleranz durch Panik-Modus-Synchronisation

### Interpreter (`src/main/kotlin/interpreter/Interpreter.kt`)
Tree-Walk-Interpreter, der den AST direkt auswertet:
- Arithmetische Operatoren: `+`, `-`, `*`, `/`
- Vergleichsoperatoren: `>`, `>=`, `<`, `<=`, `==`, `!=`
- String-Konkatenation mit `+`
- Unäre Negation (`-`) und logische Negation (`!`)
- Laufzeitfehler-Prüfung für Typ-Fehler

### Fehlerbehandlung (`src/main/kotlin/ErrorReporter.kt`)
Zentrales Fehler-Reporting mit Unterscheidung zwischen Syntax- und Laufzeitfehlern:
- Syntaxfehler mit Zeilennummer und Token-Kontext
- Laufzeitfehler mit `RuntimeError`-Klasse
- Exit-Codes gemäß Unix-Konvention (64 = Usage, 65 = Datenfehler, 70 = Laufzeitfehler)

## Entwicklungsstand

**Implementiert:**
- Scanner mit vollständiger Token-Erkennung
- AST-Datenstrukturen für Ausdrücke (Visitor-Pattern)
- AST-Generator-Tool
- AST-Pretty-Printer
- Rekursiv-Abstieg-Parser
- Tree-Walk-Interpreter für Ausdrücke
- Fehlerbehandlung (Syntax- und Laufzeitfehler)
- REPL-Modus und Datei-Ausführung

**Nächste Schritte (Kapitel folgen dem Buch):**
- Anweisungen und Zustand (Statements & State)
- Kontrollfluss (Control Flow)
- Funktionen
- Klassen

## Referenzen

- [Crafting Interpreters](https://craftinginterpreters.com/) von Robert Nystrom
- [Kotlin Dokumentation](https://kotlinlang.org/docs/home.html)

## Lizenz

TBD
