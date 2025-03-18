package org.hxnry.rsp.watcher_server.api.util;

/**
 * Created with IntelliJ IDEA.h
 * User: Purple
 * Date: 2/3/2016
 * Time: 10:09 AM
 */
public class Skills {

    public enum Skill {
        Attack(0),
        Defence(1),
        Strength(2),
        Hitpoints(3),
        Ranged(4),
        Prayer(5),
        Magic(6),
        Cooking(7),
        Woodcutting(8),
        Flecthing(9),
        Fishing(10),
        Firemaking(11),
        Crafting(12),
        Smithing(13),
        Mining(14),
        Herblore(15),
        Agility(16),
        Thieving(17),
        Slayer(18),
        Farming(19),
        Runecraft(20),
        Hunter(21),
        Construction(22);
        int ID;

        Skill(int id) {
            this.ID = id;
        }

        public int getID() {
            return ID;
        }

        public static String getName(int id) {
            for (Skill q : Skill.values()) {
                if (q.getID() == id) {
                    return q.toString();
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    public static int experienceAtLevel(int level) {
        double total = 0;
        for (int i = 1; i < level; i++) {
            total += Math.floor(i + 300 * Math.pow(2, i / 7.0));
        }
        return (int) Math.floor(total / 4);
    }

}