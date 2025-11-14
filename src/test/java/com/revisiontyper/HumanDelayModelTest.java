package com.revisiontyper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HumanDelayModelTest {

    @BeforeEach
    void resetSettings() {
        TypingSettings.setTypingDelay(140);
        TypingSettings.setRealisticDelays(true);
    }

    @Test
    void baseDelayStaysWithinBoundaries() {
        HumanDelayModel model = newModel();
        long delay = model.computeInterKeyDelay(' ', 'a', 2);
        assertTrue(delay >= 20 && delay <= 2000, "Delay should stay inside safety clamp");
    }

    @Test
    void punctuationAddsExtraPauseWhenEnabled() {
        TypingSettings.setRealisticDelays(true);
        HumanDelayModel model = newModel();
        long punctuationDelay = model.computeInterKeyDelay('a', '.', 5);
        long letterDelay = model.computeInterKeyDelay('a', 'e', 5);
        assertTrue(punctuationDelay - letterDelay >= 80,
                "Sentence endings need noticeably longer pauses");
    }

    @Test
    void punctuationMatchesBaseDelayWhenRealismDisabled() {
        TypingSettings.setRealisticDelays(false);
        HumanDelayModel model = newModel();
        long punctuationDelay = model.computeInterKeyDelay('a', '.', 5);
        long letterDelay = model.computeInterKeyDelay('a', 'e', 5);
        assertEquals(letterDelay, punctuationDelay,
                "Without realism toggled, delays should be uniform");
    }

    @Test
    void uppercaseLettersTriggerBriefPause() {
        TypingSettings.setRealisticDelays(true);
        HumanDelayModel model = newModel();
        long uppercaseDelay = model.computeInterKeyDelay(' ', 'A', 0);
        long lowercaseDelay = model.computeInterKeyDelay(' ', 'a', 0);
        assertTrue(uppercaseDelay - lowercaseDelay >= 8,
                "Capital letters should require an additional delay for Shift");
    }

    private static HumanDelayModel newModel() {
        return new HumanDelayModel(new FixedRandom());
    }

    private static final class FixedRandom extends Random {
        @Override
        protected int next(int bits) {
            return 0;
        }
    }
}
