package org.hxnry.rsp.watcher_server.api.graphics;


import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * @author Hxnry
 * @since November 07, 2016
 */
public class PaintWindow implements Paintable, MouseMotionListener {

    private Color background = new Color(147,112,219, 150);
    private final Color BACKGROUND_GRAY = new Color(0, 0, 0, 200);
    private final int BASE_HEIGHT = 28;
    private int BASE_WIDTH = 225;
    public Rectangle titleRectangle = new Rectangle(0, 0, BASE_WIDTH - 6, 21);
    protected int upX = 287, upY = 344, dragUpX, dragUpY;
    private String title = "null";
    private int titleSize = 14;
    private Text[] titleText;
    private int rows = 0;
    private int arcW = 5;
    private int arcH = 5;

    public PaintWindow(int length, Text... titleText) {
        this.rows = length;
        this.titleText = titleText;
    }

    public int getUpX() {
        return upX;
    }

    public int getUpY() {
        return upY;
    }

    public PaintWindow(String title) {
        this(1, new Text(title, Color.WHITE));
    }

    public void setTitle(String title) {
       setTitle(new Text(title));
        this.title = title;
    }

    public void setTitle(Text... text) {
        this.titleText = text;
    }

    public void setLocation(int upX, int upY) {
        this.upX = upX;
        this.upY = upY;
    }

    public void setRows(int num) {
        this.rows = num;
    }

    public int getY(int row) {
        return upY + 20 + (20 * row);
    }

    public int getX() {
        return upX + 7;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getColor() {
        return this.background;
    }

    public void display(Graphics2D graphics2D, String text, int index) {
        Drawing.drawText(graphics2D, Font.CENTER_BASELINE, Color.WHITE, 10, text, getX(), getY(index));
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

    private Point getCenter(Graphics graphics, int style, int size, String string, Rectangle rectangle) {
        final Font FONT = new Font("Helvetica", style, size);
        int centerX = (int) rectangle.getWidth() / 2;
        int centerY = (int) rectangle.getHeight() / 2;
        graphics.setFont(FONT);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(string, graphics).getBounds();
        Font font = graphics.getFont();
        FontRenderContext renderContext = ((Graphics2D) graphics).getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(renderContext, string);
        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
        int textX = centerX - stringBounds.width/2;
        int textY = centerY - visualBounds.height/2 - visualBounds.y;
        return new Point(textX, textY + 5)
                ;    }

    public void display(Graphics2D g, int index, Text... texts) {
        Drawing.drawMultiText(g, Font.CENTER_BASELINE, 10, texts, getX(), getY(index));
    }

    @Override
    public void render(Graphics2D graphics) {
        graphics.setColor(BACKGROUND_GRAY);
        graphics.fillRoundRect(upX, upY, BASE_WIDTH, BASE_HEIGHT + (15 * rows), arcW, arcH);
        graphics.setColor(background);
        graphics.drawRoundRect(upX, upY, BASE_WIDTH, BASE_HEIGHT + (15 * rows), arcW, arcH);
        graphics.setColor(background);
        graphics.fillRect(upX + 4, upY + 4, BASE_WIDTH - 7, 21);
        titleRectangle.setLocation(upX + 4, upY + 4);
        //Drawing.drawCenteredString(graphics, Font.BOLD, background, titleSize, title, titleRectangle);
        //drawCenteredTitle(graphics, Font.BOLD, titleSize, titleText, titleRectangle);
        ///Point center = getCenter(graphics, Font.CENTER_BASELINE, 12, title, titleRectangle);
        //drawText(graphics, Font.CENTER_BASELINE, Color.WHITE, 14, title, center.x, getY(0));
        drawCenteredTitle(graphics, Font.BOLD, 14, titleText, titleRectangle);
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

    @Override
    public void mouseDragged(MouseEvent e) {
        final Point mouse = e.getPoint();
        if(titleRectangle.contains(mouse)) {
            upX = (int) (mouse.getX() - dragUpX);
            upY = (int) (mouse.getY() - dragUpY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        final Point mouse = e.getPoint();
        if(titleRectangle.contains(mouse)) {
            int x = mouse.x;
            int y = mouse.y;
            dragUpX = x - upX;
            dragUpY = y - upY;
        }
    }

    public int getWidth() {
        return titleRectangle.width;
    }

    private void drawCenteredTitle(Graphics2D graphics, int style, int size, Text[] texts, Rectangle rectangle) {
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
            Drawing.drawText(graphics, font.getStyle(), text.getColor(), font.getSize(), message, offset + currentWidth, rectangle.y + textY + 1);
            currentWidth += visualBounds.width + 7;
        }
    }

    public void setWidth(int i) {
        this.BASE_WIDTH = i;
        this.titleRectangle = new Rectangle(0, 0, BASE_WIDTH - 6, 21);
    }
}

