package Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Storage {
    private static final String FILE_PATH = "data/matches.csv";

    static {
        try {
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) parentDir.mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.append("PlayerX,PlayerO,Result\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveMatch(String playerX, String playerO, char result) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.append(playerX).append(",")
                  .append(playerO).append(",")
                  .append(result == 'D' ? "Draw" : (result == 'X' ? "Player X" : "Player O"))
                  .append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
