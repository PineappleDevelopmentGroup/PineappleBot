package sh.miles.pineappleticketbot.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A Configuration Section of a json based config
 *
 * @author Miles - Dove
 */
public class JsonConfigSection {

    private final JsonConfigSection parent;
    private final JsonObject current;

    public JsonConfigSection(@NotNull final JsonObject current) {
        this(null, current);
    }

    public JsonConfigSection(@Nullable final JsonConfigSection parent, @NotNull final JsonObject current) {
        this.parent = parent;
        this.current = current;
    }

    /**
     * @param key the key
     * @return the string at that key location
     */
    @Nullable
    public String getString(@NotNull final String key) {
        return safeget(key, JsonElement::getAsString);
    }

    /**
     * @param key the key
     * @return the byte at that key location
     */
    public Byte getByte(@NotNull final String key) {
        return safeget(key, JsonElement::getAsByte);
    }

    /**
     * @param key the key
     * @return the short at that key location
     */
    public Short getShort(@NotNull final String key) {
        return safeget(key, JsonElement::getAsShort);
    }

    /**
     * @param key the key
     * @return the boolean at that key location
     */
    public Boolean getBoolean(@NotNull final String key) {
        return safeget(key, JsonElement::getAsBoolean);
    }

    /**
     * @param key the key
     * @return the int at that key location
     */
    public Integer getInt(@NotNull final String key) {
        return safeget(key, JsonElement::getAsInt);
    }

    /**
     * @param key the key
     * @return the float at that key location
     */
    public Float getFloat(@NotNull final String key) {
        return safeget(key, JsonElement::getAsFloat);
    }

    /**
     * @param key the key
     * @return the double at that key location
     */
    public Double getDouble(@NotNull final String key) {
        return safeget(key, JsonElement::getAsDouble);
    }

    /**
     * @param key the key
     * @return the long at that key location
     */
    public Long getLong(@NotNull final String key) {
        return safeget(key, JsonElement::getAsLong);
    }

    /**
     * @param key the key
     * @return the string array at that key location
     */
    @Nullable
    public String[] getStrings(@NotNull final String key) {
        return safegetarr(key, JsonElement::getAsString, String[]::new);
    }

    /**
     * @param key the key
     * @return the byte array at that key location
     */
    @Nullable
    public Byte[] getBytes(@NotNull final String key) {
        return safegetarr(key, JsonElement::getAsByte, Byte[]::new);
    }

    /**
     * @param key the key
     * @return the short array at that key location
     */
    @Nullable
    public Short[] getShorts(@NotNull final String key) {
        return safegetarr(key, JsonElement::getAsShort, Short[]::new);
    }

    /**
     * @param key the key
     * @return the boolean array at that key location
     */
    @Nullable
    public Boolean[] getBooleans(@NotNull final String key) {
        return safegetarr(key, JsonElement::getAsBoolean, Boolean[]::new);
    }

    /**
     * @param key the key
     * @return the int array at that key location
     */
    @Nullable
    public Integer[] getInts(@NotNull final String key) {
        return safegetarr(key, JsonElement::getAsInt, Integer[]::new);
    }

    /**
     * @param key the key
     * @return the float array at that key location
     */
    @Nullable
    public Float[] getFloats(@NotNull final String key) {
        return safegetarr(key, JsonElement::getAsFloat, Float[]::new);
    }

    /**
     * @param key the key
     * @return the double array at that key location
     */
    @Nullable
    public Double[] getDoubles(@NotNull final String key) {
        return safegetarr(key, JsonElement::getAsDouble, Double[]::new);
    }

    /**
     * @param key the key
     * @return the object to be mapped at that key location
     */
    @Nullable
    public <T> T getObjectAndMap(@NotNull final String key, Function<JsonObject, T> mapper) {
        JsonObject object = current.getAsJsonObject(key);
        if (object == null) {
            return null;
        }
        return mapper.apply(object);
    }

    @Nullable
    public JsonConfigSection getSection(@NotNull final String key) {
        return safeget(key, (element) -> new JsonConfigSection(this, element.getAsJsonObject()));
    }

    /**
     * @return the parent section if one exists, otherwise null
     */
    @Nullable
    public JsonConfigSection getParent() {
        return this.parent;
    }

    /**
     * @return true if this section has a parent, otherwise false
     */
    public boolean hasParent() {
        return this.parent != null;
    }

    private <T> T safeget(String key, Function<JsonElement, T> mapper) {
        final JsonElement element = current.get(key);
        if (element == null) {
            return null;
        }
        return mapper.apply(element);
    }

    private <T> T[] safegetarr(String key, Function<JsonElement, T> mapper, Function<Integer, T[]> empty) {
        final JsonElement element = current.get(key);
        if (element == null) {
            return empty.apply(0);
        }

        final JsonArray array = element.getAsJsonArray();
        T[] arr = empty.apply(array.size());
        for (int i = 0; i < arr.length; i++) {
            arr[i] = mapper.apply(array.get(i));
        }
        return arr;
    }

}
