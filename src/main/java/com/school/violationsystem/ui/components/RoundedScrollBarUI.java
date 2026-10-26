package com.school.violationsystem.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class RoundedScrollBarUI extends BasicScrollBarUI {
    private static final Color COLOR_TRACK = Color.decode("#ECEEF2");
    private static final Color COLOR_THUMB = Color.decode("#B8BCC5");
    private static final Color COLOR_THUMB_HOVER = Color.decode("#969BA6");
    private static final int SCROLLBAR_THICKNESS = 10;

    public static void install(JScrollPane scrollPane) {
        install(scrollPane.getVerticalScrollBar(), JScrollBar.VERTICAL);
        install(scrollPane.getHorizontalScrollBar(), JScrollBar.HORIZONTAL);
    }

    private static void install(JScrollBar scrollBar, int orientation) {
        scrollBar.setUI(new RoundedScrollBarUI());
        scrollBar.setOpaque(false);
        scrollBar.setUnitIncrement(16);
        scrollBar.setPreferredSize(orientation == JScrollBar.VERTICAL
                ? new Dimension(SCROLLBAR_THICKNESS, 0)
                : new Dimension(0, SCROLLBAR_THICKNESS));
    }

    @Override
    protected void configureScrollBarColors() {
        trackColor = COLOR_TRACK;
        thumbColor = COLOR_THUMB;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected void paintTrack(Graphics graphics, javax.swing.JComponent component, Rectangle bounds) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setColor(COLOR_TRACK);
        graphics2D.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        graphics2D.dispose();
    }

    @Override
    protected void paintThumb(Graphics graphics, javax.swing.JComponent component, Rectangle bounds) {
        if (bounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(isThumbRollover() ? COLOR_THUMB_HOVER : COLOR_THUMB);
        graphics2D.fillRoundRect(
                bounds.x + 2,
                bounds.y + 2,
                Math.max(0, bounds.width - 4),
                Math.max(0, bounds.height - 4),
                SCROLLBAR_THICKNESS,
                SCROLLBAR_THICKNESS);
        graphics2D.dispose();
    }

    private static JButton createEmptyButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setBorder(null);
        button.setFocusable(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        return button;
    }
}
