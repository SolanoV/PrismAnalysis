# Prism Compiler

**A visually immersive Compiler Simulator built with JavaFX.**

Prism visualizes the **Lexical**, **Syntax**, and **Semantic** analysis phases of compilation in real-time, wrapped in a futuristic Cyberpunk/Neon aesthetic.

[![Java 17+](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com)
[![JavaFX](https://img.shields.io/badge/JavaFX-UI-4285F4?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjfx.io/)

---

## :bulb: Key Features

### Interactive UI
* **Visual Pipeline:** Real-time "Traffic Light" status indicators for every analysis phase.
* **Neon Aesthetic:** Custom CSS styling with a toggleable **Dark (Cyberpunk)** and **Light** mode.
* **File Support:** Native loading of `.txt` and `.java` source files.

### Compiler Engine

| Phase | Function | Key Mechanics |
| :--- | :--- | :--- |
| **1. Lexical** | Tokenizer | Uses Regex to break code into tokens: `<type>`, `<id>`, `<val>`. |
| **2. Syntax** | Parser | Validates grammar rules (e.g., ensuring `;` exists, checking declaration structure). |
| **3. Semantic** | Logic | Manages **Symbol Table**, enforces **Scope**, and validates **Type Compatibility**. |

---

## :page_with_curl: Supported Syntax

Prism supports a strict subset of the Java language.

### Supported Data Types
`byte`, `short`, `int`, `long`, `float`, `double`, `char`, `String`, `boolean`

### Example Code
```java
// 1. Declaration
int count;
String username;

// 2. Initialization
double price = 99.99;
boolean isActive = true;

// 3. Logic & Assignment
count = 50;
```
---
## :triangular_ruler: Architecture
The project follows the **MVC (Model-View-Controller)** pattern:
-**Controller** (com.prismx.controller): Orchestrates the UI flow and analysis sequence.
-**Model** (com.prismx.model): Contains the core compiler logic (Lexer, Parser, Semantic Analyzer).
-**View** (com.prismx.view): FXML layouts and CSS stylesheets.

--- 
## :gear: Setup & Practice
1. Clone the repository.
2. Open in your favourite Java IDE(e.g. VSCode, IntelliJ).
3. Add JavaFX SDK to your project libraries.
4. Run prismView.java.

