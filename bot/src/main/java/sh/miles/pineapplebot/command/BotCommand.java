package sh.miles.pineapplebot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface BotCommand {

    void execute(SlashCommandInteractionEvent event);
}
