package sh.miles.pineappleticketbot.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sh.miles.pineappleticketbot.PineappleTicketBot;
import sh.miles.pineappleticketbot.structure.Modals;

public class ButtonListener extends ListenerAdapter {

    private final PineappleTicketBot bot;

    public ButtonListener(PineappleTicketBot bot) {
        this.bot = bot;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("plugin-commission")) {
            event.replyModal(Modals.PLUGIN_COMMISSION.getModal()).queue();
        }

        if (event.getComponentId().equals("server-setup")) {
            event.replyModal(Modals.SERVER_SETUP.getModal()).queue();
        }

        if (event.getComponentId().equals("plugin-setup")) {
            event.replyModal(Modals.PLUGIN_SETUP.getModal()).queue();
        }

        if (event.getComponentId().equals("close-ticket")) {
            if (bot.getAdminIds().contains(event.getUser().getId())) {
                event.deferEdit().queue();
                this.bot.getTicketManager().handleClose(event);
            } else {
                event.getHook().sendMessage("You do not have permission to use this button.").setEphemeral(true).queue();
            }
        }
    }
}
