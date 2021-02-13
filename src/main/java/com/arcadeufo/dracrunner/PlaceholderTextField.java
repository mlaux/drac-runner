package com.arcadeufo.dracrunner;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

public class PlaceholderTextField extends JPasswordField {

    private String placeholder;

    public PlaceholderTextField() {
        this(null, null, 0);
    }

    public PlaceholderTextField(String text) {
        this(null, text, 0);
    }

    public PlaceholderTextField(int columns) {
        this(null, null, columns);
    }

    public PlaceholderTextField(String text, int columns) {
        this(null, text, columns);
    }

    public PlaceholderTextField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
        // disable password mode unless explicitly set
        setEchoChar((char) 0);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (placeholder == null || getPassword().length > 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getDisabledTextColor());
        g2.drawString(
            placeholder,
            getInsets().left,
            g2.getFontMetrics().getMaxAscent() + getInsets().top
        );
    }
}
