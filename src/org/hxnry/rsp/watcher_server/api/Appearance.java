package org.hxnry.rsp.watcher_server.api;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScreenPosition;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.providers.RSItemDefinition;

import java.awt.*;

public class Appearance {

    int[] ids;
    int head = -1;
    int cape = -1;
    int amulet = -1;
    int body = -1;
    int weapon = -1;
    int offhand = -1;
    int gloves = -1;
    int boots = -1;
    int legs = -1;
    boolean aboveHead = true;
    Player player;

    public Appearance(Player player) {
        if(player == null) {
            this.ids = new int[9];
            this.head = 0;
            this.cape = 0;
            this.amulet = 0;
            this.weapon = 0;
            this.body = 0;
            this.offhand = 0;
            this.legs = 0;
            this.gloves = 0;
            this.boots = 0;
        } else {
            this.player = player;
            this.ids = player.getAppearance().getEquipmentIds();
            this.head = this.ids[0];
            this.cape = this.ids[1];
            this.amulet = this.ids[2];
            this.weapon = this.ids[3];
            this.body = this.ids[4];
            this.offhand = this.ids[5];
            this.legs = this.ids[7];
            this.gloves = this.ids[9];
            this.boots = this.ids[10];
        }
    }

    public int getWeapon() {
        return this.weapon;
    }

    public int getOffhand() {
        return this.offhand;
    }

    public String getName() {
        return player != null ? player.getName() : "{unknown}";
    }

    public void display(Graphics graphics) {

        if(aboveHead) {
            Position position = player.getPosition();
            position.outline(graphics);
            ScreenPosition screenPosition = Projection.fineToScreen(position.getX(), position.getY(), 25);
            if(screenPosition != null) {
                screenPosition.draw(graphics, "Hello");
            }
        }


        graphics.drawString(getName(), 7, 37);

        int offset = 0;
        for(int i = 0; i < this.ids.length; i++) {
            String desc = getDescriptor(i, this.ids[i]);
            if(desc.equalsIgnoreCase("")) continue;
            graphics.drawString(desc, 7, 51 + offset);
            offset+= 14;
        }
    }

    public String getName(int id) {
        RSItemDefinition item = Definitions.getItem(getWeapon());
        return item.getName();
    }

    private String getDescriptor(int index, int value) {
        int actualIndex = value - 512;
        if(actualIndex < 0) return "";
        switch (index) {
            case 0:
                RSItemDefinition head = Definitions.getItem(actualIndex);
                return "Head: " + head.getName() + " (id: " + value + ")";
            case 1:
                RSItemDefinition cape = Definitions.getItem(actualIndex);
                return "Cape: " + cape.getName() + " (id: " + value + ")";
            case 2:
                RSItemDefinition amulet = Definitions.getItem(actualIndex);
                return "Amulet: " + amulet.getName() + " (id: " + value + ")";
            case 3:
                RSItemDefinition weapon = Definitions.getItem(actualIndex);
                return "Weapon: " + weapon.getName() + " (id: " + value + ")";
            case 4:
                RSItemDefinition body = Definitions.getItem(actualIndex);
                return "Body: " + body.getName() + " (id: " + value + ")";
            case 5:
                RSItemDefinition offhand = Definitions.getItem(actualIndex);
                return "Off hand: " + offhand.getName() + " (id: " + value + ")";
            case 7:
                RSItemDefinition legs = Definitions.getItem(actualIndex);
                return "Legs: " + legs.getName() + " (id: " + value + ")";
            case 9:
                RSItemDefinition gloves = Definitions.getItem(actualIndex);
                return "Gloves: " + gloves.getName() + " (id: " + value + ")";
            case 10:
                RSItemDefinition boots = Definitions.getItem(actualIndex);
                return "Boots: " + boots.getName() + " (id: " + value + ")";


        }
        return "unknown[" + index + "] " + value;
    }
}
