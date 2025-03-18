package org.hxnry.rsp.watcher_server.api.util;

import org.hxnry.rsp.watcher_server.api.graphics.Text;
import org.rspeer.runetek.api.commons.StopWatch;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * Created with IntelliJ IDEA.h
 * User: Purple
 * Date: 11/25/2015
 * Time: 3:26 AM
 */
public class Window {

    public CustomStopWatch stopWatch = new CustomStopWatch();
    public StopWatch dissapearWatch;
    int backgroundMax = 150;
    int headerMaxAlpha = 200;
    private Color HEADER_COLOR = new Color(255, 255, 255, 120);
    private Color BACKGROUND_COLOR = new Color(0, 0, 0, 200);
    private final int BASE_HEIGHT = 28;
    private final int BASE_WIDTH = 237;
    public Rectangle titleRectangle = new Rectangle(0, 0, BASE_WIDTH - 6, 21);
    private String title = "";
    private Text[] titleTexts;
    private int rows = 0;
    private int x = 0;
    private int y = 0;
    private int arcW = 5;
    private int arcH = 5;
    private boolean hovering;
    public int alpha = 255;

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }



    public Window(int length, String title) {
        this(length, new Text(title));
    }

    public Window(int length, Text... texts) {
        this.titleTexts = texts;
        this.rows = length;
    }

    public void setTitle(String title) {
        this.titleTexts = new Text[] {
                new Text(title)
        };
    }

    public void setTitle(Text... texts) {
        this.titleTexts = texts;
    }

    public void setLocation(int upX, int upY) {
        this.x = upX;
        this.y = upY;
    }

    public int getY(int row) {
        return y + 20 + (20 * row);
    }

    public int getWidth() {
        return titleRectangle.width;
    }

    public int getX() {
        return x + 7;
    }

    public void setBackgroundColor(Color color) {
        BACKGROUND_COLOR = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
    }

    public void setHeaderColor(Color color) {
        HEADER_COLOR = new Color(color.getRed(), color.getGreen(), color.getBlue(), 120);
    }

    public Color getBackgroundColor() {
        try {
            return alpha > headerMaxAlpha && alpha > 0 ? BACKGROUND_COLOR : new Color(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), BACKGROUND_COLOR.getBlue(), alpha);
        } catch (Exception e) {
            return BACKGROUND_COLOR;
        }
    }

    public Color getHeaderColor() {
        try {
            return alpha > backgroundMax && alpha > 0 ? HEADER_COLOR : new Color(HEADER_COLOR.getRed(), HEADER_COLOR.getGreen(), HEADER_COLOR.getBlue(), alpha);
        } catch (Exception e) {
            return HEADER_COLOR;
        }
    }

    public void draw(Graphics g) {
        g.setColor(getBackgroundColor());
        g.fillRoundRect(x, y, BASE_WIDTH, BASE_HEIGHT + (20 * rows), arcW, arcH);
        g.setColor(getHeaderColor());
        g.drawRoundRect(x, y, BASE_WIDTH, BASE_HEIGHT + (20 * rows), arcW, arcH);
        g.setColor(getHeaderColor());
        g.fillRoundRect(x + 4, y + 4, BASE_WIDTH - 7, 21, arcW, arcH);
        titleRectangle.setLocation(x + 4, y + 4);
        //drawCenteredString(g, "Helvetica", Font.BOLD, 12, title, titleRectangle);
        drawCenteredTitle((Graphics2D) g, Font.CENTER_BASELINE, 12, titleTexts, titleRectangle);
    }

    private void drawCenteredString(Graphics graphics, String fontName, int style, int size, String string, Rectangle rectangle) {
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
        graphics.setColor(Color.BLACK);
        graphics.drawString(string, textX + (int) rectangle.getX() + 1, offset + textY + (int) rectangle.getY() + 1);
        graphics.setColor(Color.WHITE);
        graphics.drawString(string, textX + (int) rectangle.getX(), offset + textY + (int) rectangle.getY());
    }

    public void drawStat(Graphics graphics, int xLoc, int yLoc, Text... texts) {
        drawMultiText(graphics, Font.CENTER_BASELINE, 12, texts, xLoc, yLoc);
    }

    public void drawText(Graphics graphics, int style, Color color, int size, String msg, int x, int y) {
        final Font FONT = new Font("Helvetica", style, size);
        int cutoff = 33;
        if(msg.length() >  cutoff) {
            msg = msg.substring(0, cutoff) + "...";
        }
        graphics.setFont(FONT);
        graphics.setColor(new Color(0, 0, 0, alpha));
        graphics.drawString(msg, x + 1, y + 1);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        graphics.drawString(msg, x, y);
    }

    public Rectangle getTitleBounds() {
        return titleRectangle.getBounds();
    }

    public Rectangle getRowBounds(int index) {
        return new Rectangle(x, y, BASE_WIDTH, BASE_HEIGHT + (20 * rows));
    }

    public Rectangle getWindowBounds() {
        return new Rectangle(x, y, BASE_WIDTH, BASE_HEIGHT + (20 * rows));
    }

    public boolean isHovering() {
        return getWindowBounds().contains(MouseInfo.getPointerInfo().getLocation());
    }

    public void drawMultiText(Graphics graphics, int style, int size, Text[] texts, int x, int y) {
        try {
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
        } catch (Exception e) {

        }
    }

    private void drawCenteredTitle(Graphics2D graphics, int style, int size, Text[] texts, Rectangle rectangle) {
        try {
            Font font;
            int centerX = rectangle.width / 2;
            int centerY = rectangle.height / 2;
            int currentWidth = 0;
            int width = 0;
            for(Text text : texts) {
                int textSize = text.getSize();
                font = new Font("Helvetica", style, textSize == -1 ? size : textSize);
                String message = text.getMessage();
                GlyphVector glyphVector = font.createGlyphVector(graphics.getFontRenderContext(), message);
                Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
                width += visualBounds.width + 1;
            }
            int offset = rectangle.x + (centerX - width / 2);
            for(Text text : texts) {
                int textSize = text.getSize();
                font = new Font("Helvetica", style, textSize == -1 ? size : textSize);
                graphics.setFont(font);
                String message = text.getMessage();
                GlyphVector glyphVector = font.createGlyphVector(graphics.getFontRenderContext(), message);
                Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
                int textY = centerY - visualBounds.height / 2 - visualBounds.y;
                drawText(graphics, font.getStyle(), text.getColor(), font.getSize(), message, offset + currentWidth, rectangle.y + textY + 1);
                currentWidth += visualBounds.width + 4;
            }
        } catch (Exception e) {

        }
    }

    public int getHeight() {
        return BASE_HEIGHT + (20 * rows);
    }

    public void setColor(Color color) {
        this.HEADER_COLOR = color;
    }

    public int getAlpha() {
        return alpha < 0 || alpha > 255 ? 0 : alpha;
    }

    private int speed = 1;

    public void calculateAlpha(double delta) {
        double step = speed * delta;
        if(step > 0) alpha = (int) - step;
    }
}

