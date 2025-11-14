package com.revisiontyper;

/**
 * Central place for runtime typing preferences so the UI and background engine stay in sync.
 */
public final class TypingSettings {

    private static final int MIN_DELAY_MS = 30;
    private static final int MAX_DELAY_MS = 400;
    private static final double AVG_CHARS_PER_WORD = 5.0;
    private static final double MIN_TYPO_CHANCE = 0.0;
    private static final double MAX_TYPO_CHANCE = 0.25;
    private static final int MAX_TYPOS_PER_HUNDRED_WORDS = 30;
    private static final int DEFAULT_WPM = 120;
    private static final int MAX_WPM_CAP = 120;
    private static final int MIN_WPM = wpmFromDelay(MAX_DELAY_MS);
    private static final int MAX_WPM = Math.min(MAX_WPM_CAP, wpmFromDelay(MIN_DELAY_MS));
    private static volatile int typingDelayMs = delayFromWpm(DEFAULT_WPM);
    private static volatile double typoChance = 0.05;
    private static volatile boolean typosEnabled;
    private static volatile boolean realisticDelays = true;
    private static volatile boolean alwaysOnTop;

    private TypingSettings() {
    }

    public static int getTypingDelay() {
        return typingDelayMs;
    }

    public static void setTypingDelay(int delayMs) {
        int sanitized = Math.max(MIN_DELAY_MS, Math.min(MAX_DELAY_MS, delayMs));
        typingDelayMs = sanitized;
    }

    public static double getTypoChance() {
        return typoChance;
    }

    public static void setTypoChance(double chance) {
        double sanitized = Math.max(MIN_TYPO_CHANCE, Math.min(MAX_TYPO_CHANCE, chance));
        typoChance = sanitized;
        typosEnabled = sanitized > 0.0001;
    }

    public static int getTypoRatePerHundredWords() {
        return (int) Math.round(typoChance * AVG_CHARS_PER_WORD * 100.0);
    }

    public static void setTypoRatePerHundredWords(int typosPerHundredWords) {
        int sanitized = Math.max(0, Math.min(MAX_TYPOS_PER_HUNDRED_WORDS, typosPerHundredWords));
        double chance = sanitized / (AVG_CHARS_PER_WORD * 100.0);
        setTypoChance(chance);
    }

    public static boolean isTyposEnabled() {
        return typosEnabled;
    }

    public static void setTyposEnabled(boolean enabled) {
        typosEnabled = enabled;
    }

    public static boolean isRealisticDelaysEnabled() {
        return realisticDelays;
    }

    public static void setRealisticDelays(boolean enabled) {
        realisticDelays = enabled;
    }

    public static boolean isAlwaysOnTopEnabled() {
        return alwaysOnTop;
    }

    public static void setAlwaysOnTopEnabled(boolean enabled) {
        alwaysOnTop = enabled;
    }

    public static int getMinimumWpm() {
        return MIN_WPM;
    }

    public static int getMaximumWpm() {
        return MAX_WPM;
    }

    public static int getTypingWpm() {
        return wpmFromDelay(typingDelayMs);
    }

    public static void setTypingWpm(int targetWpm) {
        int sanitized = Math.max(MIN_WPM, Math.min(MAX_WPM, targetWpm));
        setTypingDelay(delayFromWpm(sanitized));
    }

    public static int estimateWpm() {
        return getTypingWpm();
    }

    public static double targetMillisPerChar() {
        int wpm = getTypingWpm();
        if (wpm <= 0) {
            return 60000.0 / (1 * AVG_CHARS_PER_WORD);
        }
        return 60000.0 / (wpm * AVG_CHARS_PER_WORD);
    }

    private static int delayFromWpm(int targetWpm) {
        if (targetWpm <= 0) {
            return MIN_DELAY_MS;
        }
        double charsPerMinute = targetWpm * AVG_CHARS_PER_WORD;
        double millisPerChar = 60000.0 / charsPerMinute;
        double adjusted = millisPerChar - TypingTimings.averageKeyHoldMs();
        int delay = (int) Math.round(adjusted);
        return Math.max(MIN_DELAY_MS, Math.min(MAX_DELAY_MS, delay));
    }

    private static int wpmFromDelay(int delayMs) {
        double millisPerChar = effectiveMillisPerChar(delayMs);
        double charsPerMinute = 60000.0 / millisPerChar;
        double wordsPerMinute = charsPerMinute / AVG_CHARS_PER_WORD;
        return (int) Math.round(wordsPerMinute);
    }

    private static double effectiveMillisPerChar(int delayMs) {
        double clamped = Math.max(MIN_DELAY_MS, Math.min(MAX_DELAY_MS, delayMs));
        return clamped + TypingTimings.averageKeyHoldMs();
    }
}
