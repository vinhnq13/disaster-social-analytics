# Disaster Social Media Analytics System

A console-based Java application for an **Object-Oriented Programming (OOP)** university project. The system analyzes social media–style posts about natural disasters and turns informal public reports into structured insights—damage types, sentiment, trends over time, and relief satisfaction—to support **humanitarian logistics** planning.

The current release focuses on a clean, extensible backend. A graphical dashboard is planned as a future enhancement.

---

## 1. Overview

During disasters, communities share urgent updates online: flooded roads, damaged homes, calls for food or medicine, and expressions of fear or gratitude. Responders need a fast way to summarize this information.

**Disaster Social Media Analytics System** loads a dataset of Vietnamese posts, preprocesses text, and runs several analyzers that print aggregated results to the console. The architecture separates **data collection**, **preprocessing**, **domain models**, and **analysis** so new data sources or analyzers can be added without rewriting the whole application.

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
| **Load posts from JSON** | Reads the dataset via Gson from the classpath. |
| **Data collection layer** | `DataCollector` interface with `JsonDataCollector`; `DataService` delegates collection to the active collector. |
| **Text preprocessing layer** | `TextPreprocessor` / `BasicTextPreprocessor` normalizes text before keyword matching (lowercase, URL removal, hashtag cleanup, punctuation and spacing). |
| **Damage classification** | `DamageAnalyzer` assigns each post to one best-matching category: `affected_people`, `economic_disruption`, `housing_damage`, `personal_asset_loss`, `infrastructure_damage`, or `other`. |
| **Sentiment analysis** | `SentimentAnalyzer` classifies posts as `positive`, `negative`, or `neutral`. |
| **Sentiment trend analysis** | `SentimentTrendAnalyzer` groups sentiment counts by date (ascending). |
| **Relief satisfaction analysis** | `ReliefSatisfactionAnalyzer` counts satisfaction-style sentiment per relief type: `food`, `medical`, `cash`, `housing`, `transport`. |

All analyzers use **keyword matching** on preprocessed Vietnamese text. Keyword lists live in maps inside each analyzer class for straightforward extension in coursework.

---

## 4. Technologies

| Technology | Role |
|------------|------|
| **Java 17** | Language and LTS runtime |
| **Maven** | Build, dependencies, `exec:java` runner |
| **Gson** | JSON parsing for posts |
| **Git / GitHub** | Version control and project hosting |

---

## 5. Project structure

```
disaster-social-analytics/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/dsa/
    │   ├── Main.java
    │   ├── model/
    │   │   └── Post.java
    │   ├── collector/
    │   │   ├── DataCollector.java
    │   │   └── JsonDataCollector.java
    │   ├── preprocess/
    │   │   ├── TextPreprocessor.java
    │   │   └── BasicTextPreprocessor.java
    │   ├── service/
    │   │   └── DataService.java
    │   └── analyzer/
    │       ├── Analyzer.java
    │       ├── DamageAnalyzer.java
    │       ├── SentimentAnalyzer.java
    │       ├── SentimentTrendAnalyzer.java
    │       └── ReliefSatisfactionAnalyzer.java
    └── resources/data/
        └── posts.json
```

### Package overview

| Package | Responsibility |
|---------|----------------|
| **`com.dsa.model`** | Domain objects (`Post`: content, date, source). |
| **`com.dsa.collector`** | Data acquisition (`DataCollector`, `JsonDataCollector`). Future: `YouTubeDataCollector`, `FacebookDataCollector`, `XDataCollector`. |
| **`com.dsa.preprocess`** | Text cleaning before analysis (`TextPreprocessor`, `BasicTextPreprocessor`). |
| **`com.dsa.service`** | Application service that loads posts through a `DataCollector` (`DataService`). |
| **`com.dsa.analyzer`** | Analysis strategies: damage, sentiment, trends, relief satisfaction. |

---

## 6. OOP principles applied

### Encapsulation

`Post` hides fields behind getters and setters. Each analyzer encapsulates its keyword maps and classification logic. Collectors and preprocessors encapsulate I/O and text rules.

### Interface-based design

- `DataCollector` — how posts are obtained  
- `TextPreprocessor` — how text is normalized  
- `Analyzer` — common contract `Map<String, Integer> analyze(List<Post>)` for flat result analyzers  

### Polymorphism

`Main` can treat `DamageAnalyzer`, `SentimentAnalyzer`, and `ReliefSatisfactionAnalyzer` as `Analyzer` references. `DataService` works with any `DataCollector` implementation. All analyzers share the same `TextPreprocessor` instance injected at construction time.

### Strategy Pattern–like analyzer architecture

Each analyzer is a **strategy** for processing a list of posts. New analysis types (e.g. location extraction) can be added by implementing `Analyzer` or following the same constructor-injection pattern as `SentimentTrendAnalyzer`, without modifying existing analyzer code.

### Separation of concerns

| Layer | Concern |
|-------|---------|
| `model` | What data looks like |
| `collector` | Where data comes from |
| `preprocess` | How text is prepared |
| `service` | How the app loads data |
| `analyzer` | What insights are computed |
| `Main` | Orchestration and console output |

### Extensibility

- Swap `JsonDataCollector` for API-based collectors.  
- Swap `BasicTextPreprocessor` for advanced normalization.  
- Add analyzers or keyword categories via new classes or map entries.  

---

## 7. How to run

### Prerequisites

- JDK **17** or newer  
- Apache Maven **3.6+**

Check your environment:

```bash
java -version
mvn -version
```

Ensure Maven uses Java 17. If `mvn -version` reports an older JDK, set `JAVA_HOME` to your Java 17 installation.

**Windows (PowerShell) example:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
```

### Run the application

From the project root:

```bash
mvn clean compile exec:java
```

The program loads posts, runs all analyzers, and prints four report sections to the console.

---

## 8. Sample output

```
=== Disaster Social Media Analytics ===

Loaded 50 posts.

--- Damage Analysis ---
  affected_people          : 6
  economic_disruption      : 5
  housing_damage           : 12
  personal_asset_loss      : 4
  infrastructure_damage    : 14
  other                    : 9

--- Sentiment Analysis ---
  positive                 : 20
  negative                 : 5
  neutral                  : 25

--- Sentiment Trend Analysis ---
  Date: 2024-09-06
    positive     : 0
    negative     : 0
    neutral      : 3

  Date: 2024-09-07
    positive     : 1
    negative     : 1
    neutral      : 2

  ... (additional dates through 2024-09-20)

--- Relief Satisfaction Analysis ---
  food_positive            : 4
  food_negative            : 1
  food_neutral             : 5
  medical_positive         : 1
  medical_negative         : 1
  medical_neutral          : 2
  cash_positive            : 3
  cash_negative            : 0
  cash_neutral             : 1
  housing_positive         : 5
  housing_negative         : 1
  housing_neutral          : 7
  transport_positive       : 1
  transport_negative       : 0
  transport_neutral        : 7
```

---

## 9. Limitations

- The dataset is **simulated and standardized**, not scraped live from social networks. Real-time APIs for Facebook, TikTok, YouTube, and X require authentication, quotas, and compliance review that are outside the scope of this coursework.
- Classification uses **simple keyword matching**, not machine learning or contextual NLP.
- Vietnamese text handling does not include full diacritic normalization or stemming.
- `SentimentTrendAnalyzer` and `ReliefSatisfactionAnalyzer` use slightly different sentiment keyword sets where relief-specific wording is needed.
- Output is **console-only**; there is no persistent storage or visualization yet.

These limits are acceptable for demonstrating OOP structure and extensibility in a university project.

---

## 10. Future improvements

- **JavaFX dashboard** — charts and tables for damage, sentiment, trends, and relief satisfaction  
- **Real social media API collectors** — `YouTubeDataCollector`, `FacebookDataCollector`, `XDataCollector` implementing `DataCollector`  
- **Database storage** — persist posts and analysis results for historical queries  
- **Advanced NLP model** — improve accuracy on informal Vietnamese disaster text  
- **Configurable keyword files** — load category and sentiment keywords from JSON or YAML without recompiling  

---

## License

Academic / educational use. Add a license file if required by your course or institution.
