package sh.miles.pineapplebot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String botPath = "PineappleBot-1.0-SNAPSHOT.jar";


    public static void main(String[] args) {
        if (System.getProperty("os.name").contains("Linux")) {
            LOGGER.info("Pineapple Bot AutoRestarter only works on windows, no linux support");
            System.exit(0);
            return;
        }

        String previousHash = null;
        Process process = startBot();

        do {
            try {
                String currentHash = calculateFileHash();

                if (!currentHash.equals(previousHash)) {
                    System.out.println("File hash has changed: " + currentHash);
                    Thread.sleep(1000);


                    process.destroy();

                    previousHash = currentHash;

                    process = startBot();
                }

                Thread.sleep(5000);
            } catch (Exception e) {
                LOGGER.error("Failed to calculate file hash", e);
                return;
            }
        } while (true);

    }

    private static String calculateFileHash() throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(botPath), md)) {
            while (dis.read() != -1) ;
        }
        byte[] digest = md.digest();
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    private static Process startBot() {
        try {
            LOGGER.info("Starting bot...");
            return new ProcessBuilder(getStartCommand()).inheritIO().start();
        } catch(IOException e) {
            LOGGER.error("Starting bot failed, used start command {}", getStartCommand(), e);
        }
        return null;
    }

    private static List<String> getStartCommand() {
        List<String> command = new ArrayList<>();
        command.add("java17");
        command.add("-Xmx4G");
        command.add("-jar");
        command.add(botPath);
        return command;
    }
}
