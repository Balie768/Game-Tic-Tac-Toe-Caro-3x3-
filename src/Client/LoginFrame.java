package Client;

import javax.swing.*;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginFrame extends JFrame {
    private JTextField nameField, ipField;
    private JButton connectBtn, historyBtn;

    public LoginFrame() {
        setTitle("Login - TicTacToe");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 30, 80, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 30, 180, 25);
        add(nameField);

        JLabel ipLabel = new JLabel("Server IP:");
        ipLabel.setBounds(50, 70, 80, 25);
        add(ipLabel);

        ipField = new JTextField("127.0.0.1");
        ipField.setBounds(150, 70, 180, 25);
        add(ipField);

        connectBtn = new JButton("Connect");
        connectBtn.setBounds(70, 140, 100, 40);
        add(connectBtn);

        historyBtn = new JButton("Lịch sử");
        historyBtn.setBounds(220, 140, 100, 40);
        add(historyBtn);

        connectBtn.addActionListener(e -> connect());
        historyBtn.addActionListener(e -> viewHistory());
    }

    private void connect() {
        try {
            String name = nameField.getText().trim();
            String ip = ipField.getText().trim();
            Socket socket = new Socket(ip, 12345);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("LOGIN:" + name);

            new GameFrame(socket, name).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection failed!");
        }
    }

    private void viewHistory() {
        try {
            String name = nameField.getText().trim();
            String ip = ipField.getText().trim();
            Socket socket = new Socket(ip, 12345);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HISTORY:" + name);

            new HistoryFrame(socket).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể lấy lịch sử!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
