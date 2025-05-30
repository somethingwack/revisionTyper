import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;


public class revisionGUI extends JPanel
{
    private static JTextArea textArea = new JTextArea();
    //private static final JLabel label = new JLabel();
    //private static JLabel sliderLabel;

    private static int sliderVal = 200;
    public static boolean realisticDelays = true;
    public static boolean typoToggle;
    private static ImageIcon revision;
    private static JLabel image;
    public static double typoChance = 0.5;

    public revisionGUI() {
        DrawUI();
    }

    private void DrawUI() {
        setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter text below, then press run button (4 second delay).    ");
        Font font = new Font("font1", Font.BOLD, 10);
        Color BG2 = new Color(20, 20, 20);
        label.setBackground(BG2);
        label.setFont(font);
        label.setForeground(Color.WHITE);
        add(label);

        JLabel typoLabel = new JLabel("25%");
        typoLabel.setBackground(BG2);
        typoLabel.setFont(font);
        typoLabel.setForeground(Color.WHITE);

//        revision = new ImageIcon("revision.png");
//        image = new JLabel();
//        image.setBounds(0,0,30,30);
//        image.setIcon(revision);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 5, 500, 250);
        slider.setMajorTickSpacing(20);

        JSlider typoSlider = new JSlider(JSlider.HORIZONTAL, 1, 25, 10);
        typoSlider.setMajorTickSpacing(20);
        typoSlider.setSize(100,100);

        typoSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                typoChance = (double) typoSlider.getValue() / 100;
                typoLabel.setText(typoSlider.getValue() + "%");
            }
        });

        Color BG1 = new Color(45, 45, 45);
        slider.setForeground(BG1);
        slider.setBackground(BG2);

        typoSlider.setForeground(BG1);
        typoSlider.setBackground(BG2);

        JLabel sliderLabel = new JLabel("Typing speed approx: 45 WPM");
        sliderLabel.setForeground(Color.WHITE);

        JButton clear = new JButton("Clear");
        clear.addActionListener(new ListenerCLR());
        clear.setBackground(BG1);
        clear.setForeground(Color.WHITE);

        /*JButton close = new JButton("Close");
        close.addActionListener(new ListenerCLS());
        close.setBackground(BG1);
        close.setForeground(Color.WHITE);
         */

        JButton submit = new JButton("Run");
        submit.addActionListener(new ListenerSUB());
        submit.setBackground(BG1);
        submit.setForeground(new Color(0,200,0));

        JButton stop = new JButton("Stop");
        stop.addActionListener(new ListenerSTOP());
        stop.setBackground(BG1);
        stop.setForeground(new Color(200,0,0));

        JCheckBox typo = new JCheckBox("Automatic Typos");
        typo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(typo.isSelected())
                    typoToggle = true;
                else
                    typoToggle = false;
            }
        });
        typo.setBackground(BG1);
        typo.setForeground(Color.WHITE);

        JCheckBox delays = new JCheckBox("Realistic Delays", true);
        delays.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(delays.isSelected())
                    realisticDelays = true;
                else
                    realisticDelays = false;
            }
        });
        delays.setBackground(BG1);
        delays.setForeground(Color.WHITE);

        add(clear);
        add(submit);
        add(stop);
        // + "MS: " + slider.getValue()
        final double[] WPM = {0.0};
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sliderVal = slider.getValue();
                WPM[0] = 167.06899 - 24.36022 * Math.log(slider.getValue());
                sliderLabel.setText("Approx typing speed: " + (int)Math.round(WPM[0]) + " WPM");
            }
        });

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
        add(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setForeground(BG2);
        scrollPane.setForeground(BG1);
        add(scrollPane);

        add(typo);
        add(delays);

        add(slider);
        add(sliderLabel);

        add(typoSlider);
        add(typoLabel);

        setBackground(new Color(20, 0, 0));

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
                        catch (Exception ignored) {}
                        Runtime.getRuntime().exit(1);
                    });
        }
    }

    private static class ListenerSUB implements ActionListener
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
        return sliderVal;
    }
}

