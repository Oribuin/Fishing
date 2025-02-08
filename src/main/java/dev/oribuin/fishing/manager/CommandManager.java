package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.command.FishCommand;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * The command manager for the fishing plugin
 */
public class CommandManager extends AbstractCommandManager {

    /**
     * Create a new command manager instance with the provided plugin instance.
     *
     * @param rosePlugin The plugin instance.
     */
    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Establish the root commands for the plugin, All sub commands should be registered in the {@link BaseRoseCommand} class.
     *
     * @return A list of root commands for the plugin.
     */
    @Override
    public @NotNull List<Function<RosePlugin, BaseRoseCommand>> getRootCommands() {
        return List.of(FishCommand::new);
    }

}
