package dev.rosewood.myplugin.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        final String query = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "myplugin` (" +
                "`uuid` VARCHAR(36) NOT NULL, " +
                "PRIMARY KEY (`uuid`))";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }

}
