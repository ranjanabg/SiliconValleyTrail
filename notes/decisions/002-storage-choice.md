# Decision 002: Save Game Storage

**Date:** 2026-04-05
**Status:** Accepted

## Context

We needed a way to persist game state (fund, morale, energy, progress, day) per player so returning players can resume where they left off. The storage choice needed to be simple to set up for the current player base of 1–2 active players.

## Alternatives Explored

### 1. JSON File Storage ✅ (Chosen)
Each player's game state is saved as a JSON file at `data/saves/{userId}.json` using Gson.

**Capacity:** Each save file is approximately 100–200 bytes. The file system handles individual file reads well up to around **500–1,000 players** before directory listing and file lookup performance degrades noticeably. Beyond that, searching across saves (e.g. leaderboards, admin views) becomes slow with no querying capability.

**Pros:**
- No server or database setup required
- Gson already a project dependency
- Human-readable — easy to inspect and debug
- One file per player, simple to manage
- Zero configuration

**Cons:**
- Not suitable for concurrent access (multiple writes at the same time)
- No querying capability (can't filter/sort saves)
- Degrades beyond ~500–1,000 players
- No built-in data integrity guarantees

---

### 2. SQLite (Embedded SQL)
An embedded relational database stored as a single file, accessed via JDBC.

**Capacity:** Handles up to **100,000–500,000 players** comfortably with proper indexing. Supports concurrent reads but has limited concurrent write performance.

**Pros:**
- Supports SQL queries
- Better data integrity than flat files
- Still no server setup needed
- Handles concurrent reads better

**Cons:**
- Requires JDBC driver dependency
- Adds schema management complexity
- Overkill for 1–2 players with simple flat game state
- More setup for no meaningful gain at current scale

---

### 3. Full SQL Database (MySQL / PostgreSQL)
A server-based relational database.

**Capacity:** Designed for **millions of concurrent players** with proper indexing, connection pooling, and hardware.

**Pros:**
- Scales to thousands of concurrent players
- Full query support, transactions, indexing
- Battle-tested for production systems

**Cons:**
- Requires a running database server
- Connection management, credentials, schema migrations
- Massive overkill for a local CLI game with 1–2 players

---

## Decision

**JSON file storage** was chosen because the project currently has 1–2 players, Gson is already a dependency, and the game state is a simple flat object. The overhead of setting up SQLite or a full database provides no benefit at this scale.

## Trade-off Comparison

| Criteria | JSON File | SQLite | Full SQL |
|---|---|---|---|
| Setup complexity | None | Low | High |
| Suitable for 1–2 players | Yes | Yes (overkill) | Yes (overkill) |
| Player capacity | ~500–1,000 | ~100,000–500,000 | Millions |
| Concurrent access | No | Limited | Yes |
| Query capability | No | Yes | Yes |
| Human-readable saves | Yes | No | No |
| Extra dependencies | None (Gson already added) | JDBC driver | JDBC + DB server |
