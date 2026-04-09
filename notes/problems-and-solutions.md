# Development Challenges & Debugging Moments

A log of notable bugs hit during development, their root causes, and how they were resolved.

---

## 001 — Shared Scanner / "No line found" error

**When:** Adding user ID input in `SiliconValleyTrail.java` while `Menu.java` also reads input.

**Problem:** After `SiliconValleyTrail` read the user ID, `Menu` threw `NoSuchElementException: No line found` when trying to read the menu choice.

**Root Cause:** Both classes created their own `Scanner` wrapping `System.in`. The first Scanner internally buffers the entire input stream — so when `Menu` created a second Scanner, the buffered data was already consumed and unavailable.

**Solution:** Created a single `Scanner` instance in `SiliconValleyTrail.java` and passed it into `Menu` (and downstream into `GameEngine`, `EventEngine`) via constructors so all reads share the same stream.

---

## 002 — Double "Press Enter to Continue" after milestone

**When:** Reaching a milestone mid-game triggered two consecutive "Press Enter" prompts back to back.

**Problem:** The player had to press Enter twice to proceed — once for the milestone screen and once for the day flow — with no content between them.

**Root Cause:** `runDay()` called `ConsoleUtils.waitForEnter()` after applying the choice, then `milestoneTracker.check()` was called — which internally also called `waitForEnter()` after printing the milestone. The ordering caused two waits to stack.

**Solution:** Moved the `waitForEnter()` in `runDay()` to just before `milestoneTracker.check()`, so the choice outcome is shown, the player presses Enter once, and then the milestone screen appears with its own wait.

---

## 003 — Game continued after fund ran out at a milestone boundary

**When:** A player ran out of funds on the exact day a milestone was reached (Palo Alto gives +$10,000 bonus).

**Problem:** The lose condition wasn't triggered even though the player's fund hit zero before the milestone bonus was applied — the bonus refloated the fund and the game carried on.

**Root Cause:** `checkLoseConditions()` was only called after `milestoneTracker.check()`, which applied the Palo Alto +$10,000 bonus first. This masked what should have been a game-ending fund depletion.

**Solution:** Added a `checkLoseConditions()` call immediately after `applyChoice()` — before any milestone or event processing. Lose conditions are now checked at three points: after the player's choice, after events, and after daily overhead.

---

## 004 — Rest Day cooldown showed wrong message after resting

**When:** A player took a Rest Day, which restores energy and morale. On the next day, the cooldown message was never shown — instead the game showed "Team is still going — rest when energy or morale drops below 20".

**Root Cause:** The condition checks in `getLockReason()` were ordered: energy/morale check first, cooldown second. After resting, both energy and morale were restored above 20, so the energy/morale check fired first and returned the "team is fine" message — the cooldown check was never reached.

**Solution:** Swapped the order — cooldown is now checked first. If the player just rested, the cooldown message always shows regardless of current stats.

---

## 005 — Substring rename corrupted a related class name

**When:** Renaming `ApiEffect` → `GameImpact` across all files using a bulk `replace_all`.

**Problem:** `ApiEffectHandler` was also renamed to `GameImpactHandler` unintentionally — the substring `ApiEffect` inside `ApiEffectHandler` was matched and replaced, changing the handler class name mid-rename.

**Root Cause:** `replace_all` on `ApiEffect` matched the substring inside `ApiEffectHandler`, which was not the intended target of the rename.

**Solution:** Caught during compilation. Since `GameImpactHandler` was actually a cleaner name, we kept it and renamed the file to match. Going forward, renames like this are done more carefully by targeting the full class name rather than a substring.

---

## 006 — Lombok `@AllArgsConstructor` not regenerated after rename

**When:** After renaming `ApiEffect.java` to `GameImpact.java` and updating the class name and `@AllArgsConstructor`, compilation failed with "cannot find symbol" errors on the constructor.

**Root Cause:** Maven's incremental compilation didn't detect that the renamed file required Lombok's annotation processor to re-run. The old generated constructor was stale.

**Solution:** Ran `mvn clean compile` to force a full rebuild, which triggered Lombok's annotation processing from scratch and regenerated the constructor correctly.

---

## 007 — Terminal emoji alignment broke choice display

**When:** Displaying the daily choices with bracket-aligned stats on the right side.

**Problem:** Choices with emojis in their labels appeared misaligned — the brackets didn't line up despite the padding logic using `String.length()`.

**Root Cause:** Java's `String.length()` counts UTF-16 code units, not terminal display columns. Two issues:
- Surrogate-pair emojis (e.g. 🏃, U+1F3C3) count as 2 chars in Java but display as 2 columns — coincidentally correct.
- Variation selectors (e.g. U+FE0F after 🗺️) count as 1 char in Java but display as 0 columns — causing off-by-one padding errors.

**Solution:** Implemented a `visualLength()` utility that iterates over Unicode code points, skips variation selectors (U+FE0F, U+FE0E), and counts surrogate-pair emojis as 2 columns. This gives terminal-accurate widths for padding calculations.
