package Client;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class HistoryFrame extends JFrame {

    public HistoryFrame(Socket socket) {
        setTitle("Lịch sử trận đấu");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== BACKGROUND =====
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(new Color(235, 242, 250));
        setContentPane(bg);

        // ===== CARD =====
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 215, 235), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(820, 460));
        bg.add(card);

        // ===== HEADER =====
        JLabel title = new JLabel("LỊCH SỬ TRẬN ĐẤU", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(45, 113, 220));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.add(title, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = {"# Trận", "Người chơi 1", "Người chơi 2", "Người thắng"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);

                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ?
                            new Color(245, 249, 255) :
                            new Color(230, 238, 252));
                    c.setForeground(Color.DARK_GRAY);
                } else {
                    c.setBackground(new Color(45, 113, 220));
                    c.setForeground(Color.WHITE);
                }

                if (col == 3) {
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                }

                return c;
            }
        };

        styleTableBlue(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        card.add(scroll, BorderLayout.CENTER);

        // ===== LOAD DATA =====
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while (!(line = in.readLine()).equals("END_HISTORY")) {
                model.addRow(line.split(","));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void styleTableBlue(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        t.setRowHeight(36);
        t.setShowHorizontalLines(false);
        t.setShowVerticalLines(false);

        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        h.setForeground(Color.WHITE);
        h.setBackground(new Color(45, 113, 220));
        h.setPreferredSize(new Dimension(h.getWidth(), 40));

        ((DefaultTableCellRenderer) h.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        t.getColumnModel().getColumn(0).setCellRenderer(center);
        t.getColumnModel().getColumn(3).setCellRenderer(center);
    }
}
