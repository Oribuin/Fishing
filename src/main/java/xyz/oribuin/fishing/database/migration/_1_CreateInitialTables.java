package xyz.oribuin.fishing.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import dev.rosewood.rosegarden.manager.AbstractDataManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Initial migration to create the users table, this is ran when the plugin is first enabled.
 *
 * @see AbstractDataManager#reload()  to see how the migrations are ran
 */
public class _1_CreateInitialTables extends DataMigration {

    /**
     * Instantiates a new Data migration, this will be run when the plugin is first enabled.
     */
    public _1_CreateInitialTables() {
        super(1);
    }

    /**
     * Migrate this change to the database using the provided connection and table prefix
     *
     * @param connector   The database connector
     * @param connection  The connection to the database
     * @param tablePrefix The table prefix
     *
     * @throws SQLException If an error occurs while executing the query
     */
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
