package org.hxnry.rsp.watcher_server.api.util;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.h
 * User: Purple
 * Date: 10/20/2015
 * Time: 6:28 AM
 */
public class StatsDrawer {

    private int statRows;

    private boolean error;

    private String targetName;
    private String enemyName;

    private HighScores targetHighScores;
    private HighScores enemyHighScores;

    public HighScores getLocal() {
        return this.targetHighScores;
    }

    public HighScores getEnemy() {
        return this.enemyHighScores;
    }

    private final Font NORMAL_FONT = new Font("Helvetica", Font.PLAIN, 11);
    private final Font STATS_FONT = new Font("Helvetica", Font.BOLD, 11);

    public int getCombatLevel(HighScores highscores) {

        int prayer = highscores.getLevel(Skills.Skill.Prayer);
        int hitpoints = highscores.getLevel(Skills.Skill.Hitpoints);
        int attack = highscores.getLevel(Skills.Skill.Attack);
        int strength = highscores.getLevel(Skills.Skill.Strength);
        int defence = highscores.getLevel(Skills.Skill.Defence);
        double base = 0.25 * (Math.floor(prayer / 2) + (hitpoints + defence));
        base += 0.325D * (attack + strength);

        return (int) base;
    }

    public StatsDrawer(String targetName, String enemyName) {
        this.targetName = targetName;
        this.enemyName = enemyName;
        new Thread(() -> {
            this.targetHighScores = HighScores.lookUp(targetName);
            this.enemyHighScores = HighScores.lookUp(enemyName);
            if(this.targetHighScores == null || this.enemyHighScores == null) {
                this.error = true;
            }
        }).start();
    }

    public void drawStats(Graphics g, int alpha, int x, int enemyX, int y) {

        statRows = 5;

        g.setFont(NORMAL_FONT);

        if(targetHighScores != null && enemyHighScores != null) {

            int[] localStats = new int[] {
                    targetHighScores.getLevel(Skills.Skill.Hitpoints),
                    targetHighScores.getLevel(Skills.Skill.Attack),
                    targetHighScores.getLevel(Skills.Skill.Strength),
                    targetHighScores.getLevel(Skills.Skill.Defence),
            };

            int[] enemyStats = new int[] {
                    enemyHighScores.getLevel(Skills.Skill.Hitpoints),
                    enemyHighScores.getLevel(Skills.Skill.Attack),
                    enemyHighScores.getLevel(Skills.Skill.Strength),
                    enemyHighScores.getLevel(Skills.Skill.Defence),
            };

            //drawText(g, Color.ORANGE, 10, "CMB (" + getCombatLevel(highscores) + ")", STATS_BASE_X + 5, STATS_BASE_Y + 14);
            //drawText(g, Color.ORANGE, 9, "You ", STATS_BASE_X + 73, STATS_BASE_Y + 14);
            //drawText(g, Color.ORANGE, 9, "Them", STATS_BASE_X + 96, STATS_BASE_Y + 14);

            for(int i = 0; i < localStats.length; i++) {
                int stat = localStats[i];
                int statE = enemyStats[i];
                int offset = 15;
                drawText(g, alpha, Color.white, 10, getSkill(i), x, y + (offset * i));
                drawText(g, alpha, STATS_FONT, (stat == statE ? Color.yellow : (stat > statE ? Color.green : Color.white)), "" + stat, x + 75, y + (offset * i));
                drawText(g, alpha, Color.white, 10, getSkill(i), enemyX, y + (offset * i));
                drawText(g, alpha, STATS_FONT, (statE == stat ? Color.yellow : (statE > stat ? Color.green : Color.white)), "" + statE,  enemyX + 75, y + (offset * i));
            }
        }
    }

    public String getSkill(int number) {
        switch (number) {
            case 0:
                return "Hitpoints:";
            case 1:
                return "Attack:";
            case 2:
                return "Strength:";
            case 3:
                return "Defence:";
        }
        return null;
    }

    private void drawText(Graphics graphics, int alpha, Color color, int size, String msg, int x, int y) {
        final Font FONT = new Font("Helvetica", Font.PLAIN, size);
        graphics.setFont(FONT);
        Color back = new Color(0, 0, 0, alpha);
        graphics.setColor(back);
        graphics.drawString(msg, x + 1, y - 1);
        graphics.drawString(msg, x - 1, y + 1);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        graphics.drawString(msg, x, y);
    }

    private void drawText(Graphics graphics, int alpha, Font font, Color color, String msg, int x, int y) {
        graphics.setFont(font);
        Color back = new Color(0, 0, 0, alpha);
        graphics.setColor(back);
        graphics.drawString(msg, x + 1, y - 1);
        graphics.drawString(msg, x - 1, y + 1);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        graphics.drawString(msg, x, y);
    }


    public boolean isLoaded() {
        return enemyHighScores != null && targetHighScores != null;
    }

    public boolean hasError() {
        return error;
    }
}
