# Guidance for AI coding agents working on RevisionTyper

This file contains concise, actionable information to help an AI coding agent be productive in this repository.

1. Big picture
- **What:** A small desktop app that simulates human typing into other applications. UI is a Swing panel (`RevisionGUI`) and the background engine (`RevisionTyper`) uses `java.awt.Robot` to emit keystrokes.
- **Main entrypoint:** `com.revisiontyper.RevisionMain` (see `src/main/java/com/revisiontyper/RevisionMain.java`). The application plugin is configured in `build.gradle`.
- **Core components:**
  - `RevisionGUI` (`src/main/java/com/revisiontyper/RevisionGUI.java`): Swing UI, sliders and controls, keyboard shortcuts.
  - `RevisionTyper` (`src/main/java/com/revisiontyper/RevisionTyper.java`): Background typing logic, typo injection, deferred corrections, sound effects.
  - `HumanDelayModel`, `TypingSettings`, `TypingTimings`, `SpeedRegulator`: timing and configuration primitives used by the engine.

2. Build / test / run
- Use Gradle wrapper to build and run locally.
  - Run app: `./gradlew run` (uses `application` plugin, main class `com.revisiontyper.RevisionMain`).
  - Build JAR: `./gradlew jar`.
  - Install distribution: `./gradlew installDist`.
  - Run tests: `./gradlew test` (JUnit 5).
- Packaging: `packageMac` / `packageWindows` tasks use `jpackage` and require `JAVA_HOME` pointing to a JDK that includes `jpackage` (JDK 17+). See `build.gradle` for details and `src/main/resources/icons/*` for icon inputs.

3. Project-specific patterns and conventions
- **Centralized settings:** `TypingSettings` is a static, volatile settings holder shared between UI and engine. Modify via `TypingSettings.set*` methods so UI and engine remain in-sync.
- **Deterministic tests:** Tests rely on constructing deterministic RNGs (see `HumanDelayModelTest` which uses a `FixedRandom`) rather than changing production classes. Prefer injecting or wrapping randomness in tests.
- **Package-private helpers:** Several helpers (e.g., `HumanDelayModel`) are package-private (no `public`)—tests are in the same package (`com.revisiontyper`) to access them.
- **Resource layout:** Audio files under `src/main/resources/audio/`; icons under `src/main/resources/icons/`. `RevisionTyper` expects sounds like `blip1.wav` and `button3.wav`.
- **UI ↔ Engine contract:** `RevisionGUI` calls `RevisionTyper.type(text)` and `RevisionTyper.stopType()`. `RevisionTyper` manages a background `TypistThread` and exposes `isRunning()`.

4. Platform & runtime notes
- `RevisionTyper` uses `java.awt.Robot` and `Toolkit` – requires a non-headless environment. Audio playback can fail silently (handled via try/catch). Packaging uses `jpackage` (see `build.gradle`); ensure `JAVA_HOME` points at a JDK 17+ that contains `jpackage` when invoking packaging tasks.
- macOS icons: `RevisionMain` prefers `.icns` for the Dock; the `packageMac` task references `src/main/resources/icons/RevWriter.icns`.

5. Fast examples (copyable)
- Run tests: ``./gradlew test``
- Run app locally: ``./gradlew run``
- Create macOS DMG (macOS + jpackage ready JDK): ``./gradlew packageMac``

6. What to look for when editing code
- If you change timing or randomness, update corresponding tests in `src/test/java/com/revisiontyper/` and use `FixedRandom`-style helpers for deterministic assertions.
- Keep `TypingSettings` API backward-compatible: UI binds sliders/check boxes to these setters/getters.
- When editing `RevisionTyper`, preserve the `TypistThread` lifecycle and the `clearActiveThread`/`requestStop` semantics to avoid orphaned Robot threads.

7. Key files to reference
- `build.gradle` — Java 17, `application` plugin and `jpackage` tasks.
- `src/main/java/com/revisiontyper/RevisionGUI.java` — UI and keyboard shortcuts.
- `src/main/java/com/revisiontyper/RevisionTyper.java` — typing engine and Robot usage.
- `src/main/java/com/revisiontyper/HumanDelayModel.java` — inter-key delay logic.
- `src/test/java/com/revisiontyper/*` — unit tests demonstrating conventions.

If anything in these instructions is unclear or you want more detail on a specific component (e.g., the typo injection heuristics in `RevisionTyper`), tell me which area to expand and I will iterate.
