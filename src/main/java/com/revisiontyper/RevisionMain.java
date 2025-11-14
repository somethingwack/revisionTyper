package com.revisiontyper;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.Taskbar;
import java.awt.Image;

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
            Image iconImage = loadAppIcon();
            if (iconImage != null) {
                if (!isMacOs()) {
                    frame.setIconImage(iconImage);
                    setTaskbarIcon(iconImage);
                }
            }
            frame.setVisible(true);
        });
    }

    private static Image loadAppIcon() {
        URL iconUrl = RevisionMain.class.getResource("/icons/RevWriter.png");
        if (iconUrl != null) {
            return new ImageIcon(iconUrl).getImage();
        }
        return null;
    }

    private static boolean isMacOs() {
        String name = System.getProperty("os.name", "").toLowerCase();
        return name.contains("mac");
    }

    private static void setTaskbarIcon(Image icon) {
        if (!Taskbar.isTaskbarSupported()) {
            return;
        }
        if (isMacOs()) {
            // Let macOS use the .icns bundled with the app to preserve rounded dock styling.
            return;
        }
        Taskbar taskbar = Taskbar.getTaskbar();
        if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
            taskbar.setIconImage(icon);
        }
    }
}
