package sh.miles.pineapplebot.listener;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import sh.miles.pineapplebot.TicketManager;

import java.util.List;

public class ModalListener extends ListenerAdapter {

    private final TicketManager ticketManager;

    public ModalListener(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getInteraction().getModalId().equals("plugin-commission")) {
            event.deferReply(true).queue();
            readPluginCommissionModal(event);
        }

        if (event.getInteraction().getModalId().equals("server-setup")) {
            event.deferReply(true).queue();
            readServerSetupModal(event);
        }

        if (event.getInteraction().getModalId().equals("plugin-setup")) {
            event.deferReply(true).queue();
            readPluginSetupModal(event);
        }
    }

    private void readPluginCommissionModal(ModalInteractionEvent event) {
        List<ModalMapping> values = event.getValues();
        String function = values.get(0).getAsString();
        String budget = values.get(1).getAsString();
        String otherInfo = values.get(2).getAsString();

        ticketManager.createPluginCommissionTicket(event.getUser().getIdLong(), function, budget, otherInfo).whenComplete((ticket, ex) -> {
            if (ex != null) {
                event.getHook().editOriginal("Something went wrong, Please contact an admin!").queue();
            } else {
                event.getHook().editOriginal("Ticket created!").queue();
            }

        });
    }

    private void readServerSetupModal(ModalInteractionEvent event) {
        List<ModalMapping> values = event.getValues();
        String serverType = values.get(0).getAsString();
        String setupNeeded = values.get(1).getAsString();
        String serverInfo = values.get(2).getAsString();
        String otherInfo = values.get(3).getAsString();
        String budget = values.get(4).getAsString();

        ticketManager.createServerSetupTicket(event.getUser().getIdLong(), serverType, setupNeeded, serverInfo, otherInfo, budget).whenComplete((ticket, ex) -> {
            if (ex != null) {
                event.getHook().editOriginal("Something went wrong, Please contact an admin!").queue();
            } else {
                event.getHook().editOriginal("Ticket created!").queue();
            }
        });
    }

    private void readPluginSetupModal(ModalInteractionEvent event) {
        List<ModalMapping> values = event.getValues();
        String setupNeeded = values.get(0).getAsString();
        String setupInfo = values.get(1).getAsString();
        String otherInfo = values.get(2).getAsString();
        String budget = values.get(3).getAsString();

        ticketManager.createPluginSetupTicket(event.getUser().getIdLong(), budget, setupNeeded, setupInfo, otherInfo).whenComplete((ticket, ex) -> {
            if (ex != null) {
                event.getHook().editOriginal("Something went wrong, Please contact an admin!").queue();
            } else {
                event.getHook().editOriginal("Ticket created!").queue();
            }
        });
    }
}
