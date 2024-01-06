package sh.miles.pineappleticketbot.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import sh.miles.pineappleticketbot.data.PluginComissionTicket;
import sh.miles.pineappleticketbot.data.PluginSetupTicket;
import sh.miles.pineappleticketbot.data.ServerSetupTicket;
import sh.miles.pineappleticketbot.data.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StorageHandler {

    private HikariDataSource dataSource;
    private int lastId = 1;

    public StorageHandler() {
        setupHikari();
    }

    private void setupHikari() {
        System.out.println("Setting up Hikari Connection Pool");
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:./pineapple-ticket-bot.db");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(15);
        config.setPoolName("PineappleTicketBot-Connection-Pool");

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


            ResultSet rs = statement.executeQuery("SELECT * FROM ticket_info ORDER BY id DESC LIMIT 1");
            if (rs.next()) {
                lastId = rs.getInt("id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> createTicketId(int id, int ticketId, String ticketType) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO ticket_info (id, ticket_id, ticket_type) VALUES (?, ?, ?)")) {

                statement.setInt(1, id);
                statement.setInt(2, ticketId);
                statement.setString(3, ticketType);

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Integer> createPluginCommissionTicket(long creatorId, String function, String budget, String otherInfo) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO plugin_commission (user_id, function, budget, other_info) VALUES (?, ?, ?, ?) RETURNING id")) {

                statement.setLong(1, creatorId);
                statement.setString(2, function);
                statement.setString(3, budget);
                statement.setString(4, otherInfo);

                ResultSet rs = statement.executeQuery();
                rs.next();
                int futureId = lastId++;
                createTicketId(futureId, rs.getInt("id"), "plugin_commission");
                return futureId;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<Void> updatePluginCommissionTicketChannel(int id, long channelId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE plugin_commission SET channel_id = ? WHERE id = (SELECT ticket_id FROM ticket_info WHERE id = ?)")) {

                statement.setLong(1, channelId);
                statement.setInt(2, id);

                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public CompletableFuture<Integer> createServerSetupTicket(long creatorId, String serverType, String setupRequired, String setupInfo, String otherInfo, String budget) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO server_setup (user_id, server_type, setup_required, setup_info, other_info, budget) VALUES (?, ?, ?, ?, ?, ?) RETURNING id")) {

                statement.setLong(1, creatorId);
                statement.setString(2, serverType);
                statement.setString(3, setupRequired);
                statement.setString(4, setupInfo);
                statement.setString(5, otherInfo);
                statement.setString(6, budget);

                ResultSet rs = statement.executeQuery();
                rs.next();
                int futureId = lastId++;
                createTicketId(futureId, rs.getInt("id"), "server_setup");
                return futureId;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<Void> updateServerSetupTicketChannel(int id, long channelId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE server_setup SET channel_id = ? WHERE id = (SELECT ticket_id FROM ticket_info WHERE id = ?)")) {

                statement.setLong(1, channelId);
                statement.setInt(2, id);

                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public CompletableFuture<Integer> createPluginSetupTicket(long creatorId, String budget, String setupNeeded, String setupInfo, String otherInfo) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO plugin_setup (budget, user_id, setup_needed, setup_info, other_info) VALUES (?, ?, ?, ?, ?) RETURNING id")) {

                statement.setString(1, budget);
                statement.setLong(2, creatorId);
                statement.setString(3, setupNeeded);
                statement.setString(4, setupInfo);
                statement.setString(5, otherInfo);

                ResultSet rs = statement.executeQuery();
                rs.next();
                int futureId = lastId++;
                createTicketId(futureId, rs.getInt("id"), "plugin_setup");
                return futureId;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<Void> updatePluginSetupTicketChannel(int id, long channelId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE plugin_setup SET channel_id = ? WHERE id = (SELECT ticket_id FROM ticket_info WHERE id = ?)")) {

                statement.setLong(1, channelId);
                statement.setInt(2, id);

                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public CompletableFuture<List<Ticket>> loadOpenTickets(List<Integer> ids) {
        return CompletableFuture.supplyAsync(() -> {
            List<Ticket> tickets = new ArrayList<>();
            for (int id : ids) {
                try (Connection connection = dataSource.getConnection();
                     PreparedStatement statement = connection.prepareStatement(
                             "SELECT * FROM ticket_info WHERE id = ?")) {

                    statement.setInt(1, id);

                    ResultSet rs = statement.executeQuery();
                    if (rs.next()) {
                        Ticket ticket = loadTicket(rs.getInt("id"),
                                rs.getInt("ticket_id"),
                                rs.getString("ticket_type"));
                        tickets.add(ticket);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM plugin_commission WHERE id = ?")) {

            statement.setInt(1, internalId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                PluginComissionTicket ticket = new PluginComissionTicket(rs.getLong("user_id"),
                        rs.getString("function"),
                        rs.getString("budget"),
                        rs.getString("other_info")
                );
                ticket.setId(id);
                ticket.setChannelId(rs.getLong("channel_id"));
                return ticket;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Ticket loadPluginSetup(int id, int internalId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM plugin_setup WHERE id = ?")) {

            statement.setInt(1, internalId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                PluginSetupTicket ticket = new PluginSetupTicket(rs.getLong("user_id"),
                        rs.getString("budget"),
                        rs.getString("setup_needed"),
                        rs.getString("setup_info"),
                        rs.getString("other_info")
                );
                ticket.setId(id);
                ticket.setChannelId(rs.getLong("channel_id"));
                return ticket;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Ticket loadServerSetup(int id, int internalId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM server_setup WHERE id = ?")) {

            statement.setInt(1, internalId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
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
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void shutdown() {
        dataSource.close();
    }
}
