# Disaster Social Media Analytics System

A Java 17 application for an **Object-Oriented Programming (OOP)** university project. The system analyzes social media–style posts about natural disasters and turns informal public reports into structured insights—damage types, sentiment, trends over time, and relief satisfaction—to support **humanitarian logistics** planning.

The project includes a **console application** and a **JavaFX desktop dashboard**. The architecture is designed so that data sources, keywords, categories, sentiment models, and analysis tasks can be extended with minimal changes to existing code.

---

## 1. Overview

During disasters, communities share urgent updates online: flooded roads, damaged homes, calls for food or medicine, and expressions of fear or gratitude. Responders need a fast way to summarize this information.

**Disaster Social Media Analytics System** loads posts, preprocesses Vietnamese text, and runs several analyzers coordinated by `AnalysisService`. Results are shown in the console or in an interactive dashboard with charts and tables.

Core design goals:

- **Separation of concerns** — collection, configuration, preprocessing, analysis, and UI are separate packages.
- **Configuration over code** — keyword lists live in JSON files, not hard-coded in analyzers.
- **Replaceable components** — collectors, preprocessors, sentiment models, and analyzers are wired through interfaces and constructor injection.

---

## 2. Disaster case study

This project uses a **standardized dataset** inspired by real events around **Typhoon Yagi (Bão Yagi)** and related flooding in Vietnam.

| Aspect | Details |
|--------|---------|
| **Disaster** | Typhoon Yagi and post-storm flooding |
| **Time range** | 2024-09-06 to 2024-09-20 |
| **Platforms** | Facebook, TikTok, YouTube, X |
| **Sample themes** | Storm impact, rescue, relief distribution, infrastructure failure |
| **Representative keywords** | bão Yagi, ngập lụt, cứu trợ, sập nhà, mất điện, đường ngập |

Posts are stored in `src/main/resources/data/posts.json` (50 entries). Each record includes `content`, `date`, and `source`.

---

## 3. Main features

| Feature | Description |
|---------|-------------|
| **Load posts from JSON** | `JsonDataCollector` reads the dataset via Gson; `DataService` exposes `loadPosts()`. |
| **Keyword configuration (JSON)** | Damage, sentiment, and relief keywords loaded from `src/main/resources/config/`. |
| **Text preprocessing** | `BasicTextPreprocessor` normalizes text before keyword matching. |
| **Damage classification** | One best-matching category per post (e.g. `housing_damage`, `infrastructure_damage`). |
| **Sentiment analysis** | `SentimentModel` abstraction with keyword-based implementation. |
| **Sentiment trend analysis** | Sentiment counts grouped by date. |
| **Relief satisfaction analysis** | Relief category × sentiment counts (e.g. `food_positive`). |
| **Analysis orchestration** | `AnalysisService` runs all analyzers through a single API. |
| **JavaFX dashboard** | Tables, summary cards, pie/bar/line charts, export to `analysis-report.txt`. |
| **Console application** | Same analysis pipeline with formatted text output. |

---

## 4. Technologies

| Technology | Role |
|------------|------|
| **Java 17** | Language and LTS runtime |
| **Maven** | Build, dependencies, `exec:java` and `javafx:run` |
| **Gson** | JSON parsing for posts and keyword configuration |
| **JavaFX** | Desktop dashboard UI |
| **Git / GitHub** | Version control and project hosting |

---

## 5. Project structure

```
disaster-social-analytics/
├── pom.xml
├── README.md
├── docs/
│   ├── class-diagram.puml
│   └── package-diagram.puml
└── src/main/
    ├── java/com/dsa/
    │   ├── Main.java                 # Console entry point
    │   ├── model/Post.java
    │   ├── collector/                # Data collection layer
    │   ├── config/KeywordConfigLoader.java
    │   ├── preprocess/               # Text preprocessing layer
    │   ├── sentiment/                # Sentiment model abstraction
    │   ├── service/
    │   │   ├── DataService.java
    │   │   └── AnalysisService.java
    │   ├── analyzer/                 # Analysis strategies
    │   └── ui/                       # JavaFX dashboard
    └── resources/
        ├── data/posts.json
        ├── config/
        │   ├── damage-keywords.json
        │   ├── sentiment-keywords.json
        │   └── relief-keywords.json
        └── styles/dashboard.css
```

### Package overview

| Package | Responsibility |
|---------|----------------|
| **`com.dsa.model`** | Domain objects (`Post`). |
| **`com.dsa.collector`** | Data acquisition (`DataCollector`, `JsonDataCollector`). |
| **`com.dsa.config`** | Loading keyword JSON from the classpath. |
| **`com.dsa.preprocess`** | Text cleaning before analysis. |
| **`com.dsa.sentiment`** | Pluggable sentiment classification (`SentimentModel`, `KeywordSentimentModel`). |
| **`com.dsa.service`** | `DataService` (load posts), `AnalysisService` (run analyses). |
| **`com.dsa.analyzer`** | Damage, sentiment, trend, and relief analyzers. |
| **`com.dsa.ui`** | JavaFX dashboard (`DashboardApp`, `DashboardController`, charts, formatting). |

---

## 6. Keyword configuration files

Keywords are stored as JSON objects: each **key** is a category or sentiment label, each **value** is an array of Vietnamese keyword strings.

### `damage-keywords.json`

Defines **damage categories** for `DamageAnalyzer`. Each post is assigned exactly one best-matching category (first match in file order; `other` is the default).

| Category key | Purpose |
|--------------|---------|
| `affected_people` | Injuries, evacuation, missing persons |
| `economic_disruption` | Business, jobs, markets |
| `housing_damage` | Homes, roofs, walls, flooding |
| `personal_asset_loss` | Vehicles, belongings, livestock, crops |
| `infrastructure_damage` | Roads, power, water, schools, hospitals |
| `other` | Default when no keyword matches (usually empty list) |

**To add or rename a damage category:** add or edit a key in this file. No change to `DamageAnalyzer` logic is required.

### `sentiment-keywords.json`

Defines **sentiment labels** for `KeywordSentimentModel`. Classification checks categories in JSON key order; default is `neutral`.

| Key | Purpose |
|-----|---------|
| `positive` | Gratitude, safety, timely support |
| `negative` | Shortages, danger, complaints |
| `neutral` | Factual updates (may be empty; unmatched text defaults to neutral) |

Used by `SentimentAnalyzer`, `SentimentTrendAnalyzer`, and `ReliefSatisfactionAnalyzer` (via `SentimentModel`).

### `relief-keywords.json`

Defines **relief item categories** for `ReliefSatisfactionAnalyzer`. A post may match **multiple** relief categories. Sentiment per match comes from `SentimentModel`, not from this file.

| Key | Purpose |
|-----|---------|
| `food` | Food, rice, water, meals |
| `medical` | Medicine, clinics, first aid |
| `cash` | Cash support, donations |
| `housing` | Shelter, temporary housing |
| `transport` | Boats, vehicles, access routes |

**To add a relief category:** add a new key and keyword list. Result keys become `{category}_{sentiment}` (e.g. `shelter_positive` if you add a `shelter` category).

---

## 7. Configuration and sentiment components

### `KeywordConfigLoader`

Located in `com.dsa.config`. Loads classpath JSON files using Gson:

- `loadDamageKeywords()` → `Map<String, List<String>>`
- `loadSentimentKeywords()` → `Map<String, List<String>>`
- `loadReliefKeywords()` → `Map<String, List<String>>`

On failure (missing file, I/O error, invalid JSON), it prints a clear error message and returns an **empty map** so the application does not crash.

`AnalysisService.createDefault()` uses this loader when wiring analyzers.

### `SentimentModel` and `KeywordSentimentModel`

**`SentimentModel`** (`com.dsa.sentiment`) is an interface for replaceable sentiment classification:

```java
String classify(String text);
List<String> getSentiments();
```

**`KeywordSentimentModel`** is the default implementation:

- Receives `TextPreprocessor` and sentiment keyword map via constructor.
- Cleans text, then matches keywords in configuration order.
- Returns `positive`, `negative`, or `neutral` (default).

Analyzers depend on **`SentimentModel`**, not on keyword maps directly. To use a different approach (e.g. machine learning), implement `SentimentModel` and pass the new class into `AnalysisService.createDefault()` (or a custom factory).

### `AnalysisService`

Coordinates all analyzers in `com.dsa.service`:

| Method | Analyzer |
|--------|----------|
| `analyzeDamage(posts)` | `DamageAnalyzer` |
| `analyzeSentiment(posts)` | `SentimentAnalyzer` |
| `analyzeSentimentTrend(posts)` | `SentimentTrendAnalyzer` |
| `analyzeReliefSatisfaction(posts)` | `ReliefSatisfactionAnalyzer` |

`Main` and `DashboardController` call these methods instead of constructing analyzers themselves. **`AnalysisService.createDefault()`** builds the full dependency graph (preprocessor, config loader, sentiment model, analyzers).

---

## 8. Flexible design — how to extend the system

| Goal | What to change |
|------|----------------|
| **Add a new data source** | Implement `DataCollector` (e.g. `YouTubeDataCollector`). Pass it to `new DataService(collector)`. Collectors return `List<Post>`; no analyzer changes needed. |
| **Change keywords without editing analyzer code** | Edit `damage-keywords.json`, `sentiment-keywords.json`, or `relief-keywords.json`. Restart the application. |
| **Change damage categories** | Add/remove/rename keys in `damage-keywords.json`. Keep `other` as the fallback category. |
| **Change relief item categories** | Add/remove/rename keys in `relief-keywords.json`. |
| **Replace sentiment model** | Create a class implementing `SentimentModel`. Inject it when building `AnalysisService` instead of `KeywordSentimentModel`. |
| **Add an analysis task** | 1) Implement a new analyzer class. 2) Add a field and method on `AnalysisService`. 3) Wire it in `createDefault()`. 4) Update `Main` / dashboard UI to display results. Existing analyzers stay unchanged. |
| **Remove an analysis task** | Remove the analyzer from `AnalysisService` and stop calling it from `Main` / UI. |

This follows **Open/Closed Principle**: behavior is extended by new classes and configuration, not by modifying stable analyzer logic.

---

## 9. OOP principles applied

### Encapsulation

`Post`, collectors, preprocessors, analyzers, and UI controllers each encapsulate their own data and rules.

### Interface-based design

| Interface | Role |
|-----------|------|
| `DataCollector` | How posts are obtained |
| `TextPreprocessor` | How text is normalized |
| `Analyzer` | Flat analysis results (`Map<String, Integer>`) |
| `SentimentModel` | How sentiment is classified |

### Polymorphism

`DataService` works with any `DataCollector`. Sentiment analyzers work with any `SentimentModel`. Console and JavaFX share the same `AnalysisService` API.

### Strategy Pattern

Each analyzer is a **strategy** for interpreting posts. Keyword configuration and `SentimentModel` are additional strategies for matching and classification rules.

### Separation of concerns

| Layer | Concern |
|-------|---------|
| `model` | Data shape |
| `collector` | Data source |
| `config` | External keyword rules |
| `preprocess` | Text normalization |
| `sentiment` | Sentiment classification algorithm |
| `analyzer` | Domain-specific analysis |
| `service` | Application workflows |
| `ui` | Presentation and interaction |

---

## 10. How to run

### Prerequisites

- JDK **17** or newer  
- Apache Maven **3.6+**

```bash
java -version
mvn -version
```

Ensure Maven uses Java 17. If needed, set `JAVA_HOME`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
```

### Console application

```bash
mvn clean compile exec:java
```

Loads posts, runs `AnalysisService`, prints four report sections.

### JavaFX dashboard

```bash
mvn clean javafx:run
```

Open the UI → **Load Data** → **Run Analysis** → view tabs and charts → **Export Report** (writes `analysis-report.txt` in the project root).

---

## 11. Sample output (console)

```
=== Disaster Social Media Analytics ===

Loaded 50 posts.

--- Damage Analysis ---
  affected_people          : 7
  economic_disruption      : 5
  housing_damage           : 11
  personal_asset_loss      : 4
  infrastructure_damage    : 14
  other                    : 9

--- Sentiment Analysis ---
  positive                 : 14
  negative                 : 10
  neutral                  : 26

--- Sentiment Trend Analysis ---
  Date: 2024-09-06
    positive     : 0
    negative     : 1
    neutral      : 2
  ...

--- Relief Satisfaction Analysis ---
  food_positive            : ...
  ...
```

Exact counts depend on the current dataset and keyword configuration files.

---

## 12. Limitations

- The dataset is **simulated and standardized**, not scraped live from social networks (API access, quotas, and compliance are outside coursework scope).
- Classification uses **keyword matching** on preprocessed text, not contextual NLP or machine learning.
- Vietnamese handling does not include full diacritic normalization or stemming.
- `SentimentTrendAnalyzer` does not implement `Analyzer` because it returns a nested map by date.
- Keyword order in JSON affects which category wins when multiple keywords match.

These limits are acceptable for demonstrating OOP structure, configurability, and extensibility.

---

## 13. Future improvements

- **Real social media API collectors** — `FacebookDataCollector`, `YouTubeDataCollector`, `XDataCollector`
- **Database storage** — persist posts and analysis history
- **ML-based `SentimentModel`** — improve accuracy on informal Vietnamese text
- **External config validation** — schema checks for keyword JSON files
- **Dashboard enhancements** — filters tied to analysis runs, export to PDF/CSV

---

## License

Academic / educational use. Add a license file if required by your course or institution.
