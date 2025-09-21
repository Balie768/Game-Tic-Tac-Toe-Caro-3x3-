package Server;

import java.util.*;

public class MatchHistory {
    private static List<String[]> history = new ArrayList<>();

    public static synchronized void addMatch(String p1, String p2, String winner) {
        history.add(new String[]{String.valueOf(history.size() + 1), p1, p2, winner});
    }

    public static synchronized List<String[]> getHistoryOf(String player) {
        List<String[]> result = new ArrayList<>();
        for (String[] match : history) {
            if (match[1].equals(player) || match[2].equals(player)) {
                result.add(match);
            }
        }
        return result;
    }
}
