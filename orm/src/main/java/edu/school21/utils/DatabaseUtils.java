package edu.school21.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public final class DatabaseUtils {
    private DatabaseUtils() {
    }

    public static boolean isConnectionAvailable(Connection connection) {
        if (connection == null) {
            return false;
        }

        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
