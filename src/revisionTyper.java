import java.awt.*;
import java.awt.event.KeyEvent;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class revisionTyper {

    public static boolean ThreadStatus = false; // True = Thread alive, False = thread dead
    private static MyThread thread = null;

    // Used to get thread status
    public static boolean getStatus() {
        return ThreadStatus;
    }

    // Delay utility
    public static void delayBetween(int max, int min) throws InterruptedException{
            Thread.sleep((int) (Math.random() * (max - min)) + min);
    }

    // Utility for typing characters that require shift to be held
    public static void extraCharacters(Robot r, char c) {
            r.keyPress(KeyEvent.VK_SHIFT);
            switch (c) {
                case '’':
                    r.keyRelease(KeyEvent.VK_SHIFT);
                    r.keyPress(KeyEvent.VK_QUOTE);
                    r.keyRelease(KeyEvent.VK_QUOTE);
                    break;
                case '"':
                    r.keyPress(KeyEvent.VK_QUOTE);
                    r.keyRelease(KeyEvent.VK_QUOTE);
                    break;
                case ';':
                    r.keyRelease(KeyEvent.VK_SHIFT);
                    r.keyPress(KeyEvent.VK_SEMICOLON);
                    break;
                case ':':
                    r.keyPress(KeyEvent.VK_SEMICOLON);
                    r.keyRelease(KeyEvent.VK_SEMICOLON);
                    break;
                case '\'':
                    r.keyRelease(KeyEvent.VK_SHIFT);
                    r.keyPress(KeyEvent.VK_QUOTE);
                    r.keyRelease(KeyEvent.VK_QUOTE);
                    break;
                case '!':
                    r.keyPress(KeyEvent.VK_1);
                    r.keyRelease(KeyEvent.VK_1);
                    break;
                case '+':
                    r.keyPress(KeyEvent.VK_EQUALS);
                    r.keyRelease(KeyEvent.VK_EQUALS);
                    break;
                case '_':
                    r.keyPress(KeyEvent.VK_MINUS);
                    r.keyRelease(KeyEvent.VK_MINUS);
                    break;
                case '?':
                    r.keyPress(KeyEvent.VK_SLASH);
                    r.keyRelease(KeyEvent.VK_SLASH);
                    break;
                case ')':
                    r.keyPress(KeyEvent.VK_0);
                    r.keyRelease(KeyEvent.VK_0);
                    break;
                case '(':
                    r.keyPress(KeyEvent.VK_9);
                    r.keyRelease(KeyEvent.VK_9);
                    break;
                case '@':
                    r.keyPress(KeyEvent.VK_2);
                    r.keyRelease(KeyEvent.VK_2);
                    break;
                case '#':
                    r.keyPress(KeyEvent.VK_3);
                    r.keyRelease(KeyEvent.VK_3);
                    break;
                case '$':
                    r.keyPress(KeyEvent.VK_4);
                    r.keyRelease(KeyEvent.VK_4);
                    break;
                case '%':
                    r.keyPress(KeyEvent.VK_5);
                    r.keyRelease(KeyEvent.VK_5);
                    break;
                case '^':
                    r.keyPress(KeyEvent.VK_6);
                    r.keyRelease(KeyEvent.VK_6);
                    break;
                case '[':
                    r.keyPress(KeyEvent.VK_BRACELEFT);
                    r.keyRelease(KeyEvent.VK_BRACELEFT);
                    break;
                case ']':
                    r.keyPress(KeyEvent.VK_BRACERIGHT);
                    r.keyRelease(KeyEvent.VK_BRACERIGHT);
                    break;
                case '|':
                    r.keyPress(KeyEvent.VK_BACK_SLASH);
                    r.keyRelease(KeyEvent.VK_BACK_SLASH);
                    break;
                case '<':
                    r.keyPress(KeyEvent.VK_COMMA);
                    r.keyRelease(KeyEvent.VK_COMMA);
                    break;
                case '>':
                    r.keyPress(KeyEvent.VK_PERIOD);
                    r.keyRelease(KeyEvent.VK_PERIOD);
                    break;
                case '~':
                    r.keyPress(KeyEvent.VK_BACK_QUOTE);
                    r.keyRelease(KeyEvent.VK_BACK_QUOTE);
                    break;
                case '*':
                    r.keyPress(KeyEvent.VK_8);
                    r.keyRelease(KeyEvent.VK_8);
                    break;
                case '{':
                    r.keyPress(KeyEvent.VK_OPEN_BRACKET);
                    r.keyRelease(KeyEvent.VK_OPEN_BRACKET);
                    break;
                case '}':
                    r.keyPress(KeyEvent.VK_CLOSE_BRACKET);
                    r.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
                    break;
                default:
                    r.keyPress(KeyEvent.VK_SLASH);
                    r.keyRelease(KeyEvent.VK_SLASH);// question mark
                    //r.keyRelease(KeyEvent.VK_SHIFT);
                    System.out.println("Unexpected value: " + c);
                    //throw new IllegalStateException("Unexpected value: " + c);
            }
        r.keyRelease(KeyEvent.VK_SHIFT);
    }

    // Utility for simulatin typos
    public static int[] randomBackSpace(Robot r, int i, char[] charArray, int wordsIndex) throws InterruptedException {
        int random = (int)(Math.random() * 4) + 1;
        int typorandom = (int)(Math.random() * 25) + 97;
        int[] temp = new int[2];

        r.keyPress(KeyEvent.getExtendedKeyCodeForChar((char)(typorandom)));
        Thread.sleep(76 + (int)(Math.random() * 44) - 22);
        r.keyRelease(KeyEvent.getExtendedKeyCodeForChar((char)(typorandom)));
        i++;

        delayBetween(500, 300);

        for (int o = 0; o < random; o++) {
            r.keyPress(KeyEvent.VK_BACK_SPACE);
            Thread.sleep(76 + (int)(Math.random() * 44) - 22);
            r.keyRelease(KeyEvent.VK_BACK_SPACE);
            delayBetween(50, 150);

            i--;
            if(charArray[i] == ' ') {
                wordsIndex--;
            }
        }

        temp[0] = i;
        temp[1] = wordsIndex;
        return temp;
    }

    public static void playSound(String path) {
        File blip = new File(path);
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(blip);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }
        catch(LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    static class MyThread extends Thread {

        private String sourcedText;

        public MyThread(String sourcedText){
            this.sourcedText = sourcedText;
        }

        public void run() {
            ThreadStatus = true;

            try {
                Thread.sleep(5000); // Delay before running
            } catch (Exception ignored) {
            }

            Robot r = null;
            try {
                r = new Robot();
            } catch (java.awt.AWTException ignored) {
            }

            int typingspeed = revisionGUI.getSlider();  // in form of ms delay between char, WPM is handled by slider

            char c = 0;
            char[] charArray = sourcedText.toCharArray();

            String[] words = sourcedText.split(" ");
            int wordsIndex = 0;
            String word;
            boolean longWord = false;

            int length = charArray.length;

            for (int i = 0; i < length; i++) {

                if (Thread.currentThread().isInterrupted())
                    break;

                try {
                    c = charArray[i];
                    int code = KeyEvent.getExtendedKeyCodeForChar(c);
                    delayBetween(typingspeed * 2, typingspeed / 2);

                    if(c == ' ') {
                        if (longWord)
                            longWord = false;

                        wordsIndex++;
                        word = words[wordsIndex];
                        System.out.println(word);

                        if(word.length() >= 8)
                            longWord = true;

                    }

                    if(longWord && revisionGUI.realisticDelays) {
                        delayBetween(typingspeed, typingspeed / 4);
                            sleep(50);
                    }

                    if (Character.isUpperCase(c))
                        r.keyPress(KeyEvent.VK_SHIFT);

                    if(revisionGUI.realisticDelays) {
                        if (c == '.' && (int) (Math.random() * 1) == 0)
                            delayBetween(2500, 500);
                        if (c == ',' && (int) (Math.random() * 2) == 0)
                            delayBetween(200, 100);
                        if(c == '\n')
                            delayBetween(2500, 500);
                    }

                    r.keyPress(code);
                    Thread.sleep(76 + (int)(Math.random() * 44) - 22);
                    r.keyRelease(code);

                    if (Character.isUpperCase(c))
                        r.keyRelease(KeyEvent.VK_SHIFT);

                } catch (IllegalArgumentException e) {

                    if(revisionGUI.realisticDelays) {
                        try {
                            delayBetween(200,100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    extraCharacters(r, c);

                } catch (InterruptedException e) {
                    break;
                }

                if (((int) (Math.random() * 50) == 0 && i > 10) && revisionGUI.typoToggle) {
                    try {
                        int[] temp = randomBackSpace(r, i, charArray, wordsIndex);
                        i = temp[0];
                        wordsIndex = temp[1];

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            playSound("resource/button3.wav");
            ThreadStatus = false;

        }
    }

    public static void type(String sourcedText) {
        thread = new MyThread(sourcedText);
        playSound("resource/blip1.wav");
        thread.start();
    }

    public static void stopType() {
        if (thread != null) {
            playSound("resource/button3.wav");
            thread.interrupt();
        }
    }
}
