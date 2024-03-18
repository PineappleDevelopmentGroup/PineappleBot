package sh.miles.pineapplebot.old;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import sh.miles.pineapplebot.old.data.*;
import sh.miles.pineapplebot.old.storage.StorageManager;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TicketManager {

    private final PineappleBot bot;
    private final Category category;
    private final StorageManager storageManager;

    private final Map<Long, Ticket> tickets = new HashMap<>(); // <channel id, Ticket>

    private final TranscriptHandler transcriptHandler;

    public TicketManager(PineappleBot bot) {
        this.bot = bot;
        this.category = bot.getTicketCategory();
        this.storageManager = StorageManager.getInstance();
        this.transcriptHandler = new TranscriptHandler(bot, this);
        loadOpenTickets();
    }

    public CompletableFuture<PluginComissionTicket> createPluginCommissionTicket(long creatorId, String function, String budget, String otherInfo) {
        return storageManager.createPluginCommissionTicket(creatorId, function, budget, otherInfo).thenCompose(id -> {
            final PluginComissionTicket ticket = new PluginComissionTicket(creatorId, function, budget, otherInfo);
            ticket.setId(id);
            createPluginCommissionChannel(ticket);
            return CompletableFuture.completedFuture(ticket);
        });
    }

    public CompletableFuture<ServerSetupTicket> createServerSetupTicket(long creatorId, String serverType, String setupNeeded, String serverInfo, String otherInfo, String budget) {
        return storageManager.createServerSetupTicket(creatorId, serverType, setupNeeded, serverInfo, otherInfo, budget)
                .thenCompose(id -> {
                    final ServerSetupTicket ticket = new ServerSetupTicket(creatorId,
                            serverType,
                            setupNeeded,
                            serverInfo,
                            otherInfo,
                            budget
                    );
                    ticket.setId(id);
                    createServerSetupChannel(ticket);
                    return CompletableFuture.completedFuture(ticket);
                });
    }

    public CompletableFuture<PluginSetupTicket> createPluginSetupTicket(long creatorId, String budget, String setupNeeded, String setupInfo, String otherInfo) {
        return storageManager.createPluginSetupTicket(creatorId, budget, setupNeeded, setupInfo, otherInfo)
                .thenCompose(id -> {
                    final PluginSetupTicket ticket = new PluginSetupTicket(
                            creatorId,
                            budget,
                            setupNeeded,
                            setupInfo,
                            otherInfo
                    );
                    ticket.setId(id);
                    createPluginSetupChannel(ticket);
                    return CompletableFuture.completedFuture(ticket);
                });
    }

    private void createPluginSetupChannel(PluginSetupTicket ticket) {
        ChannelAction<TextChannel> action = this.category.createTextChannel("ticket-" + ticket.getId());
        action.addMemberPermissionOverride(ticket.getUserId(), Permission.VIEW_CHANNEL.getRawValue(), 0);
        action.addMemberPermissionOverride(this.bot.getSelfUser().getIdLong(),
                Permission.VIEW_CHANNEL.getRawValue(),
                0
        );
        action.queue((channel) -> {
            this.storageManager.updatePluginSetupTicketChannel(ticket.getId(), channel.getIdLong());
            ticket.setChannelId(channel.getIdLong());

            EmbedBuilder mainEmbed = new EmbedBuilder();
            mainEmbed.setTitle("Plugin Setup Ticket");
            mainEmbed.setDescription("Ticket created by <@" + ticket.getUserId() + ">");
            mainEmbed.addField("Setup Needed", ticket.getSetupNeeded(), false);
            mainEmbed.addField("Setup Info", ticket.getSetupInfo(), false);
            mainEmbed.addField("Other Info", ticket.getOtherInfo(), false);
            mainEmbed.addField("Budget", ticket.getBudget(), false);
            mainEmbed.setColor(Color.PINK);
            channel.sendMessageEmbeds(mainEmbed.build()).addActionRow(
                    Button.danger("close-ticket", "Close Ticket")
            ).queue();

            this.tickets.put(channel.getIdLong(), ticket);
        });
    }

    private void createServerSetupChannel(ServerSetupTicket ticket) {
        ChannelAction<TextChannel> action = this.category.createTextChannel("ticket-" + ticket.getId());
        action.addMemberPermissionOverride(ticket.getUserId(), Permission.VIEW_CHANNEL.getRawValue(), 0);
        action.addMemberPermissionOverride(this.bot.getSelfUser().getIdLong(),
                Permission.VIEW_CHANNEL.getRawValue(),
                0
        );
        action.queue((channel) -> {
            this.storageManager.updateServerSetupTicketChannel(ticket.getId(), channel.getIdLong());
            ticket.setChannelId(channel.getIdLong());

            EmbedBuilder mainEmbed = new EmbedBuilder();
            mainEmbed.setTitle("Server Setup Ticket");
            mainEmbed.setDescription("Ticket created by <@" + ticket.getUserId() + ">");
            mainEmbed.addField("Server Type", ticket.getServerType(), false);
            mainEmbed.addField("Setup Needed", ticket.getSetupNeeded(), false);
            mainEmbed.addField("Server Info", ticket.getServerInfo(), false);
            mainEmbed.addField("Other Info", ticket.getOtherInfo(), false);
            mainEmbed.addField("Budget", ticket.getBudget(), false);
            mainEmbed.setColor(Color.BLUE);
            channel.sendMessageEmbeds(mainEmbed.build()).addActionRow(
                    Button.danger("close-ticket", "Close Ticket")
            ).queue();

            this.tickets.put(channel.getIdLong(), ticket);
        });
    }

    private void createPluginCommissionChannel(PluginComissionTicket ticket) {
        ChannelAction<TextChannel> action = this.category.createTextChannel("ticket-" + ticket.getId());
        action.addMemberPermissionOverride(ticket.getUserId(), Permission.VIEW_CHANNEL.getRawValue(), 0);
        action.addMemberPermissionOverride(this.bot.getSelfUser().getIdLong(),
                Permission.VIEW_CHANNEL.getRawValue(),
                0
        );
        action.queue((channel) -> {
            this.storageManager.updatePluginCommissionTicketChannel(ticket.getId(), channel.getIdLong());
            ticket.setChannelId(channel.getIdLong());

            EmbedBuilder mainEmbed = new EmbedBuilder();
            mainEmbed.setTitle("Plugin Commission Ticket");
            mainEmbed.setDescription("Ticket created by <@" + ticket.getUserId() + ">");
            mainEmbed.addField("Budget", ticket.getBudget(), false);
            mainEmbed.addField("Function", ticket.getFunction(), false);
            mainEmbed.addField("Other Info", ticket.getOtherInfo(), false);
            mainEmbed.setColor(Color.GREEN);
            channel.sendMessageEmbeds(mainEmbed.build()).addActionRow(
                    Button.danger("close-ticket", "Close Ticket")
            ).queue();

            this.tickets.put(channel.getIdLong(), ticket);
        });
    }

    public void handleClose(ButtonInteractionEvent event) {
        User user = event.getUser();
        if (!this.bot.getAdminIds().contains(user.getId())) {
            event.getHook().editOriginal("Only admins can close tickets!").queue();
            return;
        }

        long channelId = event.getChannelIdLong();
        try {
            this.transcriptHandler.createAndSendTranscript(channelId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Ticket getTicket(long channelId) {
        return this.tickets.get(channelId);
    }

    private void loadOpenTickets() {
        List<TextChannel> channels = this.category.getTextChannels();
        List<Integer> ids = new ArrayList<>();
        for (TextChannel channel : channels) {
            if (channel.getName().startsWith("ticket-")) {
                ids.add(Integer.parseInt(channel.getName().replace("ticket-", "")));
            }
        }

        this.storageManager.loadOpenTickets(ids).whenComplete((tickets, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            for (Ticket ticket : tickets) {
                this.tickets.put(ticket.getChannelId(), ticket);
            }
        });
    }

    public void unloadTicket(long channelId) {
        this.tickets.remove(channelId);
    }
}
