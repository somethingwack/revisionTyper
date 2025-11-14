package com.revisiontyper;

/**
 * Shared timing constants so typing speed calculations and the Robot interaction stay in sync.
 */
final class TypingTimings {

    static final int KEY_HOLD_BASE_MS = 30;
    static final int KEY_HOLD_VARIANCE_MS = 12;
    static final int MIN_KEY_HOLD_MS = 12;

    private TypingTimings() {
    }

    static int averageKeyHoldMs() {
        return KEY_HOLD_BASE_MS;
    }
}
