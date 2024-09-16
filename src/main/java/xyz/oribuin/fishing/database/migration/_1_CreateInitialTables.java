package xyz.oribuin.fishing.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "users` (" +
                       "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY," +
                       "`entropy` INT NOT NULL," +
                       "`level` INT NOT NULL," +
                       "`experience` INT NOT NULL," +
                       "`skill_points` INT NOT NULL," +
                       "`skills` TEXT NOT NULL" +
                       ");";

        connection.createStatement().execute(query);
    }

}
