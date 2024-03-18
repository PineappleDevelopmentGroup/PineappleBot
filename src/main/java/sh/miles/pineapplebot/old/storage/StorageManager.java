package sh.miles.pineapplebot.old.storage;

import sh.miles.pineapplebot.old.data.Ticket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StorageManager {

    private static final StorageManager INSTANCE = new StorageManager();

    private StorageHandler handler;

    public void init() {
        this.handler = new StorageHandler();
    }

    public CompletableFuture<Integer> createPluginCommissionTicket(long creatorId, String function, String budget, String otherInfo) {
        return this.handler.createPluginCommissionTicket(creatorId, function, budget, otherInfo);
    }

    public CompletableFuture<Void> updatePluginCommissionTicketChannel(int id, long channelId) {
        return this.handler.updatePluginCommissionTicketChannel(id, channelId);
    }

    public CompletableFuture<Integer> createServerSetupTicket(long creatorId, String serverType, String setupNeeded, String serverInfo, String otherInfo, String budget) {
        return this.handler.createServerSetupTicket(creatorId, serverType, setupNeeded, serverInfo, otherInfo, budget);
    }

    public CompletableFuture<Void> updateServerSetupTicketChannel(int id, long channelId) {
        return this.handler.updateServerSetupTicketChannel(id, channelId);
    }

    public CompletableFuture<Integer> createPluginSetupTicket(long creatorId, String budget,  String setupNeeded, String setupInfo, String otherInfo) {
        return this.handler.createPluginSetupTicket(creatorId, budget, setupNeeded, setupInfo, otherInfo);
    }

    public CompletableFuture<Void> updatePluginSetupTicketChannel(int id, long channelId) {
        return this.handler.updatePluginSetupTicketChannel(id, channelId);
    }

    public CompletableFuture<List<Ticket>> loadOpenTickets(List<Integer> ids) {
        return this.handler.loadOpenTickets(ids);
    }

    public CompletableFuture<Void> saveTextCommand(String command, String value) {
        return this.handler.saveTextCommand(command, value);
    }

    public CompletableFuture<Map<String, String>> getTextCommands() {
        return this.handler.getTextCommands();
    }

    public void shutdown() {
        this.handler.shutdown();
    }

    public static StorageManager getInstance() {
        return INSTANCE;
    }
}
