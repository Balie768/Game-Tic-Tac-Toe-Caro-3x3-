package Server;

import java.io.*;
import java.net.Socket;

public class GameRoom implements Runnable {
    private Socket p1, p2;
    private BufferedReader in1, in2;
    private PrintWriter out1, out2;
    private String name1, name2;
    private String[][] board = new String[3][3];
    private boolean turnX = true;

    public GameRoom(Socket p1, Socket p2, String name1, String name2) {
        this.p1 = p1;
        this.p2 = p2;
        this.name1 = name1;
        this.name2 = name2;
        try {
            in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            out1 = new PrintWriter(p1.getOutputStream(), true);
            out2 = new PrintWriter(p2.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        out1.println("Start X");
        out2.println("Start O");

        try {
            while (true) {
                String msg1 = in1.readLine();
                if (msg1 != null) handleMessage(msg1, out1, out2, "X");
                String msg2 = in2.readLine();
                if (msg2 != null) handleMessage(msg2, out2, out1, "O");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(String msg, PrintWriter outCur, PrintWriter outOpp, String role) {
        try {
            if (msg.startsWith("MOVE:")) {
                String[] parts = msg.substring(5).split(",");
                int r = Integer.parseInt(parts[0]), c = Integer.parseInt(parts[1]);
                if (board[r][c] == null) {
                    board[r][c] = role;
                    outCur.println("ValidMove " + r + "," + c);
                    outOpp.println("OpponentMove " + r + "," + c);
                    if (checkWin(role)) {
                        String winnerName = role.equals("X") ? name1 : name2;
                        String loserName = role.equals("X") ? name2 : name1;
                        outCur.println("Win:" + winnerName);
                        outOpp.println("Lose:" + loserName);
                        MatchHistory.addMatch(name1, name2, winnerName);
                    } else if (isBoardFull()) {
                        outCur.println("Draw");
                        outOpp.println("Draw");
                        MatchHistory.addMatch(name1, name2, "Hòa");
                    }
                }
            } else if (msg.equals("RESET")) {
                board = new String[3][3];
                outCur.println("ResetBoard");
                outOpp.println("ResetBoard");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkWin(String role) {
        for (int i = 0; i < 3; i++)
            if (role.equals(board[i][0]) && role.equals(board[i][1]) && role.equals(board[i][2]))
                return true;
        for (int j = 0; j < 3; j++)
            if (role.equals(board[0][j]) && role.equals(board[1][j]) && role.equals(board[2][j]))
                return true;
        return role.equals(board[0][0]) && role.equals(board[1][1]) && role.equals(board[2][2])
            || role.equals(board[0][2]) && role.equals(board[1][1]) && role.equals(board[2][0]);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == null) return false;
        return true;
    }
}
