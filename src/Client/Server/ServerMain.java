// Server/ServerMain.java
package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain {
    private static List<Socket> waiting = new ArrayList<>();
    private static Map<Socket, String> players = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(12345);
        System.out.println("Server started...");

        while (true) {
            Socket socket = server.accept();
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String line = in.readLine();
            if (line == null) return;

            // ĐĂNG KÝ
            if (line.startsWith("REGISTER:")) {
                // REGISTER:username:password
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    String username = parts[1];
                    String password = parts[2];
                    if (AccountManager.register(username, password)) {
                        out.println("REGISTER_OK");
                    } else {
                        out.println("REGISTER_FAIL:Tên đăng nhập đã tồn tại!");
                    }
                } else {
                    out.println("REGISTER_FAIL:Dữ liệu không hợp lệ!");
                }
                socket.close();
                return;
            }

            // ĐĂNG NHẬP
            if (line.startsWith("LOGIN:")) {
                // LOGIN:username:password
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    String username = parts[1];
                    String password = parts[2];

                    if (AccountManager.login(username, password)) {
                        out.println("LOGIN_OK");
                        players.put(socket, username);

                        synchronized (waiting) {
                            waiting.add(socket);
                            if (waiting.size() >= 2) {
                                Socket p1 = waiting.remove(0);
                                Socket p2 = waiting.remove(0);
                                String n1 = players.get(p1);
                                String n2 = players.get(p2);
                                new Thread(new GameRoom(p1, p2, n1, n2)).start();
                            }
                        }
                    } else {
                        out.println("LOGIN_FAIL:Sai tên đăng nhập hoặc mật khẩu!");
                        socket.close();
                    }
                } else {
                    out.println("LOGIN_FAIL:Dữ liệu không hợp lệ!");
                    socket.close();
                }
                return;
            }

            // LỊCH SỬ
            if (line.startsWith("HISTORY:")) {
                String name = line.substring(8);
                for (String[] match : MatchHistory.getHistoryOf(name)) {
                    out.println(String.join(",", match));
                }
                out.println("END_HISTORY");
                socket.close();
                return;
            }

            // BẢNG XẾP HẠNG
            if (line.equals("RANKING")) {
                for (String[] row : MatchHistory.getRanking()) {
                    out.println(String.join(",", row));
                }
                out.println("END_RANKING");
                socket.close();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
