package org.hxnry.rsp.watcher_server.api.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * Created with IntelliJ IDEA.h
 * User: Purple
 * Date: 11/25/2015
 * Time: 3:47 AM
 */
public class HealthBar {

    private final Color HEALTH_COLOR = new Color(7, 135, 45);
    private final Color BACK_COLOR = new Color(188, 0, 0);
    private final Color YELLOW_COLOR = Color.YELLOW;
    private int currentHealth;
    private int hpPercent = -1;
    private Rectangle rectangle;
    private boolean barDetected;

    public HealthBar(int currentHealth, int width, int height) {
        if(currentHealth == -1 ) {
            this.currentHealth = 100;
            this.rectangle = new Rectangle(0, 0, width, height);
        } else {
            this.currentHealth = currentHealth;
            this.rectangle = new Rectangle(0, 0, width, height);
        }
    }

    private void drawUnknown(Graphics graphics, int alpha, int x, int y) {
        this.rectangle.setLocation(x, y);
        graphics.setColor(YELLOW_COLOR);
        graphics.fillRoundRect(x, y, rectangle.width, rectangle.height, 5, 5);
        drawCenteredString(graphics, alpha,"Helvetica", Font.CENTER_BASELINE, 11, "Unknown", rectangle);
    }

    public void draw(Graphics graphics, int alpha, int x, int y) {

        if(this.currentHealth != -1) {
            hpPercent = Math.max(0, Math.min(100, currentHealth));
        }

        if(hpPercent == -1) {
            drawUnknown(graphics, alpha, x, y);
        } else {
            double percentWidth = (hpPercent > 0 ? ((double)hpPercent / 100) * rectangle.getWidth() : 0);

            this.rectangle.setLocation(x, y);

            graphics.setColor(new Color(BACK_COLOR.getRed(), BACK_COLOR.getGreen(), BACK_COLOR.getBlue(), alpha));
            graphics.fillRoundRect(x, y, rectangle.width, rectangle.height, 5, 5);

            graphics.setColor(new Color(HEALTH_COLOR.getRed(), HEALTH_COLOR.getGreen(), HEALTH_COLOR.getBlue(), alpha));
            graphics.fillRoundRect(x, y, (int) percentWidth, rectangle.height, 5, 5);

            graphics.setColor(new Color(0, 0, 0, alpha));
            graphics.drawRoundRect(x, y, rectangle.width, rectangle.height, 5, 5);

            this.rectangle.setLocation(x, y - 1);
            drawCenteredString(graphics, alpha, "Helvetica", Font.PLAIN, 10, hpPercent == -1 ? "???" : (hpPercent <= 0 ? "DEAD" :  hpPercent + "%"), rectangle);
        }
    }

    private void drawCenteredString(Graphics graphics, int alpha, String fontName, int style, int size, String string, Rectangle rectangle) {
        final Font font = new Font(fontName, style, size);
        graphics.setFont(font);
        int centerX = (int) rectangle.getWidth() / 2;
        int centerY = (int) rectangle.getHeight() / 2;
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(string, graphics).getBounds();
        FontRenderContext renderContext = ((Graphics2D) graphics).getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(renderContext, string);
        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
        int textX = centerX - stringBounds.width/2;
        int textY = centerY - visualBounds.height/2 - visualBounds.y;
        int offset = 1;
        graphics.setColor(new Color(0, 0, 0, alpha));
        graphics.drawString(string, textX + (int) rectangle.getX() + 1, offset+ textY + (int) rectangle.getY());
        graphics.setColor(new Color(255, 255, 255, alpha));
        graphics.drawString(string, textX + (int) rectangle.getX(), offset + textY + (int) rectangle.getY());
    }

}
