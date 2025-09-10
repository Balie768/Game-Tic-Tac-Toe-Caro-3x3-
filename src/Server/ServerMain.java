package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ServerMain {
    private static final int PORT = 88888;
    private static Queue<ClientHandler> waitingClients = new LinkedList<>();

    public static void main(String[] args) {
        System.out.println("TicTacToe Server is running on port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                client.start();

                synchronized (waitingClients) {
                    waitingClients.add(client);
                    if (waitingClients.size() >= 2) {
                        ClientHandler p1 = waitingClients.poll();
                        ClientHandler p2 = waitingClients.poll();
                        new GameRoom(p1, p2).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
