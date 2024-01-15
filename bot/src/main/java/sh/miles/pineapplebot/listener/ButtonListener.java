package sh.miles.pineapplebot.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.Modal;
import sh.miles.pineapplebot.PineappleBot;
import sh.miles.pineapplebot.structure.Modals;

public class ButtonListener extends ListenerAdapter {

    private final PineappleBot bot;

    public ButtonListener(PineappleBot bot) {
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

        if (event.getComponentId().contains("page-")) {
            Modal modal = this.bot.handleEmbedCreatorButton(event.getMessageIdLong(), event.getComponentId());
            if (modal != null) {
                event.replyModal(modal).queue();
                return;
            }
            event.deferEdit().queue();
        }
    }
}
