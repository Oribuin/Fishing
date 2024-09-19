package xyz.oribuin.fishing.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.database.migration._1_CreateInitialTables;
import xyz.oribuin.fishing.storage.Fisher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DataManager extends AbstractDataManager {

    private static final Gson GSON = new Gson();
    private final Map<UUID, Fisher> userData = new HashMap<>();

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void saveUser(Fisher fisher) {
        this.userData.put(fisher.uuid(), fisher);

        this.async(() -> this.databaseConnector.connect(connection -> {
            String query = "REPLACE INTO " + this.getTablePrefix() + "users " +
                           "(`uuid`, `entropy`, `level`, `experience`, `skill_points`, `skills`) " +
                           "VALUES(?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, fisher.uuid().toString());
                statement.setInt(2, fisher.entropy());
                statement.setInt(3, fisher.level());
                statement.setInt(4, fisher.experience());
                statement.setInt(5, fisher.points());
                statement.setString(6, GSON.toJson(new PlayerSkills(fisher.skills())));
                statement.executeUpdate();
            }
        }));
    }

    public void loadUser(UUID uuid) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String query = "SELECT * FROM " + this.getTablePrefix() + "users WHERE uuid = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    Fisher fisher = new Fisher(uuid);
                    fisher.entropy(result.getInt("entropy"));
                    fisher.level(result.getInt("level"));
                    fisher.experience(result.getInt("experience"));
                    fisher.points(result.getInt("skill_points"));
                    fisher.skills(GSON.fromJson(result.getString("skills"), PlayerSkills.class).skills());

                    this.userData.put(uuid, fisher);
                }
            }
        }));
    }

    @Override
    public @NotNull List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(
                _1_CreateInitialTables.class
        );
    }

    /**
     * Run a task asynchronously
     *
     * @param runnable The task to run
     */
    public void async(Runnable runnable) {
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, runnable);
    }

    private record PlayerSkills(Map<String, Integer> skills) {}

}
