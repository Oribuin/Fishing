package dev.oribuin.fishing.model.augment.impl;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.oribuin.fishing.api.event.impl.FailCatchEvent;
import dev.oribuin.fishing.config.TextMessage;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.storage.persistent.FishDataType;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
@ConfigSerializable
public class AugmentFailure extends Augment {

    private transient int fails;
    private TextMessage youFailed = new TextMessage("<#93bc80><b>Fish</b> <dark_grey>| <white>You have let down this fishing rod <#93bc80><fails> <white>times");

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentFailure() {
        super("failure", "<red>you're a disappointment");

        this.setMaxLevel(67);
        this.registerListener(FailCatchEvent.class, this::onFailCatch);
    }

    /**
     * The functionality provided when a player misses a fishing rod
     *
     * @param event The fishing event
     */
    @Override
    public void onFailCatch(FailCatchEvent event) {
        Player target = event.getPlayer();

        ItemStack stack = event.getRod();
        target.getWorld().strikeLightning(target.getLocation());
        this.youFailed.send(target, "fails", ++this.fails);
        this.updateAugmentContainer(stack);
    }

    /**
     * Load and deserialize data from a data container
     *
     * @param container The container to read from
     */
    @Override
    public void readContainer(PersistentDataContainer container) {
        super.readContainer(container);
        this.fails = container.getOrDefault(FAILS.key(), FAILS, 0);
    }

    /**
     * Write data into a data container
     *
     * @param container The container to write into
     */
    @Override
    public void writeContainer(PersistentDataContainer container) {
        super.writeContainer(container);
        container.set(FAILS.key(), FAILS, this.fails);
    }

    private static final FishDataType<Integer, Integer> FAILS = KeyRegistry.register("fails", DataType.INTEGER);
}

