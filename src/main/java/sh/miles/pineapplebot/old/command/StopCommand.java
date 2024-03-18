package sh.miles.pineapplebot.old.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import sh.miles.pineapplebot.old.PineappleBot;

public class StopCommand implements BotCommand {

    private final PineappleBot bot;

    public StopCommand(PineappleBot bot) {
        this.bot = bot;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String id = event.getUser().getId();
        if (!bot.getAdminIds().contains(id)) {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            return;
        }

        event.reply("Shutting down...").setEphemeral(true).queue();
        this.bot.log("Bot shutdown by <@" + id + ">");
        this.bot.shutdown();
    }
}
