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
