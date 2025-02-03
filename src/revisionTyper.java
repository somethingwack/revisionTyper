import java.awt.*;
import java.awt.event.KeyEvent;

public class revisionTyper {

    public static void delayBetween(int max, int min) {
        try { Thread.sleep((int)(Math.random() * (max - min)) + min); }
        catch (Exception e) {}
    }

    public static void type(String sourcedText) {

        Robot r = null;
        try {
            r = new Robot(); }
        catch(java.awt.AWTException awte) {}

        for (char c : sourcedText.toCharArray()) {
            try {
                int code = KeyEvent.getExtendedKeyCodeForChar(c);
                delayBetween(50, 10); // 300 100
                if (Character.isUpperCase(c))
                    r.keyPress(KeyEvent.VK_SHIFT);
                r.keyPress(code);
                r.keyRelease(code);
                if (Character.isUpperCase(c))
                    r.keyRelease(KeyEvent.VK_SHIFT);
            }
            catch(IllegalArgumentException e) {
                r.keyPress(KeyEvent.VK_SHIFT);
                switch (c) {
                    case '’':
                        r.keyRelease(KeyEvent.VK_SHIFT);
                        r.keyPress(KeyEvent.VK_QUOTE);
                        break;
                    case '"':
                        r.keyPress(KeyEvent.VK_QUOTE);
                        break;
                    case ';':
                        r.keyRelease(KeyEvent.VK_SHIFT);
                        r.keyPress(KeyEvent.VK_SEMICOLON);
                        break;
                    case ':':
                        r.keyPress(KeyEvent.VK_SEMICOLON);
                        break;
                    case '\'':
                        r.keyRelease(KeyEvent.VK_SHIFT);
                        r.keyPress(KeyEvent.VK_QUOTE);
                        break;
                    case '!':
                        r.keyPress(KeyEvent.VK_1);
                        break;
                    case '+':
                        r.keyPress(KeyEvent.VK_EQUALS);
                        break;
                    case '_':
                        r.keyPress(KeyEvent.VK_MINUS);
                        break;
                    case '?':
                        r.keyPress(KeyEvent.VK_SLASH);
                        break;
                    case ')':
                        r.keyPress(KeyEvent.VK_0);
                        break;
                    case '(':
                        r.keyPress(KeyEvent.VK_9);
                        break;
                    case '@':
                        r.keyPress(KeyEvent.VK_2);
                        break;
                    case '#':
                        r.keyPress(KeyEvent.VK_3);
                        break;
                    case '$':
                        r.keyPress(KeyEvent.VK_4);
                        break;
                    case '%':
                        r.keyPress(KeyEvent.VK_5);
                        break;
                    case '^':
                        r.keyPress(KeyEvent.VK_6);
                        break;
                    case '[':
                        r.keyPress(KeyEvent.VK_BRACELEFT);
                        break;
                    case ']':
                        r.keyPress(KeyEvent.VK_BRACERIGHT);
                        break;
                    case '|':
                        r.keyPress(KeyEvent.VK_BACK_SLASH);
                        break;
                    case '<':
                        r.keyPress(KeyEvent.VK_PERIOD);
                        break;
                    case '>':
                        r.keyPress(KeyEvent.VK_COMMA);
                        break;
                    case '~':
                        r.keyPress(KeyEvent.VK_BACK_QUOTE);
                        break;
                    case '*':
                        r.keyPress(KeyEvent.VK_8);
                        break;
                    default:
                        r.keyPress(KeyEvent.VK_SLASH); // question mark
                        //r.keyRelease(KeyEvent.VK_SHIFT);
                        System.out.println("Unexpected value: " + c);
                        //throw new IllegalStateException("Unexpected value: " + c);
                }
                r.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
    }
}
