package sh.miles.pineappleticketbot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sh.miles.pineappleticketbot.PineappleTicketBot;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {

    private final Map<String, BotCommand> commands = new HashMap<>();
    private final PineappleTicketBot bot;

    public CommandHandler(PineappleTicketBot bot) {
        this.bot = bot;
        registerCommand("create-embed", "User ID Locked, creates the embed for tickets", new TicketCreatorEmbedCommand(bot));
        registerCommand("stop", "User ID Locked, Stops the bot", new StopCommand(bot));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        BotCommand command = commands.get(event.getName());
        if (command == null) {
            System.out.println("Command not found: " + event.getName());
            return;
        }

        command.execute(event);
    }

    private void registerCommand(String name, String description, BotCommand command) {
        commands.put(name, command);
        this.bot.registerCommand(name, description);
    }
}
