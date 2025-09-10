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

    public GameFrame(Socket socket, String playerName) {
        setTitle("Tic Tac Toe - " + playerName);
        setSize(300, 300);
        setLayout(new GridLayout(3, 3));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::listenServer).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                int r = i, c = j;
                buttons[i][j].addActionListener(e -> out.println(r + "," + c));
                add(buttons[i][j]);
            }
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
                    JOptionPane.showMessageDialog(this, "Your turn!");
                } else if (line.startsWith("ValidMove")) {
                    String[] parts = line.split(" ")[1].split(",");
                    buttons[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])].setText(role);
                } else if (line.startsWith("OpponentMove")) {
                    String[] parts = line.split(" ")[1].split(",");
                    buttons[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])].setText(role.equals("X") ? "O" : "X");
                } else if (line.equals("Win")) {
                    JOptionPane.showMessageDialog(this, "You Win!");
                } else if (line.equals("Lose")) {
                    JOptionPane.showMessageDialog(this, "You Lose!");
                } else if (line.equals("Draw")) {
                    JOptionPane.showMessageDialog(this, "Draw!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
