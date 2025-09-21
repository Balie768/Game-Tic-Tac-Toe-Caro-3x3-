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

            if (line.startsWith("LOGIN:")) {
                String name = line.substring(6);
                players.put(socket, name);
                waiting.add(socket);
                if (waiting.size() >= 2) {
                    Socket p1 = waiting.remove(0);
                    Socket p2 = waiting.remove(0);
                    String n1 = players.get(p1), n2 = players.get(p2);
                    new Thread(new GameRoom(p1, p2, n1, n2)).start();
                }
            } else if (line.startsWith("HISTORY:")) {
                String name = line.substring(8);
                for (String[] match : MatchHistory.getHistoryOf(name)) {
                    out.println(String.join(",", match));
                }
                out.println("END_HISTORY");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}