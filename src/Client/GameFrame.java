// Client/GameFrame.java
package Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class GameFrame extends JFrame {
    private final JButton[][] buttons = new JButton[3][3];
    private BufferedReader in;
    private PrintWriter out;
    private String role;           
    private boolean gameOver = false;
    private boolean myTurn = false;
    private int remainingSeconds = 0;
    private Timer turnTimer;

    private final String playerName;
    private final Socket socket;

    private JLabel statusLabel;

    public GameFrame(Socket socket, String playerName, BufferedReader in, PrintWriter out) {
        this.playerName = playerName;
        this.socket = socket;
        this.in = in;
        this.out = out;

        setTitle("CỜ CARO 3X3");
        setSize(640, 460);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopTurnTimer();
                try { socket.close(); } catch (IOException ex) {}
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(0, 0, 10, 0));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(6, 15, 6, 15));
        root.add(header, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("CỜ CARO 3X3", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(lblTitle, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(127, 140, 141));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setPreferredSize(new Dimension(110, 30));
        header.add(btnLogout, BorderLayout.EAST);

        statusLabel = new JLabel("Đang chờ bắt đầu ván đấu...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(statusLabel, BorderLayout.SOUTH);

        btnLogout.addActionListener(e -> {
            stopTurnTimer();
            try { socket.close(); } catch (IOException ex) {}
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(15, 70, 10, 70));
        root.add(center, BorderLayout.CENTER);

        JPanel board = new JPanel(new GridLayout(3, 3, 4, 4));
        board.setBackground(Color.WHITE);
        center.add(board, BorderLayout.CENTER);

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 42));
                btn.setFocusPainted(false);
                btn.setBackground(Color.WHITE);
                btn.setBorder(new LineBorder(Color.BLACK, 2));
                int r = i, c = j;

                btn.addActionListener(e -> {
                    if (!gameOver && myTurn && btn.getText().isEmpty()) {
                        out.println("MOVE:" + r + "," + c);
                        myTurn = false;
                        stopTurnTimer();
                        statusLabel.setText("Đang chờ đối thủ đánh...");
                    }
                });

                buttons[i][j] = btn;
                board.add(btn);
            }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        bottom.setBorder(new EmptyBorder(10, 10, 0, 10));
        root.add(bottom, BorderLayout.SOUTH);

        JButton btnReset   = createFlatButton("Chơi lại",      new Color(46, 204, 113), Color.WHITE);
        JButton btnHistory = createFlatButton("Lịch sử đấu",   new Color(231, 76, 60),  Color.WHITE);
        JButton btnRanking = createFlatButton("Bảng xếp hạng", new Color(241, 196, 15), Color.BLACK);

        bottom.add(btnReset);
        bottom.add(btnHistory);
        bottom.add(btnRanking);

        btnReset.addActionListener(e -> out.println("RESET"));

        btnHistory.addActionListener(e -> {
            try {
                Socket socketHistory = new Socket("127.0.0.1", 12345);
                PrintWriter outHistory = new PrintWriter(socketHistory.getOutputStream(), true);
                outHistory.println("HISTORY:" + playerName);
                new HistoryFrame(socketHistory).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể lấy lịch sử!");
            }
        });

        btnRanking.addActionListener(e -> {
            try {
                Socket socketRank = new Socket("127.0.0.1", 12345);
                PrintWriter outRank = new PrintWriter(socketRank.getOutputStream(), true);
                outRank.println("RANKING");
                new RankingFrame(socketRank).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể lấy bảng xếp hạng!");
            }
        });

        new Thread(this::listenServer).start();
    }

    private JButton createFlatButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(150, 32));
        return b;
    }

    private void listenServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {

                if (line.startsWith("Start")) {
                    role = line.split(" ")[1];
                    setTitle("CỜ CARO 3X3 (" + playerName + " - " + role + ")");
                }

                else if (line.startsWith("YourMove")) {
                    myTurn = true;
                    startTurnTimer();
                }

                else if (line.startsWith("ValidMove")) {
                    String[] p = line.split(" ")[1].split(",");

                    int r = Integer.parseInt(p[0]);
                    int c = Integer.parseInt(p[1]);

                    buttons[r][c].setText(role);
                    buttons[r][c].setForeground(role.equals("X")
                            ? new Color(46, 204, 113)
                            : new Color(231, 76, 60));
                }

                else if (line.startsWith("OpponentMove")) {
                    String[] p = line.split(" ")[1].split(",");

                    int r = Integer.parseInt(p[0]);
                    int c = Integer.parseInt(p[1]);

                    String opp = role.equals("X") ? "O" : "X";

                    buttons[r][c].setText(opp);
                    buttons[r][c].setForeground(opp.equals("X")
                            ? new Color(46, 204, 113)
                            : new Color(231, 76, 60));
                }

                // ================================
                // WIN — HIỆN ĐÚNG NGƯỜI CHƠI THẮNG
                // ================================
                else if (line.startsWith("Win:")) {
                    gameOver = true;
                    stopTurnTimer();
                    highlightWinningLine(role);

                    String[] info = line.split(":");
                    String winner = info[1];

                    JOptionPane.showMessageDialog(this,
                            winner + " đã thắng!");
                }

                // ================================
                // LOSE — HIỆN ĐÚNG NGƯỜI CHƠI THUA
                // ================================
                else if (line.startsWith("Lose:")) {
                    gameOver = true;
                    stopTurnTimer();

                    String opp = role.equals("X") ? "O" : "X";
                    highlightWinningLine(opp);

                    String[] info = line.split(":");
                    String loser = info[2];

                    JOptionPane.showMessageDialog(this,
                            loser + " đã thua!");
                }

                else if ("Draw".equals(line)) {
                    gameOver = true;
                    stopTurnTimer();
                    JOptionPane.showMessageDialog(this, "Hòa!");
                }

                else if ("ResetBoard".equals(line)) {
                    resetBoard();
                }
            }
        } catch (IOException ignored) {}
    }

    private void startTurnTimer() {
        stopTurnTimer();
        remainingSeconds = 30;
        statusLabel.setText("Lượt của bạn - Thời gian còn: " + remainingSeconds + "s");

        turnTimer = new Timer(1000, e -> {
            remainingSeconds--;
            if (remainingSeconds > 0) {
                statusLabel.setText("Lượt của bạn - Thời gian còn: " + remainingSeconds + "s");
            } else {
                stopTurnTimer();
                if (!gameOver && myTurn) {
                    statusLabel.setText("Hết giờ! Bạn đã quá 30s.");
                    myTurn = false;
                    out.println("TIMEOUT");
                }
            }
        });

        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
            turnTimer = null;
        }
        if (!gameOver && !myTurn) {
            statusLabel.setText("Đang chờ đối thủ đánh...");
        }
    }

    private void highlightWinningLine(String symbol) {
        for (int i = 0; i < 3; i++)
            if (symbol.equals(buttons[i][0].getText())
                    && symbol.equals(buttons[i][1].getText())
                    && symbol.equals(buttons[i][2].getText())) {
                paintWinCell(buttons[i][0]);
                paintWinCell(buttons[i][1]);
                paintWinCell(buttons[i][2]);
                return;
            }

        for (int j = 0; j < 3; j++)
            if (symbol.equals(buttons[0][j].getText())
                    && symbol.equals(buttons[1][j].getText())
                    && symbol.equals(buttons[2][j].getText())) {
                paintWinCell(buttons[0][j]);
                paintWinCell(buttons[1][j]);
                paintWinCell(buttons[2][j]);
                return;
            }

        if (symbol.equals(buttons[0][0].getText())
                && symbol.equals(buttons[1][1].getText())
                && symbol.equals(buttons[2][2].getText())) {
            paintWinCell(buttons[0][0]);
            paintWinCell(buttons[1][1]);
            paintWinCell(buttons[2][2]);
        }

        if (symbol.equals(buttons[0][2].getText())
                && symbol.equals(buttons[1][1].getText())
                && symbol.equals(buttons[2][0].getText())) {
            paintWinCell(buttons[0][2]);
            paintWinCell(buttons[1][1]);
            paintWinCell(buttons[2][0]);
        }
    }

    private void paintWinCell(JButton btn) {
        btn.setBackground(new Color(255, 220, 220));
        btn.setBorder(new LineBorder(Color.RED, 4, true));
    }

    private void resetBoard() {
        gameOver = false;
        myTurn = false;
        stopTurnTimer();

        statusLabel.setText("Đang chờ lượt mới...");

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setForeground(Color.BLACK);
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setBorder(new LineBorder(Color.BLACK, 2));
            }
    }
}
