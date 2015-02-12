package com.oltpbenchmark.benchmarks.eve;

public class EveUtil {
    /**
     * Return the number of contestants to use for the given scale factor
     * @param scaleFactor
     */
    public static int getScaledNumShips(double scaleFactor) {
        int min_contestants = 1;
        int max_contestants = EveConstants.NUM_SHIPS;

        int num_contestants = (int)Math.round(EveConstants.NUM_SHIPS * scaleFactor);
        if (num_contestants < min_contestants) num_contestants = min_contestants;
        if (num_contestants > max_contestants) num_contestants = max_contestants;

        return (num_contestants);
    }
}
