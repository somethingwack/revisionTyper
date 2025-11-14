package com.revisiontyper;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TypingSpeedCalibrationTest {

    private static final String SAMPLE_TEXT = (
            "Fast typing should still feel natural, but it also needs to honor the configured speed. " +
            "This paragraph mixes short and long words, punctuation, and line breaks.\n" +
            "Switching between clauses, commas, and uppercase letters should not drop the average rate."
    );

    @Test
    void fastSettingRemainsCloseToTargetWpm() {
        double wpm = measureWpmAtSetting(120);
        assertTrue(wpm > 114 && wpm < 126,
                () -> "Expected ≈120 WPM for fast slider, observed " + wpm);
    }

    @Test
    void slowSettingRemainsCloseToTargetWpm() {
        double wpm = measureWpmAtSetting(50);
        assertTrue(wpm > 43 && wpm < 57,
                () -> "Expected ≈50 WPM for slow slider, observed " + wpm);
    }

    private double measureWpmAtSetting(int targetWpm) {
        int originalDelay = TypingSettings.getTypingDelay();
        boolean originalRealism = TypingSettings.isRealisticDelaysEnabled();

        TypingSettings.setTypingWpm(targetWpm);
        TypingSettings.setRealisticDelays(true);

        HumanDelayModel model = new HumanDelayModel(new Random(1234));
        double wpm = measureWpm(model, SAMPLE_TEXT);

        TypingSettings.setTypingDelay(originalDelay);
        TypingSettings.setRealisticDelays(originalRealism);
        return wpm;
    }

    private double measureWpm(HumanDelayModel model, String text) {
        long totalDelay = 0;
        int holdPerKey = TypingTimings.averageKeyHoldMs();
        int wordCharCount = 0;
        char previous = '\0';

        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            totalDelay += model.computeInterKeyDelay(previous, current, wordCharCount) + holdPerKey;
            wordCharCount = Character.isWhitespace(current) ? 0 : wordCharCount + 1;
            previous = current;
        }

        double avgDelay = (double) totalDelay / text.length();
        double charsPerMinute = 60000.0 / avgDelay;
        return charsPerMinute / 5.0;
    }
}
