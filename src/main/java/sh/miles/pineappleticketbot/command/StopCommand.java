package sh.miles.pineappleticketbot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import sh.miles.pineappleticketbot.PineappleTicketBot;

public class StopCommand implements BotCommand {

    private final PineappleTicketBot bot;

    public StopCommand(PineappleTicketBot bot) {
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
