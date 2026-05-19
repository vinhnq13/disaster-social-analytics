# Disaster Social Media Analytics

A console-based Java application that analyzes social media posts related to natural disasters. The goal is to support **humanitarian logistics** by turning informal online reports into structured insights—such as damage categories and public sentiment—that can help responders prioritize aid.

This project was built as an **Object-Oriented Programming (OOP)** coursework application. The current version focuses on a clean, extensible core; a graphical user interface is planned for a later phase.

---

## Project Overview

During disasters, people often share real-time updates on platforms like Facebook, Zalo, and Twitter: flooded streets, damaged homes, requests for relief, and expressions of fear or hope. This system reads those posts from a JSON dataset, classifies them by **type of damage** and **sentiment**, and prints summary statistics to the console.

The design separates **data loading**, **domain models**, and **analysis logic** so new analyzers or data sources can be added without rewriting the whole application.

---

## Main Features

| Feature | Description |
|--------|-------------|
| **Load posts from JSON** | Reads Vietnamese sample posts from `src/main/resources/data/posts.json` using Gson. |
| **Damage classification** | Groups posts into categories: `housing`, `transport`, `human`, `infrastructure`, and `other`. |
| **Sentiment analysis** | Groups posts into: `positive`, `negative`, and `neutral`. |
| **Extensible analyzer architecture** | All analyzers implement a common `Analyzer` interface, making it easy to add new analysis types. |

Both analyzers use **keyword matching** on post content. Keyword lists are organized in maps so you can extend categories by adding new entries without changing the core loop logic.

---

## Technologies

| Technology | Role |
|------------|------|
| **Java 17** | Language and runtime (records, modern APIs, LTS version). |
| **Maven** | Build tool, dependency management, and `exec:java` runner. |
| **Gson** | JSON parsing for loading posts into Java objects. |

---

## Project Structure

```
disaster-social-analytics/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/dsa/
    │   ├── Main.java                 # Application entry point
    │   ├── model/
    │   │   └── Post.java             # Domain model (content, date, source)
    │   ├── service/
    │   │   └── DataService.java      # Loads posts from JSON
    │   └── analyzer/
    │       ├── Analyzer.java         # Common analyzer interface
    │       ├── DamageAnalyzer.java   # Damage category classification
    │       └── SentimentAnalyzer.java# Sentiment classification
    └── resources/data/
        └── posts.json                # Sample disaster-related posts
```

**Package root:** `com.dsa`

---

## Prerequisites

- **JDK 17** or newer  
- **Apache Maven 3.6+**

Verify your setup:

```bash
java -version
mvn -version
```

Maven must use Java 17 for compilation. If `mvn -version` shows an older Java version, set `JAVA_HOME` to your JDK 17 installation before building.

**Windows (PowerShell) example:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
```

Adjust the path to match your JDK 17 install location.

---

## How to Run

From the project root directory:

```bash
mvn compile exec:java
```

The application will:

1. Load posts from `posts.json`
2. Run damage and sentiment analyzers
3. Print categorized counts to the console

**Example output:**

```
=== Disaster Social Media Analytics ===

Loaded 18 posts.

--- Damage Analysis ---
  housing          : 4
  transport        : 4
  human            : 3
  infrastructure   : 4
  other            : 3

--- Sentiment Analysis ---
  positive         : 5
  negative         : 3
  neutral          : 10
```

---

## OOP Techniques Used

### Encapsulation

The `Post` class wraps `content`, `date`, and `source` as private fields with getters and setters. External code interacts with posts through a controlled API instead of raw data structures.

### Interface

`Analyzer` defines a single contract:

```java
Map<String, Integer> analyze(List<Post> posts);
```

Any class that implements this interface can be used wherever an analyzer is needed.

### Polymorphism

`Main` treats `DamageAnalyzer` and `SentimentAnalyzer` as `Analyzer` references. The same `analyze()` call works for different implementations, and the program does not need to know each analyzer’s internal logic.

### Strategy Pattern

Each analyzer is a **strategy** for processing a list of posts. You can swap or add strategies (e.g. a future `LocationAnalyzer`) without modifying existing analyzer classes—only wiring in `Main` (or a future coordinator class) changes.

### Separation of Concerns

| Layer | Responsibility |
|-------|----------------|
| `model` | Data representation (`Post`) |
| `service` | I/O and persistence (`DataService`) |
| `analyzer` | Business rules for classification |
| `Main` | Orchestration and console output |

This layout keeps the codebase easy to read, test, and extend for a university OOP project.

---

## Sample Data

`posts.json` contains **18 Vietnamese-language sample posts** about events such as Typhoon Yagi, flooding, infrastructure damage, and relief efforts. Each entry includes:

- `content` — post text  
- `date` — date string (e.g. `2024-09-07`)  
- `source` — platform (e.g. Facebook, Zalo, Twitter)

You can edit this file to test how keyword changes affect classification results.

---

## Future Improvements

- **JavaFX dashboard** — charts and tables for damage and sentiment summaries instead of console-only output.  
- **More data sources** — APIs, CSV files, or database connectors instead of a single JSON file.  
- **More advanced NLP model** — move from keyword matching to machine learning or pre-trained language models for better accuracy on informal Vietnamese text.

---

## Authors & Course Context

This repository is intended for an **OOP course project** demonstrating clean class design, interfaces, and extensibility in Java. Contributions and extensions (new analyzers, UI, data sources) fit naturally into the existing package structure.

---

## License

Academic / educational use. Add a license file here if your course or institution requires one.
