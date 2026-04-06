# Future Extensions

A running list of planned features and ideas to add to the game.

---

## Daily Choices

Currently the game has 3 fixed choices per day related to team movement pace. Planned additions:

- **Food Break** — Small fund cost, boosts energy and morale
- **Team Event** — Larger fund cost, significant morale and energy boost, no progress
- **Hackathon** — Fund cost, drains energy, big progress boost
- **Investor Meeting** — Gains fund, drains energy, morale impact

Each new choice just needs to be added to the `DAILY_CHOICES` list in `GameEngine.java` using the existing `Choice` constructor:
```java
new Choice("description", fundDelta, moraleDelta, energyDelta, progressDelta)
```

Longer term, choices could be made dynamic — showing only a relevant subset per day or unlocking certain choices based on game context (e.g. investor meeting only available after a certain progress milestone).

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
