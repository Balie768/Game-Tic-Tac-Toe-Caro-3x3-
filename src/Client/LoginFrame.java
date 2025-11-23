// Client/LoginFrame.java
package Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnRegister;

    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 12345;

    public LoginFrame() {
        setTitle("Đăng nhập");
        setSize(450, 280);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 15));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(new Color(245, 249, 255));
        setContentPane(root);

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        root.add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        root.add(center, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        center.add(lblUser, gbc);

        txtUser = new JTextField(18);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0;
        center.add(txtUser, gbc);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(lblPass, gbc);

        txtPass = new JPasswordField(18);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1;
        center.add(txtPass, gbc);

        // ===== NÚT =====
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new GridBagLayout());
        root.add(bottom, BorderLayout.SOUTH);

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(5, 10, 5, 10);

        btnLogin = createColoredButton("Đăng nhập", new Color(46, 204, 113));
        btnRegister = createColoredButton("Đăng ký", new Color(231, 76, 60));

        g2.gridx = 0; g2.gridy = 0;
        bottom.add(btnLogin, g2);

        g2.gridx = 1;
        bottom.add(btnRegister, g2);

        // ===== SỰ KIỆN =====
        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> new RegisterFrame(this).setVisible(true));
    }

    private JButton createColoredButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(130, 32));
        return b;
    }

    private void doLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("LOGIN:" + username + ":" + password);
            String resp = in.readLine();
            if ("LOGIN_OK".equals(resp)) {
                GameFrame game = new GameFrame(socket, username, in, out);
                game.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        resp != null ? resp.replace("LOGIN_FAIL:", "") : "Lỗi đăng nhập!");
                socket.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không kết nối được đến server!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
