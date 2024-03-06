package sh.miles.pineapplebot.command.text;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import sh.miles.pineapplebot.PineappleBot;
import sh.miles.pineapplebot.storage.StorageManager;

import java.util.HashMap;
import java.util.Map;

public class TextCommandHandler extends ListenerAdapter {

    private final PineappleBot bot;

    private final Map<String, String> commands = new HashMap<>();

    public TextCommandHandler(PineappleBot bot) {
        this.bot = bot;
        load();
    }

    public void load() {
        StorageManager.getInstance().getTextCommands().whenComplete((result, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            this.commands.putAll(result);
        });
    }

    public void save(String command, String value) {
        StorageManager.getInstance().saveTextCommand(command, value);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("textcommand")) {
            return;
        }

        if (!bot.getAdminIds().contains(event.getUser().getId())) {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            return;
        }

        String command = event.getOption("command").getAsString();
        String value = event.getOption("value").getAsString();

        this.commands.put(command, value);
        this.save(command, value);
        event.reply("Created").setEphemeral(true).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (!message.startsWith("~")) return;
        String command = message.split(" ")[0].replace("~", "");

        String value = this.commands.get(command);
        if (value == null) return;

        event.getChannel().sendMessage(value).queue();
    }
}
