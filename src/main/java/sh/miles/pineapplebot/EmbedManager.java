package sh.miles.pineapplebot;

import sh.miles.pineapplebot.structure.EmbedCreator;

import java.util.HashMap;
import java.util.Map;

public class EmbedManager {

    private final Map<Long, EmbedCreator> creators = new HashMap<>(); // <message id, EmbedCreator>


    public void handleButton(long messageId, String button) {
        EmbedCreator creator = creators.get(messageId);
        if (creator == null) {
            return;
        }

        creator.handleButton(button);
    }

}
