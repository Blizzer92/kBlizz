# kBlizz

Ein Interpreter-Projekt basierend auf dem Buch "Crafting Interpreters" von Robert Nystrom, implementiert in Kotlin.

## Über das Projekt

kBlizz ist eine Programmiersprache, die im Rahmen eines Buchclubs zu "Crafting Interpreters" entwickelt wird. Das Projekt folgt den Prinzipien und Techniken aus dem Buch, um einen vollständigen Interpreter von Grund auf zu bauen.

## Technologie-Stack

- **Sprache:** Kotlin 2.3.0
- **Build-Tool:** Gradle
- **JVM:** Version 21+
- **Testing:** JUnit Platform

## Voraussetzungen

- JDK 21 oder höher
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

> **Hinweis:** Der REPL-Modus verarbeitet eine Zeile pro Eingabe. Mehrzeilige Blöcke (`{ ... }`) müssen als Datei ausgeführt werden.

### Tests ausführen
```bash
./gradlew test
```

Einzelne Testklasse:
```bash
./gradlew test --tests "at.kblizz.interpreter.InterpreterTest"
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
│   │   │   ├── Stmt.kt                # Statement-AST-Klassen (Visitor-Pattern)
│   │   │   └── AstPrinter.kt          # AST Pretty-Printer (S-Expression-Format)
│   │   ├── environment/
│   │   │   └── Environment.kt         # Lexikalische Scoping-Umgebung
│   │   ├── parser/
│   │   │   └── Parser.kt              # Rekursiv-Abstieg-Parser
│   │   ├── interpreter/
│   │   │   └── Interpreter.kt         # Tree-Walk-Interpreter
│   │   ├── error/
│   │   │   └── RuntimeError.kt        # Laufzeitfehler-Klasse
│   │   └── tool/
│   │       └── GenerateAst.kt         # AST-Klassen-Generator
│   └── test/kotlin/
│       ├── ast/
│       │   └── AstPrinterTest.kt
│       ├── environment/
│       │   └── EnvironmentTest.kt
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
Der Scanner wandelt Quellcode in eine Sequenz von Tokens um:
- Erkennt alle Lox-Sprachkonstrukte (Operatoren, Keywords, Literale)
- Unterstützt einzeilige Kommentare (`//`)
- Verarbeitet Strings, Zahlen und Identifikatoren
- Trackt Zeilennummern für Fehlerberichte

### Abstract Syntax Tree (AST)
Zwei Hierarchien mit Visitor-Pattern:

**Expr** — Ausdrücke: `Assign`, `Binary`, `Grouping`, `Literal`, `Logical`, `Unary`, `Variable`

**Stmt** — Anweisungen: `Block`, `Expression`, `If`, `Print`, `Var`, `While`

Der AST-Printer gibt Ausdrücke im S-Expression-Format aus:
```
(+ 1 (* 2 3))   →   1 + 2 * 3
(and true false) →   true and false
```

### Parser
Rekursiv-Abstieg-Parser mit vollständiger Operatorpräzedenz:

```
Zuweisung → Oder → Und → Gleichheit → Vergleich → Term → Faktor → Unär → Primär
```

`for`-Schleifen werden im Parser zu `while` + `Block` desugared — kein eigener AST-Knoten.

### Environment
Verknüpfte Kette von `HashMap`s für lexikalisches Scoping. Jeder Block erstellt eine neue `Environment(enclosing = äußere)`. Variable Lookups wandern die Kette nach oben.

### Interpreter
Tree-Walk-Interpreter:

| Kategorie | Unterstützt |
|---|---|
| Arithmetik | `+` `-` `*` `/` |
| Vergleich | `>` `>=` `<` `<=` `==` `!=` |
| Logik | `and` `or` `!` (mit Short-Circuit) |
| Strings | Konkatenation mit `+` |
| Variablen | `var`, Zuweisung, lexikalisches Scoping |
| Kontrollfluss | `if`/`else`, `while`, `for` |
| Ausgabe | `print` |

### Fehlerbehandlung
- Syntaxfehler mit Zeilennummer und Token-Kontext
- Laufzeitfehler mit `RuntimeError`-Klasse
- Exit-Codes: 64 (Usage), 65 (Datenfehler), 70 (Laufzeitfehler)

## Entwicklungsstand

**Implementiert (Kapitel 4–9):**
- [x] Scanner — vollständige Token-Erkennung
- [x] AST-Datenstrukturen mit Visitor-Pattern
- [x] AST-Generator-Tool (`GenerateAst`)
- [x] AST-Pretty-Printer
- [x] Rekursiv-Abstieg-Parser
- [x] Ausdrücke auswerten
- [x] Anweisungen & Zustand (`print`, `var`, Blöcke, Zuweisung)
- [x] Kontrollfluss (`if`/`else`, `while`, `for`, `and`, `or`)

**Nächste Schritte:**
- [ ] Funktionen
- [ ] Klassen

## Referenzen

- [Crafting Interpreters](https://craftinginterpreters.com/) von Robert Nystrom
- [Kotlin Dokumentation](https://kotlinlang.org/docs/home.html)

## Lizenz

TBD
