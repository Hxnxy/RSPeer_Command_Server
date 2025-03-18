package org.hxnry.rsp.watcher_server.api.graphics;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * @author Hxnry
 * @since November 07, 2016
 */
public class Drawing {

    public static void drawCenteredString(Graphics graphics, int style, Color color, int size, String string, Rectangle rectangle) {
        final Font FONT = new Font("Helvetica", style, size);
        int centerX = (int) rectangle.getWidth() / 2;
        int centerY = (int) rectangle.getHeight() / 2;
        graphics.setFont(FONT);
        graphics.setColor(color);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(string, graphics).getBounds();
        Font font = graphics.getFont();
        FontRenderContext renderContext = ((Graphics2D) graphics).getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(renderContext, string);
        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
        int textX = centerX - stringBounds.width / 2;
        int textY = centerY - visualBounds.height / 2 - visualBounds.y;
        graphics.setColor(Color.BLACK);
        graphics.drawString(string, textX + (int) rectangle.getX() + 1, textY + (int) rectangle.getY() + 2);
        graphics.setColor(Color.WHITE);
        graphics.drawString(string, textX + (int) rectangle.getX(), textY + (int) rectangle.getY() + 1);
    }

    public static void drawCenteredString(Graphics graphics, int style, Color color, int size, String string, int x, int y, int width, int height) {
        final Font FONT = new Font("Helvetica", style, size);
        Rectangle rectangle = new Rectangle(x, y, width, height);
        int centerX = (int) rectangle.getWidth() / 2;
        int centerY = (int) rectangle.getHeight() / 2;
        graphics.setFont(FONT);
        graphics.setColor(color);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(string, graphics).getBounds();
        Font font = graphics.getFont();
        FontRenderContext renderContext = ((Graphics2D) graphics).getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(renderContext, string);
        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
        int textX = centerX - stringBounds.width / 2;
        int textY = centerY - visualBounds.height / 2 - visualBounds.y;
        graphics.setColor(Color.BLACK);
        graphics.drawString(string, textX + (int) rectangle.getX() + 1, textY + (int) rectangle.getY() + 2);
        graphics.setColor(Color.WHITE);
        graphics.drawString(string, textX + (int) rectangle.getX(), textY + (int) rectangle.getY() + 1);
    }

    public static void drawText(Graphics graphics, int style, Color color, int size, String msg, int x, int y) {
        final Font FONT = new Font("Helvetica", style, size);
        int cutoff = 33;
        if(msg.length() >  cutoff) {
            msg = msg.substring(0, cutoff) + "...";
        }
        graphics.setFont(FONT);
        graphics.setColor(Color.BLACK);
        graphics.drawString(msg, x + 1, y + 1);
        graphics.setColor(color);
        graphics.drawString(msg, x, y);
    }

    public static void drawMultiText(Graphics graphics, int style, int size, Text[] texts, int x, int y) {
        int xLoc = x;
        for(Text text : texts) {
            int siz = text.getSize();
            Font font = new Font("Helvetica", style, siz == -1 ? size : siz);
            graphics.setFont(font);
            String message = text.getMessage();
            int width = graphics.getFontMetrics().stringWidth(message) + 1;
            drawText(graphics, font.getStyle(), text.getColor(), font.getSize(), message, xLoc, y);
            xLoc += width;
        }
    }
}
