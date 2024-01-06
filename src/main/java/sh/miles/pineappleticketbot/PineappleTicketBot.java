package sh.miles.pineappleticketbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import sh.miles.pineappleticketbot.command.CommandHandler;
import sh.miles.pineappleticketbot.json.JsonConfig;
import sh.miles.pineappleticketbot.json.JsonConfigSection;
import sh.miles.pineappleticketbot.listener.ButtonListener;
import sh.miles.pineappleticketbot.listener.ModalListener;
import sh.miles.pineappleticketbot.storage.StorageManager;

import java.awt.*;
import java.util.List;

public class PineappleTicketBot {

    private JDA jda;
    private final TextChannel buttonPanelChannel;
    private final TextChannel logChannel;
    private final TextChannel transcriptChannel;
    private final Category ticketCategory;
    private final List<String> adminIds;
    private final Guild guild;
    private final TicketManager ticketManager;

    public PineappleTicketBot(JsonConfig config) {
        this.adminIds = List.of(config.getStrings("admins"));
        JsonConfigSection channels = config.getSection("channels");
        try {
            jda = JDABuilder.createDefault(config.getString("token"))
                    .setActivity(Activity.of(Activity.ActivityType.WATCHING, "your Tickets!"))
                    .setStatus(OnlineStatus.ONLINE)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build().awaitReady();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


        guild = jda.getGuildById(config.getString("guild-id"));

        buttonPanelChannel = guild.getTextChannelById(channels.getString("button-channel"));
        logChannel = guild.getTextChannelById(channels.getString("log-channel"));
        ticketCategory = guild.getCategoryById(channels.getString("ticket-category"));
        transcriptChannel = guild.getTextChannelById(channels.getString("transcript-channel"));

        StorageManager.getInstance().init();
        this.ticketManager = new TicketManager(this);
        this.jda.addEventListener(new CommandHandler(this),
                new ButtonListener(this),
                new ModalListener(this.ticketManager)
        );
    }

    public void registerCommand(String name, String description) {
        this.guild.upsertCommand(name, description).queue();
    }

    public void log(String message) {
        this.logChannel.sendMessage(message).queue();
    }

    public void log(FileUpload upload) {
        this.logChannel.sendFiles(upload).queue();
    }

    public List<String> getAdminIds() {
        return this.adminIds;
    }

    public void sendButtonPanel(EmbedBuilder embed, Button... buttons) {
        this.buttonPanelChannel.sendMessageEmbeds(embed.build()).addActionRow(buttons).queue();
    }

    public void shutdown() {
        this.jda.shutdown();
        StorageManager.getInstance().shutdown();
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public Category getTicketCategory() {
        return ticketCategory;
    }

    public User getSelfUser() {
        return jda.getSelfUser();
    }

    public TextChannel getTranscriptChannel() {
        return transcriptChannel;
    }
}
