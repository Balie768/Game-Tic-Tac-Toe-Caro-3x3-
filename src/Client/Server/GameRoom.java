package Server;

import java.io.*;
import java.net.Socket;

public class GameRoom implements Runnable {

    private final Socket s1, s2;
    private BufferedReader in1, in2;
    private PrintWriter out1, out2;

    private final String name1, name2;

    private String[][] board = new String[3][3];
    private boolean turnX = true;
    private boolean gameOver = false;

    public GameRoom(Socket p1, Socket p2, String n1, String n2) {
        this.s1 = p1;
        this.s2 = p2;
        this.name1 = n1;
        this.name2 = n2;
    }

    @Override
    public void run() {

        try {
            in1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            in2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
            out1 = new PrintWriter(s1.getOutputStream(), true);
            out2 = new PrintWriter(s2.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startNewGame();

        new Thread(() -> listen(in1, "X")).start();
        new Thread(() -> listen(in2, "O")).start();
    }

    private void listen(BufferedReader in, String role) {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                handleMessage(msg, role);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void startNewGame() {

        board = new String[3][3];
        gameOver = false;
        turnX = true;

        out1.println("ResetBoard");
        out2.println("ResetBoard");

        out1.println("Start X");
        out2.println("Start O");

        out1.println("YourMove");
    }

    private synchronized void handleMessage(String msg, String role) {

        PrintWriter outCur = role.equals("X") ? out1 : out2;
        PrintWriter outOpp = role.equals("X") ? out2 : out1;

        if (msg.equals("RESET")) {
            startNewGame();
            return;
        }

        if (msg.startsWith("MOVE:")) {

            if (gameOver) return;

            if ((turnX && !role.equals("X")) || (!turnX && !role.equals("O")))
                return;

            String[] p = msg.substring(5).split(",");
            int r = Integer.parseInt(p[0]);
            int c = Integer.parseInt(p[1]);

            if (board[r][c] != null) return;

            board[r][c] = role;

            outCur.println("ValidMove " + r + "," + c);
            outOpp.println("OpponentMove " + r + "," + c);

            if (checkWin(role)) {
                gameOver = true;

                String winner = role.equals("X") ? name1 : name2;
                String loser  = role.equals("X") ? name2 : name1;

                // 🚀🚀 GỬI ĐÚNG DỮ LIỆU 3 PHẦN
                outCur.println("Win:" + winner + ":" + loser);
                outOpp.println("Lose:" + winner + ":" + loser);

                MatchHistory.addMatch(name1, name2, winner);
                return;
            }

            if (isFull()) {
                gameOver = true;
                out1.println("Draw");
                out2.println("Draw");
                MatchHistory.addMatch(name1, name2, "Hòa");
                return;
            }

            turnX = !turnX;

            if (turnX) out1.println("YourMove");
            else out2.println("YourMove");
        }
    }

    private boolean checkWin(String r) {
        for (int i = 0; i < 3; i++)
            if (r.equals(board[i][0]) && r.equals(board[i][1]) && r.equals(board[i][2]))
                return true;

        for (int j = 0; j < 3; j++)
            if (r.equals(board[0][j]) && r.equals(board[1][j]) && r.equals(board[2][j]))
                return true;

        return (r.equals(board[0][0]) && r.equals(board[1][1]) && r.equals(board[2][2])) ||
               (r.equals(board[0][2]) && r.equals(board[1][1]) && r.equals(board[2][0]));
    }

    private boolean isFull() {
        for (var row : board)
            for (var cell : row)
                if (cell == null) return false;
        return true;
    }
}
