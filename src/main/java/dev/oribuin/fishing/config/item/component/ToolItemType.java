package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ItemConstruct;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.util.TriState;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.papermc.paper.datacomponent.DataComponentTypes.TOOL;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public class ToolItemType extends ConstructComponent<Tool> {

    private static final Registry<BlockType> REGISTRY = ItemConstruct.getRegistry().getRegistry(RegistryKey.BLOCK);

    private List<ToolRule> rules;
    private int damagePerBlock;
    private float defaultSpeed;

    /**
     * Create a new ToolStack object to store the tool information
     */
    public ToolItemType() {
        ToolRule rule = new ToolRule(List.of("stone"), 2.0f, TriState.TRUE);
        this.enabled = false;
        this.rules = new ArrayList<>(List.of(rule));
        this.damagePerBlock = 1;
        this.defaultSpeed = 1.0f;
    }

    /**
     * Create a new ToolStack object to store the tool information
     *
     * @param rules          The list of rules for the tool
     * @param damagePerBlock The damage per block
     * @param defaultSpeed   The default speed of the tool
     */
    public ToolItemType(List<ToolRule> rules, int damagePerBlock, float defaultSpeed) {
        this.enabled = true;
        this.rules = rules;
        this.damagePerBlock = damagePerBlock;
        this.defaultSpeed = defaultSpeed;
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable Tool establish() {
        return Tool.tool()
                .addRules(this.rules.stream().map(ToolRule::asComponent).toList())
                .defaultMiningSpeed(this.defaultSpeed)
                .damagePerBlock(this.damagePerBlock)
                .build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        Tool established = this.establish();
        if (established == null || !this.enabled) return;

        stack.setData(TOOL, established);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(TOOL);
    }

    /**
     * Convert a Tool object to a ToolStack object to be used in the game
     *
     * @param tool The Tool object to convert
     *
     * @return The ToolStack object
     */
    public static ToolItemType from(Tool tool) {
        List<ToolRule> rules = tool.rules()
                .stream()
                .map(ToolRule::from)
                .toList();

        return new ToolItemType(rules, tool.damagePerBlock(), tool.defaultMiningSpeed());
    }

    /**
     * Get a block type from the list of blocks that the tool can break
     *
     * @param block The block to get the type of
     *
     * @return The block type
     */
    private static BlockType getType(String block) {
        if (block == null) return null;

        NamespacedKey namespace = NamespacedKey.fromString(block);
        return namespace != null ? REGISTRY.get(namespace) : null;
    }


    @ConfigSerializable
    public static class ToolRule {

        private List<String> blocks;
        private Float speed;
        private TriState drops;

        /**
         * Create a new ToolRule object to store the tool information
         */
        public ToolRule() {
            this.blocks = new ArrayList<>();
            this.speed = 1.0f;
            this.drops = TriState.NOT_SET;
        }

        /**
         * Create a new ToolRule object to store the tool information
         *
         * @param blocks The list of blocks that the tool can break
         * @param speed  The speed of the tool
         * @param drops  Whether the tool drops the block
         */
        public ToolRule(List<String> blocks, Float speed, TriState drops) {
            this.blocks = blocks;
            this.speed = speed;
            this.drops = drops;
        }

        /**
         * Convert the ToolRule object to a Tool.Rule object
         *
         * @return The Tool.Rule object
         */
        public Tool.Rule asComponent() {
            RegistryKeySet<@NotNull BlockType> set = RegistrySet.keySetFromValues(RegistryKey.BLOCK,
                    this.blocks.stream()
                            .map(ToolItemType::getType)
                            .filter(Objects::nonNull)
                            .toList()
            );

            return Tool.rule(set, this.speed, this.drops);
        }

        /**
         * Convert a Tool.Rule object to a ToolRule object
         *
         * @param rule The Tool.Rule object to convert
         *
         * @return The ToolRule object
         */
        public static ToolRule from(Tool.Rule rule) {
            List<String> blocks = rule.blocks()
                    .values()
                    .stream()
                    .map(key -> key.key().value())
                    .toList();


            return new ToolRule(blocks, rule.speed(), rule.correctForDrops());
        }


        public List<String> getBlocks() {
            return blocks;
        }

        public ToolRule setBlocks(List<String> blocks) {
            this.blocks = blocks;
            return this;
        }

        public Float getSpeed() {
            return speed;
        }

        public ToolRule setSpeed(Float speed) {
            this.speed = speed;
            return this;
        }

        public TriState getDrops() {
            return drops;
        }

        public ToolRule setDrops(TriState drops) {
            this.drops = drops;
            return this;
        }
    }

}
