package org.hxnry.rsp.watcher_server.api.graphics;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class FlashingText {

    private String message = "null";
    private int mode = 0;
    private int size = 22;
    double alpha = 255;

    private Rectangle bounds = new Rectangle(0, 0, 518, 339);
    private int speed = 10;

    public FlashingText() {

    }

    public FlashingText(String message) {
        this.message = message;
    }

    public FlashingText(String message, int size) {
        this.message = message;
        this.size = size;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public FlashingText setSize(int size) {
        this.size = size;
        return this;
    }

    public FlashingText setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public void handleSwitching() {
        if(alpha <= 0) {
            mode = 0;
            alpha = 1;
        } else if(alpha >= 255) {
            mode = 1;
        }
    }

    public boolean draw(Graphics graphics, double delta) {
        handleSwitching();
        if(mode == 0) {
            alpha += delta * speed;
        } else {
            alpha -= delta * speed;
        }
        drawText(graphics, Color.WHITE, size, message, bounds);
        return true;
    }

    public void drawText(Graphics graphics, Color color, int size, String msg, Rectangle rectangle) {
        Font font = new Font("Helvetica", Font.BOLD, size);
        graphics.setFont(font);
        int centerX = (int) rectangle.getWidth() / 2;
        int centerY = (int) rectangle.getHeight() / 2;
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(msg, graphics).getBounds();
        FontRenderContext renderContext = ((Graphics2D) graphics).getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(renderContext, msg);
        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
        int textX = centerX - stringBounds.width/2;
        int textY = centerY - visualBounds.height/2 - visualBounds.y;
        Color back = new Color(0, 0, 0, Math.max(0, Math.min(255, (int) alpha)));
        graphics.setColor(back);
        graphics.drawString(msg, textX + (int) rectangle.getX() + 1, textY + (int) rectangle.getY() + 1);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) alpha))));
        graphics.drawString(msg, textX + (int) rectangle.getX(), textY + (int) rectangle.getY());
    }

    public FlashingText setBounds(Rectangle bounds) {
        this.bounds = bounds;
        return this;
    }
}
