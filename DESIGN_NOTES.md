# Design Notes

Key decisions, tradeoffs, and the thinking behind Silicon Valley Trail.

---

## Table of Contents

- [Game Loop & Balance Approach](#game-loop--balance-approach)
- [Why These APIs and How They Affect Gameplay](#why-these-apis-and-how-they-affect-gameplay)
- [Data Modeling](#data-modeling)
- [Error Handling](#error-handling)
- [Tradeoffs](#tradeoffs)
- [If I Had More Time](#if-i-had-more-time)
- [Tests](#tests)

---

## Game Loop & Balance Approach

Each game day follows a fixed sequence:

```
Show stats → Player picks daily action → Apply choice → Check lose conditions
→ Milestone check → Random event (player choice) → Weather event → News event
→ Check lose/win conditions → Apply daily overhead → Advance day
```

### Balance Philosophy

The game is designed to create tension between short-term speed and long-term sustainability. The key levers:

**Daily overhead** drains `-5 morale` and `-$1,500 funds` every day regardless of what you do. This creates a ticking clock — sitting still is not free.

**Choice tradeoffs** — every action costs something:

- Sprint moves fast but drains morale and energy hard
- Rest Day recovers but costs more funds and no progress
- Hackathon builds hype and connections but drains energy
- Investor Meeting brings funds but requires hype > 25 and progress > 30%

**Lock conditions** prevent players from spamming the same choice:

- Rest Day is locked unless energy or morale < 20, and has a 5-day cooldown
- Team Event locked if morale is already high (no point boosting what isn't broken)
- Hackathon locked if energy ≤ 40 (can't run a public event on fumes) or connections ≤ 25
- Investor Meeting locked if hype ≤ 25, progress < 30%, or within 5 days of last meeting

**Tech debt** accumulates from aggressive play (Sprint: +15, pushing through the night: +5) and can be reduced by dedicated refactor time. If tech debt exceeds 60, crisis events start appearing alongside normal events (40% chance), adding a real cost to moving fast and skipping cleanup.

**Lose conditions** — fund ≤ 0, morale ≤ 0, or energy ≤ 0. These are checked at three points per day (after choice, after events, after overhead) so a bad combination of events can end the game mid-day.

---

## Why These APIs and How They Affect Gameplay

### OpenWeatherMap

Chosen because it has a generous free tier, no credit card required, and returns structured JSON with condition codes and temperature that map cleanly to game effects.

Weather is fetched once per geographic zone (San Jose → Mountain View → Palo Alto → San Francisco) and cached until the player crosses into a new zone. This avoids hammering the API and gives each region a consistent "feel" during that leg of the journey.

**Effects by condition:**

| Condition | Effect |
|---|---|
| Clear (800) | +5 morale, +5 energy — team feels alive |
| Cloudy (801–804) | No effect — neutral day |
| Rain (3xx, 5xx) | -5 morale, -8 energy — commute dampens spirits |
| Thunderstorm (2xx) | -10 morale, -15 energy — progress stalls |
| Fog (7xx) | -3 morale, -1 energy — slow pace |
| Heat wave (>35°C) | -15 energy, -$200 — someone runs out for cold drinks |

### NewsAPI

Chosen for the same reasons as OpenWeatherMap — free developer tier, simple REST API, and headlines map naturally to the startup world theme.

News is fetched at most once every 3 days (2-day cooldown after each trigger) and only fires 25% of the time in mock mode. Real mode scans headline keywords and maps them to effects:

**Effects by keyword:**

| Keywords | Effect |
|---|---|
| layoff, fired, cut jobs | -10 morale, -5 hype |
| burnout, mental health | -5 morale, -10 energy |
| recession, crash, downturn | -8 morale, -5 energy, -5 hype |
| ipo, acquisition | +5 morale, +5 energy, +8 hype |
| funding, raised, series | +8 morale, +5 energy, +10 hype, +5 connections |
| ai, artificial intelligence | +5 morale, +10 energy, +10 hype, +5 connections |

> [!NOTE]
> Both APIs fail gracefully — any network error, timeout, or non-200 response silently falls back to mock events so gameplay is never interrupted.

---

## Data Modeling

### Game State

`GameState` holds all mutable player stats as plain ints with clamped setters:

| Field | Initial Value | Range | Notes |
|---|---|---|---|
| `fund` | $50,000 | min 0 | Floored at 0 — no negative balance |
| `morale` | 100 | 0–100 | Lose if hits 0 |
| `energy` | 100 | 0–100 | Lose if hits 0 |
| `progress` | 0 | 0–100 | Win at 100% |
| `connections` | 10 | 0–100 | Required for Hackathon |
| `hype` | 10 | 0–100 | Required for Investor Meeting |
| `techDebt` | 0 | 0–100 | Crisis events trigger above 60 |
| `day` | 1 | — | Increments each day |
| `lastRestDay` | -10 | — | -10 = "never rested" |
| `lastInvestorMeetingDay` | -10 | — | -10 = "never met" |
| `nextMilestoneIndex` | 0 | — | Pointer to next unclaimed milestone |
| `gameOver` | false | — | Set to true on win or lose |

### Events

Two event types follow the same `ExternalEvent` shape (emoji, narrative, stat deltas):

- **RandomEvent** — internal events the player actively chooses between (A/B choices, each with a `RandomEventChoice` holding deltas + outcome narrative)
- **ExternalEvent** — API-driven events (weather, news) applied automatically via the `ExternalEventHandler` strategy interface

### Milestones

Four fixed checkpoints at progress thresholds (20%, 45%, 72%, 100%). Each milestone has a name, description, and fund reward. `MilestoneTracker` holds a pointer (`nextMilestoneIndex`) and advances it as thresholds are crossed — so milestones can't be double-claimed.

### Persistence

Game state is serialised to JSON using Gson and saved at `data/saves/{userId}.json`. The save is written after every day and deleted if the game ends (win or lose). `PlayerDataStore` is the only class that touches the file system — all other classes work through it.

> [!NOTE]
> A `repairMissingFields()` method handles backwards-compatible loading — if a save file pre-dates a new field (e.g. `lastRestDay`), the field defaults to `-10` rather than `0`, which avoids false cooldown triggers on load.

---

## Error Handling

### Network Failures and Timeouts

Both `WeatherApiClient` and `NewsApiClient` wrap all HTTP calls in try/catch. Any failure — connection timeout (5s), read timeout (5s), non-200 status, malformed JSON, null response — silently returns `null` or falls back to `randomMock()`. The game never shows an error to the player for API issues.

```java
try {
    // ... HTTP request
    if (response.statusCode() != 200) return randomMock(progress);
    // ... parse and return
} catch (Exception e) {
    return randomMock(progress);
}
```

### Rate Limits

NewsAPI's free tier has a rate limit. The 2-day cooldown on news events means at most one real API call every 2 in-game days, well within free tier limits for any reasonable play session.

### Missing or Blank API Keys

Checked at the start of each fetch:

```java
final String apiKey = System.getenv("API_KEY");
if (apiKey == null || apiKey.isBlank()) return randomMock();
```

> [!TIP]
> No key → mock mode automatically. No configuration or error message needed.

### Save File Corruption or Missing Fields

Gson returns `null` for missing JSON fields. `repairMissingFields()` converts null-default ints (`0`) to semantically correct defaults (`-10` for cooldown fields) after deserialisation.

---

## Tradeoffs

### What Was Prioritised

- **Playability over realism** — game balance matters more than accurate simulation. Stats are tuned so a bad streak is recoverable but a series of poor decisions genuinely leads to game over.
- **Testability** — package-private constructors allow injectable dependencies (`ExternalEventProvider` takes clients, `PlayerDataStore` takes a save directory). No Mockito needed — subclasses and `@TempDir` are enough.
- **Feature cohesion** — feature-based packages (`api/weather/`, `api/news/`) over layer-based (`services/`, `repositories/`). Each package owns its concern end to end.

### Known Limitations

- **Single `Scanner` instance** — the scanner is created in `SiliconValleyTrail.java` and passed through constructors. This works but couples the entire call chain to `System.in`. A proper solution would use an injectable `InputReader` interface.
- **`EventEngine` uses an internal `Random`** — making it hard to write deterministic tests for random event selection without refactoring to accept an injectable `Random`. Currently tested statistically (run many times, check distribution).
- **Weather event is always applied** — even on cloudy days (0 delta), the weather handler fires and prints a line. Minor but adds visual noise on no-effect days.

---

## If I Had More Time

### Status Effects / Lingering Consequences

Choices today carry mechanical weight into tomorrow. Examples:

- "Exhausted" — next day's energy recovery is halved
- "Low morale spiral" — morale drain doubles for 2 days
- "On a roll" — consecutive Sprint days add a stacking progress bonus

Requires a `StatusEffect` model (name, description, duration, stat modifier) and `GameState` holding a list of active effects ticked down each day.

### Dynamic Choice Unlocking

Instead of choices being locked/unlocked by fixed thresholds, choices could contextually appear or disappear based on game state — e.g. Investor Meeting only shows after reaching a certain milestone, Hackathon only appears when connections > 30. This would make the day feel more reactive and less like a menu.

### Storage Scaling

Current JSON file storage handles 1–2 players well. The migration path:

- **~1,000 players** — JSON files as-is
- **~500,000 players** — SQLite via JDBC (`PlayerDataStore` is the only class to change)
- **Millions** — PostgreSQL/MySQL with connection pooling (HikariCP), indexed on `userId`

Since `PlayerDataStore` is the only class touching storage, the migration only affects one file regardless of which phase.

### Richer API Integration

- **Stock market API** — market down → investor confidence drops → fund penalty
- **GitHub trending API** — a trending repo in your stack → team morale boost, tech debt reduction option
- **HackerNews API** — "Show HN" stories → hype boost if keywords match your startup description

---

## Tests

119 unit tests across 11 test classes. Coverage focuses on logic that directly affects game outcomes.

| Class | Tests | What's covered |
|---|---|---|
| `GameState` | 22 | All delta methods, clamping, reset, cooldown tracking |
| `GameEngine` | 23 | All 4 lock conditions, lose conditions, `visualLength` |
| `ExternalEventProvider` | 12 | Weather zone caching, news cooldown, reset |
| `WeatherEventHandler` | 11 | Delta application, clamping to stat bounds |
| `WeatherApiClient` | 10 | City routing for all 4 zones + boundaries |
| `EventEngine` | 10 | Normal vs crisis event selection, `pickDifferentIndex` guarantee |
| `MilestoneTracker` | 8 | Threshold detection, reward application, no double-trigger |
| `NewsEventHandler` | 8 | `hasNoImpact` skip logic, delta application |
| `PlayerDataStore` | 7 | Save, load, delete, missing file, missing fields |
| `NewsApiClient` | 5 | Non-null validation, eventual mock trigger |
| `User` | 3 | Role assignment, name storage |

Run with: `mvn test`
