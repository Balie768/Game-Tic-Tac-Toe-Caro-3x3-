package Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void send(String msg) {
        out.println(msg);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    @Override
    public void run() {
        try {
            send("EnterName");
            playerName = in.readLine();
            System.out.println("Player connected: " + playerName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
