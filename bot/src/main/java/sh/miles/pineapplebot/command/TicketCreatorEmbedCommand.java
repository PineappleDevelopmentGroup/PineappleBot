package sh.miles.pineapplebot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import sh.miles.pineapplebot.PineappleBot;

import java.awt.*;

public class TicketCreatorEmbedCommand implements BotCommand {

    private final PineappleBot bot;

    public TicketCreatorEmbedCommand(PineappleBot bot) {
        this.bot = bot;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String id = event.getUser().getId();
        if (!bot.getAdminIds().contains(id)) {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Ticket Creator");
        embed.setDescription("Click the button below to create a ticket!");
        embed.setColor(Color.GREEN);

        bot.sendButtonPanel(embed, Button.success("plugin-commission", "Commission a Plugin"),
                Button.success("server-setup", "Commission a Server Setup"),
                Button.success("plugin-setup", "Commission a Plugin Setup")
        );
        event.reply("Sent button panel to <@" + id + ">").setEphemeral(true).queue();
    }
}
