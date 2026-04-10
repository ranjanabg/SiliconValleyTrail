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
 1 ── 📊  Show stats & available choices
      │
 2 ── 🎯  Player picks daily action
      │
 3 ── ⚙️   Apply choice deltas
      │
 4 ── ❓  Lose check ──────────────── fund / morale / energy = 0 ──▶ 💀 Game Over
      │
 5 ── 🏆  Milestone check
      │
 6 ── 🎲  Random event  (A/B player choice)
      │
 7 ── 🌤️   Weather event  (auto-applied)
      │
 8 ── 📰  News event                         
      │
 9 ── ❓  Lose / Win check ─────────── progress = 100% ──────────▶ 🎉 You Win!
      │
10 ── 📉  Daily overhead  (-5 morale, -$1,500 funds)
      │
11 ── ❓  Final lose check ─────────── fund / morale / energy = 0 ──▶ 💀 Game Over
      │
12 ── ➡️   Advance to next day  (save game)
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

The game is set on a real geographic trail through Silicon Valley. These are real cities with real weather, and that's exactly why this API fits. When the game says it's raining in Palo Alto, it actually is. That connection between the real world and the game world is what makes the weather mechanic feel meaningful rather than random.

Weather directly influences how a startup team functions day to day. A sunny morning lifts morale naturally. A thunderstorm that kills the commute and traps everyone indoors is something every Bay Area engineer has experienced. The API lets the game reflect that lived reality — the player isn't just reading a fictional weather report, they're feeling the same conditions their real counterparts in the valley deal with.

Weather is fetched once per geographic zone (San Jose → Mountain View → Palo Alto → San Francisco) and cached until the player crosses into a new zone, giving each leg of the journey a consistent feel.

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

Startups don't operate in a vacuum. The mood of a founding team is shaped by what's happening in the industry around them — a wave of tech layoffs makes everyone nervous about job security, a high-profile funding round reminds the team that big things are still possible, an AI breakthrough has engineers staying late just to experiment. This is the reality of working in the valley, and it felt wrong to ignore it in a game set there.

Tech news headlines are a direct signal of the startup ecosystem's pulse. Scanning real headlines and mapping them to team morale, energy, and hype means the game world reacts to the same news cycle the player might be reading that morning. It blurs the line between the game and reality in a way that makes the experience feel alive.

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

The data layer is built around three deliberate separations: **state** lives in one place and is the single source of truth, **events** are a distinct concept that describe what happens and what changes, and **persistence** is fully isolated so the rest of the game never touches the file system directly.

### Game State

All mutable player data lives exclusively in `GameState`. No other class holds game stats — they only call `GameState` methods to read or apply changes. This means the game's current condition is always queryable from one place, and there's no risk of stats drifting out of sync across classes.

Every stat has an explicit boundary enforced at the setter level:

| Field | Initial Value | Range | Notes |
|---|---|---|---|
| `fund` | $50,000 | min 0 | Floored at 0 — a startup can't go into negative debt in this model |
| `morale` | 100 | 0–100 | Clamped — morale can't exceed 100 no matter how many boosts stack |
| `energy` | 100 | 0–100 | Clamped — same as morale |
| `progress` | 0 | 0–100 | Clamped — represents % of the trail completed |
| `connections` | 10 | 0–100 | Starts low — must be earned through events and hackathons |
| `hype` | 10 | 0–100 | Starts low — gates the Investor Meeting option |
| `techDebt` | 0 | 0–100 | Accumulates from aggressive choices; crisis events fire above 60 |
| `day` | 1 | — | Monotonically increments, never resets mid-game |
| `lastRestDay` | -10 | — | Sentinel value: -10 means "never rested", not day 0 |
| `lastInvestorMeetingDay` | -10 | — | Same sentinel pattern — avoids false cooldown triggers on a fresh game |
| `nextMilestoneIndex` | 0 | — | Pointer into the milestone list; advances forward only |
| `gameOver` | false | — | Set to true on win or lose; checked by the game loop to stop the day cycle |

**Why clamping matters:** without it, a sequence of positive events could push morale to 150, making the player effectively immune to drains for many days. Clamping at 100 keeps the game in tension regardless of how many good events fire.

**Why -10 for cooldown sentinels instead of 0:** cooldowns are calculated as `currentDay - lastEventDay`. If a new game starts on day 1 and `lastRestDay` defaulted to 0, the cooldown check `1 - 0 = 1` would incorrectly show a cooldown active. Starting at -10 means `1 - (-10) = 11`, well past any cooldown threshold, so the option is always available on day one.

### Events

Events are a distinct data concept from state — they describe *what happened* and *what changes*, but hold no persistent game data themselves. Two types:

- **RandomEvent** — internal events the player actively chooses between. Each has two `RandomEventChoice` objects, each holding stat deltas and an outcome narrative. The player picks A or B; the chosen deltas are applied to `GameState`.
- **ExternalEvent** — API-driven events (weather, news) carrying the same shape (emoji, narrative, stat deltas) but applied automatically without player input, via the `ExternalEventHandler` strategy interface.

Keeping events as plain data objects — rather than embedding logic in them — means a new event type is just a new instance with different numbers. The application logic lives in the handlers, not the events themselves.

### Milestones

Ten checkpoints mapped to real cities along the trail. Each milestone is a data object with a name, story moment, progress threshold, and stat bonus. `MilestoneTracker` holds a pointer (`nextMilestoneIndex`) that only advances forward — so milestones can't be double-claimed even if progress jumps past multiple thresholds in one day.

| City | Progress | Bonus |
|---|---|---|
| Santa Clara | 9% | +$3,000, +5 energy |
| Sunnyvale | 18% | +$5,000, +10 morale |
| Mountain View | 27% | +5 morale, +10 hype |
| Cupertino | 36% | +15 energy |
| Los Altos | 45% | +10 morale, +5 connections |
| Palo Alto | 54% | +$10,000 |
| Menlo Park | 63% | +15 morale, +10 energy |
| Redwood City | 72% | +$5,000, +10 connections |
| San Mateo | 81% | +10 morale, +8 connections |
| Burlingame | 90% | +10 morale, +15 hype |

### Persistence

`PlayerDataStore` is the only class that touches the file system. `GameState` knows nothing about saving — it's a plain data object. `GameEngine` calls `saveManager.savePlayerData()` and `saveManager.deletePlayerData()`. Nothing else does. This isolation means the storage strategy can be swapped entirely by changing one class.

Game state is serialised to JSON using Gson at `data/saves/{userId}.json`, written after every day and deleted when the game ends.

**Why JSON over SQLite or a database:**

| | JSON files | SQLite | PostgreSQL |
|---|---|---|---|
| Setup required | None | JDBC driver | Running server |
| Suitable for 1–2 players | Yes | Yes (overkill) | Yes (overkill) |
| Human-readable saves | Yes | No | No |
| Query capability | No | Yes | Yes |
| Scales beyond ~1,000 players | No | Yes | Yes |

For a local CLI game with one active player at a time, JSON is the right fit — Gson is already a dependency, saves are human-readable and easy to debug, and there is no concurrent access to worry about. If the player base grew, only `PlayerDataStore` would need to change.

> [!NOTE]
> A `repairMissingFields()` method handles backwards-compatible loading — if a save file pre-dates a new field (e.g. `lastRestDay`), the field defaults to `-10` rather than `0`, which avoids false cooldown triggers on load.

---

## Error Handling

### Network Failures and Timeouts

Both `WeatherApiClient` and `NewsApiClient` wrap all HTTP calls in try/catch. Any failure — connection timeout (5s), read timeout (5s), non-200 status, malformed JSON, null response silently returns `null` or falls back to `randomMock()`. The game never shows an error to the player for API issues.

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
