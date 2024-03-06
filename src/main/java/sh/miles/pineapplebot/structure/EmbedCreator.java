package sh.miles.pineapplebot.structure;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import sh.miles.pineapplebot.PineappleBot;

import static sh.miles.pineapplebot.structure.Buttons.*;

public class EmbedCreator {

    private final long messageId;
    private final TextChannel createChannel;
    private TextChannel publishChannel;
    private final JDA jda;
    private final EmbedBuilder embed;

    private final ActionRow firstRow = ActionRow.of(TITLE, DESCRIPTION, COLOR, THUMBNAIL, IMAGE);
    private final ActionRow secondRow = ActionRow.of(AUTHOR, FOOTER, ADD_FIELD);
    private final ActionRow thirdRow = ActionRow.of(REMOVE_FIELD);

    public EmbedCreator(long messageId, TextChannel channel) {
        this.messageId = messageId;
        this.embed = new EmbedBuilder();
        this.jda = PineappleBot.getInstance().getSelfUser().getJDA();
        this.createChannel = channel;
        addButtons();
    }

    public Modal handleButton(String button) {
        return switch (button) {
            case "page-title" -> Modals.CREATOR_TITLE.getModal();
            case "page-description" -> Modals.CREATOR_DESCRIPTION.getModal();
            case "page-color" -> Modals.CREATOR_COLOR.getModal();
            case "page-thumbnail" -> Modals.CREATOR_THUMBNAIL.getModal();
            case "page-image" -> Modals.CREATOR_IMAGE.getModal();
            case "page-author" -> Modals.CREATOR_AUTHOR.getModal();
            case "page-footer" -> Modals.CREATOR_FOOTER.getModal();
            case "page-add-field" -> Modals.CREATOR_ADD_FIELD.getModal();
            case "page-remove-field" -> Modals.CREATOR_REMOVE_FIELD.getModal();
            case "page-send" -> {
                if (publishChannel == null){
                    yield Modals.CREATOR_PUBLISH_NO_CHANNEL.getModal();
                }
                sendEmbed();

                yield null;
            }
            case "page-publish-channel" -> Modals.CREATOR_PUBLISH_CHANNEL.getModal();
            default -> null;
        };
    }

    public void handleModal(String modalId, ModalMapping... mappings) {
        switch (modalId) {
            case "creator-title" -> embed.setTitle(mappings[0].getAsString(), mappings[1].getAsString());
            case "creator-description" -> embed.setDescription(mappings[0].getAsString());
            case "creator-color" -> embed.setColor(Integer.parseInt(mappings[0].getAsString()));
            case "creator-thumbnail" -> embed.setThumbnail(mappings[0].getAsString());
            case "creator-image" -> embed.setImage(mappings[0].getAsString());
            case "creator-author" -> embed.setAuthor(mappings[0].getAsString(), mappings[1].getAsString());
            case "creator-footer" -> embed.setFooter(mappings[0].getAsString(), mappings[1].getAsString());
            case "creator-add-field" -> embed.addField(mappings[0].getAsString(), mappings[1].getAsString(), Boolean.parseBoolean(mappings[2].getAsString()));
            case "creator-remove-field" -> embed.getFields().remove(Integer.parseInt(mappings[0].getAsString()));
            case "creator-publish-channel" -> publishChannel = jda.getTextChannelById(Long.parseLong(mappings[0].getAsString()));
        }
        updateEmbed();
    }

    private void addButtons() {
        this.createChannel.editMessageComponentsById(messageId, firstRow, secondRow, thirdRow, ActionRow.of(SEND, PUBLISH_CHANNEL)).queue();
    }

    private void updateEmbed() {
        this.createChannel.editMessageEmbedsById(messageId, embed.build()).queue();
    }

    private void sendEmbed() {
        this.createChannel.sendMessageEmbeds(embed.build()).queue();
        this.createChannel.editMessageComponentsById(messageId, ActionRow.of()).queue();
        PineappleBot.getInstance().unloadEmbedCreator(this.messageId);
    }

}
