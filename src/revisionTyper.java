import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
public class revisionTyper {
    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("C:\\Users\\FamilyC11\\IdeaProjects\\revisionTyper\\src\\source2.txt");
        Scanner scan = new Scanner(file);

        String sourcedText= "";
        while(scan.hasNext()) {
            sourcedText = sourcedText.concat(scan.nextLine() + "\n");
        }

        try { Thread.sleep(5000); } // open notepad
        catch( Exception e ) {}
        Robot r = null;
        try {
            r = new Robot(); }
        catch(java.awt.AWTException awte) {}

        for (char c : sourcedText.toCharArray()) {
            int code = KeyEvent.getExtendedKeyCodeForChar(c);
            try { Thread.sleep((int)(Math.random() * 100) + 50); }
            catch (Exception e) {}
            if (Character.isUpperCase(c))
                r.keyPress(KeyEvent.VK_SHIFT);
            r.keyPress(code);
            r.keyRelease(code);
            if (Character.isUpperCase(c))
                r.keyRelease(KeyEvent.VK_SHIFT);
        }

    }
}