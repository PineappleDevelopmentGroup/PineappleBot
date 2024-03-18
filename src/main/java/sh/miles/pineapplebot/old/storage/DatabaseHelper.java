package sh.miles.pineapplebot.old.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    public static <T> T executeQuery(Function<ResultSet, T> function, HikariDataSource pool, String query, Object... params) {
        try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return function.apply(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int executeUpdate(HikariDataSource pool, String query, Object... params) {
        try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasRow(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            LOGGER.error("Failed to get next row", e);
            return false;
        }
    }

    // TODO bleh
    public static Integer getID(ResultSet resultSet) {
        try {
            return resultSet.getInt("id");
        } catch (SQLException e) {
            LOGGER.error("Failed to get ID", e);
            return null;
        }
    }
}