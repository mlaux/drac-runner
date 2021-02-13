package com.arcadeufo.dracrunner;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.security.Security;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;

public class DRACRunner {
    private static final String TITLE = "DRAC Console Runner";
    private static final String SETTINGS_KEY_HOST = "host";
    private static final String SETTINGS_KEY_USERNAME = "username";

    private static final int PADDING = 8;
    private static final int SPACING = 12;
    private static final Border FIELD_PADDING = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
    );
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

    private static final Preferences PREFS = Preferences.userNodeForPackage(DRACRunner.class);

    private static final CountDownLatch LATCH = new CountDownLatch(1);

    private static JFrame frame;
    private static JTextField hostField;
    private static JTextField usernameField;
    private static JTextField passwordField;

    private static void signalLaunch() {
        frame.dispose();
        LATCH.countDown();
    }

    private static void launchViewer(String host, String username, String password) {
        com.avocent.idrac.kvm.Main.main(
            new String[] {
                String.format("ip=%s", host),
                String.format("user=%s", username),
                String.format("passwd=%s", password),
                "kmport=5900",
                "vport=5900",
                "apcp=1",
                "version=2",
                "vmprivilege=true",
            }
        );
    }

    private static void createWindow() {
        frame = new JFrame(TITLE);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BorderLayout mainLayout = new BorderLayout(0, SPACING);
        Border padding = BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(mainLayout);
        mainPanel.setBorder(padding);

        JButton launchButton = new JButton("Launch");
        launchButton.addActionListener(command -> signalLaunch());
        launchButton.setFont(FONT);
        launchButton.setMargin(new Insets(PADDING, PADDING, PADDING, PADDING));
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(launchButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        GridLayout fieldLayout = new GridLayout(0, 1, 0, SPACING);
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(fieldLayout);

        hostField = createTextField("Host", false);
        usernameField = createTextField("Username", false);
        passwordField = createTextField("Password", true);

        hostField.setText(PREFS.get(SETTINGS_KEY_HOST, ""));
        usernameField.setText(PREFS.get(SETTINGS_KEY_USERNAME, ""));

        fieldPanel.add(hostField);
        fieldPanel.add(usernameField);
        fieldPanel.add(passwordField);

        mainPanel.add(fieldPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        if (!usernameField.getText().isEmpty()) {
            passwordField.requestFocus();
        } else if (!hostField.getText().isEmpty()) {
            usernameField.requestFocus();
        }
    }

    private static JTextField createTextField(String hint, boolean maskInput) {
        PlaceholderTextField field = new PlaceholderTextField();
        if (maskInput) {
            field.setEchoChar('*');
        }
        field.setPlaceholder(hint);
        field.setColumns(16);
        field.setFont(FONT);
        field.setBorder(FIELD_PADDING);
        return field;
    }

    private static void intentionallyWeakenSecurity() {
        Security.setProperty("jdk.tls.disabledAlgorithms", "");
        Security.setProperty("jdk.certpath.disabledAlgorithms", "");
    }

    public static void main(String[] args) throws Exception {
        intentionallyWeakenSecurity();

        SwingUtilities.invokeLater(DRACRunner::createWindow);
        LATCH.await();

        String host = hostField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        PREFS.put(SETTINGS_KEY_HOST, host);
        PREFS.put(SETTINGS_KEY_USERNAME, username);

        launchViewer(host, username, password);
    }
}
