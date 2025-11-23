// Server/MatchHistory.java
package Server;

import java.util.*;

public class MatchHistory {
    private static List<String[]> history = new ArrayList<>();

    // Lưu 1 trận: [id, p1, p2, winner]
    public static synchronized void addMatch(String p1, String p2, String winner) {
        history.add(new String[]{String.valueOf(history.size() + 1), p1, p2, winner});
    }

    // Lịch sử của 1 người chơi
    public static synchronized List<String[]> getHistoryOf(String player) {
        List<String[]> result = new ArrayList<>();
        for (String[] match : history) {
            if (match[1].equals(player) || match[2].equals(player)) {
                result.add(match);
            }
        }
        return result;
    }

    // Bảng xếp hạng: [hạng, tên, thắng, thua, hòa, tổng]
    public static synchronized List<String[]> getRanking() {
        Map<String, int[]> stats = new HashMap<>();
        // int[0]=win, [1]=lose, [2]=draw

        for (String[] match : history) {
            String p1 = match[1];
            String p2 = match[2];
            String winner = match[3];

            stats.putIfAbsent(p1, new int[3]);
            stats.putIfAbsent(p2, new int[3]);

            if ("Hòa".equalsIgnoreCase(winner)) {
                stats.get(p1)[2]++;
                stats.get(p2)[2]++;
            } else {
                String loser = winner.equals(p1) ? p2 : p1;
                stats.get(winner)[0]++;
                stats.get(loser)[1]++;
            }
        }

        List<Map.Entry<String, int[]>> list = new ArrayList<>(stats.entrySet());
        // Sắp xếp theo số trận thắng giảm dần, rồi tổng trận
        list.sort((a, b) -> {
            int[] sa = a.getValue();
            int[] sb = b.getValue();
            int winDiff = sb[0] - sa[0];
            if (winDiff != 0) return winDiff;
            int totalA = sa[0] + sa[1] + sa[2];
            int totalB = sb[0] + sb[1] + sb[2];
            return totalB - totalA;
        });

        List<String[]> ranking = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<String, int[]> e : list) {
            String name = e.getKey();
            int win = e.getValue()[0];
            int lose = e.getValue()[1];
            int draw = e.getValue()[2];
            int total = win + lose + draw;
            ranking.add(new String[]{
                    String.valueOf(rank++),
                    name,
                    String.valueOf(win),
                    String.valueOf(lose),
                    String.valueOf(draw),
                    String.valueOf(total)
            });
        }
        return ranking;
    }
}
