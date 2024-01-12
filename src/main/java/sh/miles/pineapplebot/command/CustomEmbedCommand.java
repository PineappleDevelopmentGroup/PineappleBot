package sh.miles.pineapplebot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import sh.miles.pineapplebot.PineappleBot;

public class CustomEmbedCommand implements BotCommand {

    private final PineappleBot bot;

    public CustomEmbedCommand(PineappleBot bot) {
        this.bot = bot;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String id = event.getUser().getId();
        if (!bot.getAdminIds().contains(id)) {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            return;
        }

    }
}
