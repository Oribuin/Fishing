package xyz.oribuin.fishing.manager;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.Configurable;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.impl.AugmentCallOfTheSea;
import xyz.oribuin.fishing.augment.impl.AugmentHotspot;
import xyz.oribuin.fishing.augment.impl.AugmentSaturate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AugmentManager extends Manager {

    private final Map<String, Augment> augments = new HashMap<>();
    private CommentedFileConfiguration config;

    public AugmentManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.augments.clear();

        // Register all the augments, Maybe this should be done in reflection
        this.register(new AugmentCallOfTheSea());
        this.register(new AugmentHotspot());
        this.register(new AugmentSaturate());

        // Load the augment config files
        this.augments.values().forEach(Configurable::reload);
    }

    /**
     * Get all the augments from a fishing rod item stack
     *
     * @param rod The fishing rod item stack
     *
     * @return The augments and their levels
     */
    public static Map<Augment, Integer> getAugments(ItemStack rod) {
        AugmentManager manager = FishingPlugin.get().getManager(AugmentManager.class); // static manager access inside that manager class, practical
        Map<Augment, Integer> augments = new HashMap<>();

        ItemMeta meta = rod.getItemMeta();
        if (meta == null) return augments; // Empty

        PersistentDataContainer container = meta.getPersistentDataContainer();
        Map<String, Integer> content = container.get(Augment.AUGMENTS_KEY, DataType.asMap(DataType.STRING, DataType.INTEGER));
        if (content == null || content.isEmpty()) return augments;

        // Map the augments to the actual augment object
        for (Map.Entry<String, Integer> entry : content.entrySet()) {
            Augment augment = manager.augments.get(entry.getKey());
            if (augment == null) continue;

            augments.put(augment, entry.getValue());
        }

        return augments;
    }

    /**
     * Register an augment to the manager to be used
     *
     * @param augment The augment to register
     */
    public void register(Augment augment) {
        String key = augment.name().toLowerCase();
        if (key.isEmpty() || key.contains(" ")) return;

        this.augments.put(augment.name(), augment);
    }

    @Override
    public void disable() {

    }

}
