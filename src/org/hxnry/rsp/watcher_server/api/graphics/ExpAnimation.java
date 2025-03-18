package org.hxnry.rsp.watcher_server.api.graphics;

import java.awt.*;

/**
 * @author Hxnry
 * @since November 08, 2016
 */
public class ExpAnimation {

    private Color color = new Color(65, 142, 201);
    private
    int expGained;
    double yLoc = 220;
    double alpha = 255;

    public ExpAnimation(int expGained) {
        this.expGained = expGained;
    }

    public boolean draw(Graphics graphics, double delta) {
        yLoc -= delta * 66;
        alpha -= delta * 90;
        String text = "" + expGained;
        drawText(graphics, Font.CENTER_BASELINE, Color.WHITE, 13, "+", text, 500 - graphics.getFontMetrics().stringWidth(text), (int) yLoc);
        if(yLoc <= 38) {
            return false;
        }
        return true;
    }

    public void drawText(Graphics graphics, int style, Color textColor, int size, String before, String after, int x, int y) {
        final Font FONT = new Font("Helvetica", style, size);
        graphics.setFont(FONT);
        Color back = new Color(0, 0, 0, Math.max(0, Math.min(255, (int) alpha)));
        Color customColor = color;
        int width = graphics.getFontMetrics().stringWidth(before);
        graphics.setColor(back);
        graphics.drawString(before, x + 1, y + 1);
        graphics.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), Math.max(0, Math.min(255, (int) alpha))));
        graphics.drawString(before, x, y);
        graphics.setColor(back);
        graphics.drawString(after, width + x + 1, y + 1);
        graphics.setColor(new Color(customColor.getRed(), customColor.getGreen(), customColor.getBlue(), Math.max(0, Math.min(255, (int) alpha))));
        graphics.drawString(after, width + x, y);
    }

    public void setColor(Color col) {
        color = new Color(col.getRed(), col.getGreen(), col.getBlue());
    }

    public int getExpGained() {
        return expGained;
    }
}

