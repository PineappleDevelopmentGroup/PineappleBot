package sh.miles.pineapplebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.LoggerFactory;
import sh.miles.pineapplebot.command.CommandHandler;
import sh.miles.pineapplebot.command.text.TextCommandHandler;
import sh.miles.pineapplebot.json.JsonConfig;
import sh.miles.pineapplebot.json.JsonConfigSection;
import sh.miles.pineapplebot.listener.ButtonListener;
import sh.miles.pineapplebot.listener.ModalListener;
import sh.miles.pineapplebot.storage.StorageManager;

import java.util.List;

public class PineappleBot {

    private static PineappleBot instance;

    private JDA jda;
    private final TextChannel buttonPanelChannel;
    private final TextChannel logChannel;
    private final TextChannel transcriptChannel;
    private final Category ticketCategory;
    private final List<String> adminIds;
    private final Guild guild;
    private final TicketManager ticketManager;
    private final EmbedManager embedManager;

    public PineappleBot(JsonConfig config) {
        instance = this;
        this.adminIds = List.of(config.getStrings("admins"));
        JsonConfigSection channels = config.getSection("channels");
        try {
            jda = JDABuilder.createDefault(config.getString("token"))
                    .setActivity(Activity.of(Activity.ActivityType.WATCHING, "your Tickets!"))
                    .setStatus(OnlineStatus.ONLINE)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build().awaitReady();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(getClass()).error("Failed to initialize JDA", ex);
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
                new ModalListener(this.ticketManager),
                new TextCommandHandler(this)
        );

        embedManager = new EmbedManager();

        this.guild.upsertCommand(Commands.slash("textcommand", "creates a text based command").addOption(OptionType.STRING, "command", "the command name").addOption(OptionType.STRING, "value", "the return value of the command")).queue();
    }

    public void registerCommand(String name, String description) {
        this.guild.upsertCommand(name, description).queue();
    }

    public void log(String message) {
        this.logChannel.sendMessage(message).queue();
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

    public Modal handleEmbedCreatorButton(long messageId, String button) {
        return embedManager.handleButton(messageId, button);
    }

    public void handleEmbedCreatorModal(long messageId, String modalId, ModalMapping... mappings) {
        embedManager.handleModal(messageId, modalId, mappings);
    }

    public void unloadEmbedCreator(long messageId) {
        embedManager.unload(messageId);
    }

    public void createEmbed(TextChannel channel) {
        embedManager.create(channel);
    }

    public static PineappleBot getInstance() {
        return instance;
    }
}
