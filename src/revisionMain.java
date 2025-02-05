import javax.swing.*;

public class revisionMain {

    public static void main(String[] args) {
        // GUI
        JFrame frame = new JFrame("revision typer");
        frame.setLocation(100, 100);
                frame.setSize(610, 340);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new revisionGUI());
        frame.setVisible(true);
        // GUI END

    }
}
