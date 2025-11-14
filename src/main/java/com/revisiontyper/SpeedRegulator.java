package com.revisiontyper;

/**
 * Dynamically nudges the inter-key delay so the actual observed typing rate matches the target WPM.
 */
final class SpeedRegulator {

    private static final double RESPONSE = 0.18;
    private static final double MIN_ADJUSTMENT = 0.2;
    private static final double MAX_ADJUSTMENT = 2.0;

    private long lastCharFinishedNs = -1L;
    private double adjustment = 1.0;
    private double lastRequestedDelayMs = 0.0;

    long adjustDelay(long requestedDelay) {
        lastRequestedDelayMs = requestedDelay;
        double scaled = requestedDelay * adjustment;
        if (scaled <= 1.0) {
            return 0L;
        }
        return Math.round(scaled);
    }

    void markCharTyped() {
        long now = System.nanoTime();
        if (lastCharFinishedNs > 0 && lastRequestedDelayMs > 0) {
            double elapsedMs = (now - lastCharFinishedNs) / 1_000_000.0;
            double targetMs = TypingSettings.targetMillisPerChar();
            double error = targetMs - elapsedMs;
            double delta = (error / Math.max(1.0, lastRequestedDelayMs)) * RESPONSE;
            adjustment = clamp(adjustment + delta);
        }
        lastCharFinishedNs = now;
    }

    private double clamp(double value) {
        if (value < MIN_ADJUSTMENT) {
            return MIN_ADJUSTMENT;
        }
        if (value > MAX_ADJUSTMENT) {
            return MAX_ADJUSTMENT;
        }
        return value;
    }
}
