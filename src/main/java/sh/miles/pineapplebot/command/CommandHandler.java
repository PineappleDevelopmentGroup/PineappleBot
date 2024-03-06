package sh.miles.pineapplebot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import sh.miles.pineapplebot.PineappleBot;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {

    private final Map<String, BotCommand> commands = new HashMap<>();
    private final PineappleBot bot;

    public CommandHandler(PineappleBot bot) {
        this.bot = bot;
        registerCommand("create-embed", "User ID Locked, creates the embed for tickets", new TicketCreatorEmbedCommand(bot));
        registerCommand("stop", "User ID Locked, Stops the bot", new StopCommand(bot));
        registerCommand("custom-embed", "User ID Locked, creates a custom embed", new CustomEmbedCommand(bot));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        BotCommand command = commands.get(event.getName());
        if (command == null) {
//            LoggerFactory.getLogger(getClass()).warn("Command not found: " + event.getName());
            return;
        }

        command.execute(event);
    }

    private void registerCommand(String name, String description, BotCommand command) {
        commands.put(name, command);
        this.bot.registerCommand(name, description);
    }
}
