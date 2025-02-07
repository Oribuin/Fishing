package dev.oribuin.fishing.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.manager.TierManager;

import java.util.List;

/**
 * Argument handler to match player input to a Fish.
 *
 * @see Fish The Fish class
 */
public class FishArgument extends ArgumentHandler<Fish> {

    /**
     * The constructor for the argument handler.
     */
    public FishArgument() {
        super(Fish.class);
    }

    /**
     * Serialize the player input to a Fish object if it exists. If the input does not exist, throw a HandledArgumentException.
     *
     * @param context       The context of the command
     * @param argument      The argument being handled
     * @param inputIterator The player input
     *
     * @return The Fish object
     *
     * @throws HandledArgumentException If the input does not exist
     */
    @Override
    public Fish handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Fish fish = FishingPlugin.get().getManager(TierManager.class).getFish(input);
        if (fish != null) return fish;

        throw new HandledArgumentException("argument-handler-fish", StringPlaceholders.of("input", input));
    }

    /**
     * Suggest a list of Fish based on the player input.
     *
     * @param context  The context of the command
     * @param argument The argument being handled
     * @param args     The player input
     *
     * @return A list of Fish
     */
    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return FishingPlugin.get().getManager(TierManager.class)
                .fish()
                .stream()
                .map(Fish::name)
                .toList();
    }

}
