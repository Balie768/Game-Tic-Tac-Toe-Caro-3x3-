// Client/RegisterFrame.java
package Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class RegisterFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass, txtConfirm;
    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 12345;

    public RegisterFrame(JFrame parent) {
        setTitle("Đăng ký tài khoản");
        setSize(480, 280);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 15));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(new Color(245, 249, 255));
        setContentPane(root);

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(192, 57, 43));
        root.add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        root.add(center, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        center.add(lblUser, gbc);

        txtUser = new JTextField(20);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        center.add(txtUser, gbc);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(lblPass, gbc);

        txtPass = new JPasswordField(20);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        center.add(txtPass, gbc);

        JLabel lblConfirm = new JLabel("Xác nhận mật khẩu:");
        lblConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        center.add(lblConfirm, gbc);

        txtConfirm = new JPasswordField(20);
        txtConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        center.add(txtConfirm, gbc);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        bottom.setOpaque(false);
        root.add(bottom, BorderLayout.SOUTH);

        JButton btnBack = new JButton("Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setPreferredSize(new Dimension(110, 32));

        JButton btnRegister = new JButton("Đăng ký");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(46, 204, 113));
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(110, 32));

        bottom.add(btnBack);
        bottom.add(btnRegister);

        btnBack.addActionListener(e -> dispose());
        btnRegister.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String username = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (username.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("REGISTER:" + username + ":" + pass);
            String resp = in.readLine();
            if ("REGISTER_OK".equals(resp)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Bạn có thể đăng nhập.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        resp != null ? resp.replace("REGISTER_FAIL:", "") : "Lỗi đăng ký!");
            }
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không kết nối được đến server!");
        }
    }
}
