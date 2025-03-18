package org.hxnry.rsp.watcher_server.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.h
 * User: Purple
 * Date: 2/3/2016
 * Time: 10:10 AM
 */
public class HighScores {

    public static final Vector<Person> cachedHighscores = new Vector<>();

    private static String HIGHSCORE_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=";
    public int[] levels;
    public long[] experiences;
    public int[] ranks;

    public HighScores() {
        this.levels = new int[24];
        this.experiences = new long[24];
        this.ranks = new int[24];
    }

    /**
     * Grabs the Skill level of a particular skill via the skill's ID.
     *
     * @param skillID
     *            Skill's ID. <i>Ranges from 1..24</i>
     * @return Skill level of the specified skill.
     */
    public int getLevel(final int skillID) {
        return levels[skillID + 1];
    }

    /**
     * Value is rounded up to the lowest whole.
     *
     * <p>
     * <i> Example - 4.5 returns 4. </i>
     * </p>
     *
     * @param skillID
     *            Skill's ID. <i>Ranges from 1..24</i>
     * @return Total experienced obtained from the specified skill.
     */
    public int getExperience(final int skillID) {
        return (int) experiences[skillID + 1];
    }

    /**
     * Obtains the player's HS rank of a particular skill.
     *
     * @param skillID
     *            Skill's ID. <i>Ranges from 1..24</i>
     * @return Player's rank on HS with the specified skill.
     */
    public int getRank(final int skillID) {
        return ranks[skillID + 1];
    }

    /**
     * Obtains the player's specified skill level.
     *
     * @param skill
     *            Skill object that takes int id as an argument.
     * @return Level of the specified skill.
     */
    public int getLevel(final Skills.Skill skill) {
        return levels[skill.getID() + 1];
    }

    /**
     * Obtains the player's total experience with a particular skill.
     *
     * @param skill
     *            Skill object that takes an id of type Integer as an argument.
     * @return Total experience obtained with the specified skill.
     */
    public int getExperience(final Skills.Skill skill) {
        return (int) experiences[skill.getID() + 1];
    }

    /**
     * Obtains the player's HS rank of a particular skill.
     *
     * @param skill
     *            Skill object that takes an id of type Integer as an argument.
     * @return Player's rank on HS with the specified skill.
     */
    public int getRank(final Skills.Skill skill) {
        return ranks[skill.getID() + 1];
    }

    /**
     * Obtains the player's total level which equivalent to all the skill levels added together.
     *
     * @return Total level (see the aforementioned description).
     */
    public int getTotalLevel() {
        return levels[0];
    }

    /**
     * Obtain the player's total experienced gained.
     *
     * @return Total experience gained.
     */
    public long getTotalExperience() {
        return experiences[0];
    }

    /**
     * Obtain the player's overall highscore rank (based off total level).
     *
     * @return
     */
    public int getOverallRank() {
        return ranks[0];
    }

    /**
     * Searches through the Runescape HS for a profile with the
     * specified name.
     *
     * @param name
     *            Player's name to search for in the HS.
     * @return HS object which contains the levels, experiences and ranks of the specified player.
     * 		   If the player profile does not exist the default HS values are sent, not a null reference.
     */
    public static HighScores lookUp(String name) {
        HighScores scores = new HighScores();
        Person person = new Person(name);
        try {
            List<Person> clone = HighScores.cachedHighscores;
            if(!clone.isEmpty()) {
                Optional<Person> pers = clone.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
                if(pers.isPresent()) {
                    return pers.get().data();
                }
            }
            URL url = new URL(HIGHSCORE_URL + name);
            URLConnection urlConnection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            int offset = 0;
            while ((line = br.readLine()) != null) {
                if(offset > 0) {
                    if(offset < 24) {

                        if(line != null && line.contains(",")) {
                            continue;
                        }

                        String temp = "," + line;
                        String[] data = temp.split(",");

                        int rank = Integer.valueOf(data[1]);
                        int level = Integer.valueOf(data[2]);
                        int exp = Integer.valueOf(data[3]);

                        scores.ranks[offset] = rank;
                        scores.levels[offset] = level;
                        scores.experiences[offset] = exp;
                    }
                }
                offset++;
            }
            person.setHighScores(scores);
            HighScores.cachedHighscores.add(person);
            System.out.println("New highscore user detected: " + person.name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return person.data();
    }
}
