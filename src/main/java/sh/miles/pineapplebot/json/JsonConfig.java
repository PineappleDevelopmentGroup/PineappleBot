package sh.miles.pineapplebot.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A Parent configuration class for JsonConfigs
 *
 * @author Miles - Dove
 */
public class JsonConfig extends JsonConfigSection {

    private JsonConfig(@NotNull final Gson gson, @NotNull final Path path) {
        super(init(gson, path));
    }

    private static JsonObject init(@NotNull final Gson gson, @NotNull final Path path) {
        try {
            return gson.fromJson(Files.newBufferedReader(path), JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonConfig readConfig(@NotNull final Gson gson, @NotNull final Path path, @NotNull final String name) {
        return new JsonConfig(gson, path.resolve(name));
    }
}