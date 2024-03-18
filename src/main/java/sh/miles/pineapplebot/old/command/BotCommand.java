package sh.miles.pineapplebot.old.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface BotCommand {

    void execute(SlashCommandInteractionEvent event);
}
