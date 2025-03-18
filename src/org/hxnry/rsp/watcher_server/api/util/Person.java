package org.hxnry.rsp.watcher_server.api.util;

/**
 * @author Hxnry
 * @since September 12, 2018
 */
public class Person {

    public int[] levels = new int[24];
    public int[] experiences = new int[24];
    public int[] ranks = new int[24];
    public HighScores highScores = new HighScores();
    public String name = "";

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int[] getLevels() {
        return this.levels;
    }

    public int[] getExperiences() {
        return this.experiences;
    }

    public int[] getRanks() {
        return this.ranks;
    }

    public HighScores data() {
        return this.highScores;
    }

    @Override
    public String toString() {
        return "HighScore: " + getName()
                + " \n";
    }

    public void setHighScores(HighScores hs) {
        this.highScores = hs;
    }
}
