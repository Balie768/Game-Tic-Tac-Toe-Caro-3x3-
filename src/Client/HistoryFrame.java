package Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.Socket;

public class HistoryFrame extends JFrame {
    private JTable table;

    public HistoryFrame(Socket socket) {
        setTitle("Lịch sử trận đấu");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] cols = {"Trận", "Người chơi 1", "Người chơi 2", "Người thắng"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table));

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null && !line.equals("END_HISTORY")) {
                String[] parts = line.split(",");
                model.addRow(parts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
