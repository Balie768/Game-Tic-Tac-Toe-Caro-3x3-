// Server/AccountManager.java
package Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    private static final String FILE_NAME = "accounts.txt";

    // Đọc toàn bộ tài khoản từ file
    private static Map<String, String> loadAccounts() {
        Map<String, String> map = new HashMap<>();
        File f = new File(FILE_NAME);
        if (!f.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    // password ; username
                    String password = parts[0].trim();
                    String username = parts[1].trim();
                    map.put(username, password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Đăng ký: true nếu OK, false nếu trùng username
    public static synchronized boolean register(String username, String password) {
        Map<String, String> map = loadAccounts();
        if (map.containsKey(username)) {
            return false;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            pw.println(password + ";" + username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Đăng nhập
    public static synchronized boolean login(String username, String password) {
        Map<String, String> map = loadAccounts();
        String pass = map.get(username);
        return pass != null && pass.equals(password);
    }
}
