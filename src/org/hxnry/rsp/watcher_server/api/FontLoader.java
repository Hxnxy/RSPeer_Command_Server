package org.hxnry.rsp.watcher_server.api;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class FontLoader
{
    private ResourceLoader loader;

    public FontLoader(String fontFilePath)
    {
        loader = new ResourceLoader(fontFilePath);
    }

    public Font getFont(int fontStyle, float fontSize) throws FontFormatException, IOException, URISyntaxException
    {
        Font font = Font.createFont(Font.TRUETYPE_FONT, loader.getResource());

        font = font.deriveFont(fontStyle, fontSize);

        return font;
    }
}
