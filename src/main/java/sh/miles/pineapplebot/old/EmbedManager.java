package sh.miles.pineapplebot.old;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import sh.miles.pineapplebot.old.structure.EmbedCreator;

import java.util.HashMap;
import java.util.Map;

public class EmbedManager {

    private final Map<Long, EmbedCreator> creators = new HashMap<>(); // <message id, EmbedCreator>
    private final Map<String, EmbedBuilder> commandEmbeds = new HashMap<>(); // <command, EmbedBuilder>

    public void registerCommandEmbed(long messageId, String command) {
        this.commandEmbeds.put(command, this.creators.get(messageId).build());
    }

    public void handleTextCommand(String inputCommand, TextChannel channel) {
        EmbedBuilder builder = this.commandEmbeds.get(inputCommand);
        if (builder == null) {
            return; // no embed command found
        }

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public Modal handleButton(long messageId, String button) {
        EmbedCreator creator = creators.get(messageId);
        if (creator == null) {
            return null;
        }

        return creator.handleButton(button);
    }

    public void handleModal(long messageId, String modalId, ModalMapping... mapping) {
        EmbedCreator creator = creators.get(messageId);
        if (creator == null) {
            return;
        }

        creator.handleModal(modalId, mapping);
    }

    public void unload(long messageId) {
        creators.remove(messageId);
    }

    public void create(TextChannel channel) {
        MessageCreateAction action = channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Embed Manager").build());
        action.queue((msg) -> creators.put(msg.getIdLong(), new EmbedCreator(msg.getIdLong(), channel)));
    }

}
