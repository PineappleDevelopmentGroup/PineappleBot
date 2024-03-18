package sh.miles.pineapplebot.old;

import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.miles.pineapplebot.old.json.JsonConfig;
import sh.miles.pineapplebot.old.json.JsonConfigSection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Path configDirPath = Paths.get("").resolve("config");
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static PineappleBot bot;

    public static void main(String[] args) {
        logger.info("Pineapple Ticket Bot started!");

        if (createConfigFile()) {
            logger.info("Created config file, please change the values!");
            System.exit(0);
        }

        JsonConfig config = JsonConfig.readConfig(new GsonBuilder().setPrettyPrinting().create(), configDirPath, "config.json");

        if (!checkValues(config)) {
            logger.info("Please change the values in config.json!");
            System.exit(0);
        }
        bot = new PineappleBot(config);
    }

    private static boolean createConfigFile() {
        try {
            if (!configDirPath.toFile().exists()) {
                Files.createDirectories(configDirPath);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Path configFile = configDirPath.resolve("config.json");
        if (configFile.toFile().exists()) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("config.json")))) {
            reader.transferTo(writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private static boolean checkValues(JsonConfig config) {
        if (config.getString("token").equals("YOUR TOKEN HERE")){
            logger.info("Config token is null!");
            return false;
        }
        if (config.getString("guild-id").equals("ID STR")) {
            logger.info("Config guild-id is null!");
            return false;
        }
        JsonConfigSection channels = config.getSection("channels");
        if (channels.getString("ticket-category").equals("ID STR")) {
            logger.info("Config ticket-category is null!");
            return false;
        }
        if (channels.getString("button-channel").equals("ID STR")) {
            logger.info("Config button-panel is null!");
            return false;
        }
        if (channels.getString("log-channel").equals("ID STR")) {
            logger.info("Config log is null!");
            return false;
        }
        if (channels.getString("transcript-channel").equals("ID STR")) {
            logger.info("Config transcript is null!");
            return false;
        }
        return true;
    }

}