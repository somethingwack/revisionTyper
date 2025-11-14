package com.revisiontyper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Primary Swing panel for Revision Typer.
 */
public final class RevisionGUI extends JPanel {

    private static final Color BG_PRIMARY = new Color(45, 45, 45);
    private static final Color BG_SECONDARY = new Color(30, 30, 30);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 11);

    private final JTextArea textArea;
    private final JLabel typingSpeedLabel;
    private final JLabel typoLabel;
    private final JLabel startDelayLabel;
    private final JLabel runHintLabel;
    private final JButton runButton;
    private final JButton stopButton;
    private final JLabel statusLabel;
    private final Timer statusTimer;
    private long pendingStartDeadlineMs = -1L;

    public RevisionGUI() {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG_SECONDARY);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        textArea = buildTextArea();
        typingSpeedLabel = createLabel(describeDelay());
        typoLabel = createLabel(describeTypoLabel());
        startDelayLabel = createLabel(describeStartDelay());
        runHintLabel = createLabel(buildRunHintMessage());
        statusLabel = createLabel("Status: Idle");
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setForeground(new Color(180, 220, 255));

        runButton = createButton("Run", this::handleRun, new Color(0, 200, 0));
        runButton.setToolTipText("Shortcut: Ctrl+Enter");
        stopButton = createButton("Stop", this::handleStop, new Color(200, 0, 0));
        stopButton.setToolTipText("Shortcut: Esc");
        JButton clearButton = createButton("Clear", this::handleClear, Color.WHITE);
        clearButton.setToolTipText("Shortcut: Ctrl+L");

        add(buildInfoPanel(), BorderLayout.NORTH);
        add(buildEditorPanel(), BorderLayout.CENTER);
        add(buildControlPanel(clearButton), BorderLayout.SOUTH);

        installKeyboardShortcuts();

        statusTimer = new Timer(300, e -> syncButtonState());
        syncButtonState();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        statusTimer.start();
    }

    @Override
    public void removeNotify() {
        statusTimer.stop();
        super.removeNotify();
    }
    private JComponent buildInfoPanel() {
        runHintLabel.setText(buildRunHintMessage());
        runHintLabel.setForeground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_SECONDARY);
        panel.add(runHintLabel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        return panel;
    }

    private JComponent buildEditorPanel() {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        return scrollPane;
    }

    private JComponent buildControlPanel(JButton clearButton) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_SECONDARY);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setBackground(BG_SECONDARY);
        buttonRow.add(runButton);
        buttonRow.add(stopButton);
        buttonRow.add(clearButton);

        JPanel sliders = new JPanel(new GridLayout(1, 3, 12, 0));
        sliders.setBackground(BG_SECONDARY);
        sliders.add(wrapSlider("Typing speed", typingSpeedLabel, buildSpeedSlider()));
        sliders.add(wrapSlider("Typo chance", typoLabel, buildTypoSlider()));
        sliders.add(wrapSlider("Start delay", startDelayLabel, buildStartDelaySlider()));

        JPanel checkBoxes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkBoxes.setBackground(BG_SECONDARY);
        checkBoxes.add(buildTyposBox());
        checkBoxes.add(buildDelayBox());

        container.add(buttonRow);
        container.add(Box.createVerticalStrut(10));
        container.add(sliders);
        container.add(Box.createVerticalStrut(10));
        container.add(checkBoxes);

        return container;
    }

    private JTextArea buildTextArea() {
        JTextArea area = new JTextArea(13, 90);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(BG_PRIMARY);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        return area;
    }

    private JCheckBox buildTyposBox() {
        JCheckBox box = createCheckBox("Automatic typos");
        box.setSelected(TypingSettings.isTyposEnabled());
        box.setToolTipText("Inject believable mistakes that get corrected automatically.");
        box.addActionListener(e -> TypingSettings.setTyposEnabled(box.isSelected()));
        return box;
    }

    private JCheckBox buildDelayBox() {
        JCheckBox box = createCheckBox("Realistic delays");
        box.setSelected(TypingSettings.isRealisticDelaysEnabled());
        box.setToolTipText("Add additional pauses for punctuation and long words.");
        box.addActionListener(e -> TypingSettings.setRealisticDelays(box.isSelected()));
        return box;
    }

    private JPanel wrapSlider(String title, JLabel indicator, JSlider slider) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SECONDARY);

        JLabel titleLabel = createLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        indicator.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(indicator);
        panel.add(Box.createVerticalStrut(4));
        panel.add(slider);

        return panel;
    }

    private JSlider buildSpeedSlider() {
        int minWpm = TypingSettings.getMinimumWpm();
        int maxWpm = TypingSettings.getMaximumWpm();
        JSlider slider = new JSlider(JSlider.HORIZONTAL, minWpm, maxWpm, TypingSettings.getTypingWpm());
        slider.setBackground(BG_SECONDARY);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.addChangeListener(e -> {
            TypingSettings.setTypingWpm(slider.getValue());
            typingSpeedLabel.setText(describeDelay());
        });
        return slider;
    }

    private JSlider buildTypoSlider() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 15,
                (int) Math.round(TypingSettings.getTypoChance() * 100));
        slider.setBackground(BG_SECONDARY);
        slider.addChangeListener(e -> {
            TypingSettings.setTypoChance(slider.getValue() / 100.0);
            typoLabel.setText(describeTypoLabel());
        });
        return slider;
    }

    private JSlider buildStartDelaySlider() {
        int min = TypingSettings.getMinimumStartDelayMs();
        int max = TypingSettings.getMaximumStartDelayMs();
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, TypingSettings.getStartDelayMs());
        slider.setBackground(BG_SECONDARY);
        slider.setMajorTickSpacing(1000);
        slider.setMinorTickSpacing(250);
        slider.setPaintTicks(true);
        slider.addChangeListener(e -> {
            TypingSettings.setStartDelayMs(slider.getValue());
            startDelayLabel.setText(describeStartDelay());
            updateRunHintLabel();
        });
        return slider;
    }

    private JButton createButton(String text, Runnable action, Color foreground) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        button.setForeground(foreground);
        button.setBackground(BG_PRIMARY);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    private JCheckBox createCheckBox(String text) {
        JCheckBox box = new JCheckBox(text);
        box.setFont(LABEL_FONT);
        box.setForeground(Color.WHITE);
        box.setBackground(BG_SECONDARY);
        return box;
    }

    private void handleClear() {
        textArea.setText("");
        textArea.requestFocusInWindow();
    }

    private void handleRun() {
        if (RevisionTyper.isRunning()) {
            return;
        }

        String text = textArea.getText();
        if (text == null || text.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter text before starting the typer.",
                    "Nothing to type", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        pendingStartDeadlineMs = System.currentTimeMillis() + TypingSettings.getStartDelayMs();
        RevisionTyper.type(text);
        textArea.requestFocusInWindow();
        syncButtonState();
    }

    private void handleStop() {
        RevisionTyper.stopType();
        pendingStartDeadlineMs = -1L;
        syncButtonState();
    }

    private void syncButtonState() {
        boolean running = RevisionTyper.isRunning();
        runButton.setEnabled(!running);
        stopButton.setEnabled(running);
        updateStatusLabel(running);
    }

    private void updateStatusLabel(boolean running) {
        long now = System.currentTimeMillis();
        if (pendingStartDeadlineMs > 0) {
            long remaining = pendingStartDeadlineMs - now;
            if (remaining > 0) {
                statusLabel.setText("Status: Starting in " + (int) Math.ceil(remaining / 1000.0) + "s");
                return;
            }
            pendingStartDeadlineMs = -1L;
        }

        statusLabel.setText(running ? "Status: Typing…" : "Status: Idle");
    }

    private static String describeDelay() {
        int delayMs = TypingSettings.getTypingDelay();
        int wpm = TypingSettings.getTypingWpm();
        String speedBucket = wpm <= 60 ? "slow" : wpm <= 120 ? "medium" : wpm <= 200 ? "fast" : "pro";
        return wpm + " WPM · " + delayMs + " ms · " + speedBucket;
    }

    private static String describeTypoLabel() {
        return (int) Math.round(TypingSettings.getTypoChance() * 100) + "% typo / letter";
    }

    private static String describeStartDelay() {
        int delayMs = TypingSettings.getStartDelayMs();
        if (delayMs == 0) {
            return "Instant start";
        }
        String seconds = delayMs % 1000 == 0
                ? String.valueOf(delayMs / 1000)
                : String.format("%.1f", delayMs / 1000.0);
        return seconds + " s pause";
    }

    private String buildRunHintMessage() {
        int delayMs = TypingSettings.getStartDelayMs();
        if (delayMs == 0) {
            return "Enter text then press Run (no countdown) or use Ctrl+Enter · Esc stops.";
        }
        String seconds = delayMs % 1000 == 0
                ? String.valueOf(delayMs / 1000)
                : String.format("%.1f", delayMs / 1000.0);
        String unit = "second";
        if (!"1".equals(seconds) && !"1.0".equals(seconds)) {
            unit = "seconds";
        }
        return "Enter text then press Run (" + seconds + " " + unit + " delay) or use Ctrl+Enter · Esc stops.";
    }

    private void updateRunHintLabel() {
        runHintLabel.setText(buildRunHintMessage());
    }

    private void installKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        registerShortcut(inputMap, actionMap,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, shortcutMask), "runAction", this::handleRun);
        registerShortcut(inputMap, actionMap,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "stopAction", this::handleStop);
        registerShortcut(inputMap, actionMap,
                KeyStroke.getKeyStroke(KeyEvent.VK_L, shortcutMask), "clearAction", this::handleClear);
    }

    private void registerShortcut(InputMap inputMap, ActionMap actionMap, KeyStroke stroke,
                                  String key, Runnable action) {
        inputMap.put(stroke, key);
        actionMap.put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
