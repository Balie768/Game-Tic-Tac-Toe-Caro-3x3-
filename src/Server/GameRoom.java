package Server;

import java.io.IOException;

public class GameRoom extends Thread {
    private ClientHandler playerX;
    private ClientHandler playerO;
    private char[][] board = new char[3][3];
    private char currentTurn = 'X';

    public GameRoom(ClientHandler p1, ClientHandler p2) {
        this.playerX = p1;
        this.playerO = p2;
    }

    @Override
    public void run() {
        try {
            playerX.send("Start X " + playerO.getPlayerName());
            playerO.send("Start O " + playerX.getPlayerName());

            while (true) {
                ClientHandler current = (currentTurn == 'X') ? playerX : playerO;
                ClientHandler opponent = (currentTurn == 'X') ? playerO : playerX;

                current.send("YourMove");
                String move = current.receive();
                if (move == null) break;

                String[] parts = move.split(",");
                int r = Integer.parseInt(parts[0]);
                int c = Integer.parseInt(parts[1]);

                if (board[r][c] == '\0') {
                    board[r][c] = currentTurn;
                    current.send("ValidMove " + r + "," + c);
                    opponent.send("OpponentMove " + r + "," + c);

                    if (checkWin(currentTurn)) {
                        current.send("Win");
                        opponent.send("Lose");
                        Storage.saveMatch(playerX.getPlayerName(), playerO.getPlayerName(), currentTurn);
                        break;
                    } else if (isDraw()) {
                        current.send("Draw");
                        opponent.send("Draw");
                        Storage.saveMatch(playerX.getPlayerName(), playerO.getPlayerName(), 'D');
                        break;
                    }
                    currentTurn = (currentTurn == 'X') ? 'O' : 'X';
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkWin(char p) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == p && board[i][1] == p && board[i][2] == p) return true;
            if (board[0][i] == p && board[1][i] == p && board[2][i] == p) return true;
        }
        return (board[0][0] == p && board[1][1] == p && board[2][2] == p) ||
               (board[0][2] == p && board[1][1] == p && board[2][0] == p);
    }

    private boolean isDraw() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == '\0') return false;
        return true;
    }
}
