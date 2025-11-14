package com.revisiontyper;

import javax.sound.sampled.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles all of the background typing logic so the Swing layer can remain lean.
 */
public final class RevisionTyper {

    public static final int START_DELAY_MS = 4000;
    private static final Map<Character, SpecialKey> SPECIAL_KEYS = buildSpecialKeys();
    private static final String SOUND_RESOURCE_PREFIX = "/audio/";
    private static final String[] KEYBOARD_ROWS = {
            "`1234567890-=",
            "qwertyuiop[]\\",
            "asdfghjkl;'",
            "zxcvbnm,./"
    };
    private static final Map<Character, char[]> KEYBOARD_NEIGHBORS = buildKeyboardNeighbors();

    private static TypistThread activeThread;

    private RevisionTyper() {
    }

    public static synchronized boolean isRunning() {
        return activeThread != null && activeThread.isAlive();
    }

    public static synchronized void type(String sourcedText) {
        if (sourcedText == null || sourcedText.isBlank()) {
            safeBeep();
            return;
        }
        if (isRunning()) {
            return;
        }

        TypistThread thread = new TypistThread(sourcedText);
        activeThread = thread;
        playSound("blip1.wav");
        thread.start();
    }

    public static synchronized void stopType() {
        if (activeThread != null) {
            playSound("button3.wav");
            activeThread.requestStop();
        }
    }

    private static synchronized void clearActiveThread(TypistThread thread) {
        if (activeThread == thread) {
            activeThread = null;
        }
    }

    private static void safeBeep() {
        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception ignored) {
            // Headless mode or no audio device available.
        }
    }

    private static void delayBetween(int max, int min) throws InterruptedException {
        if (max < 0) {
            max = 0;
        }
        if (min < 0) {
            min = 0;
        }

        if (max <= min) {
            Thread.sleep(min);
            return;
        }

        int span = max - min;
        int sleep = min + ThreadLocalRandom.current().nextInt(span);
        Thread.sleep(sleep);
    }

    private static void tapKey(Robot robot, int keyCode, boolean holdShift) throws InterruptedException {
        if (holdShift) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }

        robot.keyPress(keyCode);
        int minHold = Math.max(TypingTimings.MIN_KEY_HOLD_MS,
                TypingTimings.KEY_HOLD_BASE_MS - TypingTimings.KEY_HOLD_VARIANCE_MS);
        int maxHold = TypingTimings.KEY_HOLD_BASE_MS + TypingTimings.KEY_HOLD_VARIANCE_MS;
        delayBetween(maxHold, minHold);
        robot.keyRelease(keyCode);

        if (holdShift) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    private static void typeCharacter(Robot robot, char character) throws InterruptedException {
        SpecialKey specialKey = SPECIAL_KEYS.get(character);
        if (specialKey != null) {
            tapKey(robot, specialKey.keyCode(), specialKey.requiresShift());
            return;
        }

        try {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(character);
            if (keyCode == KeyEvent.VK_UNDEFINED) {
                System.out.println("Unable to type char: " + character);
                return;
            }
            boolean requireShift = Character.isUpperCase(character);
            tapKey(robot, keyCode, requireShift);
        } catch (IllegalArgumentException e) {
            System.out.println("Unsupported character encountered: " + character);
        }
    }

    private static void playSound(String resourceName) {
        URL soundUrl = RevisionTyper.class.getResource(SOUND_RESOURCE_PREFIX + resourceName);
        if (soundUrl == null) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try (AudioInputStream stream = AudioSystem.getAudioInputStream(soundUrl)) {
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                System.err.println("Unable to play sound " + resourceName + ": " + e.getMessage());
            }
        });
    }

    private static Map<Character, SpecialKey> buildSpecialKeys() {
        Map<Character, SpecialKey> map = new HashMap<>();
        map.put('\'', new SpecialKey(KeyEvent.VK_QUOTE, false));
        map.put('"', new SpecialKey(KeyEvent.VK_QUOTE, true));
        map.put('’', new SpecialKey(KeyEvent.VK_QUOTE, false));
        map.put(';', new SpecialKey(KeyEvent.VK_SEMICOLON, false));
        map.put(':', new SpecialKey(KeyEvent.VK_SEMICOLON, true));
        map.put('!', new SpecialKey(KeyEvent.VK_1, true));
        map.put('@', new SpecialKey(KeyEvent.VK_2, true));
        map.put('#', new SpecialKey(KeyEvent.VK_3, true));
        map.put('$', new SpecialKey( KeyEvent.VK_4, true));
        map.put('%', new SpecialKey(KeyEvent.VK_5, true));
        map.put('^', new SpecialKey(KeyEvent.VK_6, true));
        map.put('&', new SpecialKey(KeyEvent.VK_7, true));
        map.put('*', new SpecialKey(KeyEvent.VK_8, true));
        map.put('(', new SpecialKey(KeyEvent.VK_9, true));
        map.put(')', new SpecialKey(KeyEvent.VK_0, true));
        map.put('+', new SpecialKey(KeyEvent.VK_EQUALS, true));
        map.put('=', new SpecialKey(KeyEvent.VK_EQUALS, false));
        map.put('_', new SpecialKey(KeyEvent.VK_MINUS, true));
        map.put('-', new SpecialKey(KeyEvent.VK_MINUS, false));
        map.put('?', new SpecialKey(KeyEvent.VK_SLASH, true));
        map.put('/', new SpecialKey(KeyEvent.VK_SLASH, false));
        map.put('<', new SpecialKey(KeyEvent.VK_COMMA, true));
        map.put('>', new SpecialKey(KeyEvent.VK_PERIOD, true));
        map.put('~', new SpecialKey(KeyEvent.VK_BACK_QUOTE, true));
        map.put('`', new SpecialKey(KeyEvent.VK_BACK_QUOTE, false));
        map.put('{', new SpecialKey(KeyEvent.VK_OPEN_BRACKET, true));
        map.put('[', new SpecialKey(KeyEvent.VK_OPEN_BRACKET, false));
        map.put('}', new SpecialKey(KeyEvent.VK_CLOSE_BRACKET, true));
        map.put(']', new SpecialKey(KeyEvent.VK_CLOSE_BRACKET, false));
        map.put('|', new SpecialKey(KeyEvent.VK_BACK_SLASH, true));
        map.put('\\', new SpecialKey(KeyEvent.VK_BACK_SLASH, false));
        map.put('\n', new SpecialKey(KeyEvent.VK_ENTER, false));
        map.put('\r', new SpecialKey(KeyEvent.VK_ENTER, false));
        map.put('\t', new SpecialKey(KeyEvent.VK_TAB, false));
        return map;
    }

    private static Map<Character, char[]> buildKeyboardNeighbors() {
        Map<Character, Set<Character>> working = new HashMap<>();
        for (int row = 0; row < KEYBOARD_ROWS.length; row++) {
            String rowChars = KEYBOARD_ROWS[row];
            for (int col = 0; col < rowChars.length(); col++) {
                char key = Character.toLowerCase(rowChars.charAt(col));
                Set<Character> neighbors = working.computeIfAbsent(key, __ -> new LinkedHashSet<>());
                for (int dr = -1; dr <= 1; dr++) {
                    int neighborRow = row + dr;
                    if (neighborRow < 0 || neighborRow >= KEYBOARD_ROWS.length) {
                        continue;
                    }
                    String neighborRowChars = KEYBOARD_ROWS[neighborRow];
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) {
                            continue;
                        }
                        int neighborCol = col + dc;
                        if (neighborCol < 0 || neighborCol >= neighborRowChars.length()) {
                            continue;
                        }
                        char neighbor = Character.toLowerCase(neighborRowChars.charAt(neighborCol));
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        Map<Character, char[]> result = new HashMap<>();
        for (Map.Entry<Character, Set<Character>> entry : working.entrySet()) {
            char[] array = new char[entry.getValue().size()];
            int index = 0;
            for (char neighbor : entry.getValue()) {
                array[index++] = neighbor;
            }
            result.put(entry.getKey(), array);
        }
        return result;
    }

    private record SpecialKey(int keyCode, boolean requiresShift) {
    }

    private static final class TypistThread extends Thread {

        private final String source;
        private final List<DeferredCorrection> deferredCorrections = new ArrayList<>();
        private volatile boolean stopRequested;
        private int documentLength;

        private TypistThread(String source) {
            super("RevisionTyperThread");
            this.source = source;
        }

        private void requestStop() {
            stopRequested = true;
            interrupt();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(START_DELAY_MS);
                Robot robot = new Robot();
                typeSource(robot);
            } catch (InterruptedException ignored) {
                // Graceful stop.
            } catch (AWTException e) {
                safeBeep();
                e.printStackTrace();
            } finally {
                clearActiveThread(this);
                playSound("button3.wav");
            }
        }

        private void typeSource(Robot robot) throws InterruptedException {
            int wordCharCount = 0;
            documentLength = 0;
            char[] chars = source.toCharArray();
            int index = 0;
            HumanDelayModel delayModel = HumanDelayModel.createDefault();
            SpeedRegulator speedRegulator = new SpeedRegulator();

            while (index < chars.length && !stopRequested) {
                char current = chars[index];
                char previous = index == 0 ? '\0' : chars[index - 1];

                long delay = delayModel.computeInterKeyDelay(previous, current, wordCharCount);
                long adjustedDelay = speedRegulator.adjustDelay(delay);
                if (adjustedDelay > 0) {
                    Thread.sleep(adjustedDelay);
                }

                typeCharacterAndTrack(robot, current);
                speedRegulator.markCharTyped();

                wordCharCount = Character.isWhitespace(current) ? 0 : wordCharCount + 1;

                if (TypingSettings.isTyposEnabled() && index > 10) {
                    maybeInjectTypo(robot, previous, current);
                }

                index++;
            }
            applyDeferredCorrections(robot);
        }

        private void maybeInjectTypo(Robot robot, char previousChar, char currentChar) throws InterruptedException {
            if (ThreadLocalRandom.current().nextDouble() >= TypingSettings.getTypoChance()) {
                return;
            }

            TypoPattern pattern = pickTypoPattern(previousChar);
            switch (pattern) {
                case DOUBLE_PRESS -> simulateDoubleLetter(robot, currentChar);
                case SPACE_SLIP -> simulateSpaceSlip(robot, currentChar);
                case NEARBY_KEY -> simulateNearbyKey(robot, currentChar);
                case TRANSPOSE_PREVIOUS -> simulateTranspose(robot, previousChar, currentChar);
                case MISSING_LETTER -> simulateMissingLetter(robot, currentChar);
            }
        }

        private TypoPattern pickTypoPattern(char previousChar) {
            List<TypoPattern> options = new ArrayList<>();
            options.add(TypoPattern.DOUBLE_PRESS);
            options.add(TypoPattern.SPACE_SLIP);
            options.add(TypoPattern.NEARBY_KEY);
            if (previousChar != '\0' && !Character.isWhitespace(previousChar) && !Character.isISOControl(previousChar)) {
                options.add(TypoPattern.TRANSPOSE_PREVIOUS);
            }
            options.add(TypoPattern.MISSING_LETTER);
            return options.get(ThreadLocalRandom.current().nextInt(options.size()));
        }

        private void simulateDoubleLetter(Robot robot, char currentChar) throws InterruptedException {
            if (currentChar == '\0' || currentChar == '\n') {
                return;
            }
            int extras = 1 + ThreadLocalRandom.current().nextInt(2);
            for (int i = 0; i < extras; i++) {
                delayBetween(90, 40);
                typeCharacterAndTrack(robot, currentChar);
            }
            deferredCorrections.add(DeferredCorrection.doublePress(documentLength, extras));
        }

        private void simulateSpaceSlip(Robot robot, char currentChar) throws InterruptedException {
            if (Character.isWhitespace(currentChar)) {
                return;
            }
            delayBetween(120, 50);
            typeCharacterAndTrack(robot, ' ');
            deferredCorrections.add(DeferredCorrection.spaceSlip(documentLength, 1));
        }

        private void simulateNearbyKey(Robot robot, char currentChar) throws InterruptedException {
            if (documentLength == 0) {
                return;
            }
            backspaceAndTrack(robot);
            delayBetween(120, 60);
            char replacement = pickNearbyCharacter(currentChar);
            typeCharacterAndTrack(robot, replacement);
            deferredCorrections.add(DeferredCorrection.nearby(documentLength, currentChar));
        }

        private void simulateTranspose(Robot robot, char previousChar, char currentChar) throws InterruptedException {
            if (previousChar == '\0' || Character.isWhitespace(previousChar) || documentLength < 1) {
                simulateNearbyKey(robot, currentChar);
                return;
            }
            backspaceAndTrack(robot);
            if (documentLength == 0) {
                typeCharacterAndTrack(robot, currentChar);
                return;
            }
            backspaceAndTrack(robot);
            delayBetween(140, 60);
            typeCharacterAndTrack(robot, currentChar);
            typeCharacterAndTrack(robot, previousChar);
            deferredCorrections.add(DeferredCorrection.transpose(documentLength, previousChar, currentChar));
        }

        private void simulateMissingLetter(Robot robot, char currentChar) throws InterruptedException {
            backspaceAndTrack(robot);
            deferredCorrections.add(DeferredCorrection.missing(documentLength, currentChar));
        }

        private char pickNearbyCharacter(char reference) {
            char normalized = Character.toLowerCase(reference);
            char[] neighbors = KEYBOARD_NEIGHBORS.get(normalized);
            if (neighbors == null || neighbors.length == 0) {
                return randomLetterMatchingCase(reference);
            }
            char choice = neighbors[ThreadLocalRandom.current().nextInt(neighbors.length)];
            if (Character.isLetter(reference) && Character.isUpperCase(reference)) {
                return Character.toUpperCase(choice);
            }
            return choice;
        }

        private char randomLetterMatchingCase(char reference) {
            char letter = (char) ('a' + ThreadLocalRandom.current().nextInt(26));
            if (Character.isLetter(reference) && Character.isUpperCase(reference)) {
                return Character.toUpperCase(letter);
            }
            return letter;
        }

        private void typeCharacterAndTrack(Robot robot, char character) throws InterruptedException {
            RevisionTyper.typeCharacter(robot, character);
            documentLength++;
        }

        private void backspaceAndTrack(Robot robot) throws InterruptedException {
            tapKey(robot, KeyEvent.VK_BACK_SPACE, false);
            if (documentLength > 0) {
                documentLength--;
            }
        }

        private void applyDeferredCorrections(Robot robot) throws InterruptedException {
            if (stopRequested || deferredCorrections.isEmpty()) {
                return;
            }
            deferredCorrections.sort(Comparator.comparingInt(DeferredCorrection::typedIndex).reversed());
            moveCaretToEnd(robot);
            int currentOffsetFromEnd = 0;
            for (DeferredCorrection correction : deferredCorrections) {
                int targetOffset = Math.max(0, documentLength - correction.typedIndex);
                int moveLeft = targetOffset - currentOffsetFromEnd;
                if (moveLeft > 0) {
                    moveCaretLeftBy(robot, moveLeft);
                    currentOffsetFromEnd = targetOffset;
                }
                switch (correction.pattern) {
                    case DOUBLE_PRESS, SPACE_SLIP -> removeExtras(robot, correction.extraCount);
                    case NEARBY_KEY -> fixNearbyKey(robot, correction.primaryChar);
                    case TRANSPOSE_PREVIOUS -> fixTranspose(robot, correction.primaryChar, correction.secondaryChar);
                    case MISSING_LETTER -> insertMissingLetter(robot, correction.primaryChar);
                }
                delayBetween(80, 35);
            }
            moveCaretToEnd(robot);
            deferredCorrections.clear();
        }

        private void removeExtras(Robot robot, int count) throws InterruptedException {
            for (int i = 0; i < count; i++) {
                backspaceAndTrack(robot);
                delayBetween(110, 45);
            }
        }

        private void fixNearbyKey(Robot robot, char intended) throws InterruptedException {
            backspaceAndTrack(robot);
            delayBetween(150, 60);
            typeCharacterAndTrack(robot, intended);
        }

        private void fixTranspose(Robot robot, char first, char second) throws InterruptedException {
            backspaceAndTrack(robot);
            backspaceAndTrack(robot);
            delayBetween(140, 60);
            typeCharacterAndTrack(robot, first);
            typeCharacterAndTrack(robot, second);
        }

        private void insertMissingLetter(Robot robot, char ch) throws InterruptedException {
            typeCharacterAndTrack(robot, ch);
        }

        private void moveCaretToEnd(Robot robot) throws InterruptedException {
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_END);
            Thread.sleep(20);
            robot.keyRelease(KeyEvent.VK_END);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(20);
        }

        private void moveCaretLeftBy(Robot robot, int count) throws InterruptedException {
            if (count <= 0) {
                return;
            }
            while (count > 0) {
                int chunk = Math.min(6, count);
                for (int i = 0; i < chunk; i++) {
                    tapKey(robot, KeyEvent.VK_LEFT, false);
                }
                count -= chunk;
                if (count > 0) {
                    Thread.sleep(15);
                }
            }
        }
    }

    private enum TypoPattern {
        DOUBLE_PRESS,
        SPACE_SLIP,
        NEARBY_KEY,
        TRANSPOSE_PREVIOUS,
        MISSING_LETTER
    }

    private static final class DeferredCorrection {
        private final TypoPattern pattern;
        private final int typedIndex;
        private final int extraCount;
        private final char primaryChar;
        private final char secondaryChar;

        private DeferredCorrection(TypoPattern pattern, int typedIndex,
                                   int extraCount, char primaryChar, char secondaryChar) {
            this.pattern = pattern;
            this.typedIndex = typedIndex;
            this.extraCount = extraCount;
            this.primaryChar = primaryChar;
            this.secondaryChar = secondaryChar;
        }

        static DeferredCorrection doublePress(int typedIndex, int extras) {
            return new DeferredCorrection(TypoPattern.DOUBLE_PRESS, typedIndex, extras, '\0', '\0');
        }

        static DeferredCorrection spaceSlip(int typedIndex, int spaces) {
            return new DeferredCorrection(TypoPattern.SPACE_SLIP, typedIndex, spaces, '\0', '\0');
        }

        static DeferredCorrection nearby(int typedIndex, char intended) {
            return new DeferredCorrection(TypoPattern.NEARBY_KEY, typedIndex, 0, intended, '\0');
        }

        static DeferredCorrection transpose(int typedIndex, char first, char second) {
            return new DeferredCorrection(TypoPattern.TRANSPOSE_PREVIOUS, typedIndex, 0, first, second);
        }

        static DeferredCorrection missing(int typedIndex, char ch) {
            return new DeferredCorrection(TypoPattern.MISSING_LETTER, typedIndex, 0, ch, '\0');
        }

        int typedIndex() {
            return typedIndex;
        }
    }
}
