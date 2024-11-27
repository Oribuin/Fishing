package xyz.oribuin.fishing.manager.base;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.database.migration._1_CreateInitialTables;
import xyz.oribuin.fishing.storage.Fisher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class DataManager extends AbstractDataManager {

    private static final Gson GSON = new Gson();
    private final Map<UUID, Fisher> userData = new HashMap<>();

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        super.reload();

        this.saveBatch(this.userData.values());
        this.userData.clear(); // Clear the map

        // Load all the users who are currently online
        Collection<UUID> uuids = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList();
        this.loadBatch(uuids);
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

        this.async(() -> this.databaseConnector.connect(connection -> {
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
        this.async(() -> this.databaseConnector.connect(x -> this.loadUser(uuid, x)));
    }

    /**
     * Save a large collection of users into the database.
     *
     * @param fishers The users to save
     */
    public void saveBatch(Collection<Fisher> fishers) {
        fishers.forEach(x -> this.userData.put(x.uuid(), x));

        this.async(() -> this.databaseConnector.connect(connection -> {
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
        this.async(() -> this.databaseConnector.connect(connection -> {
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
        String query = "SELECT * FROM " + this.getTablePrefix() + "users WHERE uuid = ?";

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


    @Override
    public @NotNull List<Supplier<? extends DataMigration>> getDataMigrations() {
        return List.of(_1_CreateInitialTables::new);
    }

    /**
     * Run a task asynchronously
     *
     * @param runnable The task to run
     */
    public void async(Runnable runnable) {
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, runnable);
    }

    private record PlayerSkills(Map<String, Integer> skills) {
    }

    // SQL Queries
    private final String SAVE_USER = "REPLACE INTO " + this.getTablePrefix() + "users " +
                                     "(`uuid`, `entropy`, `level`, `experience`, `skill_points`, `skills`) " +
                                     "VALUES(?, ?, ?, ?, ?, ?)";


}
