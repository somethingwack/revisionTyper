import javax.swing.*;

public class revisionMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Revision Typer");
            ImageIcon icon = new ImageIcon("resource/RevWriter.png");
            frame.setIconImage(icon.getImage());
        frame.setLocation(100, 100);
                frame.setSize(610, 340);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new revisionGUI());
        frame.setVisible(true);
        });
    }
}
