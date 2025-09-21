package Client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class GameFrame extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private BufferedReader in;
    private PrintWriter out;
    private String role;
    private boolean gameOver = false;
    private String playerName;

    public GameFrame(Socket socket, String playerName) {
        this.playerName = playerName;
        setTitle("Tic Tac Toe - " + playerName);
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::listenServer).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // tạo bàn cờ
        int size = 80;
        int startX = 60, startY = 60;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 28));
                buttons[i][j].setBounds(startX + j * size, startY + i * size, size, size);
                int r = i, c = j;
                buttons[i][j].addActionListener(e -> {
                    if (!gameOver && buttons[r][c].getText().equals("")) {
                        out.println("MOVE:" + r + "," + c);
                    }
                });
                add(buttons[i][j]);
            }

        // nút chơi lại
        JButton resetBtn = new JButton("Chơi lại");
        resetBtn.setBounds(60, 320, 100, 40);
        resetBtn.addActionListener(e -> out.println("RESET"));
        add(resetBtn);

        // nút xem lịch sử
        JButton historyBtn = new JButton("Xem lịch sử");
        historyBtn.setBounds(200, 320, 120, 40);
        historyBtn.addActionListener(e -> {
            try {
                Socket socketHistory = new Socket("127.0.0.1", 12345);
                PrintWriter outHistory = new PrintWriter(socketHistory.getOutputStream(), true);
                outHistory.println("HISTORY:" + playerName);
                new HistoryFrame(socketHistory).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể lấy lịch sử!");
            }
        });
        add(historyBtn);
    }

    private void listenServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("Start")) {
                    String[] parts = line.split(" ");
                    role = parts[1];
                    setTitle(getTitle() + " - Role: " + role);
                } else if (line.startsWith("YourMove")) {
                    JOptionPane.showMessageDialog(this, "Lượt của bạn!");
                } else if (line.startsWith("ValidMove")) {
                    String[] parts = line.split(" ")[1].split(",");
                    int r = Integer.parseInt(parts[0]), c = Integer.parseInt(parts[1]);
                    buttons[r][c].setText(role);
                    buttons[r][c].setForeground(role.equals("X") ? Color.BLACK : Color.RED);
                } else if (line.startsWith("OpponentMove")) {
                    String[] parts = line.split(" ")[1].split(",");
                    int r = Integer.parseInt(parts[0]), c = Integer.parseInt(parts[1]);
                    String opp = role.equals("X") ? "O" : "X";
                    buttons[r][c].setText(opp);
                    buttons[r][c].setForeground(opp.equals("X") ? Color.BLACK : Color.RED);
                } else if (line.startsWith("Win:")) {
                    gameOver = true;
                    String winner = line.substring(4);
                    JOptionPane.showMessageDialog(this, winner + " thắng!");
                } else if (line.startsWith("Lose:")) {
                    gameOver = true;
                    String loser = line.substring(5);
                    JOptionPane.showMessageDialog(this, loser + " thua!");
                } else if (line.equals("Draw")) {
                    gameOver = true;
                    JOptionPane.showMessageDialog(this, "Hòa!");
                } else if (line.equals("ResetBoard")) {
                    resetBoard();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetBoard() {
        gameOver = false;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");
        if (role.equals("X")) JOptionPane.showMessageDialog(this, "Lượt của bạn!");
    }
}
