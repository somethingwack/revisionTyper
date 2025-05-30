import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class revisionGUI extends JPanel
{
    // GUI
    private static JTextArea textArea = new JTextArea();
    private final Font font = new Font("font1", Font.BOLD, 10);

    // Toggles and Slider values
    public static double typoChance = 0.5;
    public static boolean typoToggle;
    public static boolean realisticDelays = true;
    private static int WPMsliderVal = 200;

    // Colors
    private final Color BG1 = new Color(45, 45, 45);
    private final Color BG2 = new Color(20, 20, 20);

    public revisionGUI() {
        DrawUI();
    }

    private void DrawUI() {
        setLayout(new FlowLayout());

        // Buttons
        JButton clear = createButton("Clear", new ListenerCLR(), 255,255,255);
        JButton run = createButton("Run", new ListenerRUN(), 0,200,0);
        JButton stop = createButton("Stop", new ListenerSTOP(), 200,0,0);
        
        // Labels
        JLabel infoLabel = createLabel("Enter text below, then press run button (4 second delay).    ");
        JLabel typoSliderLabel = createLabel("25%");
        JLabel WPMsliderLabel = createLabel("Typing speed approx: 45 WPM");
        
        // Sliders
        JSlider WPMslider = new JSlider(JSlider.HORIZONTAL, 5, 500, 250);
            WPMslider.setMajorTickSpacing(20);
            WPMslider.setForeground(BG1);
            WPMslider.setBackground(BG2);
            final double[] WPM = {0.0};
            WPMslider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    WPMsliderVal = WPMslider.getValue();
                    WPM[0] = 167.06899 - 24.36022 * Math.log(WPMslider.getValue());
                    WPMsliderLabel.setText("Approx typing speed: " + (int)Math.round(WPM[0]) + " WPM");
                }
            });

        JSlider typoSlider = new JSlider(JSlider.HORIZONTAL, 1, 25, 10);
            typoSlider.setMajorTickSpacing(20);
            typoSlider.setForeground(BG1);
            typoSlider.setBackground(BG2);
            typoSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    typoChance = (double) typoSlider.getValue() / 100;
                    typoSliderLabel.setText(typoSlider.getValue() + "%");
                }
            });

        // Checkboxes
        JCheckBox typoCheckBox = createBox("Automatic Typos?", null);
            typoCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    typoToggle = typoCheckBox.isSelected();
                }
            });

        JCheckBox delaysCheckBox = createBox("Realistic Delays?", null);
            delaysCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    realisticDelays = delaysCheckBox.isSelected();
                }
            });

        // Primary Text Area and Scroll Pane
        textArea = new JTextArea(13, 90);
            textArea.setWrapStyleWord(false);
            textArea.setLineWrap(true);
            textArea.setOpaque(true);
            textArea.setEditable(true);
            textArea.setFocusable(true);
            textArea.setBackground(BG1);
            textArea.setForeground(Color.WHITE);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
            textArea.setBorder(UIManager.getBorder("Label.border"));

        JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setForeground(BG2);
            scrollPane.setForeground(BG1);

        // JFrame
        setBackground(new Color(20, 0, 0));

        add(infoLabel);
        add(clear);
        add(stop);
        add(run);
        add(scrollPane);
        add(WPMslider);
        add(WPMsliderLabel);
        add(typoSlider);
        add(typoSliderLabel);

    }
    // Methods below for creating common GUI elements

    private JButton createButton(String text, ActionListener listener, int r, int g, int b) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setForeground(new Color(r,g,b));
        button.setBackground(BG1);
        return button;
    }

    private JLabel createLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setBackground(BG2);
        label.setFont(font);
        label.setForeground(Color.WHITE);
        
        return label;
    }

    private JCheckBox createBox(String text, ActionListener listener)
    {
        JCheckBox box = new JCheckBox(text);
        box.addActionListener(listener);
        box.setForeground(Color.WHITE);
        box.setBackground(BG1);

        return box;
    }

    private static class ListenerCLR implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            textArea.setText("");
        }
    }

    // JButton Action Listeners

    private static class ListenerCLS implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SwingUtilities.invokeLater(
                    () -> {
                        try { Thread.sleep(1000); }
                        catch (Exception ignored) {}
                        Runtime.getRuntime().exit(1);
                    });
        }
    }

    private static class ListenerRUN implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {

            if(!revisionTyper.getStatus()) { // only run if thread is stopped
                revisionTyper.type(textArea.getText());
            }
        }
    }

    private static class ListenerSTOP implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(revisionTyper.getStatus()) { // only run if thread is started
                revisionTyper.stopType();
            }
        }
    }
    
    public static int getSlider() {
        return WPMsliderVal;
    }
}

