package xyz.oribuin.fishing.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.AugmentRegistry;
import xyz.oribuin.fishing.command.argument.AugmentArgument;

import java.util.HashMap;
import java.util.Map;

/**
 * Debugging command to apply an augment to a fishing rod for testing purposes.
 * This will not be available in the final version of the plugin. (maybe)
 */
public class ApplyCommand extends BaseRoseCommand {

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param rosePlugin The plugin instance.
     */
    public ApplyCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Apply an augment to the player's fishing rod. This is a debugging command.
     *
     * @param context The command context
     * @param augment The augment to apply
     * @param level   The level of the augment to apply
     */
    @RoseExecutable
    public void execute(CommandContext context, Augment augment, Integer level) {
        if (!(context.getSender() instanceof Player player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.FISHING_ROD) {
            player.sendMessage("You must be holding a fishing rod to apply an augment.");
            return;
        }

        // Get the augment from the argument
        Map<Augment, Integer> augments = new HashMap<>(AugmentRegistry.from(item));
        augments.put(augment, Math.min(level, augment.maxLevel()));

        // Apply the augment to the fishing rod
        AugmentRegistry.save(item, augments);

        player.sendMessage("Successfully applied the augment to the fishing rod.");
    }

    /**
     * Define the information for the command.
     *
     * @return The command information.
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("apply")
                .arguments(this.createArguments())
                .playerOnly(true)
                .build();
    }

    /**
     * Define the arguments for the command.
     *
     * @return The arguments required for the command.
     */
    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder()
                .required("augment", new AugmentArgument())
                .required("level", ArgumentHandlers.INTEGER)
                .build();
    }
}
