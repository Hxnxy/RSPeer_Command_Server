package org.hxnry.rsp.watcher_server.api.graphics;

import org.hxnry.rsp.watcher_server.api.util.ImageDecoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SettingsIconDrawer {

    private Rectangle bounds;
    public int x = 731;
    public int y = 80;
    public BufferedImage ENCODED_ICON = ImageDecoder.decodeToImage("LxZPxAbSCpHfuAmMTGNPh8bZNhxulbcFralr0rtJrVib5GovXVfx6mwcaoADGNN.NM2C6fDH3bxE5jkOKAuHNYtp1VbAqNCcjQYdoCU8HsTXcFhZqwlqPi8v/GGGH/8W7PqGGNCcp4cD1P0T1H/E5.DkTVXOU5X25.SlmAzKh2ZcLJNvp2QrJkM565SpcIMJSCbOhpLr3WoMLpCCehhruOOMup2W6KcH7S6G43Bo8iR8TaP6KpanIf4m25oaB6eOOOANrNuzoS0q8DbGzQ2ULX50wfsHDh/AflOKOKilER4kqgV79/LCJiRrI2XMK8UNS8lVFDb72B8Ed9AUvra4gZk0ycIKnDJ.2P3nOOAOAn/SWZH7ReC2ZDd2YYBWcG9ELvnknPtHXUzo6ugPweXrwpwPxh3wcKaebqe9PiKKTr/F//EiSKYfChPOoomE../y.FYkjaFmYjcT9DZ2sJ2GcBRLELnbYyVB1CS4K4OvDRhbrXQFHIiBlhHMKplO8K9g1T9/1eoE7yUYMA5ltW5dxkhBjDIs3IfqhNw.ae6C76CdHC66zzaQ1DvJHLNI3cQQPgxMvMQQRwQRxkt90O");

    private BufferedImage getImageByUrl() {
        try {
            URL url = new URL("");
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SettingsIconDrawer() {
        this.bounds = new Rectangle(x, y, ENCODED_ICON.getWidth(), ENCODED_ICON.getHeight());
    }

    public void draw(Graphics graphics) {
        graphics.drawImage(ENCODED_ICON, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, getBufferedImage().getWidth(), getBufferedImage().getHeight());
    }

    public BufferedImage getBufferedImage() {
        return ENCODED_ICON;
    }
}

