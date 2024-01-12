package sh.miles.pineapplebot.structure;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import sh.miles.pineapplebot.PineappleBot;

import static sh.miles.pineapplebot.structure.Buttons.*;

import java.util.ArrayList;
import java.util.List;

public class EmbedCreator {

    private int page = 1;
    private final long messageId;
    private final TextChannel channel;
    private final JDA jda;
    private final EmbedBuilder embed;

    private final List<ActionRow> firstPage = of(TITLE, DESCRIPTION, COLOR, THUMBNAIL, NEXT);
    private final List<ActionRow> secondPage = of(PREV, IMAGE, AUTHOR, FOOTER, NEXT);
    private final List<ActionRow> thirdPage = of(PREV,ADD_FIELD, REMOVE_FIELD, ADD_EMBED, REMOVE_EMBED);

    private List<ActionRow> of(Button... buttons) {
        List<ActionRow> list = new ArrayList<>();
        for (Button button : buttons) {
            list.add(ActionRow.of(button));
        }
        return list;
    }

    public EmbedCreator(long messageId, TextChannel channel) {
        this.messageId = messageId;
        this.embed = new EmbedBuilder();
        this.jda = PineappleBot.getInstance().getSelfUser().getJDA();
        this.channel = channel;
    }

    public void handleButton(String button) {
        switch (button) {
            case "page-previous" -> previousPage();
            case "page-next" -> nextPage();
        }
    }

    private void previousPage() {
        if (page == 1) {
            return;
        }
        page--;
        updateButtons();
    }

    private void nextPage() {
        if (page == 3) {
            return;
        }
        page++;
        updateButtons();
    }

    private void updateButtons() {
        switch (page) {
            case 1 -> this.channel.editMessageComponentsById(messageId, firstPage).queue();
            case 2 -> this.channel.editMessageComponentsById(messageId, secondPage).queue();
            case 3 -> this.channel.editMessageComponentsById(messageId, thirdPage).queue();
        }
    }
}
