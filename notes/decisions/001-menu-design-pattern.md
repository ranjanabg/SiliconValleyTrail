# Decision 001: Menu Design Pattern

**Date:** 2026-04-03  
**Status:** Accepted

## Context

The initial menu was a flat switch-case in `main()`. As the game grows, new menu options will be added and existing ones will evolve. We needed a structure that makes the menu extensible without modifying core dispatch logic each time.

## Alternatives Explored

### 1. Command Pattern ✅ (Chosen)
Each menu option is a class implementing a `Command` interface with `getLabel()` and `execute()`. A `Menu` class holds a `LinkedHashMap<String, Command>` and dispatches based on user input.

**Pros:**
- Each option is independently encapsulated — easy to add, remove, or modify
- `Menu.java` never needs to change when new options are added
- Each command is independently testable
- Scales naturally to sub-menus and context-sensitive options
- Follows Open/Closed Principle

**Cons:**
- More files than a simple switch-case
- New contributors need to understand the pattern
- Slight overhead for very simple one-liner actions

---

### 2. Enum-based Menu
Menu options defined as an enum with label and behavior attached directly to each constant.

**Pros:**
- Self-contained — all options in one file
- Type-safe; compiler catches invalid references
- Simple to read for small, stable menus

**Cons:**
- Adding or removing an option requires modifying the enum itself
- Harder to support dynamic or runtime-registered options
- Logic inside enums becomes unwieldy as behavior grows
- Difficult to extend to sub-menus

---

### 3. Strategy Pattern
Each option is a strategy object with swappable behavior, sharing a common interface.

**Pros:**
- Clean separation of behavior
- Easy to swap implementations at runtime
- Familiar pattern to most Java developers

**Cons:**
- Semantically designed for swappable algorithms, not discrete menu actions — a conceptual mismatch
- No meaningful advantage over Command Pattern for this use case
- Can confuse intent: strategies imply interchangeable behavior, not independent actions

---

### 4. Chain of Responsibility
Each handler checks if it can handle the input and passes it along the chain if not.

**Pros:**
- Very flexible routing — handlers can be added/removed dynamically
- Good for complex conditional dispatch logic

**Cons:**
- Overly complex for a straightforward numbered menu
- Harder to trace which handler is responding to which input
- No benefit over Command Pattern unless routing logic is conditional and non-trivial

---

## Decision

**Command Pattern** was chosen because it best balances simplicity and extensibility for a growing CLI game menu. Each option maps naturally to a command object, and the menu dispatcher never needs modification when options are added or changed.

## Trade-off Comparison

| Criteria | Command Pattern | Enum-based | Strategy Pattern | Chain of Responsibility |
|---|---|---|---|---|
| Extensibility | High — new class per option | Low — must modify enum | High | High |
| Simplicity | Medium | High | Medium | Low |
| Testability | High — each command isolated | Medium | High | Low |
| Scalability to sub-menus | High | Low | Medium | Medium |
| Conceptual fit for menu dispatch | High | Medium | Low | Low |
| Number of files | More | Fewer | More | More |
| Runtime dynamic options | Yes | No | Yes | Yes |
