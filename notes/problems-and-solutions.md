# Problems Encountered & Solutions

A running log of issues faced during development and how they were resolved.

---

## 001 — Shared Scanner / "No line found" error

**When:** Adding user ID input in `SiliconValleyTrail.java` while `Menu.java` also reads input.

**Problem:** After `SiliconValleyTrail` read the user ID, `Menu` threw `NoSuchElementException: No line found` when trying to read the menu choice.

**Root Cause:** Both classes created their own `Scanner` wrapping `System.in`. The first Scanner internally buffers the entire input stream — so when `Menu` created a second Scanner, the buffered data was already consumed and unavailable.

**Solution:** Created a single `Scanner` instance in `SiliconValleyTrail.java` and passed it into `Menu` via the constructor so both use the same Scanner for all reads.
