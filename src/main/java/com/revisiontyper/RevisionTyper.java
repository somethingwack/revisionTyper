package com.revisiontyper;

import javax.sound.sampled.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles all of the background typing logic so the Swing layer can remain lean.
 */
public final class RevisionTyper {

    private static final Map<Character, SpecialKey> SPECIAL_KEYS = buildSpecialKeys();
    private static final String RESOURCE_PREFIX = "/resource/";

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
        URL soundUrl = RevisionTyper.class.getResource(RESOURCE_PREFIX + resourceName);
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

    private record SpecialKey(int keyCode, boolean requiresShift) {
    }

    private static final class TypistThread extends Thread {

        private final String source;
        private volatile boolean stopRequested;

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
                Thread.sleep(TypingSettings.getStartDelayMs());
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

                typeCharacter(robot, current);
                speedRegulator.markCharTyped();

                wordCharCount = Character.isWhitespace(current) ? 0 : wordCharCount + 1;

                if (TypingSettings.isTyposEnabled() && index > 10) {
                    int retypeCount = maybeInjectTypo(robot);
                    if (retypeCount > 0) {
                        index = Math.max(0, index - retypeCount + 1);
                        continue;
                    }
                }

                index++;
            }
        }

        private int maybeInjectTypo(Robot robot) throws InterruptedException {
            if (ThreadLocalRandom.current().nextDouble() >= TypingSettings.getTypoChance()) {
                return 0;
            }

            char bogusChar = (char) ('a' + ThreadLocalRandom.current().nextInt(26));
            typeCharacter(robot, bogusChar);
            delayBetween(250, 100);

            int corrections = 1 + ThreadLocalRandom.current().nextInt(4);
            for (int i = 0; i < corrections; i++) {
                tapKey(robot, KeyEvent.VK_BACK_SPACE, false);
                delayBetween(150, 50);
            }

            return Math.max(0, corrections - 1);
        }
    }
}
