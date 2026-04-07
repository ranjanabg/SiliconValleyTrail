# Future Extensions

A running list of planned features and ideas to add to the game.

---

## V3 Planned Features

### Dynamic Daily Choices
Choices could be made context-aware — showing only a relevant subset per day or unlocking certain choices based on game state (e.g. Investor Meeting only available after reaching Palo Alto, Hackathon only available when energy > 50).

### Status Effects / Lingering Consequences
Choices today carry narrative and mechanical weight into tomorrow. For example:
- "Exhausted" state → next day's energy recovery is halved
- "Low morale spiral" → morale drain increases for 2 days
- "Food deprived" → energy cap temporarily reduced until a meal event triggers

Requires a `StatusEffect` model (name, description, duration, stat modifier) and `GameState` to hold a list of active effects applied and ticked down each day.

### Public Web API Integration
Some events trigger conditionally based on real-world live data:
- **Weather API** — bad weather triggers a "rough commute day" energy drain event
- **Stock market API** — market down triggers an "investor confidence drops" fund loss event
- **News API** — tech layoff news triggers a "team is worried" morale drop event

---

## Storage Scaling

Currently save data is stored as JSON files (`data/saves/{userId}.json`), which works well for up to ~500–1,000 players.

If the player base grows exponentially, the storage strategy must evolve:

### Phase 1 — Up to ~1,000 players (current)
JSON files per player. Simple, no setup, easy to debug.

### Phase 2 — Up to ~500,000 players
Migrate to **SQLite** — an embedded database, no server needed. Add a JDBC driver and a `SaveRepository` that replaces `SaveManager`. Game state stored in a `saves` table with `userId` as the primary key.

### Phase 3 — Millions of players
Migrate to a **server-based SQL database** (PostgreSQL or MySQL) with:
- Connection pooling (HikariCP)
- Indexed queries on `userId`
- Schema migration tooling (Flyway or Liquibase)
- Separate read replicas if needed for leaderboards/analytics

### Key design principle
`SaveManager` is the only class that touches storage. When migrating between phases, only `SaveManager` needs to change — the rest of the game is unaffected.
