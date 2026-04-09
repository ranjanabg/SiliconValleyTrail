# Silicon Valley Trail

You are the Founder.

Your startup is small, scrappy, and sitting in a co-working space in San Jose with $50,000 in the bank, a fired-up team, and one goal: make it to San Francisco.

Not just physically — San Francisco represents everything your startup is chasing. The investors. The press. The credibility. The moment when people stop asking "who are you?" and start asking "how do I get in?"

The trail between here and there is 100 miles and a hundred decisions. You'll cross Mountain View, cut through Palo Alto, and push into the city — but every mile costs money, energy, and belief. The team believes in you right now. Keep it that way.

Each day you choose how to lead the team — and every choice has a cost.

**But you don't control everything.**

Every day a random event lands on your desk — a competing offer for your best engineer, a VC cold email, a critical bug in production, a journalist who wants your story. You pick A or B. There's no perfect answer.

Then the real world weighs in. If it's raining in Palo Alto, the commute is rough and spirits drop. If tech layoffs are making headlines, the team gets nervous. If an AI breakthrough is all over the news, everyone stays late to experiment. Live weather and real tech headlines pulled from external APIs shape what kind of day it actually turns out to be.

**You win by reaching San Francisco with your team intact.**
You lose if you run out of money, your team quits (morale hits zero), or everyone burns out (energy hits zero).

The valley doesn't wait. Neither does your runway.

---

## Quick Start

### Prerequisites

- **Java 21+** — [Download from adoptium.net](https://adoptium.net)
- **Maven 3.8+** — [Download from maven.apache.org](https://maven.apache.org/download.cgi)

Verify your setup:
```bash
java -version    # Should print 21 or higher
mvn -version     # Should print 3.8 or higher
```

### Clone and run

```bash
git clone https://github.com/ranjanabg/SiliconValleyTrail.git
cd SiliconValleyTrail
mvn compile exec:java
```

That's it. No database, no server, no setup beyond Java and Maven.

---

## API Keys

The game integrates with two optional external APIs. Without API keys the game falls back to built-in mock events automatically — no configuration needed to play.

### Setting API keys

Copy the example file:
```bash
cp .env.example .env
```

Fill in your keys in `.env` — but note the game reads keys **from environment variables**, not from a `.env` file directly. Export them in your shell before running:

**macOS / Linux:**
```bash
export OPENWEATHER_API_KEY=your_key_here
export NEWS_API_KEY=your_key_here
mvn compile exec:java
```

**Windows (Command Prompt):**
```cmd
set OPENWEATHER_API_KEY=your_key_here
set NEWS_API_KEY=your_key_here
mvn compile exec:java
```

### Where to get API keys

| API | Sign up | Notes |
|---|---|---|
| OpenWeatherMap | [openweathermap.org/api](https://openweathermap.org/api) | Free tier, no credit card |
| NewsAPI | [newsapi.org](https://newsapi.org) | Free tier, developer use |

### Running in mock mode

Simply don't set the environment variables. The game detects missing keys and uses built-in mock events for both weather and news. All gameplay is fully functional in mock mode.

---

## Running Tests

```bash
mvn test
```

119 tests across 11 test classes covering game state, events, lock conditions, API handlers, persistence, and more.

To run a specific test class:
```bash
mvn test -Dtest=GameEngineTest
mvn test -Dtest=EventEngineTest
mvn test -Dtest=GameStateTest
```

---

## Building a JAR

```bash
mvn package
java -jar target/silicon-valley-trail-1.0-SNAPSHOT.jar
```

---

## Example Gameplay

```
Enter your name: Ranjana

=== Silicon Valley Trail ===
1. New Game
2. Load Game
3. Quit

> 1

==========================================
  ☀️  Day 1
==========================================
  💰 Fund         : $50,000
  😊 Team Morale  : 100/100
  🔋 Team Energy  : 100/100
  🤝 Connections  : 10/100
  🔥 Hype         : 10/100
  🗺️  Journey      : San Jose [░░░░░░░░░░] San Francisco
==========================================

What's your call for the team today, Founder?

  1. 🏃 Sprint           - Push the team hard to move faster   [💰 -$3,000     😊 -15   🔋 -15   🗺️  +7%]
  2. 🚶 Steady Pace      - Move at a sustainable speed         [💰 -$2,000     😊  -8   🔋  -8   🗺️  +3%]
  3. 😴 Rest Day         - Full day off, recover and clean up  [🔒 Team is still going — rest when energy or morale drops below 20]
  ...

Enter your choice (1-7): 1

🎲 Event: A VC sends a cold email asking to learn more about your startup.

  A. Reply and schedule a meeting — could be big
  B. Ignore it — stay focused on the product

  Your choice (A/B): A
```

---

## Architecture Overview

The game uses a **feature-based package structure** — each package owns one concern end to end.

```
com.siliconvalleytrail/
├── SiliconValleyTrail.java     # Entry point — orchestrates the game loop
├── game/                       # Core game state and engine
│   ├── GameState.java          # All player stats (fund, morale, energy, progress, …)
│   └── GameEngine.java         # Daily game loop, choice processing, win/lose conditions
├── events/                     # Random in-game events
│   ├── EventEngine.java        # Selects and applies random events each day
│   ├── RandomEvent.java        # An event with two player choices
│   └── RandomEventChoice.java  # A choice with stat deltas and outcome narrative
├── api/                        # External API integration
│   ├── ExternalEvent.java      # Represents impact from a real-world API event
│   ├── ExternalEventProvider.java  # Caches and delivers weather + news events
│   ├── ExternalEventHandler.java   # Strategy interface for applying events
│   ├── weather/                # OpenWeatherMap client + handler
│   └── news/                   # NewsAPI client + handler
├── milestone/                  # Journey milestone tracking and rewards
├── cli/                        # All console I/O
│   ├── ConsoleUtils.java       # Shared screen clear and Enter prompt utilities
│   ├── GameIntro.java          # Welcome screen and founder name prompt
│   ├── Menu.java               # Main menu display and dispatch
│   └── commands/               # Command Pattern — NewGame, LoadGame, Quit
├── player/                     # Player identity (User, UserRole)
└── storage/                    # Save/load game state (PlayerDataStore)
```

### Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| Java | 21 | Language and runtime |
| Maven | 3.8+ | Build, dependency management |
| Lombok | 1.18.38 | Reduces boilerplate (`@Getter`, `@AllArgsConstructor`) |
| Gson | 2.10.1 | JSON serialisation for save files |
| JUnit Jupiter | 5.10.2 | Unit testing |
| maven-surefire-plugin | 3.2.5 | Test runner |

No database, no web server, no external frameworks.

---

## AI Utilization

[Claude Code](https://claude.com/claude-code) (Anthropic's CLI coding assistant, powered by claude-sonnet-4-6) was used in three specific areas:

- **Architectural decisions** — discussing design pattern options (Command Pattern for menu, Strategy Pattern for API event handlers), storage tradeoffs, and package structure, with final decisions made by the developer
- **Unit tests** — assistance writing test cases for core mechanics (`GameState`, `GameEngine`, `EventEngine`, `ExternalEventProvider`, API handlers, persistence)
- **Debugging** — identifying root causes of bugs encountered during development (double Enter prompts, lose condition ordering, emoji terminal alignment)
