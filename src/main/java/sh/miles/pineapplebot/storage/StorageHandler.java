package sh.miles.pineapplebot.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.miles.pineapplebot.data.PluginComissionTicket;
import sh.miles.pineapplebot.data.PluginSetupTicket;
import sh.miles.pineapplebot.data.ServerSetupTicket;
import sh.miles.pineapplebot.data.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StorageHandler {

    private HikariDataSource dataSource;
    private int lastId = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageHandler.class);

    public StorageHandler() {
        setupHikari();
    }

    private void setupHikari() {
        LOGGER.info("Setting up Hikari Connection Pool");
        HikariConfig config = getHikariConfig();

        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS ticket_info (id INTEGER PRIMARY KEY, ticket_id INTEGER, ticket_type VARCHAR(30))"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS plugin_commission (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id LONG, channel_id LONG UNIQUE, function VARCHAR(1000), budget VARCHAR(100), other_info VARCHAR(1000))"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS server_setup (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id LONG, channel_id LONG UNIQUE, server_type VARCHAR(500), setup_required VARCHAR(1000), setup_info VARCHAR(1000), other_info VARCHAR(1000), budget VARCHAR(100))"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS plugin_setup (id INTEGER PRIMARY KEY AUTOINCREMENT, budget VARCHAR(100), user_id LONG, channel_id LONG UNIQUE, setup_needed VARCHAR(1000), setup_info VARCHAR(1000), other_info VARCHAR(1000))"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS custom_embed (id INTEGER PRIMARY KEY AUTOINCREMENT, channel_id LONG, message_id LONG, embed_id INTEGER)"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS custom_embed_embeds (id INTEGER PRIMARY KEY AUTOINCREMENT, author_name VARCHAR(256), author_url TEXT, author_icon_url TEXT, title VARCHAR(256), title_url TEXT, description VARCHAR(1024), color INTEGER, footer_name VARCHAR(256), footer_icon_url TEXT, timestamp VARCHAR(256), fields VARCHAR(200))"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS custom_embed_fields (id INTEGER PRIMARY KEY AUTOINCREMENT, field_name VARCHAR(256), field_value VARCHAR(1024), inline BOOLEAN)"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS text_commands (command TEXT, value TEXT)"
            );


            ResultSet rs = statement.executeQuery("SELECT * FROM ticket_info ORDER BY id DESC LIMIT 1");
            if (rs.next()) {
                lastId = rs.getInt("id") + 1;
            }
        } catch (SQLException ex) {
            LOGGER.error("Failed to setup Hikari Connection Pool", ex);
        }
    }

    @NotNull
    private static HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:./pineapple-ticket-bot.db");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(15);
        config.setPoolName("PineappleBot-Connection-Pool");

        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf-8");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("tcpKeepAlive", "true");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        return config;
    }

    public CompletableFuture<Void> saveTextCommand(String command, String value) {
        return CompletableFuture.runAsync(() -> DatabaseHelper.executeUpdate(this.dataSource,
                "INSERT INTO text_commands (command, value) VALUES (?, ?) ON CONFLICT DO UPDATE SET value = ? WHERE command = ?",
                command, value, value, command
        ));
    }

    public CompletableFuture<List<Pair<String, String>>> getTextCommands() {
        return CompletableFuture.supplyAsync(() -> DatabaseHelper.executeQuery((rs) -> {
            List<Pair<String, String>> result = new ArrayList<>();
            try {
                while (rs.next()) {
                    String command = rs.getString("command");
                    String value = rs.getString("value");
                    result.add(Pair.of(command, value));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return result;
        }, this.dataSource, "SELECT * FROM text_commands"));
    }

    public CompletableFuture<Void> createTicketId(int id, int ticketId, String ticketType) {
        return CompletableFuture.runAsync(() -> DatabaseHelper.executeUpdate(this.dataSource,
                "INSERT INTO ticket_info (id, ticket_id, ticket_type) VALUES (?, ?, ?)",
                id,
                ticketId,
                ticketType
        ));
    }

    public CompletableFuture<Integer> createPluginCommissionTicket(long creatorId, String function, String budget, String otherInfo) {
        return CompletableFuture.supplyAsync(() -> DatabaseHelper.executeQuery((rs -> getFutureId(rs,
                        "plugin_commission"
                )), this.dataSource,
                "INSERT INTO plugin_commission (user_id, function, budget, other_info) VALUES (?, ?, ?, ?) RETURNING id",
                creatorId, function, budget, otherInfo
        ));
    }

    public CompletableFuture<Void> updatePluginCommissionTicketChannel(int id, long channelId) {
        return CompletableFuture.runAsync(() -> DatabaseHelper.executeUpdate(this.dataSource,
                "UPDATE plugin_commission SET channel_id = ? WHERE id = (SELECT ticket_id FROM ticket_info WHERE id = ?)",
                channelId,
                id
        ));
    }

    public CompletableFuture<Integer> createServerSetupTicket(long creatorId, String serverType, String setupRequired, String setupInfo, String otherInfo, String budget) {
        return CompletableFuture.supplyAsync(() -> DatabaseHelper.executeQuery((rs -> getFutureId(rs, "server_setup")),
                this.dataSource,
                "INSERT INTO server_setup (user_id, server_type, setup_required, setup_info, other_info, budget) VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
                creatorId,
                serverType,
                setupRequired,
                setupInfo,
                otherInfo,
                budget
        ));
    }

    public CompletableFuture<Void> updateServerSetupTicketChannel(int id, long channelId) {
        return CompletableFuture.runAsync(() -> DatabaseHelper.executeUpdate(this.dataSource,
                "UPDATE server_setup SET channel_id = ? WHERE id = (SELECT ticket_id FROM ticket_info WHERE id = ?)",
                channelId, id
        ));
    }

    public CompletableFuture<Integer> createPluginSetupTicket(long creatorId, String budget, String setupNeeded, String setupInfo, String otherInfo) {
        return CompletableFuture.supplyAsync(() -> DatabaseHelper.executeQuery((rs -> getFutureId(rs, "plugin_setup")),
                this.dataSource,
                "INSERT INTO plugin_setup (budget, user_id, setup_needed, setup_info, other_info) VALUES (?, ?, ?, ?, ?) RETURNING id",
                budget,
                creatorId,
                setupNeeded,
                setupInfo,
                otherInfo
        ));
    }

    public CompletableFuture<Void> updatePluginSetupTicketChannel(int id, long channelId) {
        return CompletableFuture.runAsync(() -> DatabaseHelper.executeUpdate(this.dataSource,
                "UPDATE plugin_setup SET channel_id = ? WHERE id = (SELECT ticket_id FROM ticket_info WHERE id = ?)",
                channelId, id
        ));
    }

    public CompletableFuture<List<Ticket>> loadOpenTickets(List<Integer> ids) {
        return CompletableFuture.supplyAsync(() -> {
            List<Ticket> tickets = new ArrayList<>();
            for (int id : ids) {
                tickets.add(DatabaseHelper.executeQuery((rs -> {
                    try {
                        return loadTicket(rs.getInt("id"),
                                rs.getInt("ticket_id"),
                                rs.getString("ticket_type")
                        );
                    } catch (SQLException ex) {
                        LOGGER.error("Failed to load ticket", ex);
                        return null;
                    }
                }), this.dataSource, "SELECT * FROM ticket_info WHERE id = ?", id));
            }
            return tickets;
        });
    }

    private Ticket loadTicket(int id, int internalId, String ticketType) {
        return switch (ticketType) {
            case "plugin_commission" -> loadPluginCommission(id, internalId);
            case "server_setup" -> loadServerSetup(id, internalId);
            case "plugin_setup" -> loadPluginSetup(id, internalId);
            default -> null;
        };
    }

    private Ticket loadPluginCommission(int id, int internalId) {
        return DatabaseHelper.executeQuery((rs -> {
            try {
                PluginComissionTicket ticket = new PluginComissionTicket(rs.getLong("user_id"),
                        rs.getString("function"),
                        rs.getString("budget"),
                        rs.getString("other_info")
                );
                ticket.setId(id);
                ticket.setChannelId(rs.getLong("channel_id"));
                return ticket;
            } catch (SQLException ex) {
                LOGGER.error("Failed to load plugin commission ticket", ex);
                return null;
            }
        }), this.dataSource, "SELECT * FROM plugin_commission WHERE id = ?", internalId);
    }

    private Ticket loadPluginSetup(int id, int internalId) {
        return DatabaseHelper.executeQuery((rs -> {
            try {
                PluginSetupTicket ticket = new PluginSetupTicket(rs.getLong("user_id"),
                        rs.getString("budget"),
                        rs.getString("setup_needed"),
                        rs.getString("setup_info"),
                        rs.getString("other_info")
                );
                ticket.setId(id);
                ticket.setChannelId(rs.getLong("channel_id"));
                return ticket;
            } catch (SQLException ex) {
                LOGGER.error("Failed to load plugin setup ticket", ex);
                return null;
            }
        }), this.dataSource, "SELECT * FROM plugin_setup WHERE id = ?", internalId);
    }

    private Ticket loadServerSetup(int id, int internalId) {
        return DatabaseHelper.executeQuery((rs -> {
            try {
                ServerSetupTicket ticket = new ServerSetupTicket(rs.getLong("user_id"),
                        rs.getString("server_type"),
                        rs.getString("setup_required"),
                        rs.getString("setup_info"),
                        rs.getString("other_info"),
                        rs.getString("budget")
                );
                ticket.setId(id);
                ticket.setChannelId(rs.getLong("channel_id"));
                return ticket;
            } catch (SQLException ex) {
                LOGGER.error("Failed to load server setup ticket", ex);
                return null;
            }
        }), this.dataSource, "SELECT * FROM server_setup WHERE id = ?", internalId);
    }

    private int getFutureId(ResultSet rs, String type) {
        try {
            rs.next();
            int futureId = lastId++;
            createTicketId(futureId, rs.getInt("id"), type);
            return futureId;
        } catch (SQLException ex) {
            LOGGER.error("Failed to get future id", ex);
        }
        return -1;
    }

    public void shutdown() {
        dataSource.close();
    }
}
