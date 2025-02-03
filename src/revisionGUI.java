import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class revisionGUI extends JPanel
{
    private static JTextArea textArea = new JTextArea();
    private final Color BG1 = new Color(45, 45, 45);
    private final Color BG2 = new Color(20, 20, 20);
    private static JLabel label = new JLabel();

    public revisionGUI()
    {
        setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter text below, then press run button (4 second delay).");
        Font font = new Font("font1", Font.BOLD, 20);
        label.setFont(font);
        label.setForeground(Color.WHITE);
        add(label);

        JButton clear = new JButton("Clear");
        clear.addActionListener(new ListenerCLR());
        clear.setBackground(BG1);
        clear.setForeground(Color.WHITE);

        JButton close = new JButton("Close");
        close.addActionListener(new ListenerCLS());
        close.setBackground(BG1);
        close.setForeground(Color.WHITE);

        JButton submit = new JButton("Run");
        submit.addActionListener(new ListenerSUB());
        submit.setBackground(BG1);
        submit.setForeground(Color.WHITE);

        add(clear);
        add(close);
        add(submit);

        textArea = new JTextArea(15, 90);
        textArea.setWrapStyleWord(false);
        textArea.setLineWrap(true);
        textArea.setOpaque(true);
        textArea.setEditable(true);
        textArea.setFocusable(true);
        textArea.setBackground(BG1);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        textArea.setBorder(UIManager.getBorder("Label.border"));
        add(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setForeground(BG2);
        scrollPane.setForeground(BG1);
        add(scrollPane);

        setBackground(BG2);
    }

    private static class ListenerCLR implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            textArea.setText("");
        }
    }

    private static class ListenerCLS implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SwingUtilities.invokeLater(
                    () -> {
                        try { Thread.sleep(1000); }
                        catch (Exception d) {}
                        Runtime.getRuntime().exit(1);
                    });
        }
    }

    private static class ListenerSUB implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {

            try { Thread.sleep(5000); }
            catch (Exception a) {}

            revisionTyper.type(textArea.getText());
        }
    }
}