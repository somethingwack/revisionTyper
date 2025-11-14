package com.revisiontyper;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Models human-style typing delays so the typer can feel less robotic.
 */
final class HumanDelayModel {

    private static final long MIN_DELAY_MS = 20;
    private static final long MAX_DELAY_MS = 2000;
    private static final double MIN_REALISM_SCALE = 0.12;
    private static final double MAX_REALISM_SCALE = 1.4;
    private static final double REALISM_SCALE_DIVISOR = 340.0;

    private final Random random;

    HumanDelayModel(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    static HumanDelayModel createDefault() {
        return new HumanDelayModel(ThreadLocalRandom.current());
    }

    long computeInterKeyDelay(char previousChar, char currentChar, int wordCharCount) {
        long delay = baseDelay();
        if (!TypingSettings.isRealisticDelaysEnabled()) {
            return delay;
        }

        delay += uppercasePause(previousChar, currentChar);
        delay += punctuationPause(currentChar);
        delay += wordRhythmPause(wordCharCount, currentChar);
        delay += paragraphPause(previousChar, currentChar);

        return clamp(delay);
    }

    private long baseDelay() {
        int sliderValue = TypingSettings.getTypingDelay();
        int jitter = Math.max(10, (int) Math.round(sliderValue * 0.35));
        long offset = randomBetween(-jitter, jitter);
        return clamp(sliderValue + offset);
    }

    private long uppercasePause(char previousChar, char currentChar) {
        if (Character.isUpperCase(currentChar) && Character.isLetter(currentChar)
                && !Character.isUpperCase(previousChar)) {
            return scaledRandomBetween(22, 60);
        }
        return 0;
    }

    private long punctuationPause(char currentChar) {
        return switch (currentChar) {
            case '.', '!', '?' -> scaledRandomBetween(260, 720);
            case ',', ';', ':' -> scaledRandomBetween(110, 260);
            case '-' -> scaledRandomBetween(60, 150);
            case '\n' -> scaledRandomBetween(360, 780);
            default -> 0;
        };
    }

    private long wordRhythmPause(int wordCharCount, char currentChar) {
        if (currentChar == '\n') {
            return 0;
        }
        if (Character.isWhitespace(currentChar)) {
            return scaledRandomBetween(18, 60);
        }
        if (wordCharCount >= 7) {
            return scaledRandomBetween(35, 110);
        }
        return 0;
    }

    private long paragraphPause(char previousChar, char currentChar) {
        if (previousChar == '\n' && currentChar == '\n') {
            return scaledRandomBetween(420, 960);
        }
        return 0;
    }

    private long clamp(long delay) {
        if (delay < MIN_DELAY_MS) {
            return MIN_DELAY_MS;
        }
        return Math.min(MAX_DELAY_MS, delay);
    }

    private long randomBetween(long minInclusive, long maxInclusive) {
        if (maxInclusive <= minInclusive) {
            return minInclusive;
        }
        double span = maxInclusive - minInclusive;
        double sample = random.nextDouble();
        long value = Math.round(sample * span);
        return minInclusive + value;
    }

    private long scaledRandomBetween(long minInclusive, long maxInclusive) {
        double scale = realismScale();
        long scaledMin = Math.round(minInclusive * scale);
        long scaledMax = Math.round(maxInclusive * scale);
        if (scaledMax <= scaledMin) {
            return scaledMin;
        }
        return randomBetween(scaledMin, scaledMax);
    }

    private double realismScale() {
        double sliderValue = Math.max(1, TypingSettings.getTypingDelay());
        double normalized = sliderValue / REALISM_SCALE_DIVISOR;
        if (normalized < MIN_REALISM_SCALE) {
            return MIN_REALISM_SCALE;
        }
        if (normalized > MAX_REALISM_SCALE) {
            return MAX_REALISM_SCALE;
        }
        return normalized;
    }
}
