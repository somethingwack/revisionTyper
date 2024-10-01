import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class revisionTyper {
    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("C:\\Users\\FamilyC11\\IdeaProjects\\revisionTyper\\src\\source.txt");
        Scanner scan = new Scanner(file);

        String sourced = "";
        while(scan.hasNext()) {
            sourced = sourced.concat(scan.nextLine() + "\n");
        }
        for (int i = 0; i < sourced.length(); i++) {
            char c = sourced.charAt(i);
            System.out.print(c);
            try { Thread.sleep(10); }
            catch (Exception e) {}
        }
    }
}