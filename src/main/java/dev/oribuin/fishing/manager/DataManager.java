package dev.oribuin.fishing.manager;

import com.google.gson.Gson;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigHandler;
import dev.oribuin.fishing.config.impl.MySQLConfig;
import dev.oribuin.fishing.database.connector.DatabaseConnector;
import dev.oribuin.fishing.database.connector.MySQLConnector;
import dev.oribuin.fishing.database.connector.SQLiteConnector;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.storage.Fisher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager implements Manager {

    private static final String USERS_PREFIX = "fishingplugin_users";
    private static final Gson GSON = new Gson();

    private final FishingPlugin plugin;
    private final Map<UUID, Fisher> userData;
    private DatabaseConnector connector;
    
    public DataManager(FishingPlugin plugin) {
        this.plugin = plugin;
        this.userData = new HashMap<>();
        this.reload(plugin);
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    public void reload(FishingPlugin plugin) {
        this.disable(plugin);
        
        
        MySQLConfig sqlConfig = MySQLConfig.get();
        if (sqlConfig.isEnabled()) {
            String hostname = sqlConfig.getHostname();
            int port = sqlConfig.getPort();
            String database = sqlConfig.getDatabaseName();
            String username = sqlConfig.getUsername();
            String password = sqlConfig.getPassword();
            boolean useSSL = sqlConfig.useSSL();
            int poolSize = sqlConfig.getConnectionPoolSize();

            this.connector = new MySQLConnector(this.plugin, hostname, port, database, username, password, useSSL, poolSize);
            this.plugin.getLogger().info("Data manager connected using MySQL.");
        } else {
            this.connector = new SQLiteConnector(this.plugin);
            this.connector.cleanup();
            this.plugin.getLogger().info("Data manager connected using SQLite.");
        }
        
        // Create the initial table for the plugin
        this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
                statement.executeUpdate();
            }
        });

        this.saveBatch(this.userData.values());
        this.userData.clear(); // Clear the map

        // Load all the users who are currently online
        Collection<UUID> uuids = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList();
        this.loadBatch(uuids);
    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    public void disable(FishingPlugin plugin) {
        if (this.connector != null) {
            // Wait for all connections to finish
            long now = System.currentTimeMillis();
            long deadline = now + 5000;
            synchronized (this.connector.getLock()) {
                while (!this.connector.isFinished() && now < deadline) {
                    try {
                        this.connector.getLock().wait(deadline - now);
                        now = System.currentTimeMillis();
                    } catch (InterruptedException ex) {
                        this.plugin.getLogger().severe("Interrupted error occurred: " + ex.getMessage());
                    }
                }
            }

            this.connector.closeConnection();
        }
    }

    /**
     * Get a user's data from the cache or database if not found
     *
     * @param uuid The user's UUID
     *
     * @return The user's data
     */
    public Fisher get(UUID uuid) {
        return this.userData.get(uuid);
    }

    /**
     * Save a user's data to the database and cache
     *
     * @param fisher The user to save
     */
    public void saveUser(Fisher fisher) {
        this.userData.put(fisher.uuid(), fisher);

        this.async(() -> this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(SAVE_USER)) {
                this.saveUser(fisher, statement);
            }
        }));
    }

    /**
     * Get a user's data from the cache
     *
     * @param uuid The user's UUID
     */
    public void loadUser(UUID uuid) {
        this.async(() -> this.connector.connect(x -> this.loadUser(uuid, x)));
    }

    /**
     * Save a large collection of users into the database.
     *
     * @param fishers The users to save
     */
    public void saveBatch(Collection<Fisher> fishers) {
        fishers.forEach(x -> this.userData.put(x.uuid(), x));

        this.async(() -> this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(SAVE_USER)) {
                for (Fisher fisher : fishers) {
                    this.saveUser(fisher, statement);

                    // Add this statement as a batch
                    statement.addBatch();
                }

                // Save all the users after the loop
                statement.executeBatch();
            }
        }));
    }

    /**
     * Load a collection of uuids in a batch statement to avoid excessive connections
     *
     * @param uuids All the UUIDs to load
     */
    public void loadBatch(Collection<UUID> uuids) {
        this.async(() -> this.connector.connect(connection -> {
            for (UUID uuid : uuids) {
                this.loadUser(uuid, connection);
            }
        }));
    }

    /**
     * Implement all the required values to save a user into a PreparedStatement
     *
     * @param fisher    The user to save
     * @param statement The statement to save into
     *
     * @throws SQLException The exception which is going to be caught by the DatabaseConnector
     */
    private void saveUser(Fisher fisher, PreparedStatement statement) throws SQLException {
        statement.setString(1, fisher.uuid().toString());
        statement.setInt(2, fisher.entropy());
        statement.setInt(3, fisher.level());
        statement.setInt(4, fisher.experience());
        statement.setInt(5, fisher.points());
        statement.setString(6, GSON.toJson(new PlayerSkills(fisher.skills())));
        statement.executeUpdate();
    }

    /**
     * Load a user from the database using the pre-existing connection
     *
     * @param uuid       The user to load
     * @param connection The Connection to the database
     *
     * @throws SQLException The exception which is going to be caught by the DatabaseConnector
     */
    private void loadUser(UUID uuid, Connection connection) throws SQLException {
        Fisher fisher = new Fisher(uuid);
        String query = "SELECT * FROM " + USERS_PREFIX + " WHERE uuid = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                fisher.entropy(result.getInt("entropy"));
                fisher.level(result.getInt("level"));
                fisher.experience(result.getInt("experience"));
                fisher.points(result.getInt("skill_points"));
                fisher.skills(GSON.fromJson(result.getString("skills"), PlayerSkills.class).skills());
            }

            this.userData.put(uuid, fisher);
        }
    }

    /**
     * Get all the current players from the cache
     *
     * @return The current players in the cache
     */
    public Map<UUID, Fisher> all() {
        return this.userData;
    }


    //    @Override
    //    public @NotNull List<Supplier<? extends DataMigration>> getDataMigrations() {
    //        return List.of(_1_CreateInitialTables::new);
    //    }

    /**
     * Run a task asynchronously
     *
     * @param runnable The task to run
     */
    public void async(Runnable runnable) {
        PluginScheduler.get().runTaskAsync(runnable);
    }

    private record PlayerSkills(Map<String, Integer> skills) {
    }

    // SQL Queries
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `fishingplugin_users` (" +
                   "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY," +
                   "`entropy` INT NOT NULL," +
                   "`level` INT NOT NULL," +
                   "`experience` INT NOT NULL," +
                   "`skill_points` INT NOT NULL," +
                   "`skills` TEXT NOT NULL" +
                   ");";
    private final String SAVE_USER = "REPLACE INTO `fishingplugin_users` " +
                                     "(`uuid`, `entropy`, `level`, `experience`, `skill_points`, `skills`) " +
                                     "VALUES(?, ?, ?, ?, ?, ?)";


}
