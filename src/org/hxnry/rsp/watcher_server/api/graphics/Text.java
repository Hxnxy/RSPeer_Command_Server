package org.hxnry.rsp.watcher_server.api.graphics;

import java.awt.*;

/**
 * @author Hxnry
 * @since December 09, 2016
 */
public class Text {

    public String message = "";
    public int size;
    public Color color;

    public Text(String message, int size, Color color) {
        this.message = message;
        this.size = size;
        this.color = color;
    }

    public Text(String message, Color color) {
        this(message, -1, color);
    }

    public Text(String message) {
        this(message, -1, Color.WHITE);
    }

    public Color getColor() {
        return color;
    }

    public String getMessage() {
        return message;
    }

    public int getSize() {
        return size;
    }

    public int getLength(Graphics2D graphics2D) {
        if(getMessage().isEmpty()) {
            return -1;
        }
        return (int) graphics2D.getFontMetrics().getStringBounds(getMessage(), graphics2D).getWidth();
    }
}

