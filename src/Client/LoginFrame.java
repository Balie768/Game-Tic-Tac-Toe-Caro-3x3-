package Client;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginFrame extends JFrame {
    private JTextField nameField, ipField;

    public LoginFrame() {
        setTitle("Login - TicTacToe");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Server IP:"));
        ipField = new JTextField("127.0.0.1");
        add(ipField);

        JButton connectBtn = new JButton("Connect");
        add(connectBtn);

        connectBtn.addActionListener(e -> connect());
    }

    private void connect() {
        try {
            String name = nameField.getText().trim();
            String ip = ipField.getText().trim();
            Socket socket = new Socket(ip, 88888);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);

            new GameFrame(socket, name).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection failed!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
