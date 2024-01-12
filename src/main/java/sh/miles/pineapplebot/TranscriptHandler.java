package sh.miles.pineapplebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import sh.miles.pineapplebot.data.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TranscriptHandler {

    private final PineappleBot bot;
    private final TicketManager ticketManager;
    private final JDA jda;

    public TranscriptHandler(PineappleBot bot, TicketManager ticketManager) {
        this.bot = bot;
        this.jda = bot.getSelfUser().getJDA();
        this.ticketManager = ticketManager;
    }

    private void deleteTicketChannel(long channelId) {
        this.jda.getTextChannelById(channelId).delete().queue();
        this.ticketManager.unloadTicket(channelId);
    }

    public void createAndSendTranscript(long channelId) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            MessageHistory.getHistoryFromBeginning(this.jda.getTextChannelById(channelId))
                    .queue((messages -> {
                        List<Message> messageHistory = new ArrayList<>(messages.getRetrievedHistory());
                        Collections.reverse(messageHistory);
                        for (Message message : messageHistory) {
                            if (message.getAuthor().isBot()) {
                                continue;
                            }
                            byteArrayOutputStream.writeBytes(formatMessage(message).getBytes());
                        }

                        Ticket ticket = this.ticketManager.getTicket(channelId);
                        if (ticket != null) {
                            sendTranscript(byteArrayOutputStream.toByteArray(),
                                    ticket.getType(),
                                    ticket
                            ); // TODO remove me once data shouldnt ever be nonnull
                        }
                        deleteTicketChannel(channelId);
                    }));
        }
    }

    private void sendTranscript(byte[] bytes, TicketType type, Ticket ticket) {
        switch (type) {
            case PLUGIN_COMMISSION -> {
                PluginComissionTicket pluginComissionTicket = (PluginComissionTicket) ticket;
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Plugin Commission Transcript");
                embed.addField("Function", pluginComissionTicket.getFunction(), false);
                embed.addField("Budget", pluginComissionTicket.getBudget(), false);
                embed.addField("Other Info", pluginComissionTicket.getOtherInfo(), false);
                embed.setFooter("Ticket ID: " + pluginComissionTicket.getId());
                embed.setColor(Color.RED);

                MessageCreateAction action = this.bot.getTranscriptChannel().sendMessageEmbeds(embed.build());
                if (bytes.length != 0) {
                    action.addFiles(FileUpload.fromData(bytes, "transcript.txt")).queue();
                    return;
                }

                action.queue();
            }
            case SERVER_SETUP -> {
                ServerSetupTicket serverSetupTicket = (ServerSetupTicket) ticket;
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Server Setup Transcript");
                embed.addField("Server Type", serverSetupTicket.getServerType(), false);
                embed.addField("Setup Needed", serverSetupTicket.getSetupNeeded(), false);
                embed.addField("Server Info", serverSetupTicket.getServerInfo(), false);
                embed.addField("Other Info", serverSetupTicket.getOtherInfo(), false);
                embed.setFooter("Ticket ID: " + serverSetupTicket.getId());
                embed.setColor(Color.RED);

                MessageCreateAction action = this.bot.getTranscriptChannel().sendMessageEmbeds(embed.build());
                if (bytes.length != 0) {
                    action.addFiles(FileUpload.fromData(bytes, "transcript.txt")).queue();
                    return;
                }

                action.queue();
            }
            case PLUGIN_SETUP -> {
                PluginSetupTicket pluginSetupTicket = (PluginSetupTicket) ticket;
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Plugin Setup Transcript");
                embed.addField("Setup Needed", pluginSetupTicket.getSetupNeeded(), false);
                embed.addField("Setup Info", pluginSetupTicket.getSetupInfo(), false);
                embed.addField("Other Info", pluginSetupTicket.getOtherInfo(), false);
                embed.addField("Budget", pluginSetupTicket.getBudget(), false);
                embed.setFooter("Ticket ID: " + pluginSetupTicket.getId());
                embed.setColor(Color.RED);

                MessageCreateAction action = this.bot.getTranscriptChannel().sendMessageEmbeds(embed.build());
                if (bytes.length != 0) {
                    action.addFiles(FileUpload.fromData(bytes, "transcript.txt")).queue();
                    return;
                }

                action.queue();
            }
        }
    }

    private String formatMessage(Message message) {
        return String.format("<%s> (%s) [%s] %s: %s\n",
                message.getTimeCreated(),
                message.getAuthor().getId(),
                message.getAuthor().getName(),
                message.getAuthor().getGlobalName(),
                message.getContentRaw()
        );
    }
}
