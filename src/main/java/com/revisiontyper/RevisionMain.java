package com.revisiontyper;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class RevisionMain {

    private RevisionMain() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Revision Typer");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new RevisionGUI());
            frame.pack();
            frame.setSize(760, 420);
            frame.setMinimumSize(new Dimension(640, 360));
            frame.setLocationRelativeTo(null);
            setWindowIcon(frame);
            frame.setVisible(true);
        });
    }

    private static void setWindowIcon(JFrame frame) {
        URL iconUrl = RevisionMain.class.getResource("/resource/RevWriter.png");
        if (iconUrl != null) {
            frame.setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }
}
